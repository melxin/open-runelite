/*
 * Copyright (c) 2025, Melxin <https://github.com/melxin>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.openrl.api.managers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.TileObject;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.openrl.OpenRuneLiteConfig;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Time;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;
import net.runelite.client.plugins.openrl.api.game.GameThread;
import net.runelite.client.plugins.openrl.api.game.TickSnapshot;
import net.runelite.client.plugins.openrl.api.input.Mouse;
import net.runelite.client.plugins.openrl.api.input.naturalmouse.NaturalMouse;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.RS2NPCQuery;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.RS2PlayerQuery;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.RS2TileItemQuery;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.RS2TileObjectQuery;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2NPC;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Player;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Tile;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.api.SceneEntity;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Routes automated menu interactions through a dedicated single-thread executor
 * so that:
 * <ul>
 *   <li>The {@link #onMenuAutomated} event handler is always non-blocking,
 *       regardless of the thread that posts the event.</li>
 *   <li>Interactions are serialised — only one is in-flight at a time.</li>
 *   <li>All reads of live {@link Menu} / {@link MenuEntry} state are marshalled
 *       onto the client game thread via {@link GameThread#invokeAndWait}, which
 *       prevents ConcurrentModificationException and stale-read races.</li>
 * </ul>
 *
 * Methods that read client state ({@link #isLeftClickableMenuEntry},
 * {@link #getMenuEntryClickPoint}, {@link #getSelectedMenuEntry},
 * {@link #getHoveredEntities}) are marked with their thread requirements.
 * If you need to call them from off the client thread, wrap the call in
 * {@code GameThread.invokeAndWait(...)}.
 */
@Singleton
@Slf4j
public class InteractionManager
{
	@Inject
	private Client client;

	@Inject
	private OpenRuneLiteConfig config;

	@Inject
	private NaturalMouse naturalMouse;

	@Inject
	private InteractionSafety interactionSafety;

	/**
	 * Single-thread executor for all interaction work.
	 * Serialises interactions and keeps them off the client thread so that
	 * sleeping (mouse movement, menu-open waits) never blocks the game loop.
	 */
	private final ExecutorService interactionExecutor = Executors.newSingleThreadExecutor(r ->
	{
		final Thread t = new Thread(r, "openrl-interaction");
		t.setDaemon(true);
		return t;
	});

	@Inject
	InteractionManager(EventBus eventBus)
	{
		eventBus.register(this);
	}

	private volatile MenuAutomated lastMenuAutomated;

	/**
	 * Receives a script-submitted interaction request and enqueues it for
	 * execution on the interaction executor. Returns immediately so that
	 * the event-posting thread (often the game loop) is never blocked.
	 */
	@Subscribe
	public void onMenuAutomated(MenuAutomated event)
	{
		log.info("{}", event);
		this.lastMenuAutomated = event;
		interactionExecutor.submit(() -> interact(event));
	}

	// ---- private interaction dispatch ----

	private void interact(MenuAutomated menuAutomated)
	{
		interact(
			menuAutomated.getParam0(), menuAutomated.getParam1(),
			menuAutomated.getMenuAction(), menuAutomated.getIndex(),
			menuAutomated.getItemId(), menuAutomated.getWorldViewId(),
			menuAutomated.getOption(), menuAutomated.getTarget(),
			menuAutomated.getCanvasX(), menuAutomated.getCanvasY()
		);
	}

	private void interact(MenuEntry menuEntry)
	{
		interact(
			menuEntry.getParam0(), menuEntry.getParam1(),
			menuEntry.getType(), menuEntry.getIdentifier(),
			menuEntry.getItemId(), menuEntry.getWorldViewId(),
			menuEntry.getOption(), menuEntry.getTarget(),
			-1, -1
		);
	}

	private void interact(int param0, int param1, int opcode, int index, int itemId,
		int worldViewId, String option, String target, int canvasX, int canvasY)
	{
		interact(param0, param1, MenuAction.of(opcode), index, itemId, worldViewId, option, target, canvasX, canvasY);
	}

	/**
	 * Dispatches the interaction on the calling thread (the interaction executor).
	 * Menu state is read via {@link GameThread#invokeAndWait} to ensure it is
	 * always accessed on the client thread.
	 */
	private void interact(int param0, int param1, MenuAction menuAction, int index, int itemId,
		int worldViewId, String option, String target, int canvasX, int canvasY)
	{
		// ---- pre-interaction safety gate ----
		// Check hook health and circuit-breaker state before doing anything.
		// An invalid hook silently firing produces suspicious behaviour patterns
		// (spam clicks, impossible actions, desync) that are far more detectable
		// than simply doing nothing.
		if (!interactionSafety.isInteractionSafe())
		{
			log.warn("Interaction skipped — system not safe: {}", interactionSafety.getUnsafeReason());
			return;
		}

		// Also require a verified logged-in state from the last tick snapshot.
		// Do not interact while loading, on the login screen, or with a stale snapshot.
		final TickSnapshot snap = TickSnapshot.current();
		if (!snap.isLoggedIn())
		{
			log.debug("Interaction skipped — not logged in (gameState={})", snap.getGameState());
			return;
		}

		switch (config.interactionMethod())
		{
			case INVOKE:
				// invokeMenuAction schedules the call on the client thread internally.
				Static.invokeMenuAction(param0, param1, menuAction, index, itemId, worldViewId, option, target, canvasX, canvasY);
				break;

			case MOUSE_EVENTS:
			{
				naturalMouse.moveTo(canvasX, canvasY);

				// Read menu state on the client thread — never from the interaction thread.
				final boolean leftClickable = Boolean.TRUE.equals(
					GameThread.invokeAndWait(() ->
						isLeftClickableMenuEntry(param0, param1, menuAction, index, itemId, worldViewId)));

				if (leftClickable)
				{
					final MenuEntry selected = GameThread.invokeAndWait(this::getSelectedMenuEntry);
					if (selected == null
						|| selected.getParam0() != param0
						|| selected.getParam1() != param1
						|| selected.getType().getId() != menuAction.getId()
						|| selected.getIdentifier() != index
						|| selected.getItemId() != itemId
						|| selected.getWorldViewId() != worldViewId)
					{
						log.warn("Invalid selected menu entry — skipping interaction");
						interactionSafety.reportInteractionAttemptFailed("MOUSE_EVENTS:selectedEntryMismatch");
						return;
					}
					Mouse.click(canvasX, canvasY, false);
					interactionSafety.reportInteractionSuccess();
					return;
				}

				// Open context menu and look up the entry position.
				Mouse.click(canvasX, canvasY, true);
				Time.sleepUntil(
					() -> Boolean.TRUE.equals(GameThread.invokeAndWait(client::isMenuOpen)),
					1200);

				if (!Boolean.TRUE.equals(GameThread.invokeAndWait(client::isMenuOpen)))
				{
					log.warn("Context menu did not open in time — skipping interaction");
					interactionSafety.reportInteractionAttemptFailed("MOUSE_EVENTS:menuNeverOpened");
					return;
				}

				final Point clickPoint = GameThread.invokeAndWait(() ->
					getMenuEntryClickPoint(param0, param1, menuAction, index, itemId, worldViewId));

				if (clickPoint == null)
				{
					log.warn("Menu entry click point is null — moving out of menu");
					naturalMouse.moveOutsideOpenMenu();
					interactionSafety.reportInteractionAttemptFailed("MOUSE_EVENTS:entryNotFound");
					return;
				}

				naturalMouse.moveTo(clickPoint.x, clickPoint.y);
				Mouse.click(clickPoint.x, clickPoint.y, false);
				interactionSafety.reportInteractionSuccess();
				break;
			}

			case BOTH:
			{
				naturalMouse.moveTo(canvasX, canvasY);
				final Point mousePosition = Mouse.getPosition();
				if (mousePosition.getX() == canvasX && mousePosition.getY() == canvasY)
				{
					Mouse.click(canvasX, canvasY, false);
					Time.sleep(50, 100);
					// invokeMenuAction routes to the client thread internally.
					Static.invokeMenuAction(param0, param1, menuAction, index, itemId, worldViewId, option, target, canvasX, canvasY);
				}
				break;
			}
		}
	}

	@Getter
	@AllArgsConstructor
	public enum InteractMethod
	{
		INVOKE, MOUSE_EVENTS, BOTH
	}

	// ---- menu-state helpers (client-thread required) ----

	/**
	 * Returns {@code true} if the described entry would be triggered by a plain
	 * left-click (i.e. it is the top-most entry in the menu).
	 *
	 * <p><b>Must be called on the client game thread.</b>
	 * From off-thread, use: {@code GameThread.invokeAndWait(() -> isLeftClickableMenuEntry(...))}
	 */
	public boolean isLeftClickableMenuEntry(int param0, int param1, MenuAction menuAction,
		int index, int itemId, int worldViewId)
	{
		if (menuAction == MenuAction.WIDGET_FIRST_OPTION
			|| menuAction == MenuAction.NPC_FIRST_OPTION
			|| menuAction == MenuAction.PLAYER_FIRST_OPTION
			|| menuAction == MenuAction.GAME_OBJECT_FIRST_OPTION
			|| menuAction == MenuAction.GROUND_ITEM_FIRST_OPTION
			|| menuAction == MenuAction.ITEM_FIRST_OPTION
			|| menuAction == MenuAction.WALK
			|| itemId != -1 && index <= 1)
		{
			return true;
		}

		final Menu menu = client.getMenu();
		final MenuEntry[] menuEntries = menu.getMenuEntries();
		final MenuEntry top = menuEntries[menuEntries.length - 1];
		return top != null
			&& param0 == top.getParam0()
			&& param1 == top.getParam1()
			&& menuAction == top.getType()
			&& index == top.getIdentifier()
			&& itemId == top.getItemId()
			&& worldViewId == top.getWorldViewId();
	}

	/**
	 * Finds the screen coordinates of a specific entry in an open context menu.
	 * Returns {@code null} if the menu is not open or the entry is not present.
	 *
	 * <p><b>Must be called on the client game thread.</b>
	 */
	@Nullable
	public Point getMenuEntryClickPoint(int param0, int param1, MenuAction menuAction,
		int index, int itemId, int worldViewId)
	{
		final Menu menu = client.getMenu();

		if (!client.isMenuOpen() || menu == null)
		{
			return null;
		}

		final MenuEntry[] menuEntries = menu.getMenuEntries();
		int menuEntryIdx = -1;

		for (int i = menuEntries.length - 1; i >= 0; i--)
		{
			final MenuEntry entry = menuEntries[i];
			if (entry == null)
			{
				continue;
			}

			if (itemId != -1 && itemId == entry.getItemId() && index == entry.getIdentifier())
			{
				menuEntryIdx = i;
				break;
			}

			if (param0 == entry.getParam0()
				&& param1 == entry.getParam1()
				&& menuAction == entry.getType()
				&& index == entry.getIdentifier()
				&& itemId == entry.getItemId()
				&& worldViewId == entry.getWorldViewId())
			{
				menuEntryIdx = i;
				break;
			}
		}

		if (menuEntryIdx >= 0 && menuEntryIdx <= menuEntries.length - 1)
		{
			final int clickX = menu.getMenuX() + (menu.getMenuWidth() / 2);
			final int clickY = (menuEntries.length - 1 - menuEntryIdx - client.getMenuScroll()) * 15 + menu.getMenuY() + 31;
			return new Point(clickX, clickY);
		}

		return null;
	}

	/**
	 * Returns the highest-priority (top-most) menu entry, or {@code null} if the
	 * menu is empty.
	 *
	 * <p><b>Must be called on the client game thread.</b>
	 */
	@Nullable
	public MenuEntry getSelectedMenuEntry()
	{
		final MenuEntry[] entries = Static.getClient().getMenu().getMenuEntries();
		return entries.length > 0 ? entries[entries.length - 1] : null;
	}

	/**
	 * Returns the scene entities corresponding to the currently hovered menu entries.
	 *
	 * <p><b>Must be called on the client game thread.</b>
	 * The returned list is a fresh snapshot — safe to read off-thread after this
	 * method returns.
	 */
	@Nullable
	public static List<? extends SceneEntity> getHoveredEntities()
	{
		final MenuEntry[] menuEntries = Static.getClient().getMenu().getMenuEntries();

		if (menuEntries == null || menuEntries.length == 0)
		{
			return Collections.emptyList();
		}

		final List<SceneEntity> out = new ArrayList<>();

		for (MenuEntry menuEntry : menuEntries)
		{
			final MenuAction menuAction = menuEntry.getType();

			switch (menuAction)
			{
				case EXAMINE_OBJECT:
				case ITEM_USE_ON_GAME_OBJECT:
				case WIDGET_TARGET_ON_GAME_OBJECT:
				case GAME_OBJECT_FIRST_OPTION:
				case GAME_OBJECT_SECOND_OPTION:
				case GAME_OBJECT_THIRD_OPTION:
				case GAME_OBJECT_FOURTH_OPTION:
				case GAME_OBJECT_FIFTH_OPTION:
				{
					final int x = menuEntry.getParam0();
					final int y = menuEntry.getParam1();
					final int id = menuEntry.getIdentifier();
					RS2TileObjectQuery.query().idEquals(id).stream().forEach(rs2TileObject ->
					{
						int param0;
						int param1;
						final TileObject tileObject = rs2TileObject.getTileObject();
						if (tileObject instanceof GameObject)
						{
							final GameObject gameObject = (GameObject) tileObject;
							param0 = gameObject.sizeX() > 1 ? gameObject.getLocalLocation().getSceneX() - gameObject.sizeX() / 2 : gameObject.getLocalLocation().getSceneX();
							param1 = gameObject.sizeY() > 1 ? gameObject.getLocalLocation().getSceneY() - gameObject.sizeY() / 2 : gameObject.getLocalLocation().getSceneY();
						}
						else
						{
							param0 = tileObject.getLocalLocation().getSceneX();
							param1 = tileObject.getLocalLocation().getSceneY();
						}

						if (x == param0 && y == param1)
						{
							out.add(rs2TileObject);
						}
					});
					break;
				}

				case EXAMINE_NPC:
				case ITEM_USE_ON_NPC:
				case WIDGET_TARGET_ON_NPC:
				case NPC_FIRST_OPTION:
				case NPC_SECOND_OPTION:
				case NPC_THIRD_OPTION:
				case NPC_FOURTH_OPTION:
				case NPC_FIFTH_OPTION:
				{
					final int index = menuEntry.getIdentifier();
					out.add(new RS2NPC(RS2NPCQuery.query().byIndex(index)));
					break;
				}

				case EXAMINE_ITEM_GROUND:
				case ITEM_USE_ON_GROUND_ITEM:
				case WIDGET_TARGET_ON_GROUND_ITEM:
				case GROUND_ITEM_FIRST_OPTION:
				case GROUND_ITEM_SECOND_OPTION:
				case GROUND_ITEM_THIRD_OPTION:
				case GROUND_ITEM_FOURTH_OPTION:
				case GROUND_ITEM_FIFTH_OPTION:
				{
					final int x = menuEntry.getParam0();
					final int y = menuEntry.getParam1();
					final int id = menuEntry.getIdentifier();
					final RS2Tile tile = new RS2Tile(Static.getClient().getScene().getTiles()[Static.getClient().getPlane()][x][y]);
					out.addAll(RS2TileItemQuery.query().idEquals(id).at(tile).result());
					break;
				}

				case ITEM_USE_ON_PLAYER:
				case WIDGET_TARGET_ON_PLAYER:
				case PLAYER_FIRST_OPTION:
				case PLAYER_SECOND_OPTION:
				case PLAYER_THIRD_OPTION:
				case PLAYER_FOURTH_OPTION:
				case PLAYER_FIFTH_OPTION:
				case PLAYER_SIXTH_OPTION:
				case PLAYER_SEVENTH_OPTION:
				case PLAYER_EIGHTH_OPTION:
				{
					final int index = menuEntry.getIdentifier();
					out.add(new RS2Player(RS2PlayerQuery.query().byIndex(index)));
					break;
				}

				default:
					break;
			}
		}

		return out;
	}
}

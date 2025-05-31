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
	InteractionManager(EventBus eventBus)
	{
		eventBus.register(this);
	}

	private MenuAutomated menuAutomated;
	private MenuEntry targetMenu;

	@Subscribe
	public void onMenuAutomated(MenuAutomated event)
	{
		log.info("{}", event);
		this.menuAutomated = event;
		//this.targetMenu = event.getMenuEntry();
		interact((MenuAutomated) event);
	}

	private void interact(MenuAutomated menuAutomated)
	{
		interact(menuAutomated.getParam0(), menuAutomated.getParam1(), menuAutomated.getMenuAction(), menuAutomated.getIndex(), menuAutomated.getItemId(), menuAutomated.getWorldViewId(), menuAutomated.getOption(), menuAutomated.getTarget(), menuAutomated.getCanvasX(), menuAutomated.getCanvasY());
	}

	private void interact(MenuEntry menuEntry)
	{
		interact(menuEntry.getParam0(), menuEntry.getParam1(), menuEntry.getType(), menuEntry.getIdentifier(), menuEntry.getItemId(), menuEntry.getWorldViewId(), menuEntry.getOption(), menuEntry.getTarget(), -1, -1);
	}

	private void interact(int param0, int param1, int opcode, int index, int itemId, int worldViewId, String option, String target, int canvasX, int canvasY)
	{
		interact(param0, param1, MenuAction.of(opcode), index, itemId, worldViewId, option, target, canvasX, canvasY);
	}

	private void interact(int param0, int param1, MenuAction menuAction, int index, int itemId, int worldViewId, String option, String target, int canvasX, int canvasY)
	{
		switch (config.interactionMethod())
		{
			case INVOKE:
				Static.invokeMenuAction(param0, param1, menuAction, index, itemId, worldViewId, option, target, canvasX, canvasY);
				break;
			case MOUSE_EVENTS:
				if (!config.naturalMouse())
				{
					Mouse.moveTo(canvasX, canvasY);
				}
				else
				{
					naturalMouse.moveTo(canvasX, canvasY);
				}

				if (!config.alwaysRightClickMenu() && isLeftClickableMenuEntry(param0, param1, menuAction, index, itemId, worldViewId))
				{
					final @Nullable MenuEntry selectedMenuEntry = getSelectedMenuEntry();
					if (selectedMenuEntry == null
						|| selectedMenuEntry.getParam0() != param0
						|| selectedMenuEntry.getParam1() != param1
						|| selectedMenuEntry.getType().getId() != menuAction.getId()
						|| selectedMenuEntry.getIdentifier() != index
						|| selectedMenuEntry.getItemId() != itemId
						|| selectedMenuEntry.getWorldViewId() != worldViewId)
					{
						log.warn("Invalid selected menu entry!");
						return;
					}

					if (!config.naturalMouse())
					{
						Mouse.click(canvasX, canvasY, false);
						return;
					}
					naturalMouse.click(canvasX, canvasY, false);
					return;
				}

				if (!config.naturalMouse())
				{
					Mouse.click(canvasX, canvasY, true);
				}
				else
				{
					naturalMouse.click(canvasX, canvasY, true);
				}
				Time.sleepUntil(() -> client.isMenuOpen(), 1200);
				if (client.isMenuOpen())
				{
					final Point menuEntryClickPoint = getMenuEntryClickPoint(param0, param1, menuAction, index, itemId, worldViewId);
					if (menuEntryClickPoint == null)
					{
						log.warn("Menu entry click point is null!");
						if (!config.naturalMouse())
						{
							Mouse.moveOutsideOpenMenu();
							return;
						}
						naturalMouse.moveOutsideOpenMenu();
						return;
					}
					if (!config.naturalMouse())
					{
						Mouse.moveTo(menuEntryClickPoint.x, menuEntryClickPoint.y);
						Mouse.click(menuEntryClickPoint.x, menuEntryClickPoint.y, false);
						return;
					}
					naturalMouse.moveTo(menuEntryClickPoint.x, menuEntryClickPoint.y);
					naturalMouse.click(menuEntryClickPoint.x, menuEntryClickPoint.y, false);
				}
				break;
			case BOTH:
				if (!config.naturalMouse())
				{
					Mouse.moveTo(canvasX, canvasY);
				}
				else
				{
					naturalMouse.moveTo(canvasX, canvasY);
				}
				final Point mousePosition = Mouse.getPosition();
				if (mousePosition.getX() == canvasX && mousePosition.getY() == canvasY)
				{
					Mouse.click(canvasX, canvasY, false);
					Time.sleep(50, 100);
					Static.invokeMenuAction(param0, param1, menuAction, index, itemId, worldViewId, option, target, canvasX, canvasY);
				}
				break;
		}
	}

	@Getter
	@AllArgsConstructor
	public enum InteractMethod
	{
		INVOKE, MOUSE_EVENTS, BOTH
	}

	/*@Subscribe(priority = Integer.MAX_VALUE)
	private void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (targetMenu != null && event.getType() != targetMenu.getType().getId())
		{
			this.client.getMenu().setMenuEntries(new MenuEntry[]{});
		}

		if (targetMenu != null)
		{
			if (targetMenu.getItemId() > 0)
			{
				Reflection.setItemId(targetMenu, targetMenu.getItemId());
			}
			this.client.getMenu().setMenuEntries(new MenuEntry[]{targetMenu});
		}
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked event)
	{
		this.targetMenu = null;
	}*/

	/*public int getMenuEntryIdx(int param0, int param1, MenuAction menuAction, int index, int itemId, int worldViewId)
	{
		int idx = -1;

		final Menu menu = client.getMenu();
		if (!client.isMenuOpen() || menu == null)
		{
			return idx;
		}

		final MenuEntry[] menuEntries = menu.getMenuEntries();
		for (int i = menuEntries.length - 1; i >= 0; i--)
		{
			MenuEntry menuEntry = menuEntries[i];
			if (menuEntry != null
				&& param0 == menuEntry.getParam0()
				&& param1 == menuEntry.getParam1()
				&& menuAction == menuEntry.getType()
				&& index == menuEntry.getIdentifier()
				&& itemId == menuEntry.getItemId()
				&& worldViewId == menuEntry.getWorldViewId())
			{
				idx = i;
				break;
			}
		}

		return idx;
	}*/

	public boolean isLeftClickableMenuEntry(int param0, int param1, MenuAction menuAction, int index, int itemId, int worldViewId)
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
		final MenuEntry menuEntry = menuEntries[menuEntries.length - 1];
		return menuEntry != null
			&& param0 == menuEntry.getParam0()
			&& param1 == menuEntry.getParam1()
			&& menuAction == menuEntry.getType()
			&& index == menuEntry.getIdentifier()
			&& itemId == menuEntry.getItemId()
			&& worldViewId == menuEntry.getWorldViewId();
	}

	@Nullable
	public Point getMenuEntryClickPoint(int param0, int param1, MenuAction menuAction, int index, int itemId, int worldViewId)
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
			final MenuEntry menuEntry = menuEntries[i];
			if (menuEntry == null)
			{
				continue;
			}

			// Inventory items?
			if (itemId != -1
				&& itemId == menuEntry.getItemId()
				&& index == menuEntry.getIdentifier())
			{
				menuEntryIdx = i;
				break;
			}

			if (param0 == menuEntry.getParam0()
				&& param1 == menuEntry.getParam1()
				&& menuAction == menuEntry.getType()
				&& index == menuEntry.getIdentifier()
				&& itemId == menuEntry.getItemId()
				&& worldViewId == menuEntry.getWorldViewId())
			{
				menuEntryIdx = i;
				break;
			}
		}

		if (client.isMenuOpen() && menuEntryIdx >= 0 && menuEntryIdx <= menu.getMenuEntries().length - 1)
		{
			final int clickX = menu.getMenuX() + (menu.getMenuWidth() / 2);
			final int clickY = (menu.getMenuEntries().length - 1 - menuEntryIdx - client.getMenuScroll()) * 15 + menu.getMenuY() + 31;
			return new Point(clickX, clickY);
		}

		return null;
	}

	@Nullable
	public MenuEntry getSelectedMenuEntry()
	{
		final MenuEntry[] menuEntries = Static.getClient().getMenu().getMenuEntries();
		return menuEntries.length > 0 ? menuEntries[menuEntries.length - 1] : null;
	}

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
			//log.info("Menu entry: {}", menuEntry);
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
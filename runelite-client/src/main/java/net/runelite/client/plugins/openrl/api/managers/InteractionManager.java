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
import org.jetbrains.annotations.Nullable;
import net.runelite.api.Client;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.openrl.OpenRuneLiteConfig;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Time;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;
import net.runelite.client.plugins.openrl.api.input.Mouse;
import net.runelite.client.plugins.openrl.api.input.naturalmouse.NaturalMouse;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Point;

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

	private void interact(int param0, int param1, MenuAction menuAction, int index, int itemId, int worldViewId, String option, String target, int canvasX, int canvasY)
	{
		switch (config.interactionMethod())
		{
			case INVOKE:
				Static.invokeMenuAction(param0, param1, menuAction, index, itemId, worldViewId, option, target, canvasX, canvasY);
				break;
			case MOUSE_EVENTS:
				naturalMouse.moveTo(canvasX, canvasY);

				final @Nullable MenuEntry selectedMenuEntry = getSelectedMenuEntry();
				if (selectedMenuEntry == null
					|| selectedMenuEntry.getIdentifier() != index
					|| selectedMenuEntry.getItemId() != itemId
					|| selectedMenuEntry.getWorldViewId() != worldViewId)
				{
					log.warn("Invalid selected menu entry!");
					return;
				}

				if (isLeftClickableMenuEntry(param0, param1, menuAction, index, itemId, worldViewId))
				{
					Mouse.click(canvasX, canvasY, false);
					return;
				}

				Mouse.click(canvasX, canvasY, true);
				Time.sleepUntil(() -> client.isMenuOpen(), 1000);
				if (client.isMenuOpen())
				{
					final Point menuEntryClickPoint = getMenuEntryClickPoint(param0, param1, menuAction, index, itemId, worldViewId);
					if (menuEntryClickPoint == null)
					{
						log.warn("Menu entry click point is null!");
						naturalMouse.moveOutsideOpenMenu();
						return;
					}
					naturalMouse.moveTo(menuEntryClickPoint.x, menuEntryClickPoint.y);
					Mouse.click(menuEntryClickPoint.x, menuEntryClickPoint.y, false);
				}
				break;
			case BOTH:
				naturalMouse.moveTo(canvasX, canvasY);
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
}
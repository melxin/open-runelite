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
package net.runelite.client.plugins.openrl;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import java.awt.Graphics;
import javax.inject.Inject;
import net.runelite.api.MenuEntry;;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.openrl.api.entities.Players;
import net.runelite.client.plugins.openrl.api.events.Draw;
import net.runelite.client.plugins.openrl.api.game.Game;
import net.runelite.client.plugins.openrl.api.plugin.LoopedPlugin;

@PluginDescriptor(
	name = "Open RuneLite",
	description = "",
	tags = {"open", "runelite", "openrl", "rl"}
)
@Slf4j
public class OpenRuneLitePlugin extends LoopedPlugin
{
	@Inject
	private OpenRuneLiteConfig config;

	@Provides
	OpenRuneLiteConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OpenRuneLiteConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (configChanged.getGroup().equals(OpenRuneLiteConfig.GROUP))
		{
		}
	}

	@Override
	protected void startUp()
	{
	}

	@Override
	protected void shutDown()
	{
	}

	@Subscribe
	public void onDraw(Draw event)
	{
		final Graphics graphics = event.getGraphics();
		if (graphics != null && config.overlayEnabled())
		{
			graphics.setColor(config.overlayColor());
			//graphics.drawString("Time running: " + this.getTimeRunning(), 10, 20);
			graphics.drawString("Test: " + "test", 10, 35);
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (config.printMenuActions())
		{
			/* The RuneScape client may deprioritize an action in the menu by incrementing the opcode with 2000,
			 * undo it here so we can get the correct opcode
			 */
			boolean decremented = false;
			int opcode = event.getMenuEntry().getIdentifier();
			if (opcode >= 2000)
			{
				decremented = true;
				opcode -= 2000;
			}

			log.info("|MenuAction|: MenuOption={} MenuTarget={} Id={} Opcode={}/{} Param0={} Param1={} CanvasX={} CanvasY={} ItemId={} WorldViewId={}",
				event.getMenuOption(), event.getMenuTarget(), event.getId(),
				event.getMenuAction(), opcode + (decremented ? 2000 : 0),
				event.getParam0(), event.getParam1(), "canvasX", "canvasY", event.getItemId(), -1
			);

			MenuEntry menuEntry = event.getMenuEntry();
			if (menuEntry != null)
			{
				log.info(
					"|MenuEntry|: Idx={} MenuOption={} MenuTarget={} Id={} MenuAction={} Param0={} Param1={} Consumed={} IsItemOp={} ItemOp={} ItemID={} WorldViewId={} Widget={}",
					event.getId(), menuEntry.getOption(), menuEntry.getTarget(), menuEntry.getIdentifier(), menuEntry.getType(), menuEntry.getParam0(), menuEntry.getParam1(), event.isConsumed(), menuEntry.isItemOp(), menuEntry.getItemOp(), menuEntry.getItemId(), menuEntry.getWorldViewId(), menuEntry.getWidget()
				);
			}
		}
	}

	@Override
	protected int loop()
	{
		if (!Game.isLoggedIn() || Players.getLocal() == null)
		{
			return -1;
		}

		// Reflection
		/*for (Method m : Static.getClient().getClass().getDeclaredMethods())
		{
			if (m.getParameterCount() >= 9)
			{
				log.info("Found method: {} {} {}", m.getName(), Arrays.asList(m.getParameters()), m.getParameterCount());
			}
		}*/

		// TileItems
		/*final List<TileItem> tileItems = TileItems.getAll();
		for (TileItem tileItem : tileItems)
		{
			log.info("TileItem: {}", tileItem.getId());
			TileItems.interact(tileItem, "Take");
			//TileObjects.interact(tileObject, "Open");
		}*/

		// Inventory
		/*final List<Item> items = Inventory.getAll();
		for (int i = 0; i < items.size(); i++)
		{
			Item item = items.get(i);
			//log.info("item: {} slot: {}", item, i);
			WidgetItem widgetItem = Inventory.getWidgetItem(i);
			if (widgetItem != null && widgetItem.getId() != Inventory.ITEM_EMPTY && widgetItem.getId() > 0)
			{
				if (Inventory.hasAction(item, "Wear"))
				{
					log.info("widget item: {} {} {}", widgetItem.getId(), widgetItem.getWidget().getText(), Inventory.getSlot(item));
					Inventory.interact(item, "Wear");
					break;
				}
			}
		}*/

		// TileObjects
		/*final List<TileObject> tileObjects = TileObjects.getAll(t -> t.getWorldLocation().distanceTo2D(Players.getLocal().getWorldLocation()) <= 1);
		for (TileObject tileObject : tileObjects)
		{
			if (TileObjects.hasAction(tileObject, "Open"))
			{
				log.info("Tile object: {} {} x:{} y:{}", tileObject.getId(), tileObject.getWorldLocation(), tileObject.getX(), tileObject.getY());
				TileObjects.interact(tileObject, "Open");
			}
		}*/

		// NPCs
		/*final NPC nearest = NPCs.getNearest();
		if (nearest != null)
		{
			log.info("Found nearest: {}", NPCs.getName(nearest));
			String[] actions = NPCs.getActions(nearest);
			if (actions != null)
			{
				for (String action : actions)
				{
					log.info("Action: {}", action);
				}
			}

			if (!Static.getClient().getLocalPlayer().isInteracting())
			{
				log.info("interact!");

				//NPCs.interact(n, MenuAction.NPC_SECOND_OPTION);
				NPCs.interact(nearest, "Trade");
				//NPCs.interactMouse(n);;
				Time.sleep(1000);
			}
		}*/

		// Players
		/*final Player nearest = Players.getNearest();
		if (nearest != null)
		{
			log.info("Nearest player: {}", nearest.getName());
			Players.interact(nearest, "Follow");
		}*/
		return 2000;
	}
}

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

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.openrl.api.game.Game;
import net.runelite.client.plugins.openrl.api.plugin.LoopedPlugin;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2Players;

@PluginDescriptor(
	name = "Open RuneLite Test Plugin"
)
@Slf4j
public class OpenRuneLiteTestPlugin extends LoopedPlugin
{
	@Override
	protected int loop()
	{
		if (!Game.isLoggedIn() || RS2Players.getLocal() == null)
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

		// Animation
		/*NPC nearestNPC = NPCs.getNearest();
		int anim = Reflection.getAnimation(nearestNPC);
		log.info("nearestNPC: {} ANIM: {}", NPCs.getName(nearestNPC), anim);*/

		// TileItems
		/*final List<TileItem> tileItems = TileItems.getAll();
		for (TileItem tileItem : tileItems)
		{
			log.info("TileItem: {}", tileItem.getId());
			TileItems.interact(tileItem, "Take");
			//TileObjects.interact(tileObject, "Open");
		}*/

		// Widgets
		// Test deposit button in bank
		/*boolean bankOpen = Widgets.isVisible(Widgets.getWidget(InterfaceID.Bankmain.ITEMS_CONTAINER));
		log.info("bank open: {}", bankOpen);
		if (bankOpen)
		{
			Widget widget = Widgets.getWidget(InterfaceID.Bankmain.DEPOSITINV);
			if (widget != null)
			{
				Widgets.interact(widget, "Deposit inventory");
			}
		}*/

		/*if (RS2Bank.isOpen())
		{
			final List<RS2Item> items = RS2Bank.getAll();
			for (RS2Item item : items)
			{
				log.info("Item: {} | quantity: {} | slot: {}", item.getId(), item.getQuantity(), item.getSlot());
			}
		}*/

		// Inventory
		/*final List<Item> items = Inventory.getAll();
		for (int i = 0; i < items.size(); i++)
		{
			final Item item = items.get(i);
			//log.info("item: {} slot: {}", item, i);
			final WidgetItem widgetItem = Inventory.getWidgetItem(i);
			if (widgetItem != null && widgetItem.getId() != Inventory.ITEM_EMPTY && widgetItem.getId() > 0)
			{
				if (Inventory.hasAction(item, "Wear"))
				{
					//log.info("widget item: {} {} {}", widgetItem.getId(), widgetItem.getWidget().getText(), Inventory.getSlot(item));
					log.info("Action by index 0: {}", Inventory.getAction(item, 0));
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
				NPCs.interact(nearest, "Attack");
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
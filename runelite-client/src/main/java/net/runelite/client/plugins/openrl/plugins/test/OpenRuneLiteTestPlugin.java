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
package net.runelite.client.plugins.openrl.plugins.test;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.openrl.api.game.Game;
import net.runelite.client.plugins.openrl.api.plugin.LoopedPlugin;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2Players;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.RS2NPCQuery;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2NPC;

@PluginDescriptor(
	name = "Open RuneLite Test Plugin"
)
@Slf4j
public class OpenRuneLiteTestPlugin extends LoopedPlugin
{
	@Override
	protected void startUp()
	{
	}

	@Override
	protected void shutDown()
	{
	}

	//@Subscribe
	protected void onGameTick(GameTick event)
	{
		final RS2NPC nearest = RS2NPCQuery.query().getNearest();
		if (nearest != null)
		{
			nearest.interact(0);
		}
	}

	@Override
	protected int loop()
	{
		if (!Game.isLoggedIn() || RS2Players.getLocal() == null)
		{
			return -1;
		}

		/*if (RS2Minigames.canTeleport())
		{
			log.info("Can teleport!");
			RS2Minigames.teleport(RS2Minigames.Destination.CASTLE_WARS);
		}*/

		//log.info("Wildy Level: {}", Game.getWildyLevel());
		//Time.sleep(3000);
		//RS2Friends.add("test");
		//Time.sleep(1000);
		//RS2Friends.remove("test");
		/*RS2Shop.openNearest();
		if (RS2Shop.isOpen())
		{
			log.info("open!");
			//RS2Shop.buyOne("Bucket");
			RS2Shop.sellOne("Bucket");
		}

		if (RS2MakeX.isOpen())
		{
			log.info("is open!");
			RS2MakeX.getOptions().forEach(x -> log.info("xx: {}", x.getId()));
			RS2MakeX.setQuantity(5);
			//RS2MakeX.chooseNumericOption(2);
			//RS2MakeX.chooseItemOption(54);
			RS2MakeX.chooseItemOption("Oak shortbow");
		}*/

		/*List<RS2TileObject> to = RS2TileObjectQuery.query().result();
		for (RS2TileObject t : to)
		{
			if (t.isGameObject())
			{
				log.info("t: {}", t.getRS2GameObject().getId());
			}
		}*/
		//RS2ItemQuery.inventoryQuery().first().drag(11);
		//log.info("name: {} | {}", RS2TileItemQuery.query().first().getComposition().getClass().getName(), RS2NPCQuery.query().first().getComposition().getClass().getName());
		/*final RS2TileItem nearest = RS2TileItemQuery.query().getNearest();
		if (nearest != null)
		{
			log.info("ops: {}", Arrays.asList(nearest.getOps()).toString());
		}*/
		//List<Player> players = Static.getGameDataCached().getPlayers();
		//List<RS2Player> players2 = RS2PlayerQuery.query().result();
		//log.info("Size: {} {}", players.size(), players2.size());

		//RS2ItemQuery.inventoryQuery().first().interact("Wear");

		/*final RS2NPC nearestNpc = RS2NPCQuery.query().getNearest();
		if (nearestNpc == null)
		{
			return 1000;
		}
		nearestNpc.interact(nearestNpc.getOpIdx("Disable-XP"));*/
		//RS2ItemQuery.inventoryQuery().nameContains("Ring of duel").first().interactSubOp("Rub", "Castle Wars");

			//RS2PlayerQuery.query().result().forEach(x -> log.info("x: {} {}", x.getName(), x.getWorldLocation()));
			//RS2NPCQuery.query().nameEquals("Lanthus").first().interact("Trade");
		/*RS2TileItem ti = RS2TileItemQuery.query().getNearest();
		if (ti != null)
		{
			log.info("gr: {} {}", ti.getId(), Arrays.asList(ti.getActions()));
			ti.interact("Take");
		}*/

			//RS2Actor nearest = RS2ActorQuery.query().getNearest();
			//log.info("Nearest: {} {}", nearest.getName(), nearest.getWorldLocation());

			//RS2GameObjectQuery.query().nameContains("tree").getNearest().interact("Chop down");
			//RS2WidgetQuery.query().result().forEach(x -> log.info("x: {} {}", x.getId(), x.getParentId()));
			//RS2NPCQuery.query().result().forEach(x -> x.getWorldLocation());
			//RS2GameObjectQuery.query().result().forEach(x -> log.info("x: {} {}", x.getId(), x.getWorldLocation()));

		/*if (!RS2DepositBox.isOpen())
		{
			log.info("not open!");
			RS2DepositBox.openNearest();
		}
		if (RS2DepositBox.isOpen())
		{
			//RS2ItemQuery.from(InventoryID.INV, InterfaceID.BankDepositbox.INVENTORY).result().forEach(x -> log.info("x: {}", x.getName()));
			//RS2DepositBox.setQuantity(12);
			//RS2DepositBox.depositInventory();
			//RS2DepositBox.deposit(2, "Logs", "Sapphire");
			RS2DepositBox.depositEquipment(EquipmentInventorySlot.BODY);
			RS2DepositBox.close();
		}*/

		/*if (RS2Bank.isOpen())
		{
			RS2Bank.deposit(3, "Logs", "Sapphire");
			Time.sleepTick();
			//Time.sleepTicks(2);
			//RS2Bank.openTab(3);
			//Time.sleep(2000);
			//RS2Bank.collapseTab(3);
			//Time.sleep(2000);
			RS2Bank.withdraw("Logs", 5, RS2Bank.WithdrawMode.ITEM);
			RS2Bank.withdraw("Sapphire", 3, RS2Bank.WithdrawMode.ITEM);
			//RS2Bank.withdraw("Logs", 3, RS2Bank.WithdrawMode.ITEM);
		}*/

			// Walker
		/*final RS2Player localPlayer = RS2Players.getLocal();
		final WorldPoint localWp = RS2Players.getLocal().getWorldLocation();
		final WorldPoint targetWp = new WorldPoint(localWp.getX() + 10, localWp.getY(), localWp.getPlane());
		Pathfinder.walkPath(targetWp);
		log.info("Player moving: {} idle: {}", localPlayer.isMoving(), localPlayer.isIdle());*/

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
		return 3000;
	}
}
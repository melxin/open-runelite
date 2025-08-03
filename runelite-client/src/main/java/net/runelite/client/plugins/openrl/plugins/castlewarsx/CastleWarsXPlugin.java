/*
 * Copyright (c) 2022, Melxin <https://github.com/melxin/>
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
package net.runelite.client.plugins.openrl.plugins.castlewarsx;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import java.awt.event.KeyEvent;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.WorldView;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.RS2NPCQuery;

@PluginDescriptor(
	name = "CastleWarsX",
	description = "CastleWars",
	enabledByDefault = true,
	tags =
		{
			"castlewars",
			"cw"
		}
)
@Slf4j
public class CastleWarsXPlugin extends Plugin implements net.runelite.client.input.KeyListener
{
	@Inject
	private Client client;

	@Inject
	private KeyManager keyManager;

	@Inject
	private ClientThread clientThread;

	@Inject
	private CastleWarsXConfig config;

	@Provides
	private CastleWarsXConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CastleWarsXConfig.class);
	}

	@Override
	protected void startUp()
	{
		keyManager.registerKeyListener(this);
	}

	@Override
	protected void shutDown()
	{
		keyManager.unregisterKeyListener(this);
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		// Use explosive or bucket of water on barricade
		if (KeyEvent.getKeyText(e.getKeyCode()).equalsIgnoreCase(config.explosionBucketHotKey()))
		{
			clientThread.invoke(() ->
			{
				final NPC nearestBarricade = RS2NPCQuery.query()
					.idEquals(
						NpcID.SARADOMIN_BARRICADE,
						NpcID.SARADOMIN_BARRICADE_LIT,
						NpcID.ZAMORAK_BARRICADE,
						NpcID.ZAMORAK_BARRICADE_LIT)
					.getNearest(client.getLocalPlayer().getWorldLocation());

				if (nearestBarricade == null)
				{
					log.info("No barricade found");
					return;
				}

				final Barricade barricade = new Barricade(nearestBarricade);
				final NPC actor = barricade.getActor();

				if (barricade.isLit())
				{
					if (!containsItem(ItemID.BUCKET_WATER))
					{
						log.info("No bucket of water in inventory!");
						return;
					}

					Static.invokeMenuAction(getInventorySlot(ItemID.BUCKET_WATER), 9764864, MenuAction.WIDGET_TARGET, 0, ItemID.BUCKET_WATER, WorldView.TOPLEVEL, "Use", "<col=ff9040>Bucket of water</col>", -1, -1);
					Static.invokeMenuAction(0, 0, MenuAction.WIDGET_TARGET_ON_NPC, actor.getIndex(), -1, WorldView.TOPLEVEL, "Use", "<col=ff9040>Bucket of water</col><col=ffffff> -> <col=ffff00>Barricade", -1, -1);
					return;
				}

				if (!containsItem(ItemID.CASTLEWARS_EXPLOSIVES_POTION))
				{
					log.info("No explosive in inventory!");
					return;
				}

				Static.invokeMenuAction(getInventorySlot(ItemID.CASTLEWARS_EXPLOSIVES_POTION), 9764864, MenuAction.WIDGET_TARGET, 0, ItemID.CASTLEWARS_EXPLOSIVES_POTION, WorldView.TOPLEVEL, "Use", "<col=ff9040>Explosive potion</col>", -1, -1);
				Static.invokeMenuAction(0, 0, MenuAction.WIDGET_TARGET_ON_NPC, actor.getIndex(), -1, WorldView.TOPLEVEL, "Use", "<col=ff9040>Explosive potion</col><col=ffffff> -> <col=ffff00>Barricade", -1, -1);
			});
		}

		// Use tinderbox
		if (KeyEvent.getKeyText(e.getKeyCode()).equalsIgnoreCase(config.tinderboxHotKey()))
		{
			clientThread.invoke(() ->
			{
				final NPC nearestBarricade = RS2NPCQuery.query()
					.idEquals(NpcID.SARADOMIN_BARRICADE,
						NpcID.ZAMORAK_BARRICADE)
					.getNearest(client.getLocalPlayer().getWorldLocation());

				if (nearestBarricade == null)
				{
					log.info("No barricade found");
					return;
				}

				final Barricade barricade = new Barricade(nearestBarricade);
				final NPC actor = barricade.getActor();

				if (!containsItem(ItemID.TINDERBOX))
				{
					log.info("No tinderbox in inventory!");
					return;
				}

				Static.invokeMenuAction(getInventorySlot(ItemID.TINDERBOX), 9764864, MenuAction.WIDGET_TARGET, 0, ItemID.TINDERBOX, WorldView.TOPLEVEL, "Use", "<col=ff9040>Tinderbox</col>", -1, -1);
				Static.invokeMenuAction(0, 0, MenuAction.WIDGET_TARGET_ON_NPC, actor.getIndex(), -1, WorldView.TOPLEVEL, "Use", "<col=ff9040>Tinderbox</col><col=ffffff> -> <col=ffff00>Barricade", -1, -1);
			});
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}

	/**
	 * Contains item in inventory
	 *
	 * @param itemId
	 * @return true if item was found
	 */
	private boolean containsItem(int itemId)
	{
		final ItemContainer itemContainer = client.getItemContainer(InventoryID.INV);

		if (itemContainer == null)
		{
			return false;
		}

		final Item[] items = itemContainer.getItems();
		for (Item item : items)
		{
			if (item != null && item.getId() == itemId)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Get inventory slot
	 *
	 * @param itemId
	 * @return inventory slot or -1 if not found
	 */
	private int getInventorySlot(int itemId)
	{
		final ItemContainer itemContainer = client.getItemContainer(InventoryID.INV);

		if (itemContainer == null)
		{
			return -1;
		}

		final Item[] items = itemContainer.getItems();

		for (int i = 0; i < items.length; i++)
		{
			Item item = items[i];
			if (item != null && item.getId() == itemId)
			{
				return i;
			}
		}
		return -1;
	}
}
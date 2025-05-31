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

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Player;
import net.runelite.api.PlayerComposition;
import net.runelite.api.TileObject;
import net.runelite.api.VarbitComposition;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.events.VarClientIntChanged;
import net.runelite.api.events.VarClientStrChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarClientID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.openrl.Static;

@Singleton
@Slf4j
public class GameDataCachedManager
{
	@Inject
	GameDataCachedManager(EventBus eventBus)
	{
		eventBus.register(this);
	}

	private static final Map<Integer, ItemContainer> ITEM_CONTAINER_CACHE = new ConcurrentHashMap<>();
	private static final Map<Integer, ItemComposition> ITEM_COMPOSITION_CACHE = new HashMap<>();
	private static final Map<Integer, ObjectComposition> OBJECT_COMPOSITION_CACHE = new HashMap<>();
	private static final Map<Integer, NPCComposition> NPC_COMPOSITION_CACHE = new HashMap<>();
	private static final Map<Player, PlayerComposition> PLAYER_COMPOSITION_CACHE = new HashMap<>();

	private static final List<Player> PLAYER_CACHE = new ArrayList<>();
	private static final List<NPC> NPC_CACHE = new ArrayList<>();

	private static final Map<Integer, Integer> VARBIT_CACHE = new HashMap<>();
	private static final Map<Integer, VarbitComposition> VARBIT_COMPOSITION_CACHE = new HashMap<>();
	private static final Map<Integer, Integer> VARP_CACHE = new HashMap<>();
	private static final Map<Integer, Integer> VARC_INT_CACHE = new HashMap<>();
	private static final Map<Integer, String> VARC_STR_CACHE = new HashMap<>();

	public ItemContainer getItemContainer(int containerId)
	{
		return ITEM_CONTAINER_CACHE.computeIfAbsent(containerId, id -> Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getItemContainer(id)).orElse(null));
	}

	public ItemComposition getItemComposition(int itemId)
	{
		return ITEM_COMPOSITION_CACHE.computeIfAbsent(itemId, id -> Static.getClientThread().runOnClientThreadOptional(() -> Static.getItemManager().getItemComposition(id)).orElse(null));
	}

	public ObjectComposition getObjectComposition(int objectId)
	{
		return OBJECT_COMPOSITION_CACHE.computeIfAbsent(objectId, id -> Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getObjectDefinition(id)).orElse(null));
	}

	public NPCComposition getNPCComposition(int npcId)
	{
		return NPC_COMPOSITION_CACHE.computeIfAbsent(npcId, id -> Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getNpcDefinition(id)).orElse(null));
	}

	public PlayerComposition getPlayerComposition(Player player)
	{
		return PLAYER_COMPOSITION_CACHE.computeIfAbsent(player, p -> Static.getClientThread().runOnClientThreadOptional(() -> p.getPlayerComposition()).orElse(null));
	}

	public int getVarbitValue(int varbitId)
	{
		return VARBIT_CACHE.computeIfAbsent(varbitId, id -> Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getVarbitValue(id)).orElse(null));
	}

	public VarbitComposition getVarbitComposition(int varbitId)
	{
		return VARBIT_COMPOSITION_CACHE.computeIfAbsent(varbitId, id -> Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getVarbit(id)).orElse(null));
	}

	public int getVarpValue(int varpId)
	{
		return VARP_CACHE.computeIfAbsent(varpId, id -> Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getVarpValue(id)).orElse(null));
	}

	public int getVarcIntValue(int varClientId)
	{
		return VARC_INT_CACHE.computeIfAbsent(varClientId, id -> Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getVarcIntValue(id)).orElse(null));
	}

	public String getVarcStrValue(int varClientId)
	{
		return VARC_STR_CACHE.computeIfAbsent(varClientId, id -> Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getVarcStrValue(id)).orElse(null));
	}

	public List<Player> getPlayers()
	{
		return PLAYER_CACHE;
	}

	public List<NPC> getNPCs()
	{
		return NPC_CACHE;
	}

	private static final Stopwatch stopwatch = Stopwatch.createUnstarted();

	@Subscribe(priority = Integer.MAX_VALUE)
	protected void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN)
		{
			// Items
			if (ITEM_COMPOSITION_CACHE.isEmpty())
			{
				stopwatch.reset();
				stopwatch.start();
				Arrays.asList(ItemID.class.getFields()).forEach(f ->
				{
					try
					{
						f.setAccessible(true);
						final int itemId = f.getInt(null);
						f.setAccessible(false);
						final ItemComposition itemComposition = Static.getClient().getItemDefinition(itemId);
						ITEM_COMPOSITION_CACHE.put(itemId, itemComposition);
					}
					catch (IllegalAccessException e)
					{
						log.error("", e);
					}
				});
				log.info("Cached: {} item compositions in {}", ITEM_COMPOSITION_CACHE.size(), stopwatch.stop());
			}

			// Objects
			if (OBJECT_COMPOSITION_CACHE.isEmpty())
			{
				stopwatch.reset();
				stopwatch.start();
				Arrays.asList(ObjectID.class.getFields()).forEach(f ->
				{
					try
					{
						f.setAccessible(true);
						final int objectId = f.getInt(null);
						f.setAccessible(false);
						final ObjectComposition objectComposition = Static.getClient().getObjectDefinition(objectId);
						OBJECT_COMPOSITION_CACHE.put(objectId, objectComposition);
					}
					catch (IllegalAccessException e)
					{
						log.error("", e);
					}
				});
				log.info("Cached: {} object compositions in {}", OBJECT_COMPOSITION_CACHE.size(), stopwatch.stop());
			}

			// NPCs
			if (NPC_COMPOSITION_CACHE.isEmpty())
			{
				stopwatch.reset();
				stopwatch.start();
				Arrays.asList(NpcID.class.getFields()).forEach(f ->
				{
					try
					{
						f.setAccessible(true);
						final int npcId = f.getInt(null);
						f.setAccessible(false);
						final NPCComposition npcComposition = Static.getClient().getNpcDefinition(npcId);
						NPC_COMPOSITION_CACHE.put(npcId, npcComposition);
					}
					catch (IllegalAccessException e)
					{
						log.error("", e);
					}
				});
				log.info("Cached: {} npc compositions in {}", NPC_COMPOSITION_CACHE.size(), stopwatch.stop());
			}

			// Varbits
			if (VARBIT_CACHE.isEmpty() || VARBIT_COMPOSITION_CACHE.isEmpty())
			{
				stopwatch.reset();
				stopwatch.start();
				Arrays.asList(VarbitID.class.getFields()).forEach(f ->
				{
					try
					{
						f.setAccessible(true);
						final int varbitId = f.getInt(null);
						f.setAccessible(false);
						final int varbitValue;
						try
						{
							varbitValue = Static.getClient().getVarbitValue(varbitId);
						}
						catch (IndexOutOfBoundsException e)
						{
							log.warn("{}", e.getMessage());
							return;
						}
						final VarbitComposition varbitComposition = Static.getClient().getVarbit(varbitId);
						VARBIT_CACHE.put(varbitId, varbitValue);
						VARBIT_COMPOSITION_CACHE.put(varbitId, varbitComposition);
					}
					catch (IllegalAccessException e)
					{
						log.error("", e);
					}
				});
				log.info("Cached: {} varbit values in {}", VARBIT_CACHE.size(), stopwatch);
				log.info("Cached: {} varbit compositions in {}", VARBIT_COMPOSITION_CACHE.size(), stopwatch.stop());
			}

			// Varps
			if (VARP_CACHE.isEmpty())
			{
				stopwatch.reset();
				stopwatch.start();
				Arrays.asList(VarPlayerID.class.getFields()).forEach(f ->
				{
					try
					{
						f.setAccessible(true);
						final int varpId = f.getInt(null);
						f.setAccessible(false);
						final int varpValue = Static.getClient().getVarpValue(varpId);
						VARP_CACHE.put(varpId, varpValue);
					}
					catch (IllegalAccessException e)
					{
						log.error("", e);
					}
				});
				log.info("Cached: {} varp values in {}", VARP_CACHE.size(), stopwatch.stop());
			}

			// Varcs
			if (VARC_INT_CACHE.isEmpty() || VARC_STR_CACHE.isEmpty())
			{
				stopwatch.reset();
				stopwatch.start();;
				Arrays.asList(VarClientID.class.getFields()).forEach(f ->
				{
					try
					{
						f.setAccessible(true);
						final int id = f.getInt(null);
						f.setAccessible(false);
						final int varcIntValue = Static.getClient().getVarcIntValue(id);
						final String varcStrValue = Static.getClient().getVarcStrValue(id);
						VARC_INT_CACHE.put(id, varcIntValue);
						VARC_STR_CACHE.put(id, varcStrValue);
					}
					catch (IllegalAccessException e)
					{
						log.error("", e);
					}
				});
				log.info("Cached: {} varc int values in {}", VARC_INT_CACHE.size(), stopwatch);
				log.info("Cached: {} varc str values in {}", VARC_STR_CACHE.size(), stopwatch.stop());
			}
		}

		if (!PLAYER_CACHE.isEmpty())
		{
			PLAYER_CACHE.clear();
		}

		if (!NPC_CACHE.isEmpty())
		{
			NPC_CACHE.clear();
		}
	}

	@Subscribe(priority = Integer.MAX_VALUE)
	protected void onItemContainerChanged(ItemContainerChanged event)
	{
		final ItemContainer itemContainer = event.getItemContainer();
		ITEM_CONTAINER_CACHE.put(itemContainer.getId(), itemContainer);

		final Item[] items = itemContainer.getItems();
		for (Item item : items)
		{
			ITEM_COMPOSITION_CACHE.computeIfAbsent(item.getId(), id -> Static.getItemManager().getItemComposition(id));
		}
	}

	@Subscribe(priority = Integer.MAX_VALUE)
	protected void onGameObjectSpawned(GameObjectSpawned event)
	{
		onTileObjectSpawned(event.getGameObject());
	}

	@Subscribe(priority = Integer.MAX_VALUE)
	protected void onGroundObjectSpawned(GroundObjectSpawned event)
	{
		onTileObjectSpawned(event.getGroundObject());
	}

	@Subscribe(priority = Integer.MAX_VALUE)
	protected void onWallObjectSpawned(WallObjectSpawned event)
	{
		onTileObjectSpawned(event.getWallObject());
	}

	@Subscribe(priority = Integer.MAX_VALUE)
	protected void onDecorativeObjectSpawned(DecorativeObjectSpawned event)
	{
		onTileObjectSpawned(event.getDecorativeObject());
	}

	protected void onTileObjectSpawned(TileObject tileObject)
	{
		OBJECT_COMPOSITION_CACHE.computeIfAbsent(tileObject.getId(), id -> Static.getClient().getObjectDefinition(id));
	}

	@Subscribe(priority = Integer.MAX_VALUE)
	protected void onPlayerSpawned(PlayerSpawned event)
	{
		final Player player = event.getPlayer();
		PLAYER_COMPOSITION_CACHE.put(player, player.getPlayerComposition());
		PLAYER_CACHE.add(player);
	}

	@Subscribe(priority = Integer.MAX_VALUE)
	protected void onPlayerDespawned(PlayerDespawned event)
	{
		final Player player = event.getPlayer();
		PLAYER_COMPOSITION_CACHE.remove(player);
		PLAYER_CACHE.remove(player);
	}

	@Subscribe(priority = Integer.MAX_VALUE)
	protected void onNpcSpawned(NpcSpawned event)
	{
		final NPC npc = event.getNpc();
		NPC_COMPOSITION_CACHE.computeIfAbsent(npc.getId(), id -> Static.getClient().getNpcDefinition(id));
		NPC_CACHE.add(npc);
	}

	@Subscribe(priority = Integer.MAX_VALUE)
	protected void onNpcDespawned(NpcDespawned event)
	{
		NPC_CACHE.remove(event.getNpc());
	}

	@Subscribe(priority = Integer.MAX_VALUE)
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarbitId() != -1)
		{
			VARBIT_CACHE.put(event.getVarbitId(), Static.getClient().getVarbitValue(event.getVarbitId()));
			VARBIT_COMPOSITION_CACHE.put(event.getVarbitId(), Static.getClient().getVarbit(event.getVarbitId()));
		}

		if (event.getVarpId() != -1)
		{
			VARP_CACHE.put(event.getVarpId(), Static.getClient().getVarpValue(event.getVarpId()));
		}
	}

	@Subscribe(priority = Integer.MAX_VALUE)
	public void onVarClientIntChanged(VarClientIntChanged event)
	{
		VARC_INT_CACHE.put(event.getIndex(), Static.getClient().getVarcIntValue(event.getIndex()));
	}

	@Subscribe(priority = Integer.MAX_VALUE)
	public void onVarClientStrChanged(VarClientStrChanged event)
	{
		VARC_STR_CACHE.put(event.getIndex(), Static.getClient().getVarcStrValue(event.getIndex()));
	}
}
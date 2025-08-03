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
package net.runelite.client.plugins.openrl.plugins.castlewars;

import com.google.inject.Provides;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.IndexedObjectSet;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.PlayerComposition;
import net.runelite.api.Skill;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.WallObject;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.DecorativeObjectDespawned;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.WallObjectDespawned;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.kit.KitType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.input.Keyboard;
import net.runelite.client.plugins.openrl.api.plugin.LoopedPlugin;
import net.runelite.client.plugins.openrl.api.rs2.providers.items.RS2Equipment;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.dialog.RS2Dialog;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Item;
import net.runelite.client.plugins.openrl.plugins.castlewars.barricade.Barricade;
import net.runelite.client.plugins.openrl.plugins.castlewars.barricade.BarricadeMiniMapOverlay;
import net.runelite.client.plugins.openrl.plugins.castlewars.barricade.BarricadeSceneOverlay;
import net.runelite.client.plugins.openrl.plugins.castlewars.barricade.BarricadeTimerOverlay;
import net.runelite.client.plugins.openrl.plugins.castlewars.cave.Cave;
import net.runelite.client.plugins.openrl.plugins.castlewars.cave.CaveSceneOverlay;
import net.runelite.client.plugins.openrl.plugins.castlewars.door.Door;
import net.runelite.client.plugins.openrl.plugins.castlewars.door.DoorSceneOverlay;
import net.runelite.client.plugins.openrl.plugins.castlewars.grounditem.GroundItem;
import net.runelite.client.plugins.openrl.plugins.castlewars.grounditem.GroundItemOverlay;
import net.runelite.client.plugins.openrl.plugins.castlewars.id.EquipmentID;
import net.runelite.client.plugins.openrl.plugins.castlewars.id.GroundItemID;
import net.runelite.client.plugins.openrl.plugins.castlewars.id.NpcID;
import net.runelite.client.plugins.openrl.plugins.castlewars.id.ObjectID;
import net.runelite.client.plugins.openrl.plugins.castlewars.id.RegionID;
import net.runelite.client.plugins.openrl.plugins.castlewars.rock.Rock;
import net.runelite.client.plugins.openrl.plugins.castlewars.rock.RockMiniMapOverlay;
import net.runelite.client.plugins.openrl.plugins.castlewars.rock.RockSceneOverlay;
import net.runelite.client.plugins.openrl.plugins.castlewars.rock.TunnelMiniMapOverlay;
import net.runelite.client.plugins.openrl.plugins.castlewars.tap.Tap;
import net.runelite.client.plugins.openrl.plugins.castlewars.tap.TapSceneOverlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

@PluginDescriptor(
	name = "Castle Wars",
	description = "castle wars plugin"
)

@Slf4j
public class CastleWarsPlugin extends LoopedPlugin
{
	// The client
	@Inject
	private Client client;

	// The config
	@Inject
	private CastleWarsConfig config;

	// The overlay manager
	@Inject
	private OverlayManager overlayManager;

	// Barricade scene overlay for highlights
	@Inject
	private BarricadeSceneOverlay barricadeSceneOverlay;

	// Barricade MiniMap overlay
	@Inject
	private BarricadeMiniMapOverlay barricadeMiniMapOverlay;

	// Barricade timer overlay for lit barricades
	@Inject
	private BarricadeTimerOverlay barricadeTimerOverlay;

	// Rocks underground scene overlay for highlights
	@Inject
	private RockSceneOverlay rockSceneOverlay;

	// Rocks underground MiniMap overlay
	@Inject
	private RockMiniMapOverlay rockMiniMapOverlay;

	// Open Tunnel MiniMap overlay
	@Inject
	private TunnelMiniMapOverlay tunnelMiniMapOverlay;

	// Door scene overlay for highlights
	@Inject
	private DoorSceneOverlay doorSceneOverlay;

	// Tap scene overlay for highlights
	@Inject
	private TapSceneOverlay tapSceneOverlay;

	// GroundItem overlay for tinderbox/bucket
	@Inject
	private GroundItemOverlay groundItemOverlay;

	// Underground cave scene overlay for highlights
	@Inject
	private CaveSceneOverlay caveSceneOverlay;

	// ItemManager for ground items
	@Inject
	private ItemManager itemManager;

	// Saradomin standard(flag)
	@Getter(AccessLevel.PACKAGE)
	private GameObject saradominStandard;

	// Zamorak standard(flag)
	@Getter(AccessLevel.PACKAGE)
	private GameObject zamorakStandard;

	// DeSpawned rocks underground
	@Getter
	private final List<WorldPoint> deSpawnedRocks = new ArrayList<>();

	// SpawnedRocks underground to highlight
	@Getter
	private final Map<WorldPoint, Rock> highlightRocks = new HashMap<>();

	// Set TindTimer on lit barricades
	@Getter
	private final Map<WorldPoint, Barricade> litBarricades = new HashMap<>();

	// Last action time for TindTimer
	@Getter(AccessLevel.PACKAGE)
	private Instant lastActionTime = Instant.ofEpochMilli(0);

	// The barricades to highlight for each team blue/red
	@Getter
	private final List<Barricade> highlightBarricades = new ArrayList<>();

	// The doors to highlight
	@Getter
	private final Map<WorldPoint, Door> highlightDoors = new HashMap<>();

	// The taps to highlight
	@Getter
	private final Map<WorldPoint, Tap> highlightTaps = new HashMap<>();

	// The groundItems to highlight, tinderbox/bucket
	@Getter
	private final Map<GroundItem.GroundItemKey, GroundItem> highlightGroundItems = new LinkedHashMap<>();

	// The underground caves to highlight
	@Getter
	private final Map<WorldPoint, Cave> highlightCaves = new HashMap<>();

	/**
	 * CastleWarsConfig getter
	 *
	 * @param configManager
	 * @return CastleWarsConfig
	 */
	@Provides
	CastleWarsConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CastleWarsConfig.class);
	}

	@SneakyThrows
	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("castlewars"))
		{
			return;
		}

		if (event.getKey().equals("mirrorMode"))
		{
			barricadeSceneOverlay.determineLayer();
			barricadeMiniMapOverlay.determineLayer();
			barricadeTimerOverlay.determineLayer();

			rockSceneOverlay.determineLayer();
			rockMiniMapOverlay.determineLayer();
			tunnelMiniMapOverlay.determineLayer();

			doorSceneOverlay.determineLayer();
			tapSceneOverlay.determineLayer();

			caveSceneOverlay.determineLayer();

			overlayManager.remove(barricadeSceneOverlay);
			overlayManager.remove(barricadeMiniMapOverlay);
			overlayManager.remove(barricadeTimerOverlay);

			overlayManager.remove(rockSceneOverlay);
			overlayManager.remove(rockMiniMapOverlay);
			overlayManager.remove(tunnelMiniMapOverlay);

			overlayManager.remove(doorSceneOverlay);
			overlayManager.remove(tapSceneOverlay);

			overlayManager.remove(caveSceneOverlay);

			overlayManager.add(barricadeSceneOverlay);
			overlayManager.add(barricadeMiniMapOverlay);
			overlayManager.add(barricadeTimerOverlay);

			overlayManager.add(rockSceneOverlay);
			overlayManager.add(rockMiniMapOverlay);
			overlayManager.add(tunnelMiniMapOverlay);

			overlayManager.add(doorSceneOverlay);
			overlayManager.add(tapSceneOverlay);

			overlayManager.add(caveSceneOverlay);
		}

		if (!config.barricadeHighlight())
		{
			highlightBarricades.clear();
		}
		if (!config.rocksHighlight())
		{
			highlightRocks.clear();
		}
		if (!config.useTindTimer())
		{
			litBarricades.clear();
		}
		if (!config.doorsHighlight())
		{
			highlightDoors.clear();
		}
		if (!config.tapHighlight())
		{
			highlightTaps.clear();
		}
		if (!config.groundItemHighlight())
		{
			highlightGroundItems.clear();
		}
		if (!config.caveHighlight())
		{
			highlightCaves.clear();
		}
		rebuildAllHighlightBarricades();
		barricadeTimerOverlay.updateConfig();
	}

	/**
	 * Rebuild highlight barricades,
	 * for when config has changed.
	 */
	private void rebuildAllHighlightBarricades()
	{
		highlightBarricades.clear();

		if (client.getGameState() != GameState.LOGGED_IN || !config.barricadeHighlight())
		{
			return;
		}

		for (NPC npc : client.getNpcs())
		{
			//final WorldPoint npcLocation = npc.getWorldLocation();
			switch (npc.getId())
			{
				case NpcID.SARADOMIN_BARRICADE:
				case NpcID.ZAMORAK_BARRICADE:
					if (config.barricadeHighlight())
					{
						highlightBarricades.add(new Barricade(npc));
					}
					log.debug("Rebuild all highlight barricades");
					break;
			}
		}
	}

	@SneakyThrows
	@Override
	protected void startUp()
	{
		overlayManager.add(barricadeSceneOverlay);
		overlayManager.add(barricadeMiniMapOverlay);
		overlayManager.add(barricadeTimerOverlay);

		overlayManager.add(rockSceneOverlay);
		overlayManager.add(rockMiniMapOverlay);
		overlayManager.add(tunnelMiniMapOverlay);

		overlayManager.add(doorSceneOverlay);
		overlayManager.add(tapSceneOverlay);

		overlayManager.add(groundItemOverlay);

		overlayManager.add(caveSceneOverlay);

		rebuildAllHighlightBarricades();
		barricadeTimerOverlay.updateConfig();
	}

	@SneakyThrows
	@Override
	protected void shutDown()
	{
		overlayManager.remove(barricadeSceneOverlay);
		overlayManager.remove(barricadeMiniMapOverlay);
		overlayManager.remove(barricadeTimerOverlay);

		overlayManager.remove(rockSceneOverlay);
		overlayManager.remove(rockMiniMapOverlay);
		overlayManager.remove(tunnelMiniMapOverlay);

		overlayManager.remove(doorSceneOverlay);
		overlayManager.remove(tapSceneOverlay);

		overlayManager.remove(groundItemOverlay);

		overlayManager.remove(caveSceneOverlay);

		lastActionTime = Instant.ofEpochMilli(0);
		litBarricades.clear();
		highlightBarricades.clear();
		highlightRocks.clear();
		deSpawnedRocks.clear();
		highlightDoors.clear();
		highlightTaps.clear();
		highlightGroundItems.clear();
		highlightCaves.clear();
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		// Clear all things when logged out.
		if (event.getGameState() == GameState.HOPPING || event.getGameState() == GameState.LOGIN_SCREEN)
		{
			litBarricades.clear();
			highlightBarricades.clear();
			highlightRocks.clear();
			deSpawnedRocks.clear();
			highlightDoors.clear();
			highlightTaps.clear();
			highlightGroundItems.clear();
			highlightCaves.clear();
			saradominStandard = null;
			zamorakStandard = null;
		}
	}

	/**
	 * Method to check if inside Castle Wars.
	 */
	private boolean inCastleWars()
	{
		final Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null)
		{
			return false;
		}
		final int regionID = localPlayer.getWorldLocation().getRegionID();
		return regionID == RegionID.CASTLE_WARS /* 9520 = Castle Wars */ || regionID == RegionID.CASTLE_WARS_UNDERGROUND; /* 9620 = Castle Wars underground */
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded menuEntryAdded)
	{
		final String option = Text.removeTags(menuEntryAdded.getOption()).toLowerCase();
		final String target = Text.standardize(menuEntryAdded.getTarget());
		final int identifier = menuEntryAdded.getIdentifier();
		final MenuEntry[] menuEntries = client.getMenuEntries();

		// Don't hide player options when not inside Castle Wars, or when using bandages to heal other players.
		if (!inCastleWars() || target.contains("bandages"))
			return;

		final WorldView wv = client.getTopLevelWorldView();
		final IndexedObjectSet<? extends Player> players = wv.players();
		final IndexedObjectSet<? extends NPC> npcs = wv.npcs();

		// Hide player options when using Explosive potion, Tinderbox, Bucket of water or a Bucket.
		if (config.hidePlayerOptions() && option.startsWith("use"))
		{
			if (target.contains("explosive potion")
				|| target.contains("tinderbox")
				|| target.contains("bucket of water")
				|| target.contains("bucket"))
			{
				final Player player = players.byIndex(identifier);

				if (player == null)
					return;

				if (menuEntries.length > 0 && target.contains(player.getName().toLowerCase()))
					client.setMenuEntries(Arrays.copyOf(menuEntries, menuEntries.length - 1));
			}
		}

		// Hide same team players menu's upon casting
		if (option.startsWith("cast"))
		{
			if (config.hideCastPlayers())
			{
				final Player player = players.byIndex(identifier);

				if (player == null)
					return;

				final PlayerComposition localPlayerComposition = client.getLocalPlayer().getPlayerComposition();
				final PlayerComposition playerComposition = player.getPlayerComposition();
				if (localPlayerComposition == null || playerComposition == null)
					return;
				final boolean isSameTeam = localPlayerComposition.getEquipmentId(KitType.CAPE) == playerComposition.getEquipmentId(KitType.CAPE);
				if (!isSameTeam)
					return;

				if (menuEntries.length > 0 && target.contains(player.getName().toLowerCase()))
					client.setMenuEntries(Arrays.copyOf(menuEntries, menuEntries.length - 1));
			}
		}

		// Hide npc & player options if the flag is on the same location for quick grab
		if (config.hideFlagOptions())
		{
			if (saradominStandard == null && zamorakStandard == null)
				return;

			final NPC npc = npcs.byIndex(identifier);
			final Player player = players.byIndex(identifier);

			if (npc != null)
			{
				if (saradominStandard != null && saradominStandard.getLocalLocation().equals(npc.getLocalLocation()) || zamorakStandard != null && zamorakStandard.getLocalLocation().equals(npc.getLocalLocation()))
					if (menuEntries.length > 0 && target.contains(npc.getName().toLowerCase()))
						client.setMenuEntries(Arrays.copyOf(menuEntries, menuEntries.length - 1));
			}

			if (player != null)
			{
				if (saradominStandard != null && saradominStandard.getLocalLocation().equals(player.getLocalLocation()) || zamorakStandard != null && zamorakStandard.getLocalLocation().equals(player.getLocalLocation()))
					if (menuEntries.length > 0 && target.contains(player.getName().toLowerCase()))
						client.setMenuEntries(Arrays.copyOf(menuEntries, menuEntries.length - 1));
			}
		}
		/*if (config.hideNpcOptions())
		{
			if (saradominStandard == null && zamorakStandard == null)
				return;

			NPC npc = null;

			if (identifier >= 0 && identifier < npcs.length)
				npc = npcs[identifier];

			if (npc == null)
				return;

			if (saradominStandard != null && saradominStandard.getLocalLocation().equals(npc.getLocalLocation()) || zamorakStandard != null && zamorakStandard.getLocalLocation().equals(npc.getLocalLocation()))
				if (menuEntries.length > 0 && target.contains(npc.getName().toLowerCase()))
					client.setMenuEntries(Arrays.copyOf(menuEntries, menuEntries.length - 1));
		}*/
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		final GameObject gameObject = event.getGameObject();
		final WorldPoint gameObjectLocation = gameObject.getWorldLocation();

		final Tile tile = event.getTile();
		final WorldPoint tileLocation = tile.getWorldLocation();

		switch (gameObject.getId())
		{
			// This is used to know which standard(flag) is dropped by a player.
			case ObjectID.SARADOMIN_STANDARD: // Saradomin Standard
				this.saradominStandard = gameObject;
				log.debug("Saradomin flag spawn: {}", saradominStandard.getId(), gameObjectLocation);
				break;

			case ObjectID.ZAMORAK_STANDARD: // Zamorak Standard
				this.zamorakStandard = gameObject;
				log.debug("Zamorak flag spawn: {}", zamorakStandard.getId(), gameObjectLocation);
				break;

			// Remove tunnel MiniMap overlay if rock has spawned and add highlights on rocks.
			case ObjectID.ROCKS_FULL: // Underground rocks full
			case ObjectID.ROCKS_HALF: // Underground rocks half
				deSpawnedRocks.remove(tileLocation);
				if (config.rocksHighlight() && gameObject.getWorldLocation().getRegionID() == RegionID.CASTLE_WARS_UNDERGROUND)
				{
					highlightRocks.put(gameObjectLocation, new Rock(gameObject));
				}
				log.debug("Rock spawn: {} {}", gameObject.getId(), gameObjectLocation);
				break;
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		final GameObject gameObject = event.getGameObject();
		final WorldPoint gameObjectLocation = gameObject.getWorldLocation();

		final Tile tile = event.getTile();
		final WorldPoint tileLocation = tile.getWorldLocation();

		switch (gameObject.getId())
		{
			// Null standards if it has been taken again.
			case ObjectID.SARADOMIN_STANDARD: // Saradomin Standard
				this.saradominStandard = null;
				break;

			case ObjectID.ZAMORAK_STANDARD: // Zamorak Standard
				this.zamorakStandard = null;
				break;

			// Remove highlight and add tunnel MiniMap overlay if rock has been deSpawned.
			case ObjectID.ROCKS_FULL: // Underground rocks Full
			case ObjectID.ROCKS_HALF: // Underground rocks half
				highlightRocks.remove(gameObjectLocation);
				deSpawnedRocks.add(tileLocation);
				break;
		}
	}

	@Override
	protected int loop()
	{
		if (client.getLocalPlayer() == null || client.getGameState() != GameState.LOGGED_IN)
		{
			return -1;
		}

		if (config.autoJoin() && RS2Dialog.isOpen() && RS2Dialog.getQuestion().contains("There's a free space, do you want to join?"))
		{
			Keyboard.type(1);
		}
		return -1;
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		// Surrender/teleport
		if (config.surrender() && inCastleWars())
		{
			final int realHitpoints = client.getRealSkillLevel(Skill.HITPOINTS);
			final int currentHitpoints = client.getBoostedSkillLevel(Skill.HITPOINTS);
			final double percentage = (double) currentHitpoints / realHitpoints * 100;
			if (percentage < 15)
			{
				log.info("[Teleport] Hitpoints real: {} curr: {} percentage: {}", realHitpoints, currentHitpoints, percentage);

				final RS2Item cape = RS2Equipment.fromSlot(EquipmentInventorySlot.CAPE);
				if (cape != null && cape.hasAction("Surrender"))
				{
					Static.invokeMenuAction(-1, InterfaceID.Wornitems.SLOT1, MenuAction.CC_OP, 2, -1, 0, "Surrender", "<col=ff9040>" + cape.getName() + "</col>", -1, -1);
				}
			}
		}

		// Set hintArrow on player with the standard(flag).
		for (Player player : client.getPlayers())
		{
			final int[] equipmentIds = player.getPlayerComposition().getEquipmentIds();
			for (int equipmentId : equipmentIds)
			{
				if (equipmentId == EquipmentID.SARADOMIN_STANDARD
					|| equipmentId == EquipmentID.ZAMORAK_STANDARD)
				{
					client.setHintArrow(player);
				}
			}
		}

		// Clear highlights from rocks if player is not underground.
		if (client.getLocalPlayer().getWorldLocation().getRegionID() != RegionID.CASTLE_WARS_UNDERGROUND && !highlightRocks.isEmpty())
		{
			highlightRocks.clear();
		}

		// Clear highlights from doors if not inside CastleWars or when underground.
		if (!inCastleWars()
			&& !highlightDoors.isEmpty()
			|| !highlightDoors.isEmpty() && client.getLocalPlayer().getWorldLocation().getRegionID() == RegionID.CASTLE_WARS_UNDERGROUND)
		{
			highlightDoors.clear();
		}

		// Check if all TindTimers are still there, and remove the ones that are not.
		final Iterator<Map.Entry<WorldPoint, Barricade>> it = getLitBarricades().entrySet().iterator();
		final Tile[][][] tiles = client.getScene().getTiles();

		//Instant expire = Instant.now().minus(Barricade.TIND_TIME.multipliedBy(2));
		final Instant expire = Instant.now().plusSeconds(7);

		while (it.hasNext())
		{
			final Map.Entry<WorldPoint, Barricade> entry = it.next();
			final WorldPoint worldPoint = entry.getKey();
			final Barricade barricade = entry.getValue();
			final LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);

			// Not within the client's viewport
			if (localPoint == null)
			{
				// remove TindTimer if it has expired
				if (barricade.getLitOn().isBefore(expire))
				{
					it.remove();
				}
			}
		}

		// Check if all highlighted Rocks are still there, and remove the ones that are not.
		final Iterator<Map.Entry<WorldPoint, Rock>> highlightRocksIter = getHighlightRocks().entrySet().iterator();
		while (highlightRocksIter.hasNext())
		{
			final Map.Entry<WorldPoint, Rock> entry = highlightRocksIter.next();
			final WorldPoint worldPoint = entry.getKey();
			final Rock rock = entry.getValue();
			final LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);

			// Not within the client's viewport
			if (localPoint == null)
			{
				highlightRocksIter.remove();
			}
		}

		// Check if all highlighted Doors are still there, and remove the ones that are not.
		final Iterator<Map.Entry<WorldPoint, Door>> highlightDoorsIter = getHighlightDoors().entrySet().iterator();
		while (highlightDoorsIter.hasNext())
		{
			final Map.Entry<WorldPoint, Door> entry = highlightDoorsIter.next();
			final WorldPoint worldPoint = entry.getKey();
			final Door door = entry.getValue();
			final LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);

			// Not within the client's viewport
			if (localPoint == null)
			{
				highlightDoorsIter.remove();
			}
		}

		// Check if all highlighted Ground items are still there, and remove the ones that are not.
		final Iterator<Map.Entry<GroundItem.GroundItemKey, GroundItem>> highlightGroundItemsIter = getHighlightGroundItems().entrySet().iterator();
		while (highlightGroundItemsIter.hasNext())
		{
			final Map.Entry<GroundItem.GroundItemKey, GroundItem> entry = highlightGroundItemsIter.next();
			final WorldPoint worldPoint = entry.getKey().getLocation();
			final GroundItem groundItem = entry.getValue();
			final LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);

			// Not within the client's viewport
			if (localPoint == null)
			{
				highlightGroundItemsIter.remove();
			}
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		// Only allow this to work when barricadeHighlight is enabled in config.
		if (!config.barricadeHighlight())
			return;

		final NPC npc = npcSpawned.getNpc();
		//final WorldPoint npcLocation = npc.getWorldLocation();

		switch (npc.getId())
		{
			// Set highlights on barricades. ->
			// Normal barricade,
			// Lit barricade
			case NpcID.SARADOMIN_BARRICADE:
			case NpcID.SARADOMIN_BARRICADE_LIT:
				highlightBarricades.add(new Barricade(npc));
				log.debug("Saradomin barricade spawn: {} {}", npc.getId(), npc.getWorldLocation());
				log.debug("Added barricade: {} {} -> highlightBarricades", new Barricade(npc), npc.getWorldLocation());
				break;

			case NpcID.ZAMORAK_BARRICADE:
			case NpcID.ZAMORAK_BARRICADE_LIT:
				highlightBarricades.add(new Barricade(npc));
				log.debug("Zamorak barricade spawn: {} {}", npc.getId(), npc.getWorldLocation());
				log.debug("Added barricade: {} {} -> highlightBarricades", new Barricade(npc), npc.getWorldLocation());
				break;
		}
	}

	@Subscribe
	public void onNpcChanged(NpcChanged npcChanged)
	{
		final NPC npc = npcChanged.getNpc();
		final WorldPoint npcLocation = npc.getWorldLocation();

		switch (npc.getId())
		{
			// Remove TindTimers from lit barricades when bucket is used.
			case NpcID.SARADOMIN_BARRICADE:
			case NpcID.ZAMORAK_BARRICADE:
				litBarricades.remove(npcLocation);
				log.debug("Removed npcLocation: {} -> litBarricades", npcLocation);
				break;

			// Set highlights and TindTimers on lit barricades.
			case NpcID.SARADOMIN_BARRICADE_LIT:
			case NpcID.ZAMORAK_BARRICADE_LIT:
				if (config.useTindTimer())
				{
					litBarricades.put(npcLocation, new Barricade(npc));
					lastActionTime = Instant.now();
					log.debug("Added barricade: {} {} -> litBarricades", new Barricade(npc), npc.getWorldLocation());
				}
				break;
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		// Remove highlights and TindTimers from lit barricades.
		final NPC npc = npcDespawned.getNpc();
		final WorldPoint npcLocation = npc.getWorldLocation();
		//final Barricade litcade = barricades.get(npcLocation);

		switch (npc.getId())
		{
			case NpcID.SARADOMIN_BARRICADE:
			case NpcID.ZAMORAK_BARRICADE:
				highlightBarricades.remove(npc);
				log.debug("Removed npc: {} {} -> highlightBarricades", npc.getComposition(), npc.getWorldLocation());
				break;

			case NpcID.SARADOMIN_BARRICADE_LIT:
			case NpcID.ZAMORAK_BARRICADE_LIT:
				highlightBarricades.remove(npc);
				log.debug("Removed npc: {} {} -> highlightBarricades", npc.getComposition(), npc.getWorldLocation());
				litBarricades.remove(npcLocation);
				log.debug("Removed npcLocation: {} -> litBarricades", npcLocation);
				break;
		}
	}

	@Subscribe
	public void onWallObjectSpawned(WallObjectSpawned wallObjectSpawned)
	{
		final WallObject wallObject = wallObjectSpawned.getWallObject();
		//final ObjectDefinition comp = client.getObjectDefinition(wallObject.getId());
		//final ObjectDefinition impostor = comp.getImpostorIds() != null ? comp.getImpostor() : comp;
		final WorldPoint wallObjectLocation = wallObject.getWorldLocation();

		if (wallObject != null && ObjectID.wallObject_Ids_DOORS.contains(wallObject.getId()))
		{
			highlightDoors.put(wallObjectLocation, new Door(wallObject));
			log.debug("Added wallObject: {} {} -> highlightDoors", new Door(wallObject), wallObject.getWorldLocation());
		}

		if (wallObject != null && wallObject.getId() == ObjectID.CAVE)
		{
			highlightCaves.put(wallObjectLocation, new Cave(wallObject));
			log.debug("Added wallObject: {} {} -> highlightCaves", new Cave(wallObject), wallObject.getWorldLocation());
		}
	}

	@Subscribe
	public void onWallObjectDespawned(WallObjectDespawned wallObjectDespawned)
	{
		final WallObject wallObject = wallObjectDespawned.getWallObject();
		final WorldPoint wallObjectLocation = wallObject.getWorldLocation();

		if (ObjectID.wallObject_Ids_DOORS.contains(wallObject.getId()))
		{
			highlightDoors.remove(wallObjectLocation);
			log.debug("Removed wallObject: {} {} -> highlightDoors", wallObject.getId(), wallObject.getWorldLocation());
		}

		if (wallObject.getId() == ObjectID.CAVE)
		{
			highlightCaves.remove(wallObjectLocation);
			log.debug("Removed wallObject: {} {} -> highlightCaves", wallObject.getId(), wallObject.getWorldLocation());
		}
	}

	@Subscribe
	public void onDecorativeObjectSpawned(DecorativeObjectSpawned decorativeObjectSpawned)
	{
		final DecorativeObject decorativeObject = decorativeObjectSpawned.getDecorativeObject();
		final WorldPoint decorativeObjectLocation = decorativeObject.getWorldLocation();

		switch (decorativeObject.getId())
		{
			case ObjectID.TAP:
				if (decorativeObject.getWorldLocation().equals(new WorldPoint(2431, 3077, 0))
					|| decorativeObject.getWorldLocation().equals(new WorldPoint(2368, 3130, 0)))
				{
					highlightTaps.put(decorativeObjectLocation, new Tap(decorativeObject));
					log.debug("Added decorativeObject: {} {} -> highlightTaps", new Tap(decorativeObject), decorativeObject.getWorldLocation());
				}
				break;
		}
	}

	@Subscribe
	public void onDecorativeObjectDespawned(DecorativeObjectDespawned decorativeObjectDespawned)
	{
		final DecorativeObject decorativeObject = decorativeObjectDespawned.getDecorativeObject();
		final WorldPoint decorativeObjectLocation = decorativeObject.getWorldLocation();

		switch (decorativeObject.getId())
		{
			case ObjectID.TAP:
				highlightTaps.remove(decorativeObjectLocation);
				log.debug("Removed decorativeObject: {} {} -> highlightTaps", decorativeObject.getId(), decorativeObject.getWorldLocation());
				break;
		}
	}

	@Subscribe
	public void onItemSpawned(ItemSpawned itemSpawned)
	{
		if (!config.groundItemHighlight())
		{
			return;
		}

		final TileItem item = itemSpawned.getItem();
		final Tile tile = itemSpawned.getTile();

		switch (item.getId())
		{
			case GroundItemID.TINDERBOX:
			case GroundItemID.BUCKET:
			case GroundItemID.BUCKET_OF_WATER:
				GroundItem groundItem = buildGroundItem(tile, item);
				GroundItem.GroundItemKey groundItemKey = new GroundItem.GroundItemKey(item.getId(), tile.getWorldLocation());
				highlightGroundItems.putIfAbsent(groundItemKey, groundItem);
				log.debug("Added groundItem: {} -> highlightGroundItems", groundItemKey);
				break;
		}
	}

	@Subscribe
	public void onItemDespawned(ItemDespawned itemDespawned)
	{
		if (!config.groundItemHighlight())
		{
			return;
		}

		final TileItem item = itemDespawned.getItem();
		final Tile tile = itemDespawned.getTile();

		if (item == null || tile == null)
		{
			return;
		}

		switch (item.getId())
		{
			case GroundItemID.TINDERBOX:
			case GroundItemID.BUCKET:
			case GroundItemID.BUCKET_OF_WATER:
				GroundItem.GroundItemKey groundItemKey = new GroundItem.GroundItemKey(item.getId(), tile.getWorldLocation());
				if (groundItemKey == null)
				{
					log.debug("groundItemKey is null: {}", groundItemKey);
					return;
				}

				if (!highlightGroundItems.containsKey(groundItemKey))
				{
					log.debug("highlightGroundItems doesn't contain key: {}", groundItemKey);
					return;
				}

				highlightGroundItems.remove(groundItemKey);
				log.debug("Removed groundItem: {} -> highlightGroundItems", groundItemKey);
				break;
		}
	}

	/**
	 * Build a ground item
	 *
	 * @param tile
	 * @param item
	 * @return groundItem
	 */
	private GroundItem buildGroundItem(final Tile tile, final TileItem item)
	{
		// Collect the data for the item
		final int itemId = item.getId();
		final ItemComposition itemComposition = itemManager.getItemComposition(itemId);
		final GroundItem groundItem = GroundItem.builder()
			.id(itemId)
			.location(tile.getWorldLocation())
			.height(tile.getItemLayer().getHeight())
			.name(itemComposition.getName())
			.build();
		log.debug("Build groundItem: {}", groundItem);
		return groundItem;
	}
}
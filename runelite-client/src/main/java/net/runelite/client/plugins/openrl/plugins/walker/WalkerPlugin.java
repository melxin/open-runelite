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
package net.runelite.client.plugins.openrl.plugins.walker;

import com.google.inject.Provides;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.CollisionData;
import net.runelite.api.CollisionDataFlag;
import net.runelite.api.MenuAction;
import net.runelite.api.WorldView;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.events.Draw;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.Movement;
import net.runelite.client.plugins.openrl.api.plugin.LoopedPlugin;
import net.runelite.client.plugins.openrl.api.rs2.providers.entities.RS2Players;
import net.runelite.client.plugins.openrl.api.rs2.providers.scene.RS2Tiles;
import net.runelite.client.plugins.openrl.api.rs2.providers.worldmap.RS2WorldMap;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Player;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Tile;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2WorldPoint;

@PluginDescriptor(
	name = "Open RuneLite Walker plugin",
	description = "Add walk option",
	tags = {"walker", "movement", "menu option", "test", "collision"},
	enabledByDefault = true
)
@Slf4j
public class WalkerPlugin extends LoopedPlugin
{
	@Inject
	private Client client;

	@Inject
	private RegionHandler regionHandler;

	private WorldPoint targetWorldPoint;

	@Setter
	private static List<WorldPoint> currentPath;

	@Inject
	protected WalkerConfig config;

	@Provides
	private WalkerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WalkerConfig.class);
	}

	@Subscribe
	protected void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals(WalkerConfig.GROUP))
		{
			return;
		}
	}

	@Subscribe
	protected void onConfigButtonClicked(ConfigButtonClicked event)
	{
		if (!event.getGroup().equals(WalkerConfig.GROUP) || !event.getKey().equals("resetPath"))
		{
			return;
		}

		this.targetWorldPoint = null;
		currentPath = null;
	}

	@Override
	protected void startUp()
	{
		this.targetWorldPoint = null;
		currentPath = null;
	}

	@Override
	protected void shutDown()
	{
		this.targetWorldPoint = null;
		currentPath = null;
	}

	@Subscribe
	protected void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (!config.testWalkerMenuOptions())
		{
			return;
		}

		final int type = event.getType();
		if (type == MenuAction.WALK.getId())
		{
			client.createMenuEntry(-2)
				.setParam0(event.getActionParam0())
				.setParam1(event.getActionParam1())
				.setTarget(event.getTarget())
				.setOption("Test walker")
				.setType(MenuAction.RUNELITE)
				.setIdentifier(event.getIdentifier())
				.setItemId(event.getItemId())
				.onClick(e ->
				{
					final WorldPoint selectedSceneWorldPoint = RS2Tiles.getSelectedSceneWorldPoint();
					if (selectedSceneWorldPoint == null)
					{
						log.warn("Selected scene world point is null!");
						return;
					}
					log.info("Selected scene world point: {}", selectedSceneWorldPoint);
					this.targetWorldPoint = selectedSceneWorldPoint;
					Movement.walkTo(targetWorldPoint);
				});
		}
		else if (RS2WorldMap.isOpen() && type == MenuAction.CANCEL.getId())
		{
			client.createMenuEntry(-2)
				.setParam0(event.getActionParam0())
				.setParam1(event.getActionParam1())
				.setTarget(event.getTarget())
				.setOption("Test walker")
				.setType(MenuAction.RUNELITE)
				.setIdentifier(event.getIdentifier())
				.setItemId(event.getItemId())
				.onClick(e ->
				{
					final WorldPoint selectedWorldPoint = RS2WorldMap.getWorldPointAtMouse();
					if (selectedWorldPoint == null)
					{
						log.warn("Selected world point is null!");
						return;
					}
					log.info("Selected world point: {}", selectedWorldPoint);
					this.targetWorldPoint = selectedWorldPoint;
					Movement.walkTo(selectedWorldPoint);
				});
		}
	}

	@Override
	protected int loop()
	{
		if (targetWorldPoint == null /*|| currentPath == null*/)
		{
			return -1;
		}
		final RS2Player local = RS2Players.getLocal();
		if (local == null)
		{
			return -1;
		}

		/*if (Movement.isWalking())
		{
			log.info("Walking!");
		}*/

		if (local.getWorldLocation().equals(targetWorldPoint) /*|| local.distanceTo(targetWorldPoint) <= 1*/)
		{
			log.info("Destination reached!");
			this.targetWorldPoint = null;
			currentPath = null;
			return -1;
		}

		if (targetWorldPoint != null)
		{
			Movement.walkTo(targetWorldPoint);
		}
		return -1;
	}

	@Subscribe
	protected void onDraw(Draw event)
	{
		final Graphics graphics = event.getGraphics();
		if (graphics == null)
		{
			return;
		}
		final Graphics2D g2d = (Graphics2D) graphics;

		if (config.collisionMapOverlay())
		{
			final WorldView wv = client.getTopLevelWorldView();
			final CollisionData[] collisionMaps = wv.getCollisionMaps();
			if (collisionMaps == null)
			{
				return;
			}
			final CollisionData collisionData = collisionMaps[wv.getPlane()];
			final int[][] flags = collisionData.getFlags();
			final List<RS2Tile> tiles = RS2Tiles.getAll();
			for (RS2Tile tile : tiles)
			{
				final int flag = flags[tile.getSceneLocation().getX()][tile.getSceneLocation().getY()];
				final Set<MovementFlag> movementFlags = MovementFlag.getSetFlags(flag);
				final RS2WorldPoint wp = new RS2WorldPoint(tile.getWorldLocation());
				if (movementFlags.isEmpty())
				{
					wp.outline(Static.getClient(), g2d, Color.GREEN, "0");
				}
				else
				{
					wp.outline(Static.getClient(), g2d, Color.RED, "" + flag);
				}
			}
		}

		if (!config.pathOverlay() || targetWorldPoint == null || currentPath == null)
		{
			return;
		}

		//final List<WorldPoint> remainingPath = Walker.remainingPath(Walker.path);
		for (WorldPoint wp : currentPath)
		{
			new RS2WorldPoint(wp).outline(Static.getClient(), g2d, Color.GREEN);
		}
	}

	/**
	 * An enum that binds a name to each movement flag.
	 *
	 * @see CollisionDataFlag
	 */
	@AllArgsConstructor
	protected enum MovementFlag
	{
		BLOCK_MOVEMENT_NORTH_WEST(CollisionDataFlag.BLOCK_MOVEMENT_NORTH_WEST),
		BLOCK_MOVEMENT_NORTH(CollisionDataFlag.BLOCK_MOVEMENT_NORTH),
		BLOCK_MOVEMENT_NORTH_EAST(CollisionDataFlag.BLOCK_MOVEMENT_NORTH_EAST),
		BLOCK_MOVEMENT_EAST(CollisionDataFlag.BLOCK_MOVEMENT_EAST),
		BLOCK_MOVEMENT_SOUTH_EAST(CollisionDataFlag.BLOCK_MOVEMENT_SOUTH_EAST),
		BLOCK_MOVEMENT_SOUTH(CollisionDataFlag.BLOCK_MOVEMENT_SOUTH),
		BLOCK_MOVEMENT_SOUTH_WEST(CollisionDataFlag.BLOCK_MOVEMENT_SOUTH_WEST),
		BLOCK_MOVEMENT_WEST(CollisionDataFlag.BLOCK_MOVEMENT_WEST),

		BLOCK_MOVEMENT_OBJECT(CollisionDataFlag.BLOCK_MOVEMENT_OBJECT),
		BLOCK_MOVEMENT_FLOOR_DECORATION(CollisionDataFlag.BLOCK_MOVEMENT_FLOOR_DECORATION),
		BLOCK_MOVEMENT_FLOOR(CollisionDataFlag.BLOCK_MOVEMENT_FLOOR),
		BLOCK_MOVEMENT_FULL(CollisionDataFlag.BLOCK_MOVEMENT_FULL);

		@Getter
		private int flag;

		/**
		 * @param collisionData The tile collision flags.
		 * @return The set of {@link MovementFlag}s that have been set.
		 */
		protected static Set<MovementFlag> getSetFlags(int collisionData)
		{
			return Arrays.stream(values())
				.filter(movementFlag -> (movementFlag.flag & collisionData) != 0)
				.collect(Collectors.toSet());
		}
	}
}
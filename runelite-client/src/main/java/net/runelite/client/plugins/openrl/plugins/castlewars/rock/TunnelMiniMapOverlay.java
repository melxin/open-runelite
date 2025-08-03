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
package net.runelite.client.plugins.openrl.plugins.castlewars.rock;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.plugins.castlewars.CastleWarsConfig;
import net.runelite.client.plugins.openrl.plugins.castlewars.CastleWarsPlugin;
import net.runelite.client.plugins.openrl.plugins.castlewars.id.RegionID;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class TunnelMiniMapOverlay extends Overlay
{
	private final Client client;
	private final CastleWarsConfig config;
	private final CastleWarsPlugin plugin;

	@Inject
	TunnelMiniMapOverlay(Client client, CastleWarsConfig config, CastleWarsPlugin plugin)
	{
		this.client = client;
		this.config = config;
		this.plugin = plugin;
		determineLayer();
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		// Tunnels MiniMap overlay
		if (config.displayOpenTunnels())
		{
			renderTunnelMiniMapOverlay(graphics);
		}

		return null;
	}

	/**
	 * Render MiniMap overlay for open tunnels
	 *
	 * @param graphics
	 */
	private void renderTunnelMiniMapOverlay(Graphics2D graphics)
	{
		for (WorldPoint deSpawnedRock : plugin.getDeSpawnedRocks())
		{
			if (!config.displayOpenTunnels() || client.getLocalPlayer().getWorldLocation().getRegionID() != RegionID.CASTLE_WARS_UNDERGROUND || deSpawnedRock == null)
			{
				return;
			}

			/**
			 * Ugly as f*ck but it works.
			 *
			 * @TODO REWRITE THIS SHIT
			 */
			final String Text = "Open";
			final LocalPoint deSpawnedRocksLocation = LocalPoint.fromWorld(client, deSpawnedRock);
			final Point deSpawnedRocksMiniMapText = Perspective.getCanvasTextMiniMapLocation(client, graphics,
				deSpawnedRocksLocation, Text);
			graphics.setColor(Color.GREEN);

			// Saradomin tunnel north direction
			if (deSpawnedRock.getX() == 2409 && deSpawnedRock.getY() == 9503)
			{
				if (deSpawnedRocksMiniMapText != null)
				{
					graphics.drawString(Text, deSpawnedRocksMiniMapText.getX() + 1, deSpawnedRocksMiniMapText.getY() + 1);
				}
				final LocalPoint saradominTunnelNorthLocation = LocalPoint.fromWorld(client, 2424, 9493);
				if (saradominTunnelNorthLocation == null)
				{
					continue;
				}
				final Point saradominTunnelMiniMapTextNorth = Perspective.getCanvasTextMiniMapLocation(client, graphics,
					saradominTunnelNorthLocation, Text);
				if (saradominTunnelMiniMapTextNorth != null)
				{
					graphics.drawString(Text, saradominTunnelMiniMapTextNorth.getX() + 1, saradominTunnelMiniMapTextNorth.getY() + 1);
				}
			}

			// Saradomin tunnel west direction
			if (deSpawnedRock.getX() == 2401 && deSpawnedRock.getY() == 9494)
			{
				if (deSpawnedRocksMiniMapText != null)
				{
					graphics.drawString(Text, deSpawnedRocksMiniMapText.getX() + 1, deSpawnedRocksMiniMapText.getY() + 1);
				}
				final LocalPoint saradominTunnelWestLocation = LocalPoint.fromWorld(client, 2418, 9483);
				if (saradominTunnelWestLocation == null)
				{
					continue;
				}
				final Point saradominTunnelMiniMapTextWest = Perspective.getCanvasTextMiniMapLocation(client, graphics,
					saradominTunnelWestLocation, Text);
				if (saradominTunnelMiniMapTextWest != null)
				{
					graphics.drawString(Text, saradominTunnelMiniMapTextWest.getX() + 1, saradominTunnelMiniMapTextWest.getY() + 1);
				}
			}

			// Zamorak tunnel south direction
			if (deSpawnedRock.getX() == 2391 && deSpawnedRock.getY() == 9501)
			{
				if (deSpawnedRocksMiniMapText != null)
				{
					graphics.drawString(Text, deSpawnedRocksMiniMapText.getX() + 1, deSpawnedRocksMiniMapText.getY() + 1);
				}
				final LocalPoint zamorakTunnelSouthLocation = LocalPoint.fromWorld(client, 2371, 9516);
				if (zamorakTunnelSouthLocation == null)
				{
					continue;
				}
				final Point zamorakTunnelMiniMapTextSouth = Perspective.getCanvasTextMiniMapLocation(client, graphics,
					zamorakTunnelSouthLocation, Text);
				if (zamorakTunnelMiniMapTextSouth != null)
				{
					graphics.drawString(Text, zamorakTunnelMiniMapTextSouth.getX() + 1, zamorakTunnelMiniMapTextSouth.getY() + 1);
				}
			}

			// Zamorak tunnel east direction
			if (deSpawnedRock.getX() == 2400 && deSpawnedRock.getY() == 9512)
			{
				if (deSpawnedRocksMiniMapText != null)
				{
					graphics.drawString(Text, deSpawnedRocksMiniMapText.getX() + 1, deSpawnedRocksMiniMapText.getY() + 1);
				}
				final LocalPoint zamorakTunnelEastLocation = LocalPoint.fromWorld(client, 2382, 9527);
				if (zamorakTunnelEastLocation == null)
				{
					continue;
				}
				final Point zamorakTunnelMiniMapTextEast = Perspective.getCanvasTextMiniMapLocation(client, graphics,
					zamorakTunnelEastLocation, Text);
				if (zamorakTunnelMiniMapTextEast != null)
				{
					graphics.drawString(Text, zamorakTunnelMiniMapTextEast.getX() + 1, zamorakTunnelMiniMapTextEast.getY() + 1);
				}
			}
		}
	}

	public void determineLayer()
	{
		if (config.mirrorMode())
		{
			setLayer(OverlayLayer.ALWAYS_ON_TOP);
		}

		if (!config.mirrorMode())
		{
			setLayer(OverlayLayer.ABOVE_WIDGETS);
		}
	}
}
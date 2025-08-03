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
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.plugins.castlewars.CastleWarsConfig;
import net.runelite.client.plugins.openrl.plugins.castlewars.CastleWarsPlugin;
import net.runelite.client.plugins.openrl.plugins.castlewars.id.ObjectID;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

public class RockMiniMapOverlay extends Overlay
{
	private final Client client;
	private final CastleWarsConfig config;
	private final CastleWarsPlugin plugin;

	@Inject
	RockMiniMapOverlay(Client client, CastleWarsConfig config, CastleWarsPlugin plugin)
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
		// Underground rocks MiniMap overlay
		for (Map.Entry<WorldPoint, Rock> entry : plugin.getHighlightRocks().entrySet())
		{
			final Rock rock = entry.getValue();

			switch (rock.getRockID())
			{
				case ObjectID.ROCKS_FULL: // Underground rocks full
				case ObjectID.ROCKS_HALF: // Underground rocks half
					renderRockMiniMapOverlay(graphics, rock, rock.getRock().getId(), config.getRocksHighlightColor());
					break;
			}
		}
		return null;
	}

	/**
	 * Render MiniMap overlay for underground rocks
	 *
	 * @param graphics
	 * @param rock
	 * @param ID
	 * @param color
	 */
	private void renderRockMiniMapOverlay(Graphics2D graphics, Rock rock, int ID, Color color)
	{
		final GameObject actor = rock.getRock();
		if (actor == null)
		{
			return;
		}

		final Point miniMapLocation = actor.getMinimapLocation();
		if (miniMapLocation != null)
		{
			OverlayUtil.renderMinimapLocation(graphics, miniMapLocation, color.darker());
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
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
package net.runelite.client.plugins.openrl.plugins.castlewars.cave;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.plugins.castlewars.CastleWarsConfig;
import net.runelite.client.plugins.openrl.plugins.castlewars.CastleWarsPlugin;
import net.runelite.client.plugins.openrl.plugins.castlewars.id.ObjectID;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

public class CaveSceneOverlay extends Overlay
{
	private final Client client;
	private final CastleWarsConfig config;
	private final CastleWarsPlugin plugin;

	@Inject
	CaveSceneOverlay(Client client, CastleWarsConfig config, CastleWarsPlugin plugin)
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
		// Caves to highlight
		for (Map.Entry<WorldPoint, Cave> entry : plugin.getHighlightCaves().entrySet())
		{
			final Cave cave = entry.getValue();

			switch (cave.getCaveID())
			{
				case ObjectID.CAVE:
					if (config.caveHighlightAsCircle())
					{
						renderCircleOnCave(graphics, cave, config.getCaveHighlightColor(), Color.gray);
					}
					else
					{
						renderCaveSceneOverlay(graphics, cave, config.getCaveHighlightColor());
					}
					break;
			}
		}
		return null;
	}

	/**
	 * Draws a circle on the cave.
	 *
	 * @param graphics
	 * @param cave The barricade on which the circle needs to be drawn
	 * @param fill The fill color of the timer
	 * @param border The border color of the timer
	 */
	private void renderCircleOnCave(Graphics2D graphics, Cave cave, Color fill, Color border)
	{
		if (cave.getWorldLocation().getPlane() != client.getPlane())
		{
			return;
		}

		final LocalPoint localLoc = LocalPoint.fromWorld(client, cave.getWorldLocation());
		if (localLoc == null)
		{
			return;
		}

		final Point loc = Perspective.localToCanvas(client, localLoc, client.getPlane());

		final ProgressPieComponent pie = new ProgressPieComponent();
		pie.setFill(fill);
		pie.setBorderColor(border);
		pie.setPosition(loc);
		pie.setProgress(1);
		pie.render(graphics);
	}

	/**
	 * Render Cave highlights
	 *
	 * @param graphics
	 * @param cave
	 * @param color
	 */
	private void renderCaveSceneOverlay(Graphics2D graphics, Cave cave, Color color)
	{
		if (cave.getWorldLocation().getPlane() != client.getPlane())
		{
			return;
		}

		final LocalPoint localLoc = LocalPoint.fromWorld(client, cave.getWorldLocation());
		if (localLoc == null)
		{
			return;
		}

		final Point loc = Perspective.localToCanvas(client, localLoc, client.getPlane());
		if (loc == null)
		{
			return;
		}

		final Point mousePosition = client.getMouseCanvasPosition();
		final Shape objectClickBox = cave.getCave().getConvexHull();
		if (objectClickBox == null)
		{
			return;
		}

		if (objectClickBox.contains(mousePosition.getX(), mousePosition.getY()))
		{
			renderPoly(graphics, color.darker(), objectClickBox);
		}
		else
		{
			renderPoly(graphics, color, objectClickBox);
		}
	}

	/**
	 * Render Polygon
	 *
	 * @param graphics
	 * @param color
	 * @param polygon
	 */
	private void renderPoly(Graphics2D graphics, Color color, Shape polygon)
	{
		if (polygon != null)
		{
			graphics.setColor(color);
			graphics.setStroke(new BasicStroke(2));
			graphics.draw(polygon);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
			graphics.fill(polygon);
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
			setLayer(OverlayLayer.ABOVE_SCENE);
		}
	}
}
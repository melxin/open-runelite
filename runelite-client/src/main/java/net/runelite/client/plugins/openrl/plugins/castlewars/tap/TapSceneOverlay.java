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
package net.runelite.client.plugins.openrl.plugins.castlewars.tap;

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

public class TapSceneOverlay extends Overlay
{
	private final Client client;
	private final CastleWarsConfig config;
	private final CastleWarsPlugin plugin;

	@Inject
	TapSceneOverlay(Client client, CastleWarsConfig config, CastleWarsPlugin plugin)
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
		// Taps to highlight
		for (Map.Entry<WorldPoint, Tap> entry : plugin.getHighlightTaps().entrySet())
		{
			final Tap tap = entry.getValue();

			switch (tap.getTapID())
			{
				case ObjectID.TAP:
					renderTapSceneOverlay(graphics, tap, config.getTapHighlightColor()); // new Color(-5635841): == magenta
					break;
			}
		}
		return null;
	}

	/**
	 * Render Tap highlights
	 *
	 * @param graphics
	 * @param tap
	 * @param color
	 */
	private void renderTapSceneOverlay(Graphics2D graphics, Tap tap, Color color)
	{
		if (tap.getWorldLocation().getPlane() != client.getPlane())
		{
			return;
		}

		final LocalPoint localLoc = LocalPoint.fromWorld(client, tap.getWorldLocation());
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
		final Shape objectClickBox = tap.getTap().getConvexHull();
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
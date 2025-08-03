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
package net.runelite.client.plugins.openrl.plugins.castlewars.barricade;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.plugins.castlewars.CastleWarsConfig;
import net.runelite.client.plugins.openrl.plugins.castlewars.CastleWarsPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

/**
 * Represents the tindTimer overlay that shows timers on lit barricades that are placed by the player.
 */
public class BarricadeTimerOverlay extends Overlay
{
	// The timer is low when only 25% is left.
	private static final double TIMER_LOW = 0.25; // When the timer is under a quarter left, if turns red.

	private final Client client;
	private final CastleWarsPlugin plugin;
	private final CastleWarsConfig config;

	private Color colorOpen, colorOpenBorder;
	private Color colorEmpty, colorEmptyBorder;

	@Inject
	BarricadeTimerOverlay(Client client, CastleWarsPlugin plugin, CastleWarsConfig config)
	{
		this.plugin = plugin;
		this.config = config;
		this.client = client;
		determineLayer();
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		drawTimer(graphics);
		return null;
	}

	/**
	 * Updates the timer colors.
	 */
	public void updateConfig()
	{
		colorEmptyBorder = config.getEmptyColor();
		colorEmpty = new Color(colorEmptyBorder.getRed(), colorEmptyBorder.getGreen(), colorEmptyBorder.getBlue(), 100);

		colorOpenBorder = config.getLitColor();
		colorOpen = new Color(colorOpenBorder.getRed(), colorOpenBorder.getGreen(), colorOpenBorder.getBlue(), 100);
	}

	/**
	 * Iterates over all the lit barricades that were placed
	 * draws a circle or a timer on the lit barricade, depending on the barricade state.
	 *
	 * @param graphics
	 */
	private void drawTimer(Graphics2D graphics)
	{
		for (Map.Entry<WorldPoint, Barricade> entry : plugin.getLitBarricades().entrySet())
		{
			final Barricade barricade = entry.getValue();

			switch (barricade.getState())
			{
				case LIT_BARRICADE:
					drawTimerOnBarricade(graphics, barricade, colorOpen, colorOpenBorder, colorEmpty, colorOpenBorder);
					break;
			}
		}
	}

	/**
	 * Draws a timer on a given barricade.
	 *
	 * @param graphics
	 * @param barricade The barricade on which the timer needs to be drawn
	 * @param fill The fill color of the timer
	 * @param border The border color of the timer
	 * @param fillTimeLow The fill color of the timer when it is low
	 * @param borderTimeLow The border color of the timer when it is low
	 */
	private void drawTimerOnBarricade(Graphics2D graphics, Barricade barricade, Color fill, Color border, Color fillTimeLow, Color borderTimeLow)
	{
		if (barricade.getWorldLocation().getPlane() != client.getPlane())
		{
			return;
		}

		final LocalPoint localLoc = LocalPoint.fromWorld(client, barricade.getWorldLocation());
		if (localLoc == null)
		{
			return;
		}

		final Point loc = Perspective.localToCanvas(client, localLoc, client.getPlane());
		if (loc == null)
		{
			return;
		}

		final double timeLeft = 1 - barricade.getTindTimeRelative();

		final ProgressPieComponent pie = new ProgressPieComponent();
		pie.setFill(timeLeft > TIMER_LOW ? fill : fillTimeLow);
		pie.setBorderColor(timeLeft > TIMER_LOW ? border : borderTimeLow);
		pie.setPosition(loc);
		pie.setProgress(timeLeft);
		pie.render(graphics);
	}

	/**
	 * Draws a timer on a given barricade.
	 *
	 * @param graphics
	 * @param barricade The barricade on which the timer needs to be drawn
	 * @param fill The fill color of the timer
	 * @param border The border color of the timer
	 */
	private void drawCircleOnBarricade(Graphics2D graphics, Barricade barricade, Color fill, Color border)
	{
		if (barricade.getWorldLocation().getPlane() != client.getPlane())
		{
			return;
		}

		final LocalPoint localLoc = LocalPoint.fromWorld(client, barricade.getWorldLocation());
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

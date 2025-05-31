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
package net.runelite.client.plugins.openrl.api.rs2.providers.worldmap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.awt.Rectangle;
import java.util.function.Supplier;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.worldmap.WorldMap;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.RS2Widgets;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;

public class RS2WorldMap
{
	private static final Supplier<RS2Widget> MAP_CONTAINER = () -> RS2Widgets.getWidget(InterfaceID.Worldmap.MAP_CONTAINER);

	public static WorldMap getWorldMap()
	{
		return Static.getClient().getWorldMap();
	}

	public static boolean isOpen()
	{
		return RS2Widgets.isVisible(MAP_CONTAINER.get());
	}

	public static void open(WorldMapView worldMapView)
	{
		final RS2Widget openButton = RS2Widgets.getWidget(InterfaceID.Orbs.WORLDMAP);
		if (openButton == null)
		{
			return;
		}
		openButton.interact(worldMapView.getActionIndex());
	}

	public static void close()
	{
		final RS2Widget closeButton = RS2Widgets.getWidget(InterfaceID.Worldmap.CLOSE);
		if (closeButton == null)
		{
			return;
		}
		closeButton.interact("Close");
	}

	public static WorldPoint getWorldPointAtMouse()
	{
		final Client client = Static.getClient();
		final WorldMap worldMap = client.getWorldMap();
		if (worldMap == null)
		{
			return null;
		}

		final RS2Widget mapWidget = MAP_CONTAINER.get();
		if (mapWidget == null)
		{
			return null;
		}

		final Rectangle mapBounds = mapWidget.getBounds();

		// Get map zoom (pixels per tile)
		float zoom = worldMap.getWorldMapZoom();

		// Get current map position (center in world coords)
		final Point mapCenter = worldMap.getWorldMapPosition();

		// Mouse position relative to the game canvas
		final Point mousePoint = client.getMouseCanvasPosition();
		if (mousePoint == null)
		{
			return null;
		}

		// Convert mouse position to relative position within the map widget
		int relativeX = mousePoint.getX() - mapBounds.x;
		int relativeY = mousePoint.getY() - mapBounds.y;

		// Calculate the offset in tiles from the center based on mouse position
		int xOffsetInTiles = (int) ((relativeX - (mapBounds.width / 2)) / zoom);
		int yOffsetInTiles = (int) ((relativeY - (mapBounds.height / 2)) / zoom);

		// Derive the world coordinates under the mouse cursor
		int worldX = mapCenter.getX() + xOffsetInTiles;
		int worldY = mapCenter.getY() + yOffsetInTiles;

		return new WorldPoint(worldX, worldY, client.getPlane());
	}

	@Getter
	@RequiredArgsConstructor
	public enum WorldMapView
	{
		FLOATING(1),
		FULL_SCREEN(2);

		private final int actionIndex;
	}
}
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
package net.runelite.client.plugins.openrl.api.rs2.providers.query;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.Point;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.worldmap.WorldMap;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.abstraction.AbstractQuery;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Tile;

public class RS2TileQuery extends AbstractQuery<RS2Tile, RS2TileQuery>
{
	private RS2TileQuery()
	{
	}

	public static RS2TileQuery query()
	{
		return new RS2TileQuery();
	}

	@Override
	protected List<RS2Tile> all(Predicate<? super RS2Tile> filter)
	{
		final List<RS2Tile> out = new ArrayList<>();

		final WorldView worldView = Static.getClient().getTopLevelWorldView();
		final Scene scene = worldView.getScene();
		final Tile[][][] tiles = scene.getTiles();

		final int z = worldView.getPlane();

		for (int x = 0; x < Constants.SCENE_SIZE; ++x)
		{
			for (int y = 0; y < Constants.SCENE_SIZE; ++y)
			{
				final RS2Tile tile = new RS2Tile(tiles[z][x][y]);
				if (tile != null && filter.test(tile))
				{
					out.add(tile);
				}
			}
		}

		return out;
	}

	// Results

	public RS2Tile getAt(WorldPoint worldPoint)
	{
		return getAt(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane());
	}

	public RS2Tile getAt(LocalPoint localPoint)
	{
		return new RS2Tile(Static.getClient().getScene().getTiles()[Static.getClient().getPlane()][localPoint.getSceneX()][localPoint.getSceneY()]);
	}

	public RS2Tile getAt(int worldX, int worldY, int plane)
	{
		final Client client = Static.getClient();
		final WorldView wv = client.getTopLevelWorldView();
		final Scene scene = wv.getScene();

		final int correctedX = worldX < Constants.SCENE_SIZE ? worldX + scene.getBaseX() : worldX;
		final int correctedY = worldY < Constants.SCENE_SIZE ? worldY + scene.getBaseY() : worldY;

		if (!WorldPoint.isInScene(client, correctedX, correctedY))
		{
			return null;
		}

		final int x = correctedX - scene.getBaseX();
		final int y = correctedY - scene.getBaseY();

		return new RS2Tile(scene.getTiles()[plane][x][y]);
	}

	public List<RS2Tile> getSurrounding(WorldPoint worldPoint, int radius)
	{
		final List<RS2Tile> out = new ArrayList<>();
		for (int x = -radius; x <= radius; x++)
		{
			for (int y = -radius; y <= radius; y++)
			{
				out.add(getAt(worldPoint.dx(x).dy(y)));
			}
		}

		return out;
	}

	@Deprecated
	public RS2Tile getHoveredTile()
	{
		return getSelectedSceneTile();
	}

	public RS2Tile getSelectedSceneTile()
	{
		final WorldView wv = Static.getClient().getTopLevelWorldView();
		if (wv == null)
		{
			return null;
		}
		final Tile selectedSceneTile = wv.getSelectedSceneTile();
		return selectedSceneTile != null ? new RS2Tile(selectedSceneTile) : null;
	}

	public WorldPoint getSelectedSceneWorldPoint()
	{
		final RS2Tile selectedSceneTile = getSelectedSceneTile();
		return selectedSceneTile != null ? WorldPoint.fromLocalInstance(Static.getClient(), selectedSceneTile.getLocalLocation()) : null;
	}

	public List<WorldPoint> getWorldMapTiles(int plane)
	{
		final Widget worldMap = Static.getClient().getWidget(InterfaceID.Worldmap.MAP_CONTAINER);
		if (worldMap == null)
		{
			return Collections.emptyList();
		}

		final List<WorldPoint> out = new ArrayList<>();
		final WorldMap wm = Static.getClient().getWorldMap();

		final Rectangle worldMapRect = worldMap.getBounds();

		final float pixelsPerTile = wm.getWorldMapZoom();
		final int widthInTiles = (int) Math.ceil(worldMapRect.getWidth() / pixelsPerTile);
		final int heightInTiles = (int) Math.ceil(worldMapRect.getHeight() / pixelsPerTile);

		final Point worldMapPosition = wm.getWorldMapPosition();
		final int leftX = worldMapPosition.getX() - (widthInTiles / 2);
		final int rightX = leftX + widthInTiles;
		final int topY = worldMapPosition.getY() + (heightInTiles / 2);
		final int bottomY = topY - heightInTiles;

		for (int x = leftX; x < rightX; x++)
		{
			for (int y = topY; y >= bottomY; y--)
			{
				out.add(new WorldPoint(x, y, plane));
			}
		}

		return out;
	}
}
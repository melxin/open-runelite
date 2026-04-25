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
package net.runelite.client.plugins.openrl.api.rs2.wrappers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import net.runelite.api.CollisionData;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.ItemLayer;
import net.runelite.api.MenuAction;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.SceneTileModel;
import net.runelite.api.SceneTilePaint;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.WallObject;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.api.Locatable;

@RequiredArgsConstructor
public class RS2Tile implements Tile, Locatable
{
	@NonNull
	@Getter(AccessLevel.PUBLIC)
	private final Tile tile;

	@Override
	public List<TileItem> getGroundItems()
	{
		return tile.getGroundItems();
	}

	@Override
	public DecorativeObject getDecorativeObject()
	{
		return tile.getDecorativeObject();
	}

	@Override
	public GameObject[] getGameObjects()
	{
		return tile.getGameObjects();
	}

	@Override
	public ItemLayer getItemLayer()
	{
		return tile.getItemLayer();
	}

	@Override
	public GroundObject getGroundObject()
	{
		return tile.getGroundObject();
	}

	@Override
	public void setGroundObject(GroundObject groundObject)
	{
		tile.setGroundObject(groundObject);
	}

	@Override
	public WallObject getWallObject()
	{
		return tile.getWallObject();
	}

	@Override
	public SceneTilePaint getSceneTilePaint()
	{
		return tile.getSceneTilePaint();
	}

	@Override
	public void setSceneTilePaint(SceneTilePaint paint)
	{
		tile.setSceneTilePaint(paint);
	}

	@Override
	public SceneTileModel getSceneTileModel()
	{
		return tile.getSceneTileModel();
	}

	@Override
	public void setSceneTileModel(SceneTileModel model)
	{
		tile.setSceneTileModel(model);
	}

	@Override
	public WorldPoint getWorldLocation()
	{
		return tile.getWorldLocation();
	}

	@Override
	public Point getSceneLocation()
	{
		return tile.getSceneLocation();
	}

	@Override
	public LocalPoint getLocalLocation()
	{
		return tile.getLocalLocation();
	}

	@Override
	public int getPlane()
	{
		return tile.getPlane();
	}

	@Override
	public int getRenderLevel()
	{
		return tile.getRenderLevel();
	}

	@Override
	public Tile getBridge()
	{
		return tile.getBridge();
	}

	public int getX()
	{
		return getSceneLocation().getX();
	}

	public int getY()
	{
		return getSceneLocation().getY();
	}

	public int getWorldX()
	{
		return getX() + Static.getClient().getTopLevelWorldView().getScene().getBaseX();
	}

	public int getWorldY()
	{
		return getY() + Static.getClient().getTopLevelWorldView().getScene().getBaseY();
	}

	public Polygon getCanvasTilePoly()
	{
		return Perspective.getCanvasTilePoly(Static.getClient(), getLocalLocation());
	}

	public Rectangle getClickPoint()
	{
		final Polygon canvasTilePoly = getCanvasTilePoly();
		return canvasTilePoly == null ? null : canvasTilePoly.getBounds();
	}

	public void walkHere()
	{
		final Rectangle clickPoint = getClickPoint();
		if (clickPoint == null)
		{
			return;
		}

		Static.getEventBus().post(new MenuAutomated(getX(), getY(), MenuAction.WALK, 0, -1, WorldView.TOPLEVEL, "Walk here", "", clickPoint.x, clickPoint.y));
	}

	public List<RS2Tile> pathTo(Tile other)
	{
		int z = this.getPlane();
		if (z != other.getPlane())
		{
			return null;
		}

		CollisionData[] collisionData = Static.getClient().getCollisionMaps();
		if (collisionData == null)
		{
			return null;
		}

		int[][] directions = new int[128][128];
		int[][] distances = new int[128][128];
		int[] bufferX = new int[4096];
		int[] bufferY = new int[4096];

		// Initialise directions and distances
		for (int i = 0; i < 128; ++i)
		{
			for (int j = 0; j < 128; ++j)
			{
				directions[i][j] = 0;
				distances[i][j] = Integer.MAX_VALUE;
			}
		}

		Point p1 = this.getSceneLocation();
		Point p2 = other.getSceneLocation();

		int middleX = p1.getX();
		int middleY = p1.getY();
		int currentX = middleX;
		int currentY = middleY;
		int offsetX = 64;
		int offsetY = 64;
		// Initialise directions and distances for starting tile
		directions[offsetX][offsetY] = 99;
		distances[offsetX][offsetY] = 0;
		int index1 = 0;
		bufferX[0] = currentX;
		int index2 = 1;
		bufferY[0] = currentY;
		int[][] collisionDataFlags = collisionData[z].getFlags();

		boolean isReachable = false;

		while (index1 != index2)
		{
			currentX = bufferX[index1];
			currentY = bufferY[index1];
			index1 = index1 + 1 & 4095;
			// currentX is for the local coordinate while currentMapX is for the index in the directions and distances arrays
			int currentMapX = currentX - middleX + offsetX;
			int currentMapY = currentY - middleY + offsetY;
			if ((currentX == p2.getX()) && (currentY == p2.getY()))
			{
				isReachable = true;
				break;
			}

			int currentDistance = distances[currentMapX][currentMapY] + 1;
			if (currentMapX > 0 && directions[currentMapX - 1][currentMapY] == 0 && (collisionDataFlags[currentX - 1][currentY] & 19136776) == 0)
			{
				// Able to move 1 tile west
				bufferX[index2] = currentX - 1;
				bufferY[index2] = currentY;
				index2 = index2 + 1 & 4095;
				directions[currentMapX - 1][currentMapY] = 2;
				distances[currentMapX - 1][currentMapY] = currentDistance;
			}

			if (currentMapX < 127 && directions[currentMapX + 1][currentMapY] == 0 && (collisionDataFlags[currentX + 1][currentY] & 19136896) == 0)
			{
				// Able to move 1 tile east
				bufferX[index2] = currentX + 1;
				bufferY[index2] = currentY;
				index2 = index2 + 1 & 4095;
				directions[currentMapX + 1][currentMapY] = 8;
				distances[currentMapX + 1][currentMapY] = currentDistance;
			}

			if (currentMapY > 0 && directions[currentMapX][currentMapY - 1] == 0 && (collisionDataFlags[currentX][currentY - 1] & 19136770) == 0)
			{
				// Able to move 1 tile south
				bufferX[index2] = currentX;
				bufferY[index2] = currentY - 1;
				index2 = index2 + 1 & 4095;
				directions[currentMapX][currentMapY - 1] = 1;
				distances[currentMapX][currentMapY - 1] = currentDistance;
			}

			if (currentMapY < 127 && directions[currentMapX][currentMapY + 1] == 0 && (collisionDataFlags[currentX][currentY + 1] & 19136800) == 0)
			{
				// Able to move 1 tile north
				bufferX[index2] = currentX;
				bufferY[index2] = currentY + 1;
				index2 = index2 + 1 & 4095;
				directions[currentMapX][currentMapY + 1] = 4;
				distances[currentMapX][currentMapY + 1] = currentDistance;
			}

			if (currentMapX > 0 && currentMapY > 0 && directions[currentMapX - 1][currentMapY - 1] == 0 && (collisionDataFlags[currentX - 1][currentY - 1] & 19136782) == 0 && (collisionDataFlags[currentX - 1][currentY] & 19136776) == 0 && (collisionDataFlags[currentX][currentY - 1] & 19136770) == 0)
			{
				// Able to move 1 tile south-west
				bufferX[index2] = currentX - 1;
				bufferY[index2] = currentY - 1;
				index2 = index2 + 1 & 4095;
				directions[currentMapX - 1][currentMapY - 1] = 3;
				distances[currentMapX - 1][currentMapY - 1] = currentDistance;
			}

			if (currentMapX > 0 && currentMapY < 127 && directions[currentMapX - 1][currentMapY + 1] == 0 && (collisionDataFlags[currentX - 1][currentY + 1] & 19136824) == 0 && (collisionDataFlags[currentX - 1][currentY] & 19136776) == 0 && (collisionDataFlags[currentX][currentY + 1] & 19136800) == 0)
			{
				// Able to move 1 tile north-west
				bufferX[index2] = currentX - 1;
				bufferY[index2] = currentY + 1;
				index2 = index2 + 1 & 4095;
				directions[currentMapX - 1][currentMapY + 1] = 6;
				distances[currentMapX - 1][currentMapY + 1] = currentDistance;
			}

			if (currentMapX < 127 && currentMapY > 0 && directions[currentMapX + 1][currentMapY - 1] == 0 && (collisionDataFlags[currentX + 1][currentY - 1] & 19136899) == 0 && (collisionDataFlags[currentX + 1][currentY] & 19136896) == 0 && (collisionDataFlags[currentX][currentY - 1] & 19136770) == 0)
			{
				// Able to move 1 tile south-east
				bufferX[index2] = currentX + 1;
				bufferY[index2] = currentY - 1;
				index2 = index2 + 1 & 4095;
				directions[currentMapX + 1][currentMapY - 1] = 9;
				distances[currentMapX + 1][currentMapY - 1] = currentDistance;
			}

			if (currentMapX < 127 && currentMapY < 127 && directions[currentMapX + 1][currentMapY + 1] == 0 && (collisionDataFlags[currentX + 1][currentY + 1] & 19136992) == 0 && (collisionDataFlags[currentX + 1][currentY] & 19136896) == 0 && (collisionDataFlags[currentX][currentY + 1] & 19136800) == 0)
			{
				// Able to move 1 tile north-east
				bufferX[index2] = currentX + 1;
				bufferY[index2] = currentY + 1;
				index2 = index2 + 1 & 4095;
				directions[currentMapX + 1][currentMapY + 1] = 12;
				distances[currentMapX + 1][currentMapY + 1] = currentDistance;
			}
		}
		if (!isReachable)
		{
			// Try find a different reachable tile in the 21x21 area around the target tile, as close as possible to the target tile
			int upperboundDistance = Integer.MAX_VALUE;
			int pathLength = Integer.MAX_VALUE;
			int checkRange = 10;
			int approxDestinationX = p2.getX();
			int approxDestinationY = p2.getY();
			for (int i = approxDestinationX - checkRange; i <= checkRange + approxDestinationX; ++i)
			{
				for (int j = approxDestinationY - checkRange; j <= checkRange + approxDestinationY; ++j)
				{
					int currentMapX = i - middleX + offsetX;
					int currentMapY = j - middleY + offsetY;
					if (currentMapX >= 0 && currentMapY >= 0 && currentMapX < 128 && currentMapY < 128 && distances[currentMapX][currentMapY] < 100)
					{
						int deltaX = 0;
						if (i < approxDestinationX)
						{
							deltaX = approxDestinationX - i;
						}
						else if (i > approxDestinationX)
						{
							deltaX = i - (approxDestinationX);
						}

						int deltaY = 0;
						if (j < approxDestinationY)
						{
							deltaY = approxDestinationY - j;
						}
						else if (j > approxDestinationY)
						{
							deltaY = j - (approxDestinationY);
						}

						int distanceSquared = deltaX * deltaX + deltaY * deltaY;
						if (distanceSquared < upperboundDistance || distanceSquared == upperboundDistance && distances[currentMapX][currentMapY] < pathLength)
						{
							upperboundDistance = distanceSquared;
							pathLength = distances[currentMapX][currentMapY];
							currentX = i;
							currentY = j;
						}
					}
				}
			}
			if (upperboundDistance == Integer.MAX_VALUE)
			{
				// No path found
				return null;
			}
		}

		// Getting path from directions and distances
		bufferX[0] = currentX;
		bufferY[0] = currentY;
		int index = 1;
		int directionNew;
		int directionOld;
		for (directionNew = directionOld = directions[currentX - middleX + offsetX][currentY - middleY + offsetY]; p1.getX() != currentX || p1.getY() != currentY; directionNew = directions[currentX - middleX + offsetX][currentY - middleY + offsetY])
		{
			if (directionNew != directionOld)
			{
				// "Corner" of the path --> new checkpoint tile
				directionOld = directionNew;
				bufferX[index] = currentX;
				bufferY[index++] = currentY;
			}

			if ((directionNew & 2) != 0)
			{
				++currentX;
			}
			else if ((directionNew & 8) != 0)
			{
				--currentX;
			}

			if ((directionNew & 1) != 0)
			{
				++currentY;
			}
			else if ((directionNew & 4) != 0)
			{
				--currentY;
			}
		}

		int checkpointTileNumber = 1;
		final Tile[][][] tiles = Static.getClient().getScene().getTiles();
		final List<RS2Tile> checkpointTiles = new ArrayList<>();
		while (index-- > 0)
		{
			checkpointTiles.add(new RS2Tile(tiles[this.getPlane()][bufferX[index]][bufferY[index]]));
			if (checkpointTileNumber == 25)
			{
				// Pathfinding only supports up to the 25 first checkpoint tiles
				break;
			}
			checkpointTileNumber++;
		}
		return checkpointTiles;
	}
}
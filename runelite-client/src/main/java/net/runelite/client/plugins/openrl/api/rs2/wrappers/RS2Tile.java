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
import java.util.List;
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

		Static.getEventBus().post(new MenuAutomated(getX(), getY(), MenuAction.WALK, 0, -1, -1, "Walk here", "", clickPoint.x, clickPoint.y));
	}
}
package net.runelite.client.plugins.openrl.api.rs2.scene;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import java.util.List;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.ItemLayer;
import net.runelite.api.Point;
import net.runelite.api.SceneTileModel;
import net.runelite.api.SceneTilePaint;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.WallObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

@RequiredArgsConstructor
public class RS2Tile implements Tile
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
	public SceneTileModel getSceneTileModel()
	{
		return tile.getSceneTileModel();
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
}
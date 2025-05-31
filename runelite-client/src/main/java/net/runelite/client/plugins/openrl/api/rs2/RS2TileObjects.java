package net.runelite.client.plugins.openrl.api.rs2;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.Tile;
import net.runelite.api.WallObject;
import net.runelite.api.coords.WorldPoint;

public class RS2TileObjects
{
	private static final RS2TileObjects TILE_OBJECTS = new RS2TileObjects();

	private RS2TileObjects()
	{
	}

	public static List<RS2TileObject> getAll()
	{
		return getAll(x -> true);
	}

	public static List<RS2TileObject> getAll(Predicate<RS2TileObject> filter)
	{
		return TILE_OBJECTS.all(filter);
	}

	private static List<RS2TileObject> getTileObjects(Tile tile)
	{
		List<RS2TileObject> out = new ArrayList<>();
		if (tile == null)
		{
			return out;
		}

		DecorativeObject dec = tile.getDecorativeObject();
		if (dec != null && dec.getId() != -1)
		{
			out.add(new RS2TileObject(dec));
		}

		WallObject wall = tile.getWallObject();
		if (wall != null && wall.getId() != -1)
		{
			out.add(new RS2TileObject(wall));
		}

		GroundObject grnd = tile.getGroundObject();
		if (grnd != null && grnd.getId() != -1)
		{
			out.add(new RS2TileObject(grnd));
		}

		GameObject[] gameObjects = tile.getGameObjects();
		if (gameObjects != null)
		{
			for (GameObject gameObject : gameObjects)
			{
				if (gameObject == null || gameObject.getId() == -1)
				{
					continue;
				}

				out.add(new RS2TileObject(gameObject));
			}
		}

		return out;
	}

	protected List<RS2TileObject> all(Predicate<? super RS2TileObject> filter)
	{
		final List<RS2TileObject> out = new ArrayList<>();

		for (RS2Tile tile : RS2Tiles.getAll())
		{
			out.addAll(getTileObjects(tile.getTile()));
		}

		return out.stream()
			.filter(filter)
			.collect(Collectors.toList());
	}

	protected List<RS2TileObject> at(RS2Tile tile, Predicate<? super RS2TileObject> filter)
	{
		return getTileObjects(tile.getTile()).stream()
			.filter(filter)
			.collect(Collectors.toList());
	}

	public static List<RS2TileObject> getAt(WorldPoint worldPoint, Predicate<? super RS2TileObject> filter)
	{
		return TILE_OBJECTS.at(RS2Tiles.getAt(worldPoint), filter);
	}

	public static List<RS2TileObject> getAt(int x, int y, int z, Predicate<? super RS2TileObject> filter)
	{
		return TILE_OBJECTS.at(RS2Tiles.getAt(x, y, z), filter);
	}
}
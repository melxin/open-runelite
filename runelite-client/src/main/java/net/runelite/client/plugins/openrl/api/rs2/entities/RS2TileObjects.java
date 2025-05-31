package net.runelite.client.plugins.openrl.api.rs2.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.Tile;
import net.runelite.api.WallObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.api.rs2.scene.RS2Tile;
import net.runelite.client.plugins.openrl.api.rs2.scene.RS2Tiles;

public class RS2TileObjects
{
	private static final RS2TileObjects TILE_OBJECTS = new RS2TileObjects();

	private RS2TileObjects()
	{
	}

	protected List<RS2TileObject> all(Predicate<? super RS2TileObject> filter)
	{
		return RS2Tiles.getAll().stream()
			.flatMap(tile -> getTileObjects(tile.getTile()).stream())
			.filter(filter)
			.collect(Collectors.toList());
	}

	private static List<RS2TileObject> getTileObjects(Tile tile)
	{
		final List<RS2TileObject> out = new ArrayList<>();
		if (tile == null)
		{
			return out;
		}

		final DecorativeObject dec = tile.getDecorativeObject();
		if (dec != null && dec.getId() != -1)
		{
			out.add(new RS2TileObject(dec));
		}

		final WallObject wall = tile.getWallObject();
		if (wall != null && wall.getId() != -1)
		{
			out.add(new RS2TileObject(wall));
		}

		final GroundObject grnd = tile.getGroundObject();
		if (grnd != null && grnd.getId() != -1)
		{
			out.add(new RS2TileObject(grnd));
		}

		final GameObject[] gameObjects = tile.getGameObjects();
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

	public static List<RS2TileObject> getAll(Predicate<RS2TileObject> filter)
	{
		return TILE_OBJECTS.all(filter);
	}

	public static List<RS2TileObject> getAll()
	{
		return TILE_OBJECTS.all(x -> true);
	}

	public static List<RS2TileObject> getAll(int... ids)
	{
		final Set<Integer> idSet = Arrays.stream(ids).boxed().collect(Collectors.toSet());
		return TILE_OBJECTS.all(x -> idSet.contains(x.getId()));
	}

	public static List<RS2TileObject> getAll(String... names)
	{
		final Set<String> nameSet = new HashSet<>(Arrays.asList(names));
		return TILE_OBJECTS.all(x -> nameSet.contains(x.getName()));
	}

	public static RS2TileObject getFirst(Predicate<RS2TileObject> filter)
	{
		return TILE_OBJECTS.all(filter).stream().findFirst().orElse(null);
	}

	public static RS2TileObject getFirst(int... ids)
	{
		return getAll(ids).stream().findFirst().orElse(null);
	}

	public static RS2TileObject getFirst(String... names)
	{
		return getAll(names).stream().findFirst().orElse(null);
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
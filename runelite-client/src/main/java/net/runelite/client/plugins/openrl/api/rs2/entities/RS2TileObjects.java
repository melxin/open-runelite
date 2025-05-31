package net.runelite.client.plugins.openrl.api.rs2.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import net.runelite.api.coords.WorldArea;
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
		return TILE_OBJECTS.all(Predicates.ids(ids));
	}

	public static List<RS2TileObject> getAll(String... names)
	{
		return TILE_OBJECTS.all(Predicates.names(names));
	}

	protected RS2TileObject nearest(WorldPoint to, Predicate<RS2TileObject> filter)
	{
		return TILE_OBJECTS.all(filter).stream().min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo2D(to))).orElse(null);
	}

	public static RS2TileObject getNearest(WorldPoint to, Predicate<RS2TileObject> filter)
	{
		return TILE_OBJECTS.nearest(to, filter);
	}

	public static RS2TileObject getNearest(WorldPoint to, int... ids)
	{
		return TILE_OBJECTS.nearest(to, Predicates.ids(ids));
	}

	public static RS2TileObject getNearest(WorldPoint to, String... names)
	{
		return TILE_OBJECTS.nearest(to, Predicates.names(names));
	}

	public static RS2TileObject getNearest(Predicate<RS2TileObject> filter)
	{
		return TILE_OBJECTS.nearest(RS2Players.getLocal().getWorldLocation(), filter);
	}

	public static RS2TileObject getNearest()
	{
		return getNearest(x -> true);
	}

	public static RS2TileObject getNearest(int... ids)
	{
		return getNearest(Predicates.ids(ids));
	}

	public static RS2TileObject getNearest(String... names)
	{
		return getNearest(Predicates.names(names));
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

	public static List<RS2TileObject> getAt(RS2Tile tile, Predicate<RS2TileObject> filter)
	{
		if (tile == null)
		{
			return Collections.emptyList();
		}

		return TILE_OBJECTS.at(tile, filter);
	}

	public static List<RS2TileObject> getAt(RS2Tile tile, int... ids)
	{
		return TILE_OBJECTS.at(tile, Predicates.ids(ids));
	}

	public static List<RS2TileObject> getAt(RS2Tile tile, String... names)
	{
		return TILE_OBJECTS.at(tile, Predicates.names(names));
	}

	public static List<RS2TileObject> getAt(WorldPoint worldPoint, Predicate<RS2TileObject> filter)
	{
		return TILE_OBJECTS.at(RS2Tiles.getAt(worldPoint), filter);
	}

	public static List<RS2TileObject> getAt(WorldPoint worldPoint, int... ids)
	{
		return TILE_OBJECTS.at(RS2Tiles.getAt(worldPoint), Predicates.ids(ids));
	}

	public static List<RS2TileObject> getAt(WorldPoint worldPoint, String... names)
	{
		return TILE_OBJECTS.at(RS2Tiles.getAt(worldPoint), Predicates.names(names));
	}

	public static List<RS2TileObject> getAt(int worldX, int worldY, int plane, Predicate<RS2TileObject> filter)
	{
		return TILE_OBJECTS.at(RS2Tiles.getAt(worldX, worldY, plane), filter);
	}

	public static List<RS2TileObject> getAt(int worldX, int worldY, int plane, int... ids)
	{
		return TILE_OBJECTS.at(RS2Tiles.getAt(worldX, worldY, plane), Predicates.ids(ids));
	}

	public static List<RS2TileObject> getAt(int worldX, int worldY, int plane, String... names)
	{
		return TILE_OBJECTS.at(RS2Tiles.getAt(worldX, worldY, plane), Predicates.names(names));
	}

	public static RS2TileObject getFirstAt(RS2Tile tile, Predicate<RS2TileObject> filter)
	{
		return getAt(tile, filter).stream().findFirst().orElse(null);
	}

	public static RS2TileObject getFirstAt(RS2Tile tile, int... ids)
	{
		return getAt(tile, ids).stream().findFirst().orElse(null);
	}

	public static RS2TileObject getFirstAt(RS2Tile tile, String... names)
	{
		return getAt(tile, names).stream().findFirst().orElse(null);
	}

	public static RS2TileObject getFirstAt(WorldPoint worldPoint, Predicate<RS2TileObject> filter)
	{
		return getAt(worldPoint, filter).stream().findFirst().orElse(null);
	}

	public static RS2TileObject getFirstAt(WorldPoint worldPoint, int... ids)
	{
		return getAt(worldPoint, ids).stream().findFirst().orElse(null);
	}

	public static RS2TileObject getFirstAt(WorldPoint worldPoint, String... names)
	{
		return getAt(worldPoint, names).stream().findFirst().orElse(null);
	}

	public static RS2TileObject getFirstAt(int worldX, int worldY, int plane, Predicate<RS2TileObject> filter)
	{
		return getAt(worldX, worldY, plane, filter).stream().findFirst().orElse(null);
	}

	public static RS2TileObject getFirstAt(int worldX, int worldY, int plane, int... ids)
	{
		return getAt(worldX, worldY, plane, ids).stream().findFirst().orElse(null);
	}

	public static RS2TileObject getFirstAt(int worldX, int worldY, int plane, String... names)
	{
		return getAt(worldX, worldY, plane, names).stream().findFirst().orElse(null);
	}

	protected List<RS2TileObject> surrounding(int worldX, int worldY, int plane, int radius, Predicate<? super RS2TileObject> filter)
	{
		List<RS2TileObject> out = new ArrayList<>();
		for (int x = -radius; x <= radius; x++)
		{
			for (int y = -radius; y <= radius; y++)
			{
				out.addAll(at(RS2Tiles.getAt(worldX + x, worldY + y, plane), filter));
			}
		}

		return out;
	}

	public static RS2TileObject getFirstSurrounding(int worldX, int worldY, int plane, int radius, Predicate<RS2TileObject> filter)
	{
		return TILE_OBJECTS.surrounding(worldX, worldY, plane, radius, filter).stream().findFirst().orElse(null);
	}

	public static RS2TileObject getFirstSurrounding(int worldX, int worldY, int plane, int radius, int... ids)
	{
		return TILE_OBJECTS.surrounding(worldX, worldY, plane, radius, Predicates.ids(ids)).stream().findFirst().orElse(null);
	}

	public static RS2TileObject getFirstSurrounding(int worldX, int worldY, int plane, int radius, String... names)
	{
		return TILE_OBJECTS.surrounding(worldX, worldY, plane, radius, Predicates.names(names)).stream().findFirst().orElse(null);
	}

	public static RS2TileObject getFirstSurrounding(WorldPoint worldPoint, int radius, Predicate<RS2TileObject> filter)
	{
		return getSurrounding(worldPoint, radius, filter).stream().findFirst().orElse(null);
	}

	public static RS2TileObject getFirstSurrounding(WorldPoint worldPoint, int radius, int... ids)
	{
		return getSurrounding(worldPoint, radius, ids).stream().findFirst().orElse(null);
	}

	public static RS2TileObject getFirstSurrounding(WorldPoint worldPoint, int radius, String... names)
	{
		return getSurrounding(worldPoint, radius, names).stream().findFirst().orElse(null);
	}

	public static RS2TileObject getFirstSurrounding(RS2Tile tile, int radius, Predicate<RS2TileObject> filter)
	{
		return getSurrounding(tile, radius, filter).stream().findFirst().orElse(null);
	}

	public static RS2TileObject getFirstSurrounding(RS2Tile tile, int radius, int... ids)
	{
		return getSurrounding(tile, radius, ids).stream().findFirst().orElse(null);
	}

	public static RS2TileObject getFirstSurrounding(RS2Tile tile, int radius, String... names)
	{
		return getSurrounding(tile, radius, names).stream().findFirst().orElse(null);
	}

	public static List<RS2TileObject> getSurrounding(int worldX, int worldY, int plane, int radius, Predicate<RS2TileObject> filter)
	{
		return TILE_OBJECTS.surrounding(worldX, worldY, plane, radius, filter);
	}

	public static List<RS2TileObject> getSurrounding(int worldX, int worldY, int plane, int radius, int... ids)
	{
		return TILE_OBJECTS.surrounding(worldX, worldY, plane, radius, Predicates.ids(ids));
	}

	public static List<RS2TileObject> getSurrounding(int worldX, int worldY, int plane, int radius, String... names)
	{
		return TILE_OBJECTS.surrounding(worldX, worldY, plane, radius, Predicates.names(names));
	}

	public static List<RS2TileObject> getSurrounding(WorldPoint worldPoint, int radius, Predicate<RS2TileObject> filter)
	{
		return getSurrounding(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane(), radius, filter);
	}

	public static List<RS2TileObject> getSurrounding(WorldPoint worldPoint, int radius, int... ids)
	{
		return getSurrounding(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane(), radius, Predicates.ids(ids));
	}

	public static List<RS2TileObject> getSurrounding(WorldPoint worldPoint, int radius, String... names)
	{
		return getSurrounding(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane(), radius, Predicates.names(names));
	}

	public static List<RS2TileObject> getSurrounding(RS2Tile tile, int radius, Predicate<RS2TileObject> filter)
	{
		return getSurrounding(tile.getWorldX(), tile.getWorldY(), tile.getPlane(), radius, filter);
	}

	public static List<RS2TileObject> getSurrounding(RS2Tile tile, int radius, int... ids)
	{
		return getSurrounding(tile.getWorldX(), tile.getWorldY(), tile.getPlane(), radius, ids);
	}

	public static List<RS2TileObject> getSurrounding(RS2Tile tile, int radius, String... names)
	{
		return getSurrounding(tile.getWorldX(), tile.getWorldY(), tile.getPlane(), radius, names);
	}

	protected List<RS2TileObject> in(WorldArea area, Predicate<? super RS2TileObject> filter)
	{
		List<RS2TileObject> out = new ArrayList<>();
		for (WorldPoint worldPoint : area.toWorldPointList())
		{
			out.addAll(at(RS2Tiles.getAt(worldPoint), filter));
		}

		return out;
	}

	public static List<RS2TileObject> within(WorldArea area, Predicate<RS2TileObject> filter)
	{
		return TILE_OBJECTS.in(area, filter);
	}

	protected List<RS2TileObject> within(WorldArea area, int... ids)
	{
		return TILE_OBJECTS.in(area, Predicates.ids(ids));
	}

	protected List<RS2TileObject> within(WorldArea area, String... names)
	{
		return TILE_OBJECTS.in(area, Predicates.names(names));
	}

	private static class Predicates
	{
		private static Predicate<RS2TileObject> ids(int... ids)
		{
			final Set<Integer> idSet = Arrays.stream(ids).boxed().collect(Collectors.toSet());
			return x -> idSet.contains(x.getId());
		}

		private static Predicate<RS2TileObject> names(String... names)
		{
			final Set<String> nameSet = new HashSet<>(Arrays.asList(names));
			return x -> nameSet.contains(x.getName());
		}
	}
}
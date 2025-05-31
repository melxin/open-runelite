package net.runelite.client.plugins.openrl.api.rs2.providers.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static java.lang.Math.abs;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.WallObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.RS2TileObjectQuery;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Tile;
import net.runelite.client.plugins.openrl.api.rs2.providers.scene.RS2Tiles;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2TileObject;

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

	public static RS2TileObjectQuery query()
	{
		return RS2TileObjectQuery.query();
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

	protected RS2TileObject farthest(WorldPoint to, Predicate<RS2TileObject> filter)
	{
		return TILE_OBJECTS.all(filter).stream().max(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo2D(to))).orElse(null);
	}

	public static RS2TileObject getFarthest(WorldPoint to, Predicate<RS2TileObject> filter)
	{
		return TILE_OBJECTS.farthest(to, filter);
	}

	public static RS2TileObject getFarthest(WorldPoint to, int... ids)
	{
		return TILE_OBJECTS.farthest(to, Predicates.ids(ids));
	}

	public static RS2TileObject getFarthest(WorldPoint to, String... names)
	{
		return TILE_OBJECTS.farthest(to, Predicates.names(names));
	}

	public static RS2TileObject getFarthest(Predicate<RS2TileObject> filter)
	{
		return TILE_OBJECTS.farthest(RS2Players.getLocal().getWorldLocation(), filter);
	}

	public static RS2TileObject getFarthest()
	{
		return getFarthest(x -> true);
	}

	public static RS2TileObject getFarthest(int... ids)
	{
		return getFarthest(Predicates.ids(ids));
	}

	public static RS2TileObject getFarthest(String... names)
	{
		return getFarthest(Predicates.names(names));
	}

	public static RS2TileObject getFirst(Predicate<RS2TileObject> filter)
	{
		return TILE_OBJECTS.all(filter).stream().findFirst().orElse(null);
	}

	public static RS2TileObject getFirst()
	{
		return getFirst(x -> true);
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
		final List<RS2TileObject> out = new ArrayList<>();
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
		final List<RS2TileObject> out = new ArrayList<>();
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

	public static List<RS2TileObject> within(WorldArea area, int... ids)
	{
		return TILE_OBJECTS.in(area, Predicates.ids(ids));
	}

	public static List<RS2TileObject> within(WorldArea area, String... names)
	{
		return TILE_OBJECTS.in(area, Predicates.names(names));
	}

	public static List<RS2TileObject> within(WorldArea area)
	{
		return TILE_OBJECTS.in(area, x -> true);
	}

	public static List<RS2TileObject> withinArea(WorldPoint from, int area, Predicate<RS2TileObject> filter)
	{
		return TILE_OBJECTS.all(x -> filter.test(x) && abs(x.getWorldLocation().getX() - from.getX()) <= area && abs(x.getWorldLocation().getY() - from.getY()) <= area);
	}

	public static List<RS2TileObject> withinArea(WorldPoint from, int area, int... ids)
	{
		return withinArea(from, area, Predicates.ids(ids));
	}

	public static List<RS2TileObject> withinArea(WorldPoint from, int area, String... names)
	{
		return withinArea(from, area, Predicates.names(names));
	}

	public static List<RS2TileObject> withinArea(WorldPoint from, int area)
	{
		return withinArea(from, area, x -> true);
	}

	public static List<RS2TileObject> withinArea(LocalPoint from, int area, Predicate<RS2TileObject> filter)
	{
		return TILE_OBJECTS.all(x -> filter.test(x) && abs(x.getLocalLocation().getX() - from.getX()) <= area && abs(x.getLocalLocation().getY() - from.getY()) <= area);
	}

	public static List<RS2TileObject> withinArea(LocalPoint from, int area, int... ids)
	{
		return withinArea(from, area, Predicates.ids(ids));
	}

	public static List<RS2TileObject> withinArea(LocalPoint from, int area, String... names)
	{
		return withinArea(from, area, Predicates.names(names));
	}

	public static List<RS2TileObject> withinArea(LocalPoint from, int area)
	{
		return withinArea(from, area, x -> true);
	}

	public static List<RS2TileObject> withinDistance(WorldPoint to, int distance, Predicate<RS2TileObject> filter)
	{
		return TILE_OBJECTS.all(x -> filter.test(x) && x.getWorldLocation().distanceTo2D(to) <= distance);
	}

	public static List<RS2TileObject> withinDistance(WorldPoint to, int distance, int... ids)
	{
		return withinDistance(to, distance, Predicates.ids(ids));
	}

	public static List<RS2TileObject> withinDistance(WorldPoint to, int distance, String... names)
	{
		return withinDistance(to, distance, Predicates.names(names));
	}

	public static List<RS2TileObject> withinDistance(WorldPoint to, int distance)
	{
		return withinDistance(to, distance, x -> true);
	}

	public static List<RS2TileObject> withinDistance(LocalPoint to, int distance, Predicate<RS2TileObject> filter)
	{
		return TILE_OBJECTS.all(x -> filter.test(x) && x.getLocalLocation().distanceTo(to) <= distance);
	}

	public static List<RS2TileObject> withinDistance(LocalPoint to, int distance, int... ids)
	{
		return withinDistance(to, distance, Predicates.ids(ids));
	}

	public static List<RS2TileObject> withinDistance(LocalPoint to, int distance, String... names)
	{
		return withinDistance(to, distance, Predicates.names(names));
	}

	public static List<RS2TileObject> withinDistance(LocalPoint to, int distance)
	{
		return withinDistance(to, distance, x -> true);
	}

	protected List<RS2TileObject> sort(Predicate<RS2TileObject> filter, Comparator<RS2TileObject> comparator)
	{
		return TILE_OBJECTS.all(filter).stream()
			.sorted(comparator)
			.collect(Collectors.toList());
	}

	public static List<RS2TileObject> getSorted(Predicate<RS2TileObject> filter, Comparator<RS2TileObject> comparator)
	{
		return TILE_OBJECTS.sort(filter, comparator);
	}

	public static List<RS2TileObject> getSorted(Comparator<RS2TileObject> comparator, int... ids)
	{
		return getSorted(Predicates.ids(ids), comparator);
	}

	public static List<RS2TileObject> getSorted(Comparator<RS2TileObject> comparator, String... names)
	{
		return getSorted(Predicates.names(names), comparator);
	}

	public static List<RS2TileObject> getSorted(Comparator<RS2TileObject> comparator)
	{
		return getSorted(x -> true, comparator);
	}

	public static List<RS2TileObject> getSortedByDistance(WorldPoint to, Predicate<RS2TileObject> filter)
	{
		return getSorted(filter, Comparator.comparingInt(x -> x.getWorldLocation().distanceTo2D(to)));
	}

	public static List<RS2TileObject> getSortedByDistance(WorldPoint to, int... ids)
	{
		return getSortedByDistance(to, Predicates.ids(ids));
	}

	public static List<RS2TileObject> getSortedByDistance(WorldPoint to, String... names)
	{
		return getSortedByDistance(to, Predicates.names(names));
	}

	public static List<RS2TileObject> getSortedByDistance(WorldPoint to)
	{
		return getSortedByDistance(to, x -> true);
	}

	public static List<RS2TileObject> getByType(Predicate<RS2TileObject> filter, Class<? extends TileObject> type)
	{
		return TILE_OBJECTS.all(x -> filter.test(x) && type.isInstance(x.getTileObject()));
	}

	public static List<RS2TileObject> getByType(Class<? extends TileObject> type)
	{
		return getByType(x -> true, type);
	}

	public static List<RS2TileObject> actionEquals(Predicate<RS2TileObject> filter, String... actions)
	{
		return TILE_OBJECTS.all(Predicates.actions(actions).and(x -> filter.test(x)));
	}

	public static List<RS2TileObject> actionEquals(String... actions)
	{
		return actionEquals(x -> true, actions);
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

		private static Predicate<RS2TileObject> actions(String... actions)
		{
			final Set<String> actionSet = new HashSet<>(Arrays.asList(actions));
			return x ->
			{
				final String[] objectActions = x.getActions();
				if (objectActions == null)
				{
					return false;
				}

				for (String objectAction : objectActions)
				{
					if (objectAction != null && actionSet.contains(objectAction))
					{
						return true;
					}
				}
				return false;
			};
		}
	}
}
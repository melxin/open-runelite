package net.runelite.client.plugins.openrl.api.rs2.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.api.rs2.scene.RS2Tile;
import net.runelite.client.plugins.openrl.api.rs2.scene.RS2Tiles;

public class RS2TileItems
{
	private static final RS2TileItems TILE_ITEMS = new RS2TileItems();

	private RS2TileItems()
	{
	}

	protected List<RS2TileItem> all(Predicate<? super RS2TileItem> filter)
	{
		return RS2Tiles.getAll().stream()
			.flatMap(tile -> at(tile, filter).stream())
			.collect(Collectors.toList());
	}

	protected List<RS2TileItem> at(RS2Tile tile, Predicate<? super RS2TileItem> filter)
	{
		final List<RS2TileItem> out = new ArrayList<>();
		if (tile == null)
		{
			return out;
		}

		if (tile.getGroundItems() != null)
		{
			for (TileItem item : tile.getGroundItems())
			{
				if (item == null || item.getId() == -1)
				{
					continue;
				}

				final RS2TileItem rs2TileItem = new RS2TileItem(item);
				if (!filter.test(rs2TileItem))
				{
					continue;
				}

				out.add(rs2TileItem);
			}
		}

		return out;
	}

	public static List<RS2TileItem> getAll(Predicate<RS2TileItem> filter)
	{
		return TILE_ITEMS.all(filter);
	}

	public static List<RS2TileItem> getAll()
	{
		return TILE_ITEMS.all(x -> true);
	}

	public static List<RS2TileItem> getAll(int... ids)
	{
		return TILE_ITEMS.all(Predicates.ids(ids));
	}

	public static List<RS2TileItem> getAll(String... names)
	{
		return TILE_ITEMS.all(Predicates.names(names));
	}

	protected RS2TileItem nearest(WorldPoint to, Predicate<RS2TileItem> filter)
	{
		final Map<RS2Tile, RS2TileItem> tileToTileItemMap = new HashMap<>();

		for (RS2Tile tile : RS2Tiles.getAll())
		{
			if (tile.getGroundItems() != null)
			{
				for (TileItem tileItem : tile.getGroundItems())
				{
					if (tileItem == null || tileItem.getId() == -1)
					{
						continue;
					}

					if (!filter.test(new RS2TileItem(tileItem)))
					{
						continue;
					}
					tileToTileItemMap.put(tile, new RS2TileItem(tileItem));
				}
			}
		}

		final RS2Tile key = tileToTileItemMap.keySet()
			.stream()
			.min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo2D(to)))
			.orElse(null);

		return key != null ? tileToTileItemMap.get(key) : null;
		//return TILE_ITEMS.nearest(to, filter);
	}

	public static RS2TileItem getNearest(WorldPoint to, Predicate<RS2TileItem> filter)
	{
		return TILE_ITEMS.nearest(to, filter);
	}

	public static RS2TileItem getNearest(WorldPoint to, int... ids)
	{
		return TILE_ITEMS.nearest(to, Predicates.ids(ids));
	}

	public static RS2TileItem getNearest(WorldPoint to, String... names)
	{
		return TILE_ITEMS.nearest(to, Predicates.names(names));
	}

	public static RS2TileItem getNearest(Predicate<RS2TileItem> filter)
	{
		return TILE_ITEMS.nearest(RS2Players.getLocal().getWorldLocation(), filter);
	}

	public static RS2TileItem getNearest()
	{
		return getNearest(x -> true);
	}

	public static RS2TileItem getNearest(int... ids)
	{
		return getNearest(Predicates.ids(ids));
	}

	public static RS2TileItem getNearest(String... names)
	{
		return getNearest(Predicates.names(names));
	}

	public static List<RS2TileItem> getAt(RS2Tile tile, Predicate<RS2TileItem> filter)
	{
		if (tile == null)
		{
			return Collections.emptyList();
		}

		return TILE_ITEMS.at(tile, filter);
	}

	public static List<RS2TileItem> getAt(RS2Tile tile, int... ids)
	{
		return TILE_ITEMS.at(tile, Predicates.ids(ids));
	}

	public static List<RS2TileItem> getAt(RS2Tile tile, String... names)
	{
		return TILE_ITEMS.at(tile, Predicates.names(names));
	}

	public static List<RS2TileItem> getAt(WorldPoint worldPoint, Predicate<RS2TileItem> filter)
	{
		return TILE_ITEMS.at(RS2Tiles.getAt(worldPoint), filter);
	}

	public static List<RS2TileItem> getAt(WorldPoint worldPoint, int... ids)
	{
		return TILE_ITEMS.at(RS2Tiles.getAt(worldPoint), Predicates.ids(ids));
	}

	public static List<RS2TileItem> getAt(WorldPoint worldPoint, String... names)
	{
		return TILE_ITEMS.at(RS2Tiles.getAt(worldPoint), Predicates.names(names));
	}

	public static List<RS2TileItem> getAt(int worldX, int worldY, int plane, Predicate<RS2TileItem> filter)
	{
		return TILE_ITEMS.at(RS2Tiles.getAt(worldX, worldY, plane), filter);
	}

	public static List<RS2TileItem> getAt(int worldX, int worldY, int plane, int... ids)
	{
		return TILE_ITEMS.at(RS2Tiles.getAt(worldX, worldY, plane), Predicates.ids(ids));
	}

	public static List<RS2TileItem> getAt(int worldX, int worldY, int plane, String... names)
	{
		return TILE_ITEMS.at(RS2Tiles.getAt(worldX, worldY, plane), Predicates.names(names));
	}

	public static RS2TileItem getFirstAt(RS2Tile tile, Predicate<RS2TileItem> filter)
	{
		return getAt(tile, filter).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstAt(RS2Tile tile, int... ids)
	{
		return getAt(tile, ids).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstAt(RS2Tile tile, String... names)
	{
		return getAt(tile, names).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstAt(WorldPoint worldPoint, Predicate<RS2TileItem> filter)
	{
		return getAt(worldPoint, filter).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstAt(WorldPoint worldPoint, int... ids)
	{
		return getAt(worldPoint, ids).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstAt(WorldPoint worldPoint, String... names)
	{
		return getAt(worldPoint, names).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstAt(int worldX, int worldY, int plane, Predicate<RS2TileItem> filter)
	{
		return getAt(worldX, worldY, plane, filter).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstAt(int worldX, int worldY, int plane, int... ids)
	{
		return getAt(worldX, worldY, plane, ids).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstAt(int worldX, int worldY, int plane, String... names)
	{
		return getAt(worldX, worldY, plane, names).stream().findFirst().orElse(null);
	}

	protected List<RS2TileItem> surrounding(int worldX, int worldY, int plane, int radius, Predicate<? super RS2TileItem> filter)
	{
		List<RS2TileItem> out = new ArrayList<>();
		for (int x = -radius; x <= radius; x++)
		{
			for (int y = -radius; y <= radius; y++)
			{
				out.addAll(at(RS2Tiles.getAt(worldX + x, worldY + y, plane), filter));
			}
		}

		return out;
	}

	public static RS2TileItem getFirstSurrounding(int worldX, int worldY, int plane, int radius, Predicate<RS2TileItem> filter)
	{
		return TILE_ITEMS.surrounding(worldX, worldY, plane, radius, filter).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstSurrounding(int worldX, int worldY, int plane, int radius, int... ids)
	{
		return TILE_ITEMS.surrounding(worldX, worldY, plane, radius, Predicates.ids(ids)).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstSurrounding(int worldX, int worldY, int plane, int radius, String... names)
	{
		return TILE_ITEMS.surrounding(worldX, worldY, plane, radius, Predicates.names(names)).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstSurrounding(WorldPoint worldPoint, int radius, Predicate<RS2TileItem> filter)
	{
		return getSurrounding(worldPoint, radius, filter).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstSurrounding(WorldPoint worldPoint, int radius, int... ids)
	{
		return getSurrounding(worldPoint, radius, ids).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstSurrounding(WorldPoint worldPoint, int radius, String... names)
	{
		return getSurrounding(worldPoint, radius, names).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstSurrounding(RS2Tile tile, int radius, Predicate<RS2TileItem> filter)
	{
		return getSurrounding(tile, radius, filter).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstSurrounding(RS2Tile tile, int radius, int... ids)
	{
		return getSurrounding(tile, radius, ids).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstSurrounding(RS2Tile tile, int radius, String... names)
	{
		return getSurrounding(tile, radius, names).stream().findFirst().orElse(null);
	}

	public static List<RS2TileItem> getSurrounding(int worldX, int worldY, int plane, int radius, Predicate<RS2TileItem> filter)
	{
		return TILE_ITEMS.surrounding(worldX, worldY, plane, radius, filter);
	}

	public static List<RS2TileItem> getSurrounding(int worldX, int worldY, int plane, int radius, int... ids)
	{
		return TILE_ITEMS.surrounding(worldX, worldY, plane, radius, Predicates.ids(ids));
	}

	public static List<RS2TileItem> getSurrounding(int worldX, int worldY, int plane, int radius, String... names)
	{
		return TILE_ITEMS.surrounding(worldX, worldY, plane, radius, Predicates.names(names));
	}

	public static List<RS2TileItem> getSurrounding(WorldPoint worldPoint, int radius, Predicate<RS2TileItem> filter)
	{
		return getSurrounding(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane(), radius, filter);
	}

	public static List<RS2TileItem> getSurrounding(WorldPoint worldPoint, int radius, int... ids)
	{
		return getSurrounding(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane(), radius, Predicates.ids(ids));
	}

	public static List<RS2TileItem> getSurrounding(WorldPoint worldPoint, int radius, String... names)
	{
		return getSurrounding(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane(), radius, Predicates.names(names));
	}

	public static List<RS2TileItem> getSurrounding(RS2Tile tile, int radius, Predicate<RS2TileItem> filter)
	{
		return getSurrounding(tile.getWorldX(), tile.getWorldY(), tile.getPlane(), radius, filter);
	}

	public static List<RS2TileItem> getSurrounding(RS2Tile tile, int radius, int... ids)
	{
		return getSurrounding(tile.getWorldX(), tile.getWorldY(), tile.getPlane(), radius, ids);
	}

	public static List<RS2TileItem> getSurrounding(RS2Tile tile, int radius, String... names)
	{
		return getSurrounding(tile.getWorldX(), tile.getWorldY(), tile.getPlane(), radius, names);
	}

	protected List<RS2TileItem> in(WorldArea area, Predicate<? super RS2TileItem> filter)
	{
		List<RS2TileItem> out = new ArrayList<>();
		for (WorldPoint worldPoint : area.toWorldPointList())
		{
			out.addAll(at(RS2Tiles.getAt(worldPoint), filter));
		}

		return out;
	}

	public static List<RS2TileItem> within(WorldArea area, Predicate<RS2TileItem> filter)
	{
		return TILE_ITEMS.in(area, filter);
	}

	protected List<RS2TileItem> within(WorldArea area, int... ids)
	{
		return TILE_ITEMS.in(area, Predicates.ids(ids));
	}

	protected List<RS2TileItem> within(WorldArea area, String... names)
	{
		return TILE_ITEMS.in(area, Predicates.names(names));
	}

	public static class Predicates
	{
		public static Predicate<RS2TileItem> ids(int... ids)
		{
			final Set<Integer> idSet = Arrays.stream(ids).boxed().collect(Collectors.toSet());
			return x -> idSet.contains(x.getId());
		}

		public static Predicate<RS2TileItem> names(String... names)
		{
			final Set<String> nameSet = new HashSet<>(Arrays.asList(names));
			return x -> nameSet.contains(x.getName());
		}
	}
}

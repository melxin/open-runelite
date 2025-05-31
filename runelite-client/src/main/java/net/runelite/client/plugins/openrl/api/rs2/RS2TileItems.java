package net.runelite.client.plugins.openrl.api.rs2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;

public class RS2TileItems
{
	private static final RS2TileItems TILE_ITEMS = new RS2TileItems();

	private RS2TileItems()
	{
	}

	public static List<RS2TileItem> getAll()
	{
		return getAll(x -> true);
	}

	public static List<RS2TileItem> getAll(Predicate<RS2TileItem> filter)
	{
		return TILE_ITEMS.all(filter);
	}

	public static List<RS2TileItem> getAll(int... ids)
	{
		return TILE_ITEMS.all(x -> Arrays.asList(ids).contains(x.getId()));
	}

	public static List<RS2TileItem> getAll(String... names)
	{
		return TILE_ITEMS.all(x -> Arrays.asList(names).contains(Static.getItemManager().getItemComposition(x.getId()).getName()));
	}

	public static RS2TileItem getNearest(Predicate<RS2TileItem> filter)
	{
		return getNearest(RS2Players.getLocal().getWorldLocation(), filter);
	}

	public static RS2TileItem getNearest(int... ids)
	{
		return getNearest(RS2Players.getLocal().getWorldLocation(), ids);
	}

	public static RS2TileItem getNearest(String... names)
	{
		return getNearest(RS2Players.getLocal().getWorldLocation(), names);
	}

	public static RS2TileItem getNearest(WorldPoint to, Predicate<RS2TileItem> filter)
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
			.min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo2D(RS2Players.getLocal().getWorldLocation())))
			.orElse(null);

		return key != null ? tileToTileItemMap.get(key) : null;
		//return TILE_ITEMS.nearest(to, filter);
	}

	public static RS2TileItem getNearest(WorldPoint to, int... ids)
	{
		return getNearest(to, x -> Arrays.asList(ids).contains(x.getId()));
	}

	public static RS2TileItem getNearest(WorldPoint to, String... names)
	{
		return getNearest(to, x -> Arrays.asList(names).contains(Static.getItemManager().getItemComposition(x.getId()).getName()));
	}

	public static List<RS2TileItem> getAt(int worldX, int worldY, int plane, int... ids)
	{
		return getAt(RS2Tiles.getAt(worldX, worldY, plane), ids);
	}

	public static List<RS2TileItem> getAt(int worldX, int worldY, int plane, String... names)
	{
		return getAt(RS2Tiles.getAt(worldX, worldY, plane), names);
	}

	public static List<RS2TileItem> getAt(int worldX, int worldY, int plane, Predicate<RS2TileItem> filter)
	{
		return getAt(RS2Tiles.getAt(worldX, worldY, plane), filter);
	}

	public static List<RS2TileItem> getAt(WorldPoint worldPoint, Predicate<RS2TileItem> filter)
	{
		return getAt(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane(), filter);
	}

	public static List<RS2TileItem> getAt(WorldPoint worldPoint, int... ids)
	{
		return getAt(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane(), ids);
	}

	public static List<RS2TileItem> getAt(WorldPoint worldPoint, String... names)
	{
		return getAt(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane(), names);
	}

	public static List<RS2TileItem> getAt(RS2Tile tile, int... ids)
	{
		return getAt(tile, x -> Arrays.asList(ids).contains(x.getId()));
	}

	public static List<RS2TileItem> getAt(RS2Tile tile, String... names)
	{
		return TILE_ITEMS.at(tile, x -> Arrays.asList(names).contains(Static.getItemManager().getItemComposition(x.getId()).getName()));
	}

	public static List<RS2TileItem> getAt(RS2Tile tile, Predicate<RS2TileItem> filter)
	{
		if (tile == null)
		{
			return Collections.emptyList();
		}

		return TILE_ITEMS.at(tile, filter);
	}

	public static RS2TileItem getFirstAt(int worldX, int worldY, int plane, int... ids)
	{
		return getAt(worldX, worldY, plane, ids).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstAt(int worldX, int worldY, int plane, String... names)
	{
		return getAt(worldX, worldY, plane, names).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstAt(int worldX, int worldY, int plane, Predicate<RS2TileItem> filter)
	{
		return getAt(worldX, worldY, plane, filter).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstAt(WorldPoint worldPoint, int... ids)
	{
		return getAt(worldPoint, ids).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstAt(WorldPoint worldPoint, String... names)
	{
		return getAt(worldPoint, names).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstAt(WorldPoint worldPoint, Predicate<RS2TileItem> filter)
	{
		return getAt(worldPoint, filter).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstAt(RS2Tile tile, int... ids)
	{
		return getAt(tile, ids).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstAt(RS2Tile tile, String... names)
	{
		return getAt(tile, names).stream().findFirst().orElse(null);
	}

	public static RS2TileItem getFirstAt(RS2Tile tile, Predicate<RS2TileItem> filter)
	{
		return getAt(tile, filter).stream().findFirst().orElse(null);
	}

	protected List<RS2TileItem> all(Predicate<? super RS2TileItem> filter)
	{
		return RS2Tiles.getAll().stream()
			.flatMap(tile -> at(tile, filter).stream())
			.collect(Collectors.toList());
	}

	protected List<RS2TileItem> at(RS2Tile tile, Predicate<? super RS2TileItem> filter)
	{
		List<RS2TileItem> out = new ArrayList<>();
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
}

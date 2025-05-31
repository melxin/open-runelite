package net.runelite.client.plugins.openrl.api.entities;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuAction;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;
import net.runelite.client.plugins.openrl.api.reflection.Reflection;
import net.runelite.client.plugins.openrl.api.scene.Tiles;

public class TileItems
{
	private static final TileItems TILE_ITEMS = new TileItems();

	private TileItems()
	{
	}

	public static List<TileItem> getAll()
	{
		return getAll(x -> true);
	}

	public static List<TileItem> getAll(Predicate<TileItem> filter)
	{
		return TILE_ITEMS.all(filter);
	}

	public static List<TileItem> getAll(int... ids)
	{
		return TILE_ITEMS.all(x -> Arrays.asList(ids).contains(x.getId()));
	}

	public static List<TileItem> getAll(String... names)
	{
		return TILE_ITEMS.all(x -> Arrays.asList(names).contains(Static.getItemManager().getItemComposition(x.getId()).getName()));
	}

	public static TileItem getNearest(Predicate<TileItem> filter)
	{
		return getNearest(Players.getLocal().getWorldLocation(), filter);
	}

	public static TileItem getNearest(int... ids)
	{
		return getNearest(Players.getLocal().getWorldLocation(), ids);
	}

	public static TileItem getNearest(String... names)
	{
		return getNearest(Players.getLocal().getWorldLocation(), names);
	}

	public static TileItem getNearest(WorldPoint to, Predicate<TileItem> filter)
	{
		final Map<Tile, TileItem> tileToTileItemMap = new HashMap<>();

		for (Tile tile : Tiles.getAll())
		{
			if (tile.getGroundItems() != null)
			{
				for (TileItem tileItem : tile.getGroundItems())
				{
					if (tileItem == null || tileItem.getId() == -1)
					{
						continue;
					}

					if (!filter.test(tileItem))
					{
						continue;
					}
					tileToTileItemMap.put(tile, tileItem);
				}
			}
		}

		final Tile key = tileToTileItemMap.keySet()
			.stream()
			.min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo2D(Players.getLocal().getWorldLocation())))
			.orElse(null);

		return key != null ? tileToTileItemMap.get(key) : null;
		//return TILE_ITEMS.nearest(to, filter);
	}

	public static TileItem getNearest(WorldPoint to, int... ids)
	{
		return getNearest(to, x -> Arrays.asList(ids).contains(x.getId()));
	}

	public static TileItem getNearest(WorldPoint to, String... names)
	{
		return getNearest(to, x -> Arrays.asList(names).contains(Static.getItemManager().getItemComposition(x.getId()).getName()));
	}

	public static List<TileItem> getAt(int worldX, int worldY, int plane, int... ids)
	{
		return getAt(Tiles.getAt(worldX, worldY, plane), ids);
	}

	public static List<TileItem> getAt(int worldX, int worldY, int plane, String... names)
	{
		return getAt(Tiles.getAt(worldX, worldY, plane), names);
	}

	public static List<TileItem> getAt(int worldX, int worldY, int plane, Predicate<TileItem> filter)
	{
		return getAt(Tiles.getAt(worldX, worldY, plane), filter);
	}

	public static List<TileItem> getAt(WorldPoint worldPoint, Predicate<TileItem> filter)
	{
		return getAt(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane(), filter);
	}

	public static List<TileItem> getAt(WorldPoint worldPoint, int... ids)
	{
		return getAt(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane(), ids);
	}

	public static List<TileItem> getAt(WorldPoint worldPoint, String... names)
	{
		return getAt(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane(), names);
	}

	public static List<TileItem> getAt(Tile tile, int... ids)
	{
		return getAt(tile, x -> Arrays.asList(ids).contains(x.getId()));
	}

	public static List<TileItem> getAt(Tile tile, String... names)
	{
		return TILE_ITEMS.at(tile, x -> Arrays.asList(names).contains(Static.getItemManager().getItemComposition(x.getId()).getName()));
	}

	public static List<TileItem> getAt(Tile tile, Predicate<TileItem> filter)
	{
		if (tile == null)
		{
			return Collections.emptyList();
		}

		return TILE_ITEMS.at(tile, filter);
	}

	public static TileItem getFirstAt(int worldX, int worldY, int plane, int... ids)
	{
		return getAt(worldX, worldY, plane, ids).stream().findFirst().orElse(null);
	}

	public static TileItem getFirstAt(int worldX, int worldY, int plane, String... names)
	{
		return getAt(worldX, worldY, plane, names).stream().findFirst().orElse(null);
	}

	public static TileItem getFirstAt(int worldX, int worldY, int plane, Predicate<TileItem> filter)
	{
		return getAt(worldX, worldY, plane, filter).stream().findFirst().orElse(null);
	}

	public static TileItem getFirstAt(WorldPoint worldPoint, int... ids)
	{
		return getAt(worldPoint, ids).stream().findFirst().orElse(null);
	}

	public static TileItem getFirstAt(WorldPoint worldPoint, String... names)
	{
		return getAt(worldPoint, names).stream().findFirst().orElse(null);
	}

	public static TileItem getFirstAt(WorldPoint worldPoint, Predicate<TileItem> filter)
	{
		return getAt(worldPoint, filter).stream().findFirst().orElse(null);
	}

	public static TileItem getFirstAt(Tile tile, int... ids)
	{
		return getAt(tile, ids).stream().findFirst().orElse(null);
	}

	public static TileItem getFirstAt(Tile tile, String... names)
	{
		return getAt(tile, names).stream().findFirst().orElse(null);
	}

	public static TileItem getFirstAt(Tile tile, Predicate<TileItem> filter)
	{
		return getAt(tile, filter).stream().findFirst().orElse(null);
	}

	/*public static TileItem getFirstSurrounding(int worldX, int worldY, int plane, int radius, int... ids)
	{
		return getSurrounding(worldX, worldY, plane, radius, ids).stream().findFirst().orElse(null);
	}

	public static TileItem getFirstSurrounding(int worldX, int worldY, int plane, int radius, String... names)
	{
		return getSurrounding(worldX, worldY, plane, radius, names).stream().findFirst().orElse(null);
	}

	public static TileItem getFirstSurrounding(int worldX, int worldY, int plane, int radius, Predicate<TileItem> filter)
	{
		return getSurrounding(worldX, worldY, plane, radius, filter).stream().findFirst().orElse(null);
	}

	public static TileItem getFirstSurrounding(WorldPoint worldPoint, int radius, int... ids)
	{
		return getSurrounding(worldPoint, radius, ids).stream().findFirst().orElse(null);
	}

	public static TileItem getFirstSurrounding(WorldPoint worldPoint, int radius, String... names)
	{
		return getSurrounding(worldPoint, radius, names).stream().findFirst().orElse(null);
	}

	public static TileItem getFirstSurrounding(WorldPoint worldPoint, int radius, Predicate<TileItem> filter)
	{
		return getSurrounding(worldPoint, radius, filter).stream().findFirst().orElse(null);
	}

	public static TileItem getFirstSurrounding(Tile tile, int radius, int... ids)
	{
		return getSurrounding(tile, radius, ids).stream().findFirst().orElse(null);
	}

	public static TileItem getFirstSurrounding(Tile tile, int radius, String... names)
	{
		return getSurrounding(tile, radius, names).stream().findFirst().orElse(null);
	}

	public static TileItem getFirstSurrounding(Tile tile, int radius, Predicate<TileItem> filter)
	{
		return getSurrounding(tile, radius, filter).stream().findFirst().orElse(null);
	}

	public static List<TileItem> getSurrounding(int worldX, int worldY, int plane, int radius, int... ids)
	{
		return getSurrounding(worldX, worldY, plane, radius, x -> Arrays.asList(ids).contains(x.getId()));
	}

	public static List<TileItem> getSurrounding(int worldX, int worldY, int plane, int radius, String... names)
	{
		return getSurrounding(worldX, worldY, plane, radius, x -> Arrays.asList(names).contains(Static.getItemManager().getItemComposition(x.getId()).getName()));
	}

	public static List<TileItem> getSurrounding(int worldX, int worldY, int plane, int radius, Predicate<TileItem> filter)
	{
		return TILE_ITEMS.surrounding(worldX, worldY, plane, radius, filter);
	}

	public static List<TileItem> getSurrounding(WorldPoint worldPoint, int radius, int... ids)
	{
		return getSurrounding(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane(), radius, ids);
	}

	public static List<TileItem> getSurrounding(WorldPoint worldPoint, int radius, String... names)
	{
		return getSurrounding(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane(), radius, names);
	}

	public static List<TileItem> getSurrounding(WorldPoint worldPoint, int radius, Predicate<TileItem> filter)
	{
		return getSurrounding(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane(), radius, filter);
	}

	public static List<TileItem> getSurrounding(Tile tile, int radius, int... ids)
	{
		return getSurrounding(tile.getWorldLocation().getX(), tile.getWorldLocation().getY(), tile.getPlane(), radius, ids);
	}

	public static List<TileItem> getSurrounding(Tile tile, int radius, String... names)
	{
		return getSurrounding(tile.getWorldLocation().getX(), tile.getWorldLocation().getY(), tile.getPlane(), radius, names);
	}

	public static List<TileItem> getSurrounding(Tile tile, int radius, Predicate<TileItem> filter)
	{
		return getSurrounding(tile.getWorldLocation().getX(), tile.getWorldLocation().getY(), tile.getPlane(), radius, filter);
	}

	public static List<TileItem> within(WorldArea area, String... names)
	{
		return TILE_ITEMS.in(area, names);
	}

	public static List<TileItem> within(WorldArea area, int... ids)
	{
		return TILE_ITEMS.in(area, ids);
	}

	public static List<TileItem> within(WorldArea area, Predicate<TileItem> filter)
	{
		return TILE_ITEMS.in(area, filter);
	}*/

	/*public static void interact(TileItem tileItem, String action)
	{
		final WidgetItem widgetItem = getWidgetItem(slot);
		final String[] actions = widgetItem.getWidget().getActions();
		final int actionIndex = Arrays.asList(stripColTags(actions)).indexOf(action) + 1;

		final MenuAction menuAction = isItemSelected() ? MenuAction.WIDGET_TARGET_ON_WIDGET
			: action.equalsIgnoreCase("use") ? MenuAction.WIDGET_TARGET
			: action.equalsIgnoreCase("cast") ? MenuAction.WIDGET_TARGET_ON_WIDGET
			: MenuAction.CC_OP;

		Static.getEventBus().post(MenuAutomated.InventoryMenuAutomated.builder()
			.widget(widgetItem.getWidget())
			.slot(slot)
			.index(actionIndex)
			.menuAction(menuAction)
			.build());
	}*/

	protected List<TileItem> all(Predicate<? super TileItem> filter)
	{
		return Tiles.getAll().stream()
				.flatMap(tile -> at(tile, filter).stream())
				.collect(Collectors.toList());
	}

	protected List<TileItem> at(Tile tile, Predicate<? super TileItem> filter)
	{
		List<TileItem> out = new ArrayList<>();
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

				if (!filter.test(item))
				{
					continue;
				}

				out.add(item);
			}
		}

		return out;
	}

	public static void interact(TileItem tileItem, String action)
	{
		final LocalPoint localPoint = LocalPoint.fromWorld(Static.getClient().getTopLevelWorldView(), getTile(tileItem).getWorldLocation());
		if (localPoint == null)
		{
			return;
		}
		final int param0 = localPoint.getSceneX();
		final int param1 = localPoint.getSceneY();

		final ItemComposition itemComposition = Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getItemDefinition(tileItem.getId())).orElse(null);
		if (itemComposition == null)
		{
			return;
		}

		final MenuAction menuAction = getMenuAction(getActionIndex(tileItem, action));

		final int identifier = tileItem.getId();
		final int itemId = -1;
		final int worldViewId = -1;
		final String option = "";
		final String target = "<col=ff9040>" + itemComposition.getName();

		/*final Rectangle bounds = PointRandomizer.getBoundsFor(tileItem);
		final int x = (int) bounds.getX();
		final int y = (int) bounds.getY();*/

		Static.getEventBus().post(new MenuAutomated(param0, param1, menuAction, identifier, itemId, worldViewId, option, target, -1, -1));
	}

	@Nullable
	public static MenuAction getMenuAction(int index)
	{
		if (Static.getClient().isWidgetSelected())
		{
			return MenuAction.WIDGET_TARGET_ON_GROUND_ITEM;
		}
		switch (index)
		{
			case 0:
				return MenuAction.GROUND_ITEM_FIRST_OPTION;
			case 1:
				return MenuAction.GROUND_ITEM_SECOND_OPTION;
			case 2:
				return MenuAction.GROUND_ITEM_THIRD_OPTION;
			case 3:
				return MenuAction.GROUND_ITEM_FOURTH_OPTION;
			case 4:
				return MenuAction.GROUND_ITEM_FIFTH_OPTION;
			default:
				return null;
		}
	}

	public static int getActionIndex(TileItem tileItem, String action)
	{
		final ItemComposition itemComposition = Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getItemDefinition(tileItem.getId())).orElse(null);
		if (itemComposition == null)
		{
			return -1;
		}

		final String[] groundActions = Reflection.getGroundItemActions(itemComposition);
		for (int i = 0; i < groundActions.length; i++)
		{
			final String groundAction = groundActions[i];
			if (groundAction != null && groundAction.equalsIgnoreCase(action))
			{
				return i;
			}
		}

		return -1;
	}

	public static Tile getTile(TileItem tileItem)
	{
		for (Tile tile : Tiles.getAll())
		{
			if (tile.getGroundItems() != null)
			{
				for (TileItem item : tile.getGroundItems())
				{
					if (item == tileItem)
					{
						return tile;
					}
				}
			}
		}
		return null;
	}
}

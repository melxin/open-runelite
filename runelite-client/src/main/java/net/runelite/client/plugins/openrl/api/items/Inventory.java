package net.runelite.client.plugins.openrl.api.items;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.Point;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;
import net.runelite.client.plugins.openrl.api.input.utils.Randomizer;

public class Inventory
{
	public static final int ITEM_EMPTY = 6512;
	public static final int SIZE = 28;

	private static final Inventory INVENTORY = new Inventory();

	private Inventory()
	{
	}

	public static List<Item> getAll(Predicate<Item> filter)
	{
		return INVENTORY.all(filter);
	}

	public static List<Item> getAll()
	{
		return getAll(x -> true);
	}

	public static List<Item> getAll(int... ids)
	{
		return INVENTORY.all(x -> Arrays.asList(ids).contains(x.getId()));
	}

	public static List<Item> getAll(String... names)
	{
		return INVENTORY.all(x -> Arrays.asList(names).contains(x.getId()));
	}

	public static Item getFirst(Predicate<Item> filter)
	{
		return INVENTORY.all(filter).stream().findFirst().orElse(null);
	}

	public static Item getFirst(int... ids)
	{
		return getAll(ids).stream().findFirst().orElse(null);
	}

	public static Item getFirst(String... names)
	{
		return getAll(names).stream().findFirst().orElse(null);
	}

	protected boolean exists(Predicate<Item> filter)
	{
		return getFirst(filter) != null;
	}

	/*protected boolean exists(String... name)
	{
		return getFirst(name) != null;
	}*/

	protected boolean exists(int... id)
	{
		return getFirst(id) != null;
	}

	public static Item getItem(int slot)
	{
		final ItemContainer itemContainer = Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getItemContainer(InventoryID.INV)).orElse(null);
		if (itemContainer == null)
		{
			return null;
		}

		final Item[] items = itemContainer.getItems();

		return items[slot];
	}

	public static boolean contains(Predicate<Item> filter)
	{
		return !INVENTORY.all(filter).isEmpty();
	}

	public static boolean contains(int... id)
	{
		return !INVENTORY.all(x -> Arrays.asList(x).contains(id)).isEmpty();
	}

	/*public static boolean contains(String... name)
	{
		return INVENTORY.all(name);
	}*/

	public static int getCount(boolean stacks, Predicate<Item> filter)
	{
		return INVENTORY.all(filter).stream().mapToInt(x -> stacks ? x.getQuantity() : 1).sum();
	}

	public static int getCount(boolean stacks, int... ids)
	{
		return getCount(stacks, x -> Arrays.asList(ids).contains(x.getId()));
	}

	/*public static int getCount(boolean stacks, String... names)
	{
		return INVENTORY.count(stacks, names);
	}*/

	public static int getCount(Predicate<Item> filter)
	{
		return getCount(false, filter);
	}

	public static int getCount(int... ids)
	{
		return getCount(false, ids);
	}

	/*public static int getCount(String... names)
	{
		return INVENTORY.count(false, names);
	}*/

	public static boolean isFull()
	{
		return getFreeSlots() == 0;
	}

	public static boolean isEmpty()
	{
		return getFreeSlots() == Inventory.SIZE;
	}

	public static int getFreeSlots()
	{
		return Inventory.SIZE - getAll().size();
	}

	protected List<Item> all(Predicate<Item> filter)
	{
		final ItemContainer itemContainer = Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getItemContainer(InventoryID.INV)).orElse(null);
		if (itemContainer == null)
		{
			return Collections.emptyList();
		}

		final Item[] items = itemContainer.getItems();
		return Stream.of(items)
			.filter(filter)
			.collect(Collectors.toList());
	}

	public static void interact(Item item, String action)
	{
		interact(getSlot(item), action);
	}

	public static void interact(Item item, int index)
	{
		interact(item, getAction(getSlot(item), index));
	}

	public static void interact(int slot, int index)
	{
		interact(slot, getAction(slot, index));
	}

	public static void interact(int slot, String action)
	{
		final WidgetItem widgetItem = getWidgetItem(slot);
		final Widget widget = widgetItem.getWidget();
		final int param0 = slot;
		final int param1 = widget.getId();
		final MenuAction menuAction = getMenuAction(action);
		final String[] actions = widget.getActions();
		final int actionIndex = Arrays.asList(stripColTags(actions)).indexOf(action) + 1;
		final int itemId = widget.getItemId();
		final int worldViewId = -1;
		final String option = "";
		final String target = "";
		final Point clickPoint = Randomizer.getRandomPointIn(widget.getBounds());
		final int x = clickPoint.getX();
		final int y = clickPoint.getY();
		Static.getEventBus().post(new MenuAutomated(param0, param1, menuAction, actionIndex, itemId, worldViewId, option, target, x, y));
	}

	public static int getSlot(Item item)
	{
		final ItemContainer itemContainer = Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getItemContainer(InventoryID.INV)).orElse(null);
		if (itemContainer == null)
		{
			return -1;
		}

		final Item[] items = itemContainer.getItems();

		for (int i = 0; i < items.length; i++)
		{
			if (items[i].getId() == item.getId())
			{
				return i;
			}
		}

		return -1;
	}

	public static WidgetItem getWidgetItem(int idx)
	{
		final Widget inventoryWidget = Static.getClient().getWidget(InterfaceID.Inventory.ITEMS);
		if (inventoryWidget == null || !inventoryWidget.isIf3())
		{
			return null;
		}

		final Widget item = inventoryWidget.getChild(idx);
		return new WidgetItem(item.getItemId(), item.getItemQuantity(), item.getBounds(), item, item.getBounds());
	}

	/**
	 * Checks whether an item is currently selected in your inventory.
	 *
	 * @return True if an item is selected, false otherwise.
	 */
	public static boolean isItemSelected()
	{
		return Static.getClient().isWidgetSelected();
	}

	private static String[] stripColTags(String[] sourceList)
	{
		List<String> resultList = new ArrayList<>();
		String regex = "<col=[^>]*>";

		for (String item : sourceList)
		{
			if (item != null)
			{
				resultList.add(item.replaceAll(regex, ""));
			}
			else
			{
				resultList.add(null);
			}
		}

		return resultList.toArray(String[]::new);
	}

	public static boolean hasAction(Item item, String action)
	{
		final WidgetItem widgetItem = getWidgetItem(getSlot(item));
		if (widgetItem == null)
		{
			return false;
		}

		final Widget widget = widgetItem.getWidget();
		if (widget == null)
		{
			return false;
		}

		final String[] actions = widget.getActions();
		return actions != null && Arrays.asList(actions).contains(action);
	}

	public static String getAction(Item item, int index)
	{
		return getAction(getSlot(item), index);
	}

	public static String getAction(int slot, int index)
	{
		final WidgetItem widgetItem = getWidgetItem(slot);
		final Widget widget = widgetItem.getWidget();
		final String[] actions = Arrays.stream(widget.getActions())
			.filter(a -> a != null && !a.equals("null"))
			.toArray(String[]::new);
		if (index >= 0 && index < actions.length)
		{
			return actions[index];
		}
		return "null";
	}

	public static MenuAction getMenuAction(String action)
	{
		final MenuAction menuAction = isItemSelected() ? MenuAction.WIDGET_TARGET_ON_WIDGET
			: action.equalsIgnoreCase("use") ? MenuAction.WIDGET_TARGET
			: action.equalsIgnoreCase("cast") ? MenuAction.WIDGET_TARGET_ON_WIDGET
			: MenuAction.CC_OP;
		return menuAction;
	}
}

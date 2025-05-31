package net.runelite.client.plugins.openrl.api.rs2;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;

public class RS2Bank
{
	private static final RS2Bank BANK = new RS2Bank();

	private RS2Bank()
	{
	}

	public static boolean isOpen()
	{
		return RS2Widgets.isVisible(RS2Widgets.getWidget(InterfaceID.Bankmain.ITEMS_CONTAINER));
	}

	public static List<RS2Item> getAll()
	{
		return getAll(x -> true);
	}

	public static List<RS2Item> getAll(Predicate<RS2Item> filter)
	{
		return BANK.all(filter);
	}

	protected List<RS2Item> all(Predicate<RS2Item> filter)
	{
		final ItemContainer itemContainer = Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getItemContainer(InventoryID.BANK)).orElse(null);
		if (itemContainer == null)
		{
			return Collections.EMPTY_LIST;
		}

		final Item[] items = itemContainer.getItems();
		final RS2Item[] rs2Items = new RS2Item[items.length];
		for (int i = 0; i < rs2Items.length; i++)
		{
			rs2Items[i] = new RS2Item(items[i], i);
		}

		return Stream.of(rs2Items)
			.filter(filter)
			.collect(Collectors.toList());
	}

	public static void depositInventory()
	{
		final RS2Widget widget = RS2Widgets.getWidget(InterfaceID.Bankmain.DEPOSITINV);
		if (widget != null)
		{
			widget.interact("Deposit inventory");
		}
	}

	public static void depositEquipment()
	{
		final RS2Widget widget = RS2Widgets.getWidget(InterfaceID.Bankmain.DEPOSITWORN);
		if (widget != null)
		{
			widget.interact("Deposit worn items");
		}
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
		final int actionIndex = Arrays.asList(actions).indexOf(action) + 1;
		final int itemId = widget.getItemId();
		final int worldViewId = -1;
		final String option = "";
		final String target = "";
		final Rectangle bounds = widget.getBounds();
		final int x = (int) bounds.getX();
		final int y = (int) bounds.getY();
		Static.getEventBus().post(new MenuAutomated(param0, param1, menuAction, actionIndex, itemId, worldViewId, option, target, x, y));
	}

	public static int getSlot(Item item)
	{
		final ItemContainer itemContainer = Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getItemContainer(InventoryID.BANK)).orElse(null);
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
		final MenuAction menuAction = Static.getClient().isWidgetSelected() ? MenuAction.WIDGET_TARGET_ON_WIDGET
			: action.equalsIgnoreCase("use") ? MenuAction.WIDGET_TARGET
			: action.equalsIgnoreCase("cast") ? MenuAction.WIDGET_TARGET_ON_WIDGET
			: MenuAction.CC_OP;
		return menuAction;
	}
}

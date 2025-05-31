package net.runelite.client.plugins.openrl.api.rs2;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
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

@Getter
@RequiredArgsConstructor
public class RS2Item
{
	@NonNull
	private final Item item;

	private final int slot;

	public int getId()
	{
		return item.getId();
	}

	public int getQuantity()
	{
		return item.getQuantity();
	}

	public String getName()
	{
		final ItemComposition composition = getComposition();
		return composition != null ? composition.getName() : null;
	}

	public ItemComposition getComposition()
	{
		return Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getItemDefinition(item.getId())).orElse(null);
	}

	public void interact(int index)
	{
		interact(getAction(index));
	}

	public void interact(String action)
	{
		final WidgetItem widgetItem = getWidgetItem(getSlot());
		final Widget widget = widgetItem.getWidget();
		final int param0 = getSlot();
		final int param1 = widget.getId();
		final MenuAction menuAction = getMenuAction(action);
		final String[] actions = widget.getActions();
		final int actionIndex = Arrays.asList(stripColTags(actions)).indexOf(action) + 1;
		final int itemId = widget.getItemId();
		final int worldViewId = -1;
		final String option = action;
		final String target = "<col=ff9040>" + this.getName() + "</col>";
		final Point clickPoint = Randomizer.getRandomPointIn(widget.getBounds());
		final int x = clickPoint.getX();
		final int y = clickPoint.getY();
		Static.getEventBus().post(new MenuAutomated(param0, param1, menuAction, actionIndex, itemId, worldViewId, option, target, x, y));
	}

	/*public int getSlot()
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
	}*/

	public WidgetItem getWidgetItem(int idx)
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
	public boolean isItemSelected()
	{
		return Static.getClient().isWidgetSelected();
	}

	private String[] stripColTags(String[] sourceList)
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

	public boolean hasAction(String action)
	{
		final WidgetItem widgetItem = getWidgetItem(getSlot());
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

	public String getAction(int index)
	{
		final WidgetItem widgetItem = getWidgetItem(getSlot());
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

	public MenuAction getMenuAction(String action)
	{
		final MenuAction menuAction = isItemSelected() ? MenuAction.WIDGET_TARGET_ON_WIDGET
			: action.equalsIgnoreCase("use") ? MenuAction.WIDGET_TARGET
			: action.equalsIgnoreCase("cast") ? MenuAction.WIDGET_TARGET_ON_WIDGET
			: MenuAction.CC_OP;
		return menuAction;
	}
}

package net.runelite.client.plugins.openrl.api.rs2.items;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuAction;
import net.runelite.api.Point;
import net.runelite.api.annotations.Component;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;
import net.runelite.client.plugins.openrl.api.input.utils.Randomizer;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2NPC;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2Player;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2TileItem;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2TileObject;
import net.runelite.client.plugins.openrl.api.rs2.widgets.RS2Widget;
import net.runelite.client.plugins.openrl.api.rs2.widgets.RS2Widgets;

@RequiredArgsConstructor
public class RS2Item
{
	@NonNull
	@Getter(AccessLevel.PUBLIC)
	private final Item item;

	@Getter(AccessLevel.PUBLIC)
	private final int slot;

	@Getter(AccessLevel.PUBLIC)
	private final int itemContainerId;

	@Getter(AccessLevel.PUBLIC)
	@Component
	private final int inventoryInterfaceId;

	public int getId()
	{
		return item.getId();
	}

	public int getQuantity()
	{
		return item.getQuantity();
	}

	@Nullable
	public String getName()
	{
		final ItemComposition composition = getComposition();
		return composition != null ? composition.getName() : null;
	}

	private ItemComposition itemComposition;

	@Nullable
	public ItemComposition getComposition()
	{
		if (itemComposition == null)
		{
			this.itemComposition = Static.getGameDataCached().getItemComposition(getId());
		}
		return itemComposition;
	}

	public void interact(int index)
	{
		interact(getAction(index));
	}

	public void interact(String action)
	{
		final WidgetItem widgetItem = getWidgetItem();
		final Widget widget = widgetItem.getWidget();
		final int param0 = widget.getIndex(); // getSlot();
		final int param1 = widget.getId();
		final MenuAction menuAction = getMenuAction(action);
		final String[] actions = widget.getActions();
		final int actionIndex = Arrays.asList(stripColTags(actions)).indexOf(action) + 1;
		final int itemId = widget.getItemId();
		final int worldViewId = -1;
		final String option = action;
		final String target = "<col=ff9040>" + this.getName() + "</col>";
		final Point clickPoint = getClickPoint();
		final int x = clickPoint.getX();
		final int y = clickPoint.getY();
		Static.getEventBus().post(new MenuAutomated(param0, param1, menuAction, actionIndex, itemId, worldViewId, option, target, x, y));
	}

	public WidgetItem getWidgetItem()
	{
		if (inventoryInterfaceId == InterfaceID.WORNITEMS)
		{
			return getEquipmentWidgetItem();
		}

		final Widget inventoryWidget = Static.getClient().getWidget(inventoryInterfaceId);
		//final Widget inventoryWidget = Static.getClient().getWidget(InterfaceID.Inventory.ITEMS);
		if (inventoryWidget == null || !inventoryWidget.isIf3())
		{
			return null;
		}

		final Widget item = inventoryWidget.getChild(getSlot());
		return new WidgetItem(item.getItemId(), item.getItemQuantity(), item.getBounds(), item, item.getBounds());
	}

	public WidgetItem getEquipmentWidgetItem()
	{
		final RS2Widget equipmentItemWidget;
		switch (getSlot())
		{
			case 0:
				equipmentItemWidget = RS2Widgets.getWidget(InterfaceID.Wornitems.SLOT0);
				break;
			case 1:
				equipmentItemWidget = RS2Widgets.getWidget(InterfaceID.Wornitems.SLOT1);
				break;
			case 2:
				equipmentItemWidget = RS2Widgets.getWidget(InterfaceID.Wornitems.SLOT2);
				break;
			case 3:
				equipmentItemWidget = RS2Widgets.getWidget(InterfaceID.Wornitems.SLOT3);
				break;
			case 4:
				equipmentItemWidget = RS2Widgets.getWidget(InterfaceID.Wornitems.SLOT4);
				break;
			case 5:
				equipmentItemWidget = RS2Widgets.getWidget(InterfaceID.Wornitems.SLOT5);
				break;
			case 7:
				equipmentItemWidget = RS2Widgets.getWidget(InterfaceID.Wornitems.SLOT7);
				break;
			case 9:
				equipmentItemWidget = RS2Widgets.getWidget(InterfaceID.Wornitems.SLOT9);
				break;
			case 10:
				equipmentItemWidget = RS2Widgets.getWidget(InterfaceID.Wornitems.SLOT10);
				break;
			case 12:
				equipmentItemWidget = RS2Widgets.getWidget(InterfaceID.Wornitems.SLOT12);
				break;
			case 13:
				equipmentItemWidget = RS2Widgets.getWidget(InterfaceID.Wornitems.SLOT13);
				break;
			default:
				equipmentItemWidget = null;
				break;
		}
		return new WidgetItem(equipmentItemWidget.getItemId(), equipmentItemWidget.getItemQuantity(), equipmentItemWidget.getBounds(), equipmentItemWidget, equipmentItemWidget.getBounds());
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

	public String[] getActions()
	{
		final WidgetItem widgetItem = getWidgetItem();
		final Widget widget = widgetItem.getWidget();
		final String[] actions = Arrays.stream(widget.getActions())
			.filter(a -> a != null && !a.equals("null"))
			.toArray(String[]::new);
		return actions;
	}

	public String getAction(int index)
	{
		final String[] actions = getActions();

		if (index >= 0 && index < actions.length)
		{
			return actions[index];
		}
		return "null";
	}

	public int getActionIndex(String action)
	{
		return Arrays.asList(stripColTags(getActions())).indexOf(action) + 1;
	}

	public boolean hasAction(String action)
	{
		final WidgetItem widgetItem = getWidgetItem();
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

	public MenuAction getMenuAction(String action)
	{
		final MenuAction menuAction = isItemSelected() ? MenuAction.WIDGET_TARGET_ON_WIDGET
			: action.equalsIgnoreCase("use") ? MenuAction.WIDGET_TARGET
			: action.equalsIgnoreCase("cast") ? MenuAction.WIDGET_TARGET_ON_WIDGET
			: MenuAction.CC_OP;
		return menuAction;
	}

	public Point getClickPoint()
	{
		final WidgetItem widgetItem = getWidgetItem();
		if (widgetItem == null)
		{
			return null;
		}
		final Widget widget = widgetItem.getWidget();
		return widget != null ? Randomizer.getRandomPointIn(widget.getBounds()) : null;
	}

	public boolean isTradable()
	{
		return getComposition().isTradeable();
	}

	public boolean isStackable()
	{
		return getComposition().isStackable();
	}

	public boolean isMembers()
	{
		return getComposition().isMembers();
	}

	public int getNotedId()
	{
		return getComposition().getLinkedNoteId();
	}

	public boolean isNoted()
	{
		return getComposition().getNote() > -1;
	}

	public boolean isPlaceholder()
	{
		return getComposition().getPlaceholderTemplateId() > -1;
	}

	public int getStorePrice()
	{
		return getComposition().getPrice();
	}

	public void use()
	{
		if (isItemSelected())
		{
			return;
		}

		interact("Use");
	}

	public void useOn(RS2TileItem tileItem)
	{
		use();
		tileItem.interact(MenuAction.WIDGET_TARGET_ON_GROUND_ITEM);
	}

	public void useOn(RS2TileObject object)
	{
		use();
		object.interact(MenuAction.WIDGET_TARGET_ON_GAME_OBJECT);
	}

	public void useOn(RS2Item item)
	{
		use();
		item.interact("Cast");
	}

	public void useOn(RS2Player player)
	{
		use();
		player.interact(MenuAction.WIDGET_TARGET_ON_PLAYER);
	}

	public void useOn(RS2NPC npc)
	{
		use();
		npc.interact(MenuAction.WIDGET_TARGET_ON_NPC);
	}

	public void useOn(RS2Widget widget)
	{
		use();
		widget.interact("Cast");
	}
}
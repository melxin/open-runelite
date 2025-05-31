package net.runelite.client.plugins.openrl.api.rs2.items;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.runelite.api.Item;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Time;
import net.runelite.client.plugins.openrl.api.game.Vars;
import net.runelite.client.plugins.openrl.api.input.Mouse;
import net.runelite.client.plugins.openrl.api.managers.InteractionManager;
import net.runelite.client.plugins.openrl.api.rs2.widgets.RS2Dialog;
import net.runelite.client.plugins.openrl.api.rs2.widgets.RS2Widget;
import net.runelite.client.plugins.openrl.api.rs2.widgets.RS2Widgets;

public class RS2Bank extends RS2Items
{
	private static final RS2Bank BANK = new RS2Bank();
	private static final Inventory BANK_INVENTORY = new Inventory();

	private static final Supplier<RS2Widget> TAB_CONTAINER = () -> RS2Widgets.getWidget(InterfaceID.Bankmain.TABS);
	private static final Supplier<RS2Widget> BANK_CAPACITY = () -> RS2Widgets.getWidget(InterfaceID.Bankmain.CAPACITY);
	private static final Supplier<RS2Widget> RELEASE_PLACEHOLDERS = () -> RS2Widgets.getWidget(InterfaceID.Bankmain.RELEASE_PLACEHOLDERS);
	private static final Supplier<RS2Widget> SETTINGS_CONTAINER = () -> RS2Widgets.get(WidgetID.BANK_GROUP_ID, 48);
	private static final Supplier<RS2Widget> WITHDRAW_ITEM = () -> RS2Widgets.getWidget(InterfaceID.Bankmain.ITEM);
	private static final Supplier<RS2Widget> WITHDRAW_NOTE = () -> RS2Widgets.getWidget(InterfaceID.Bankmain.NOTE);
	private static final Supplier<RS2Widget> EXIT = () -> RS2Widgets.get(WidgetID.BANK_GROUP_ID, 2, 11);

	private RS2Bank()
	{
		super(InventoryID.BANK, InterfaceID.Bankmain.ITEMS);
	}

	/*@Nullable
	public static ItemContainer getItemContainer()
	{
		return Static.getGameDataCached().getItemContainer(InventoryID.BANK);
	}*/

	public static void setQuantityMode(QuantityMode quantityMode)
	{
		if (getQuantityMode() != quantityMode)
		{
			RS2Widget component = RS2Widgets.getWidget(quantityMode.interfaceId);
			if (RS2Widgets.isVisible(component))
			{
				component.interact(0);
			}
		}
	}

	public static QuantityMode getQuantityMode()
	{
		return QuantityMode.getCurrent();
	}

	public static int getFreeSlots()
	{
		if (!isOpen())
		{
			return -1;
		}

		return getCapacity() - getOccupiedSlots();
	}

	public static int getCapacity()
	{
		RS2Widget widget = BANK_CAPACITY.get();
		if (RS2Widgets.isVisible(widget))
		{
			return Integer.parseInt(widget.getText());
		}

		return -1;
	}

	public static int getOccupiedSlots()
	{
		RS2Widget widget = RS2Widgets.getWidget(InterfaceID.Bankmain.OCCUPIEDSLOTS);
		if (RS2Widgets.isVisible(widget))
		{
			return Integer.parseInt(widget.getText());
		}

		return -1;
	}

	public static void releasePlaceholders()
	{
		if (!isSettingsOpen())
		{
			toggleSettings();
			Time.sleepUntil(RS2Bank::isSettingsOpen, 5000);
		}

		RS2Widget widget = RELEASE_PLACEHOLDERS.get();
		if (widget != null)
		{
			widget.interact(5);
		}
	}

	public static void toggleSettings()
	{
		RS2Widget settingsButton = RS2Widgets.get(WidgetInfo.BANK_SETTINGS_BUTTON);
		if (settingsButton != null)
		{
			settingsButton.interact(0);
		}
	}

	public static boolean isSettingsOpen()
	{
		return RS2Widgets.isVisible(SETTINGS_CONTAINER.get());
	}

	public static void depositInventory()
	{
		RS2Widget widget = RS2Widgets.getWidget(InterfaceID.Bankmain.DEPOSITINV);
		if (widget != null)
		{
			widget.interact("Deposit inventory");
		}
	}

	public static void depositEquipment()
	{
		RS2Widget widget = RS2Widgets.getWidget(InterfaceID.Bankmain.DEPOSITWORN);
		if (widget != null)
		{
			widget.interact("Deposit worn items");
		}
	}

	public static boolean isOpen()
	{
		return RS2Widgets.isVisible(RS2Widgets.getWidget(InterfaceID.Bankmain.ITEMS_CONTAINER));
	}

	public static boolean isEmpty()
	{
		return getAll().isEmpty();
	}

	public static void depositAll(String... names)
	{
		depositAll(RS2Items.Predicates.names(names));
	}

	public static void depositAll(int... ids)
	{
		depositAll(RS2Items.Predicates.ids(ids));
	}

	public static void depositAll(Predicate<RS2Item> filter)
	{
		deposit(filter, Integer.MAX_VALUE);
	}

	public static void depositAllExcept(String... names)
	{
		depositAllExcept(RS2Items.Predicates.names(names));
	}

	public static void depositAllExcept(int... ids)
	{
		depositAllExcept(RS2Items.Predicates.ids(ids));
	}

	public static void depositAllExcept(Predicate<RS2Item> filter)
	{
		depositAll(filter.negate());
	}

	public static void deposit(String name, int amount)
	{
		deposit(x -> Objects.equals(x.getName(), name), amount);
	}

	public static void deposit(int id, int amount)
	{
		deposit(x -> x.getId() == id, amount);
	}

	public static void deposit(Predicate<RS2Item> filter, int amount)
	{
		RS2Item item = BANK_INVENTORY.getFirst(filter);
		if (item == null)
		{
			return;
		}

		String action = getAction(item, amount, false);
		item.interact(action);

		if (action.equals("Deposit-X"))
		{
			Time.sleepUntil(() -> RS2Dialog.isOpen(), 2000);
			if (RS2Dialog.isOpen())
			{
				RS2Dialog.enterAmount(amount);
			}
			Time.sleepTick();
		}
	}

	public static void withdrawAll(String name, WithdrawMode withdrawMode)
	{
		withdrawAll(x -> Objects.equals(x.getName(), name), withdrawMode);
	}

	public static void withdrawAll(int id, WithdrawMode withdrawMode)
	{
		withdrawAll(x -> x.getId() == id, withdrawMode);
	}

	public static void withdrawAll(Predicate<RS2Item> filter, WithdrawMode withdrawMode)
	{
		withdraw(filter, Integer.MAX_VALUE, withdrawMode);
	}

	public static void withdraw(String name, int amount, WithdrawMode withdrawMode)
	{
		withdraw(x -> Objects.equals(x.getName(), name), amount, withdrawMode);
	}

	public static void withdraw(int id, int amount, WithdrawMode withdrawMode)
	{
		withdraw(x -> x.getId() == id, amount, withdrawMode);
	}

	public static void withdraw(Predicate<RS2Item> filter, int amount, WithdrawMode withdrawMode)
	{
		RS2Item item = getFirst(filter.and(x -> !x.isPlaceholder()));

		if (item == null)
		{
			return;
		}

		String action = getAction(item, amount, true);

		if (withdrawMode == WithdrawMode.NOTED && !isNotedWithdrawMode())
		{
			setWithdrawMode(true);
			Time.sleepUntil(RS2Bank::isNotedWithdrawMode, 1200);
		}

		if (withdrawMode == WithdrawMode.ITEM && isNotedWithdrawMode())
		{
			setWithdrawMode(false);
			Time.sleepUntil(() -> !isNotedWithdrawMode(), 1200);
		}

		scrollToItem(item);

		item.interact(action);

		if (action.equals("Withdraw-X"))
		{
			Time.sleepUntil(() -> RS2Dialog.isOpen(), 2000);
			if (RS2Dialog.isOpen())
			{
				RS2Dialog.enterAmount(amount);
			}
			Time.sleepTick();
		}
	}

	public static void withdrawLastQuantity(String name, WithdrawMode withdrawMode)
	{
		withdrawLastQuantity(x -> Objects.equals(name, x.getName()), withdrawMode);
	}

	public static void withdrawLastQuantity(int id, WithdrawMode withdrawMode)
	{
		withdrawLastQuantity(x -> x.getId() == id, withdrawMode);
	}

	public static void withdrawLastQuantity(Predicate<RS2Item> filter, WithdrawMode withdrawMode)
	{
		RS2Item item = getFirst(filter.and(x -> !x.isPlaceholder()));

		if (item == null)
		{
			return;
		}

		WithdrawOption withdrawOption = WithdrawOption.LAST_QUANTITY;
		if (withdrawMode == WithdrawMode.NOTED && !isNotedWithdrawMode())
		{
			setWithdrawMode(true);
		}

		if (withdrawMode == WithdrawMode.ITEM && isNotedWithdrawMode())
		{
			setWithdrawMode(false);
		}

		scrollToItem(item);

		item.interact(withdrawOption.getMenuIndex());
	}

	public static void setWithdrawMode(boolean noted)
	{
		RS2Widget widget = noted ? WITHDRAW_NOTE.get() : WITHDRAW_ITEM.get();
		if (widget != null)
		{
			widget.interact(0);
		}
	}

	public static boolean isNotedWithdrawMode()
	{
		return Vars.getBit(VarbitID.BANK_QUANTITY_TYPE) == 1;
	}

	public static List<RS2Item> getAll(Predicate<RS2Item> filter)
	{
		return BANK.all(filter);
	}

	public static List<RS2Item> getAll()
	{
		return getAll(x -> true);
	}

	public static List<RS2Item> getAll(int... ids)
	{
		return BANK.all(ids);
	}

	public static List<RS2Item> getAll(String... names)
	{
		return BANK.all(names);
	}

	public static RS2Item getFirst(Predicate<RS2Item> filter)
	{
		return BANK.first(filter);
	}

	public static RS2Item getFirst(int... ids)
	{
		return BANK.first(ids);
	}

	public static RS2Item getFirst(String... names)
	{
		return BANK.first(names);
	}

	public static boolean contains(Predicate<RS2Item> filter)
	{
		return BANK.exists(filter);
	}

	public static boolean contains(int... id)
	{
		return BANK.exists(id);
	}

	public static boolean contains(String... name)
	{
		return BANK.exists(name);
	}

	public static int getCount(boolean stacks, Predicate<RS2Item> filter)
	{
		return BANK.count(stacks, filter);
	}

	public static int getCount(boolean stacks, int... ids)
	{
		return BANK.count(stacks, ids);
	}

	public static int getCount(boolean stacks, String... names)
	{
		return BANK.count(stacks, names);
	}

	public static int getCount(Predicate<RS2Item> filter)
	{
		return BANK.count(false, filter);
	}

	public static int getCount(int... ids)
	{
		return BANK.count(false, ids);
	}

	public static int getCount(String... names)
	{
		return BANK.count(false, names);
	}

	public static List<RS2Widget> getTabs()
	{
		return RS2Widgets.getChildren(WidgetInfo.BANK_TAB_CONTAINER, x -> x.hasAction("Collapse tab"));
	}

	public static boolean hasTabs()
	{
		return !getTabs().isEmpty();
	}

	public static void collapseTabs()
	{
		for (int i = 0; i < getTabs().size(); i++)
		{
			Widget tab = getTabs().get(i);
			//Static.getClient().interact(6, 1007, 11 + i, tab.getId());
		}
	}

	public static void collapseTab(int index)
	{
		RS2Widget tabContainer = RS2Widgets.getWidget(InterfaceID.Bankmain.TABS);
		if (!RS2Widgets.isVisible(tabContainer))
		{
			return;
		}

		int tabIdx = 11 + index;
		RS2Widget tab = tabContainer.getChild(tabIdx);
		if (!RS2Widgets.isVisible(tab))
		{
			return;
		}

		//Static.getClient().interact(6, 1007, tabIdx, tab.getId());
	}

	public static boolean isMainTabOpen()
	{
		return isTabOpen(0);
	}

	public static boolean isTabOpen(int index)
	{
		return Vars.getBit(VarbitID.BANK_CURRENTTAB) == index;
	}

	public static void openMainTab()
	{
		openTab(0);
	}

	public static void openTab(int index)
	{
		if (index < 0 || index > getTabs().size())
		{
			return;
		}

		RS2Widget tabContainer = TAB_CONTAINER.get();

		if (RS2Widgets.isVisible(tabContainer) && !isTabOpen(index))
		{
			tabContainer.getChild(10 + index).interact(0);
		}
	}

	public static class Inventory extends RS2Items
	{
		public Inventory()
		{
			super(InventoryID.INV, InterfaceID.Bankside.ITEMS);
		}

		public static List<RS2Item> getAll()
		{
			return getAll(x -> true);
		}

		public static List<RS2Item> getAll(Predicate<RS2Item> filter)
		{
			return BANK_INVENTORY.all(filter);
		}

		public static List<RS2Item> getAll(int... ids)
		{
			return BANK_INVENTORY.all(ids);
		}

		public static List<RS2Item> getAll(String... names)
		{
			return BANK_INVENTORY.all(names);
		}

		public static RS2Item getFirst(Predicate<RS2Item> filter)
		{
			return BANK_INVENTORY.first(filter);
		}

		public static RS2Item getFirst(int... ids)
		{
			return BANK_INVENTORY.first(ids);
		}

		public static RS2Item getFirst(String... names)
		{
			return BANK_INVENTORY.first(names);
		}

		public static int getCount(boolean stacks, Predicate<RS2Item> filter)
		{
			return BANK_INVENTORY.count(stacks, filter);
		}

		public static int getCount(boolean stacks, int... ids)
		{
			return BANK_INVENTORY.count(stacks, ids);
		}

		public static int getCount(boolean stacks, String... names)
		{
			return BANK_INVENTORY.count(stacks, names);
		}

		public static int getCount(Predicate<RS2Item> filter)
		{
			return BANK_INVENTORY.count(false, filter);
		}

		public static int getCount(int... ids)
		{
			return BANK_INVENTORY.count(false, ids);
		}

		public static int getCount(String... names)
		{
			return BANK_INVENTORY.count(false, names);
		}

		public static int getFreeSlots()
		{
			return 28 - getAll().size();
		}
	}

	private static String getAction(RS2Item item, int amount, Boolean withdraw)
	{
		String action = withdraw ? "Withdraw" : "Deposit";
		if (amount == 1)
		{
			action += "-1";
		}
		else if (amount == 5)
		{
			action += "-5";
		}
		else if (amount == 10)
		{
			action += "-10";
		}
		else if (withdraw && amount >= item.getQuantity())
		{
			action += "-All";
		}
		else if (!withdraw && amount >= BANK_INVENTORY.getCount(true, item.getId()))
		{
			action += "-All";
		}
		else
		{
			if (item.hasAction(action + "-" + amount))
			{
				action += "-" + amount;
			}
			else
			{
				action += "-X";
			}
		}
		return action;
	}

	/*public enum Component
	{
		BANK_REARRANGE_SWAP(InterfaceID.Bankmain.SWAP),
		BANK_REARRANGE_INSERT(InterfaceID.Bankmain.INSERT),
		BANK_WITHDRAW_ITEM(InterfaceID.Bankmain.ITEM),
		BANK_WITHDRAW_NOTE(InterfaceID.Bankmain.NOTE),
		BANK_QUANTITY_BUTTONS_CONTAINER(InterfaceID.Bankmain.QUANTITY_LAYER),
		BANK_QUANTITY_ONE(InterfaceID.Bankmain.QUANTITY1),
		BANK_QUANTITY_FIVE(InterfaceID.Bankmain.QUANTITY5),
		BANK_QUANTITY_TEN(InterfaceID.Bankmain.QUANTITY10),
		BANK_QUANTITY_X(InterfaceID.Bankmain.QUANTITYX),
		BANK_QUANTITY_ALL(InterfaceID.Bankmain.QUANTITYALL),
		BANK_PLACEHOLDERS_BUTTON(InterfaceID.Bankmain.PLACEHOLDER),
		EMPTY(-1);

		private final int interfaceId;

		Component(int interfaceId)
		{
			this.interfaceId = interfaceId;
		}
	}*/

	public enum QuantityMode
	{
		ONE(InterfaceID.Bankmain.QUANTITY1, 0),
		FIVE(InterfaceID.Bankmain.QUANTITY5, 1),
		TEN(InterfaceID.Bankmain.QUANTITY10, 2),
		X(InterfaceID.Bankmain.QUANTITYX, 3),
		ALL(InterfaceID.Bankmain.QUANTITYALL, 4),
		UNKNOWN(-1, -1);

		private final int interfaceId;
		private final int bitValue;

		QuantityMode(int interfaceId, int bitValue)
		{
			this.interfaceId = interfaceId;
			this.bitValue = bitValue;
		}

		public static QuantityMode getCurrent()
		{
			switch (Vars.getBit(VarbitID.BANK_QUANTITY_TYPE))
			{
				case 0:
					return QuantityMode.ONE;
				case 1:
					return QuantityMode.FIVE;
				case 2:
					return QuantityMode.TEN;
				case 3:
					return QuantityMode.X;
				case 4:
					return QuantityMode.ALL;
				default:
					return UNKNOWN;
			}
		}
	}

	public enum WithdrawMode
	{
		NOTED, ITEM, DEFAULT
	}

	private enum WithdrawOption
	{
		ONE(2),
		FIVE(3),
		TEN(4),
		LAST_QUANTITY(5),
		X(6),
		ALL(7),
		ALL_BUT_1(8);

		private final int menuIndex;

		WithdrawOption(int menuIndex)
		{
			this.menuIndex = menuIndex;
		}

		public int getMenuIndex()
		{
			//Special case
			if (getQuantityMode() == QuantityMode.ONE && this == WithdrawOption.ONE)
			{
				return 1;
			}
			return menuIndex;
		}

		public static WithdrawOption ofAmount(Item item, int amount)
		{
			if (amount <= 1)
			{
				return WithdrawOption.ONE;
			}

			if (amount == 5)
			{
				return WithdrawOption.FIVE;
			}

			if (amount == 10)
			{
				return WithdrawOption.TEN;
			}

			if (amount > item.getQuantity())
			{
				return WithdrawOption.ALL;
			}

			return WithdrawOption.X;
		}
	}

	public static void close()
	{
		RS2Widget exitBank = EXIT.get();
		if (!RS2Widgets.isVisible(exitBank))
		{
			return;
		}

		exitBank.interact("Close");
	}

	public static boolean isInViewport(RS2Item item)
	{
		final Rectangle bankItemsViewport = RS2Widgets.getWidget(InterfaceID.Bankmain.ITEMS).getBounds();
		final Rectangle itemBounds = item.getWidgetItem().getWidget().getBounds();
		return bankItemsViewport.intersects(itemBounds);
	}

	public static void scrollToItem(RS2Item item)
	{
		if (Static.getOpenRuneLiteConfig().interactionMethod() != InteractionManager.InteractMethod.MOUSE_EVENTS)
		{
			return;
		}

		while (!isInViewport(item))
		{
			final Rectangle bankItemsViewport = RS2Widgets.getWidget(InterfaceID.Bankmain.ITEMS).getBounds();
			Mouse.scrollDown(new Point(bankItemsViewport.x + bankItemsViewport.width / 2, bankItemsViewport.y + bankItemsViewport.height / 2));
			Time.sleepTicksUntil(() -> isInViewport(item), 1);
		}
	}
}
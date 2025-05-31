package net.runelite.client.plugins.openrl.api.rs2.providers.items;

import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.RS2ItemQuery;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Item;

@Slf4j
public class RS2Inventory extends RS2Items
{
	public static final int INV_COLUMS = 4;
	public static final int INV_ROWS = 7;
	public static final int INV_SIZE = INV_COLUMS * INV_ROWS;

	private static final RS2Inventory INVENTORY = new RS2Inventory();

	private RS2Inventory()
	{
		super(InventoryID.INV, InterfaceID.Inventory.ITEMS);
	}

	public static RS2ItemQuery query()
	{
		return RS2ItemQuery.inventoryQuery();
	}

	public static List<RS2Item> getAll(int... ids)
	{
		return INVENTORY.all(ids);
	}

	public static List<RS2Item> getAll(String... names)
	{
		return INVENTORY.all(names);
	}

	public static List<RS2Item> getAll()
	{
		return INVENTORY.all(x -> true);
	}

	public static List<RS2Item> getAll(Predicate<RS2Item> filter)
	{
		return INVENTORY.all(filter);
	}

	public static RS2Item getFirst(int... ids)
	{
		return INVENTORY.first(ids);
	}

	public static RS2Item getFirst(String... names)
	{
		return INVENTORY.first(names);
	}

	public static RS2Item getFirst(Predicate<RS2Item> filter)
	{
		return INVENTORY.all(filter).stream().findFirst().orElse(null);
	}

	public static boolean contains(int... ids)
	{
		return INVENTORY.exists(ids);
	}

	public static boolean contains(String... names)
	{
		return INVENTORY.exists(names);
	}

	public static boolean contains(Predicate<RS2Item> filter)
	{
		return INVENTORY.exists(filter);
	}

	public static int getCount(boolean stacks, int... ids)
	{
		return INVENTORY.count(stacks, ids);
	}

	public static int getCount(boolean stacks, String... names)
	{
		return INVENTORY.count(stacks, names);
	}

	public static int getCount(boolean stacks, Predicate<RS2Item> filter)
	{
		return INVENTORY.count(stacks, filter);
	}

	public static int getCount(int... ids)
	{
		return INVENTORY.count(false, ids);
	}

	public static int getCount(String... names)
	{
		return INVENTORY.count(false, names);
	}

	public static int getCount(Predicate<RS2Item> filter)
	{
		return INVENTORY.count(false, filter);
	}

	public static boolean isFull()
	{
		return getFreeSlots() == 0;
	}

	public static boolean isEmpty()
	{
		return getFreeSlots() == INV_SIZE;
	}

	public static int getFreeSlots()
	{
		return INV_SIZE - getAll().size();
	}

	public static RS2Item getItem(int slot)
	{
		final ItemContainer itemContainer = INVENTORY.getItemContainer();
		if (itemContainer == null)
		{
			return null;
		}

		final Item[] items = itemContainer.getItems();

		return new RS2Item(items[slot], slot, itemContainer.getId(), InterfaceID.Inventory.ITEMS);
	}

	public static int getSlot(int itemId)
	{
		final ItemContainer itemContainer = INVENTORY.getItemContainer();
		if (itemContainer == null)
		{
			return -1;
		}

		final Item[] items = itemContainer.getItems();

		for (int i = 0; i < items.length; i++)
		{
			if (items[i].getId() == itemId)
			{
				return i;
			}
		}
		return -1;
	}

	public static void dropAllExcept(int itemId)
	{
		dropAllExcept(net.runelite.client.plugins.openrl.api.commons.Predicates.idEquals(itemId));
	}

	public static void dropAllExcept(int... itemIds)
	{
		dropAllExcept(net.runelite.client.plugins.openrl.api.commons.Predicates.idEquals(itemIds));
	}

	public static void dropAllExcept(String itemName)
	{
		dropAllExcept(net.runelite.client.plugins.openrl.api.commons.Predicates.nameEquals(itemName));
	}

	public static void dropAllExcept(String... itemNames)
	{
		dropAllExcept(net.runelite.client.plugins.openrl.api.commons.Predicates.nameEquals(itemNames));
	}

	public static void dropAllExcept(Predicate<RS2Item> filter)
	{
		dropAll(filter.negate());
	}

	public static void dropAll(int itemId)
	{
		dropAll(net.runelite.client.plugins.openrl.api.commons.Predicates.idEquals(itemId));
	}

	public static void dropAll(int... itemIds)
	{
		dropAll(net.runelite.client.plugins.openrl.api.commons.Predicates.idEquals(itemIds));
	}

	public static void dropAll(String itemName)
	{
		dropAll(net.runelite.client.plugins.openrl.api.commons.Predicates.nameEquals(itemName));
	}

	public static void dropAll(String... itemNames)
	{
		dropAll(net.runelite.client.plugins.openrl.api.commons.Predicates.nameEquals(itemNames));
	}

	public static void dropAll(Predicate<RS2Item> filter)
	{
		dropAllShiftClick(getAll(filter));
	}

	public static void dropAllShiftClick(List<RS2Item> items)
	{
		net.runelite.client.plugins.openrl.Static.getClientThread().invoke(() ->
		{
			if (net.runelite.client.plugins.openrl.Static.getClient().getVarbitValue(net.runelite.api.gameval.VarbitID.DESKTOP_SHIFTCLICKDROP_ENABLED) == 0)
			{
				log.info("Enabling shift-click drop!");
				net.runelite.client.plugins.openrl.Static.getClient().setVarbit(net.runelite.api.gameval.VarbitID.DESKTOP_SHIFTCLICKDROP_ENABLED, 1);
			}
		});

		java.util.Collections.shuffle(items);

		net.runelite.client.plugins.openrl.api.input.Keyboard.pressed(java.awt.event.KeyEvent.VK_SHIFT);
		net.runelite.client.plugins.openrl.api.commons.Time.sleep(60, 200);
		for (RS2Item item : items)
		{
			if (item.getAction(0).equalsIgnoreCase("Drop"))
			{
				item.interact(0);
			}
			else
			{
				log.warn("Shift-click drop is broken!");
				item.interact("Drop");
			}
			net.runelite.client.plugins.openrl.api.commons.Time.sleep(45, 150);
		}
		net.runelite.client.plugins.openrl.api.input.Keyboard.released(java.awt.event.KeyEvent.VK_SHIFT);
	}
}
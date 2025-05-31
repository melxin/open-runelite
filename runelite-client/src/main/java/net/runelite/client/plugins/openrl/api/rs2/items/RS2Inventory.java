package net.runelite.client.plugins.openrl.api.rs2.items;

import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;

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

	public static List<RS2Item> getAll(Predicate<RS2Item> filter)
	{
		return INVENTORY.all(filter);
	}

	public static List<RS2Item> getAll()
	{
		return INVENTORY.all(x -> true);
	}

	public static List<RS2Item> getAll(int... ids)
	{
		return INVENTORY.all(ids);
	}

	public static List<RS2Item> getAll(String... names)
	{
		return INVENTORY.all(names);
	}

	public static RS2Item getFirst(Predicate<RS2Item> filter)
	{
		return INVENTORY.all(filter).stream().findFirst().orElse(null);
	}

	public static RS2Item getFirst(int... ids)
	{
		return INVENTORY.first(ids);
	}

	public static RS2Item getFirst(String... names)
	{
		return INVENTORY.first(names);
	}

	public static boolean contains(Predicate<RS2Item> filter)
	{
		return INVENTORY.exists(filter);
	}

	public static boolean contains(int... ids)
	{
		return INVENTORY.exists(ids);
	}

	public static boolean contains(String... names)
	{
		return INVENTORY.exists(names);
	}

	public static int getCount(boolean stacks, Predicate<RS2Item> filter)
	{
		return INVENTORY.count(stacks, filter);
	}

	public static int getCount(boolean stacks, int... ids)
	{
		return INVENTORY.count(stacks, ids);
	}

	public static int getCount(boolean stacks, String... names)
	{
		return INVENTORY.count(stacks, names);
	}

	public static int getCount(Predicate<RS2Item> filter)
	{
		return INVENTORY.count(false, filter);
	}

	public static int getCount(int... ids)
	{
		return INVENTORY.count(false, ids);
	}

	public static int getCount(String... names)
	{
		return INVENTORY.count(false, names);
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
}
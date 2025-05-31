package net.runelite.client.plugins.openrl.api.rs2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.plugins.openrl.Static;

public class RS2Inventory
{
	public static final int ITEM_EMPTY = 6512;
	public static final int SIZE = 28;

	private static final RS2Inventory INVENTORY = new RS2Inventory();

	private RS2Inventory()
	{
	}

	protected List<RS2Item> all(Predicate<RS2Item> filter)
	{
		final ItemContainer itemContainer = Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getItemContainer(InventoryID.INV)).orElse(null);
		if (itemContainer == null)
		{
			return Collections.emptyList();
		}

		final Item[] items = itemContainer.getItems();
		final RS2Item[] rs2Items = new RS2Item[items.length];
		for (int i = 0; i < items.length; i++)
		{
			rs2Items[i] = new RS2Item(items[i], i);
		}
		return Stream.of(rs2Items)
			.filter(filter)
			.collect(Collectors.toList());
	}

	public static List<RS2Item> getAll(Predicate<RS2Item> filter)
	{
		return INVENTORY.all(filter);
	}

	public static List<RS2Item> getAll()
	{
		return getAll(x -> true);
	}

	public static List<RS2Item> getAll(int... ids)
	{
		return INVENTORY.all(x -> Arrays.asList(ids).contains(x.getId()));
	}

	public static List<RS2Item> getAll(String... names)
	{
		return INVENTORY.all(x -> Arrays.asList(names).contains(x.getName()));
	}

	public static RS2Item getFirst(Predicate<RS2Item> filter)
	{
		return INVENTORY.all(filter).stream().findFirst().orElse(null);
	}

	public static RS2Item getFirst(int... ids)
	{
		return getAll(ids).stream().findFirst().orElse(null);
	}

	public static RS2Item getFirst(String... names)
	{
		return getAll(names).stream().findFirst().orElse(null);
	}

	protected boolean exists(Predicate<RS2Item> filter)
	{
		return getFirst(filter) != null;
	}

	protected boolean exists(String... name)
	{
		return getFirst(name) != null;
	}

	protected boolean exists(int... id)
	{
		return getFirst(id) != null;
	}

	public static RS2Item getItem(int slot)
	{
		final ItemContainer itemContainer = Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getItemContainer(InventoryID.INV)).orElse(null);
		if (itemContainer == null)
		{
			return null;
		}

		final Item[] items = itemContainer.getItems();

		return new RS2Item(items[slot], slot);
	}

	public int getSlot(int itemId)
	{
		final ItemContainer itemContainer = Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getItemContainer(InventoryID.INV)).orElse(null);
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

	public static boolean contains(Predicate<RS2Item> filter)
	{
		return !INVENTORY.all(filter).isEmpty();
	}

	public static boolean contains(int... id)
	{
		return !INVENTORY.all(x -> Arrays.asList(x).contains(id)).isEmpty();
	}

	public static boolean contains(String... name)
	{
		return !INVENTORY.all(x -> Arrays.asList(name).contains(x.getName())).isEmpty();
	}

	public static int getCount(boolean stacks, Predicate<RS2Item> filter)
	{
		return INVENTORY.all(filter).stream().mapToInt(x -> stacks ? x.getQuantity() : 1).sum();
	}

	public static int getCount(boolean stacks, int... ids)
	{
		return getCount(stacks, x -> Arrays.asList(ids).contains(x.getId()));
	}

	public static int getCount(boolean stacks, String... names)
	{
		return getCount(stacks, x -> Arrays.asList(names).contains(x.getName()));
	}

	public static int getCount(Predicate<RS2Item> filter)
	{
		return getCount(false, filter);
	}

	public static int getCount(int... ids)
	{
		return getCount(false, ids);
	}

	public static int getCount(String... names)
	{
		return getCount(false, names);
	}

	public static boolean isFull()
	{
		return getFreeSlots() == 0;
	}

	public static boolean isEmpty()
	{
		return getFreeSlots() == RS2Inventory.SIZE;
	}

	public static int getFreeSlots()
	{
		return RS2Inventory.SIZE - getAll().size();
	}
}
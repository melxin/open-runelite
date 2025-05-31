package net.runelite.client.plugins.openrl.api.rs2.items;

import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;

public class RS2Equipment extends RS2Items
{
	private static final RS2Equipment EQUIPMENT = new RS2Equipment();

	private RS2Equipment()
	{
		super(InventoryID.WORN, InterfaceID.WORNITEMS);
	}

	public static List<RS2Item> getAll()
	{
		return getAll(x -> true);
	}

	public static List<RS2Item> getAll(Predicate<RS2Item> filter)
	{
		return EQUIPMENT.all(filter);
	}

	public static List<RS2Item> getAll(int... ids)
	{
		return EQUIPMENT.all(ids);
	}

	public static List<RS2Item> getAll(String... names)
	{
		return EQUIPMENT.all(names);
	}

	public static RS2Item getFirst(Predicate<RS2Item> filter)
	{
		return EQUIPMENT.first(filter);
	}

	public static RS2Item getFirst(int... ids)
	{
		return EQUIPMENT.first(ids);
	}

	public static RS2Item getFirst(String... names)
	{
		return EQUIPMENT.first(names);
	}

	/*private static WidgetInfo getEquipmentWidgetInfo(int itemIndex)
	{
		for (EquipmentInventorySlot equipmentInventorySlot : EquipmentInventorySlot.values())
		{
			if (equipmentInventorySlot.getSlotIdx() == itemIndex)
			{
				return equipmentInventorySlot.getWidgetInfo();
			}
		}

		return null;
	}*/

	public static boolean contains(Predicate<RS2Item> filter)
	{
		return EQUIPMENT.exists(filter);
	}

	public static boolean contains(int... id)
	{
		return EQUIPMENT.exists(id);
	}

	public static boolean contains(String... name)
	{
		return EQUIPMENT.exists(name);
	}

	public static int getCount(boolean stacks, Predicate<RS2Item> filter)
	{
		return EQUIPMENT.count(stacks, filter);
	}

	public static int getCount(boolean stacks, int... ids)
	{
		return EQUIPMENT.count(stacks, ids);
	}

	public static int getCount(boolean stacks, String... names)
	{
		return EQUIPMENT.count(stacks, names);
	}

	public static int getCount(Predicate<RS2Item> filter)
	{
		return EQUIPMENT.count(false, filter);
	}

	public static int getCount(int... ids)
	{
		return EQUIPMENT.count(false, ids);
	}

	public static int getCount(String... names)
	{
		return EQUIPMENT.count(false, names);
	}

	/*public static Item fromSlot(EquipmentInventorySlot slot)
	{
		return getFirst(x -> slot.getWidgetInfo().getPackedId() == x.getWidgetId());
	}*/
}

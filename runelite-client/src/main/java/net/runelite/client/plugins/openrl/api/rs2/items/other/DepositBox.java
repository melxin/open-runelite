package net.runelite.client.plugins.openrl.api.rs2.items.other;

import java.util.function.Supplier;
import net.runelite.client.plugins.openrl.api.rs2.widgets.RS2Widget;
import net.runelite.client.plugins.openrl.api.rs2.widgets.RS2Widgets;

public class DepositBox
{
	private DepositBox()
	{
	}

	private static final Supplier<RS2Widget> DEPOSIT_INV = () -> RS2Widgets.get(192, 4);
	private static final Supplier<RS2Widget> DEPOSIT_EQUIPS = () -> RS2Widgets.get(192, 6);
	private static final Supplier<RS2Widget> DEPOSIT_LOOTINGBAG = () -> RS2Widgets.get(192, 8);
	private static final Supplier<RS2Widget> ROOT = () -> RS2Widgets.get(192, 1);
	private static final Supplier<RS2Widget> EXIT = () -> RS2Widgets.get(192, 1, 11);
	private static final Supplier<RS2Widget> QUANTITY_ONE = () -> RS2Widgets.get(192, 11);
	private static final Supplier<RS2Widget> QUANTITY_FIVE = () -> RS2Widgets.get(192, 13);
	private static final Supplier<RS2Widget> QUANTITY_TEN = () -> RS2Widgets.get(192, 15);
	private static final Supplier<RS2Widget> QUANTITY_X = () -> RS2Widgets.get(192, 17);
	private static final Supplier<RS2Widget> QUANTITY_ALL = () -> RS2Widgets.get(192, 19);

	public static void depositInventory()
	{
		RS2Widget depositInventory = DEPOSIT_INV.get();
		if (!RS2Widgets.isVisible(depositInventory))
		{
			return;
		}

		depositInventory.interact(0);
	}

	public static void depositEquipment()
	{
		RS2Widget depositEquipment = DEPOSIT_EQUIPS.get();
		if (!RS2Widgets.isVisible(depositEquipment))
		{
			return;
		}

		depositEquipment.interact(0);
	}

	public static void depositLootingBag()
	{
		RS2Widget depositLootingbag = DEPOSIT_LOOTINGBAG.get();
		if (!RS2Widgets.isVisible(depositLootingbag))
		{
			return;
		}

		depositLootingbag.interact(0);
	}

	public static void selectQuantityOne()
	{
		RS2Widget selectQuantityOne = QUANTITY_ONE.get();
		if (!RS2Widgets.isVisible(selectQuantityOne))
		{
			return;
		}

		selectQuantityOne.interact(0);
	}

	public static void selectQuantityFive()
	{
		RS2Widget selectQuantityFive = QUANTITY_FIVE.get();
		if (!RS2Widgets.isVisible(selectQuantityFive))
		{
			return;
		}

		selectQuantityFive.interact(0);
	}

	public static void selectQuantityTen()
	{
		RS2Widget selectQuantityTen = QUANTITY_TEN.get();
		if (!RS2Widgets.isVisible(selectQuantityTen))
		{
			return;
		}

		selectQuantityTen.interact(0);
	}

	public static void selectQuantityX()
	{
		RS2Widget selectQuantityX = QUANTITY_X.get();
		if (!RS2Widgets.isVisible(selectQuantityX))
		{
			return;
		}

		selectQuantityX.interact(0);
	}

	public static void selectQuantityAll()
	{
		RS2Widget selectQuantityAll = QUANTITY_ALL.get();
		if (!RS2Widgets.isVisible(selectQuantityAll))
		{
			return;
		}

		selectQuantityAll.interact(0);
	}

	public static boolean isOpen()
	{
		RS2Widget depositBox = ROOT.get();
		return RS2Widgets.isVisible(depositBox);
	}

	public static void close()
	{
		RS2Widget exitDepositBox = EXIT.get();
		if (!RS2Widgets.isVisible(exitDepositBox))
		{
			return;
		}

		exitDepositBox.interact(0);
	}
}

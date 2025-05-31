/*
 * Copyright (c) 2025, Melxin <https://github.com/melxin>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.openrl.api.rs2.providers.items.other;

import lombok.extern.slf4j.Slf4j;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.plugins.openrl.api.commons.Predicates;
import net.runelite.client.plugins.openrl.api.commons.Time;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.RS2GameObjectQuery;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.RS2ItemQuery;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.RS2Widgets;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.dialog.RS2Dialog;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2GameObject;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Item;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;

@Slf4j
public class RS2DepositBox
{
	private RS2DepositBox()
	{
	}

	private static final Supplier<RS2Widget> DEPOSIT_INV = () -> RS2Widgets.getWidget(InterfaceID.BankDepositbox.DEPOSIT_INV);
	private static final Supplier<RS2Widget> DEPOSIT_WORN = () -> RS2Widgets.getWidget(InterfaceID.BankDepositbox.DEPOSIT_WORN);
	private static final Supplier<RS2Widget> DEPOSIT_LOOTINGBAG = () -> RS2Widgets.getWidget(InterfaceID.BankDepositbox.DEPOSIT_LOOTINGBAG);
	private static final Supplier<RS2Widget> ROOT = () -> RS2Widgets.getWidget(InterfaceID.BankDepositbox.FRAME);
	private static final Supplier<RS2Widget> EXIT = () -> ROOT.get().getChild(11);
	private static final Supplier<RS2Widget> QUANTITY_ONE = () -> RS2Widgets.getWidget(InterfaceID.BankDepositbox._1);
	private static final Supplier<RS2Widget> QUANTITY_FIVE = () -> RS2Widgets.getWidget(InterfaceID.BankDepositbox._5);
	private static final Supplier<RS2Widget> QUANTITY_TEN = () -> RS2Widgets.getWidget(InterfaceID.BankDepositbox._10);
	private static final Supplier<RS2Widget> QUANTITY_X = () -> RS2Widgets.getWidget(InterfaceID.BankDepositbox.X);
	private static final Supplier<RS2Widget> QUANTITY_ALL = () -> RS2Widgets.getWidget(InterfaceID.BankDepositbox.ALL);

	public static RS2ItemQuery inventoryQuery()
	{
		return RS2ItemQuery.depositBoxInventoryQuery();
	}

	public static RS2ItemQuery equipmentQuery()
	{
		return RS2ItemQuery.depositBoxEquipmentQuery();
	}

	public static void depositInventory()
	{
		final RS2Widget depositInventory = DEPOSIT_INV.get();
		if (!RS2Widgets.isVisible(depositInventory))
		{
			return;
		}

		depositInventory.interact(0);
	}

	public static void depositEquipment()
	{
		final RS2Widget depositEquipment = DEPOSIT_WORN.get();
		if (!RS2Widgets.isVisible(depositEquipment))
		{
			return;
		}

		depositEquipment.interact(0);
	}

	public static void depositLootingBag()
	{
		final RS2Widget depositLootingbag = DEPOSIT_LOOTINGBAG.get();
		if (!RS2Widgets.isVisible(depositLootingbag))
		{
			return;
		}

		depositLootingbag.interact(0);
	}

	public static void selectQuantityOne()
	{
		final RS2Widget selectQuantityOne = QUANTITY_ONE.get();
		if (!RS2Widgets.isVisible(selectQuantityOne))
		{
			return;
		}

		selectQuantityOne.interact(0);
	}

	public static void selectQuantityFive()
	{
		final RS2Widget selectQuantityFive = QUANTITY_FIVE.get();
		if (!RS2Widgets.isVisible(selectQuantityFive))
		{
			return;
		}

		selectQuantityFive.interact(0);
	}

	public static void selectQuantityTen()
	{
		final RS2Widget selectQuantityTen = QUANTITY_TEN.get();
		if (!RS2Widgets.isVisible(selectQuantityTen))
		{
			return;
		}

		selectQuantityTen.interact(0);
	}

	public static void selectQuantityX()
	{
		final RS2Widget selectQuantityX = QUANTITY_X.get();
		if (!RS2Widgets.isVisible(selectQuantityX))
		{
			return;
		}

		selectQuantityX.interact(0);
	}

	public static void setQuantity(int quantity)
	{
		final RS2Widget selectQuantityX = QUANTITY_X.get();
		if (!RS2Widgets.isVisible(selectQuantityX))
		{
			return;
		}

		if (selectQuantityX.hasAction("Set custom quantity"))
		{
			selectQuantityX.interact("Set custom quantity");
		}
		else
		{
			selectQuantityX.interact(0);
		}

		Time.sleepUntil(() -> RS2Dialog.isEnterInputOpen(), 1200);
		if (RS2Dialog.isEnterInputOpen())
		{
			RS2Dialog.enterAmount(quantity);
		}
	}

	public static void selectQuantityAll()
	{
		final RS2Widget selectQuantityAll = QUANTITY_ALL.get();
		if (!RS2Widgets.isVisible(selectQuantityAll))
		{
			return;
		}

		selectQuantityAll.interact(0);
	}

	public static boolean isOpen()
	{
		final RS2Widget depositBox = ROOT.get();
		return RS2Widgets.isVisible(depositBox);
	}

	public static void close()
	{
		final RS2Widget exitDepositBox = EXIT.get();
		if (!RS2Widgets.isVisible(exitDepositBox))
		{
			return;
		}

		exitDepositBox.interact(0);
	}

	public static void openNearest()
	{
		if (isOpen())
		{
			return;
		}

		final RS2GameObject depositBox = RS2GameObjectQuery.query()
			.nameContains("deposit box")
			.getNearest();

		if (depositBox == null)
		{
			log.warn("No deposit box nearby!");
			return;
		}

		depositBox.interact("Deposit");
	}

	public static void depositAll(int id)
	{
		depositAll(Predicates.idEquals(id));
	}

	public static void depositAll(int... ids)
	{
		depositAll(Predicates.idEquals(ids));
	}

	public static void depositAll(Collection<Integer> ids)
	{
		depositAll(Predicates.idEquals(ids));
	}

	public static void depositAll(String name)
	{
		depositAll(Predicates.nameEquals(name));
	}

	public static void depositAll(String... names)
	{
		depositAll(Predicates.nameEquals(names));
	}

	public static void depositAll(Predicate<RS2Item> filter)
	{
		deposit(filter, Integer.MAX_VALUE);
	}

	public static void depositAllExcept(int id)
	{
		depositAllExcept(Predicates.idEquals(id));
	}

	public static void depositAllExcept(int... ids)
	{
		depositAllExcept(Predicates.idEquals(ids));
	}

	public static void depositAllExcept(String name)
	{
		depositAllExcept(Predicates.nameEquals(name));
	}

	public static void depositAllExcept(String... names)
	{
		depositAllExcept(Predicates.nameEquals(names));
	}

	public static void depositAllExcept(Predicate<RS2Item> filter)
	{
		depositAll(filter.negate());
	}

	public static void deposit(int id, int amount)
	{
		deposit(Predicates.idEquals(id), amount);
	}

	public static void deposit(String name, int amount)
	{
		deposit(Predicates.nameEquals(name), amount);
	}

	public static void deposit(int amount, int... ids)
	{
		deposit(Predicates.idEquals(ids), amount);
	}

	public static void deposit(int amount, String... names)
	{
		deposit(Predicates.nameEquals(names), amount);
	}

	public static void deposit(Predicate<RS2Item> filter, int amount)
	{
		inventoryQuery()
			.and(filter)
			.distinctBy(RS2Item::getId)
			.shuffle()
			.result()
			.forEach(item ->
			{
				final String depositAction = getDepositAction(item, amount);
				item.interact(depositAction);
				if (depositAction.equals("Deposit-X"))
				{
					Time.sleepUntil(() -> RS2Dialog.isOpen(), 1200);
					if (RS2Dialog.isOpen())
					{
						RS2Dialog.enterAmount(amount);
					}
					Time.sleepTick();
				}
			});
	}

	private static String getDepositAction(RS2Item item, int amount)
	{
		String action = "Deposit";
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
		else if (amount >= inventoryQuery().idEquals(item.getId()).count(true))
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

	public static void depositEquipmentAll(int id)
	{
		depositEquipmentAll(Predicates.idEquals(id));
	}

	public static void depositEquipmentAll(int... ids)
	{
		depositEquipmentAll(Predicates.idEquals(ids));
	}

	public static void depositEquipmentAll(Collection<Integer> ids)
	{
		depositEquipmentAll(Predicates.idEquals(ids));
	}

	public static void depositEquipmentAll(String name)
	{
		depositEquipmentAll(Predicates.nameEquals(name));
	}

	public static void depositEquipmentAll(String... names)
	{
		depositEquipmentAll(Predicates.nameEquals(names));
	}

	public static void depositEquipmentAll(Predicate<RS2Item> filter)
	{
		depositEquipment(filter);
	}

	public static void depositEquipmentAllExcept(int id)
	{
		depositEquipmentAllExcept(Predicates.idEquals(id));
	}

	public static void depositEquipmentAllExcept(int... ids)
	{
		depositEquipmentAllExcept(Predicates.idEquals(ids));
	}

	public static void depositEquipmentAllExcept(String name)
	{
		depositEquipmentAllExcept(Predicates.nameEquals(name));
	}

	public static void depositEquipmentAllExcept(String... names)
	{
		depositEquipmentAllExcept(Predicates.nameEquals(names));
	}

	public static void depositEquipmentAllExcept(Predicate<RS2Item> filter)
	{
		depositEquipmentAll(filter.negate());
	}

	public static void depositEquipment(int id)
	{
		depositEquipment(Predicates.idEquals(id));
	}

	public static void depositEquipment(String name)
	{
		depositEquipment(Predicates.nameEquals(name));
	}

	public static void depositEquipment(int... ids)
	{
		depositEquipment(Predicates.idEquals(ids));
	}

	public static void depositEquipment(String... names)
	{
		depositEquipment(Predicates.nameEquals(names));
	}

	public static void depositEquipment(EquipmentInventorySlot slot)
	{
		depositEquipment(x -> x.getSlot() == slot.getSlotIdx());
	}

	public static void depositEquipment(Predicate<RS2Item> filter)
	{
		equipmentQuery()
			.and(filter)
			.result()
			.forEach(item ->
			{
				item.interact("Bank");
			});
	}
}
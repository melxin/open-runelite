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
package net.runelite.client.plugins.openrl.api.rs2.providers.query;

import lombok.Getter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.annotations.Component;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Predicates;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.abstraction.AbstractQuery;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Item;

public class RS2ItemQuery extends AbstractQuery<RS2Item, RS2ItemQuery>
{
	public static final int ITEM_EMPTY = 6512;

	@Getter
	private final int itemContainerId;

	@Getter
	private final int inventoryInterfaceId;

	private RS2ItemQuery(int itemContainerId, @Component int inventoryInterfaceId)
	{
		this.itemContainerId = itemContainerId;
		this.inventoryInterfaceId = inventoryInterfaceId;
	}

	public static RS2ItemQuery from(int itemContainerId, @Component int inventoryInterfaceId)
	{
		return new RS2ItemQuery(itemContainerId, inventoryInterfaceId);
	}

	public static RS2ItemQuery inventoryQuery()
	{
		return from(InventoryID.INV, InterfaceID.Inventory.ITEMS);
	}

	public static RS2ItemQuery equipmentQuery()
	{
		return from(InventoryID.WORN, InterfaceID.WORNITEMS);
	}

	public static RS2ItemQuery bankQuery()
	{
		return from(InventoryID.BANK, InterfaceID.Bankmain.ITEMS);
	}

	public static RS2ItemQuery bankInventoryQuery()
	{
		return from(InventoryID.INV, InterfaceID.Bankside.ITEMS);
	}

	public static RS2ItemQuery depositBoxInventoryQuery()
	{
		return from(InventoryID.INV, InterfaceID.BankDepositbox.INVENTORY);
	}

	public static RS2ItemQuery depositBoxEquipmentQuery()
	{
		return from(InventoryID.WORN, InterfaceID.BankDepositbox.WORN);
	}

	protected ItemContainer getItemContainer()
	{
		return Static.getGameDataCached().getItemContainer(itemContainerId);
	}

	@Override
	protected List<RS2Item> all(Predicate<? super RS2Item> filter)
	{
		final ItemContainer itemContainer = this.getItemContainer();
		if (itemContainer == null)
		{
			return Collections.emptyList();
		}

		final Item[] items = itemContainer.getItems();

		return IntStream.range(0, items.length)
			.mapToObj(i -> new RS2Item(items[i], i, itemContainerId, inventoryInterfaceId))
			.filter(rs2Item ->
			{
				int id = rs2Item.getId();
				return id != -1 && id != ITEM_EMPTY && filter.test(rs2Item);
			})
			.collect(Collectors.toList());
	}

	// Query

	public RS2ItemQuery idEquals(int id)
	{
		return and(Predicates.idEquals(id));
	}

	public RS2ItemQuery idEquals(int... ids)
	{
		return and(Predicates.idEquals(ids));
	}

	public RS2ItemQuery idEquals(Collection<Integer> ids)
	{
		return and(Predicates.idEquals(ids));
	}

	public RS2ItemQuery nameEquals(String name)
	{
		return and(Predicates.nameEquals(name));
	}

	public RS2ItemQuery nameEquals(String... names)
	{
		return and(Predicates.nameEquals(names));
	}

	public RS2ItemQuery nameEquals(Collection<String> names)
	{
		return and(Predicates.nameEquals(names));
	}

	public RS2ItemQuery nameContains(String name)
	{
		return and(Predicates.nameContains(name));
	}

	public RS2ItemQuery nameContains(String... names)
	{
		return and(Predicates.nameContains(names));
	}

	public RS2ItemQuery actionEquals(String action)
	{
		return and(Predicates.actionEquals(action));
	}

	public RS2ItemQuery actionEquals(String... actions)
	{
		return and(Predicates.actionEquals(actions));
	}

	public RS2ItemQuery actionContains(String action)
	{
		return and(Predicates.actionContains(action));
	}

	public RS2ItemQuery actionContains(String... actions)
	{
		return and(Predicates.actionContains(actions));
	}

	public RS2ItemQuery slotEquals(int slot)
	{
		return and(x -> x.getSlot() == slot);
	}

	public RS2ItemQuery slotEquals(int... slots)
	{
		return and(x ->
		{
			final int itemSlot = x.getSlot();
			for (int slot : slots)
			{
				if (itemSlot == slot)
				{
					return true;
				}
			}
			return false;
		});
	}

	public RS2ItemQuery slotEquals(Collection<Integer> slots)
	{
		return and(x -> slots.contains(x.getSlot()));
	}

	public RS2ItemQuery tradable()
	{
		return and(x -> x.isTradable());
	}

	public RS2ItemQuery notTradable()
	{
		return not(x -> x.isTradable());
	}

	public RS2ItemQuery stackable()
	{
		return and(x -> x.isStackable());
	}

	public RS2ItemQuery notStackable()
	{
		return not(x -> x.isStackable());
	}

	public RS2ItemQuery members()
	{
		return and(x -> x.isMembers());
	}

	public RS2ItemQuery notMembers()
	{
		return not(x -> x.isMembers());
	}

	public RS2ItemQuery noted()
	{
		return and(x -> x.isNoted());
	}

	public RS2ItemQuery notNoted()
	{
		return not(x -> x.isNoted());
	}

	public RS2ItemQuery placeholder()
	{
		return and(x -> x.isPlaceholder());
	}

	public RS2ItemQuery notPlaceholder()
	{
		return not(x -> x.isPlaceholder());
	}

	// Results

	public boolean exists()
	{
		return first() != null;
	}

	public int count(boolean stacks)
	{
		return stream().mapToInt(x -> stacks ? x.getQuantity() : 1).sum();
	}
}
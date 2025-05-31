package net.runelite.client.plugins.openrl.api.rs2.providers.items;

import lombok.Getter;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.annotations.Component;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Predicates;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Item;

public abstract class RS2Items
{
	public static final int ITEM_EMPTY = 6512;

	@Getter
	private final int itemContainerId;

	@Getter
	private final int inventoryInterfaceId;

	protected RS2Items(int itemContainerId, @Component int inventoryInterfaceId)
	{
		this.itemContainerId = itemContainerId;
		this.inventoryInterfaceId = inventoryInterfaceId;
	}

	protected ItemContainer getItemContainer()
	{
		return Static.getGameDataCached().getItemContainer(itemContainerId);
	}

	protected List<RS2Item> all(int... ids)
	{
		return all(Predicates.idEquals(ids));
	}

	protected List<RS2Item> all(String... names)
	{
		return all(Predicates.nameEquals(names));
	}

	protected List<RS2Item> all(Predicate<RS2Item> filter)
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

	protected RS2Item first(int... ids)
	{
		return first(Predicates.idEquals(ids));
	}

	protected RS2Item first(String... names)
	{
		return first(Predicates.nameEquals(names));
	}

	protected RS2Item first(Predicate<RS2Item> filter)
	{
		return all(filter).stream().findFirst().orElse(null);
	}

	protected boolean exists(int... ids)
	{
		return first(ids) != null;
	}

	protected boolean exists(String... names)
	{
		return first(names) != null;
	}

	protected boolean exists(Predicate<RS2Item> filter)
	{
		return first(filter) != null;
	}

	protected int count(boolean stacks, int... ids)
	{
		return all(ids).stream().mapToInt(x -> stacks ? x.getQuantity() : 1).sum();
	}

	protected int count(boolean stacks, String... names)
	{
		return all(names).stream().mapToInt(x -> stacks ? x.getQuantity() : 1).sum();
	}

	protected int count(boolean stacks, Predicate<RS2Item> filter)
	{
		return all(filter).stream().mapToInt(x -> stacks ? x.getQuantity() : 1).sum();
	}
}
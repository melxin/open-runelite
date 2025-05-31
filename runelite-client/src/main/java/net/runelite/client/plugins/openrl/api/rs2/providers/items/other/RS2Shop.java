package net.runelite.client.plugins.openrl.api.rs2.providers.items.other;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.RS2NPCQuery;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.RS2Widgets;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2NPC;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;

@Slf4j
public class RS2Shop
{
	private static final Supplier<RS2Widget> SHOP = () -> RS2Widgets.getWidget(InterfaceID.Shopmain.UNIVERSE);
	private static final Supplier<RS2Widget> SHOP_ITEMS = () -> RS2Widgets.getWidget(InterfaceID.Shopmain.ITEMS);
	private static final Supplier<RS2Widget> INVENTORY = () -> RS2Widgets.getWidget(InterfaceID.Shopside.ITEMS);
	//private static final Supplier<RS2Widget> SHOP = () -> RS2Widgets.get(300, 0);
	//private static final Supplier<RS2Widget> SHOP_ITEMS = () -> RS2Widgets.get(300, 16);
	//private static final Supplier<RS2Widget> INVENTORY = () -> RS2Widgets.get(301, 0);

	public static boolean isOpen()
	{
		return RS2Widgets.isVisible(SHOP.get());
	}

	public static void openNearest()
	{
		if (isOpen())
		{
			return;
		}

		final RS2NPC shopNpc = RS2NPCQuery.query()
			.actionEquals("Trade")
			.getNearest();

		if (shopNpc == null)
		{
			log.warn("No shop nearby!");
			return;
		}

		shopNpc.interact("Trade");
	}

	public static int getStock(int itemId)
	{
		final RS2Widget items = SHOP_ITEMS.get();
		if (!RS2Widgets.isVisible(items))
		{
			return 0;
		}

		final RS2Widget[] children = items.getChildren();
		if (children == null)
		{
			return 0;
		}

		return Arrays.stream(children)
			.filter(child -> child.getItemId() == itemId)
			.mapToInt(Widget::getItemQuantity)
			.sum();
	}

	public static void buyOne(int itemId)
	{
		buy(itemId, 1);
	}

	public static void buyOne(String itemName)
	{
		buy(itemName, 1);
	}

	public static void buyFive(int itemId)
	{
		buy(itemId, 5);
	}

	public static void buyFive(String itemName)
	{
		buy(itemName, 5);
	}

	public static void buyTen(int itemId)
	{
		buy(itemId, 10);
	}

	public static void buyTen(String itemName)
	{
		buy(itemName, 10);
	}

	public static void buyFifty(int itemId)
	{
		buy(itemId, 50);
	}

	public static void buyFifty(String itemName)
	{
		buy(itemName, 50);
	}

	public static void sellOne(int itemId)
	{
		sell(itemId, 1);
	}

	public static void sellOne(String itemName)
	{
		sell(itemName, 1);
	}

	public static void sellFive(int itemId)
	{
		sell(itemId, 5);
	}

	public static void sellFive(String itemName)
	{
		sell(itemName, 5);
	}

	public static void sellTen(int itemId)
	{
		sell(itemId, 10);
	}

	public static void sellTen(String itemName)
	{
		sell(itemName, 10);
	}

	public static void sellFifty(int itemId)
	{
		sell(itemId, 50);
	}

	public static void sellFifty(String itemName)
	{
		sell(itemName, 50);
	}

	public static List<Integer> getItems()
	{
		final List<Integer> out = new ArrayList<>();
		final RS2Widget container = SHOP_ITEMS.get();
		if (container == null)
		{
			return out;
		}

		final RS2Widget[] items = container.getChildren();
		if (items == null)
		{
			return out;
		}

		for (RS2Widget item : items)
		{
			final int itemId = item.getId();
			if (itemId == -1 || itemId == 6512)
			{
				continue;
			}
			out.add(item.getItemId());
		}
		return out;
	}

	private static void buy(int itemId, int amount)
	{
		exchange(itemId, amount, SHOP_ITEMS.get());
	}

	private static void buy(String itemName, int amount)
	{
		exchange(itemName, amount, SHOP_ITEMS.get());
	}

	private static void sell(int itemId, int amount)
	{
		exchange(itemId, amount, INVENTORY.get());
	}

	private static void sell(String itemName, int amount)
	{
		exchange(itemName, amount, INVENTORY.get());
	}

	private static void exchange(int itemId, int amount, RS2Widget container)
	{
		if (container == null)
		{
			return;
		}

		final RS2Widget[] items = container.getChildren();
		if (items == null)
		{
			return;
		}

		for (RS2Widget item : items)
		{
			if (item.getItemId() == itemId)
			{
				final String action = Arrays.stream(item.getActions())
					.filter(x -> x != null && x.contains(String.valueOf(amount)))
					.findFirst()
					.orElse(null);
				if (action == null)
				{
					return;
				}

				item.interact(action);
				return;
			}
		}
	}

	private static void exchange(String itemName, int amount, RS2Widget container)
	{
		if (container == null)
		{
			return;
		}

		final RS2Widget[] items = container.getChildren();
		if (items == null)
		{
			return;
		}

		for (RS2Widget item : items)
		{
			final var nestedName = StringUtils.substringBetween(item.getName(), ">", "<");
			if (nestedName != null && nestedName.equals(itemName))
			{
				final String action = Arrays.stream(item.getActions())
					.filter(x -> x != null && x.contains(String.valueOf(amount)))
					.findFirst()
					.orElse(null);
				if (action == null)
				{
					return;
				}

				item.interact(action);
				return;
			}
		}
	}
}
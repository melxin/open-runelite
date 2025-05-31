package net.runelite.client.plugins.openrl.api.items;

import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.client.plugins.openrl.Static;

public class Items
{
	public static ItemComposition getComposition(Item item)
	{
		final ItemComposition composition = Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getItemDefinition(item.getId())).orElse(null);
		return composition;
	}

	public static String getName(Item item)
	{
		return getComposition(item).getName();
	}
}

package net.runelite.client.plugins.openrl.api.rs2.entities;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.IndexedObjectSet;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;

public class RS2Players
{
	private static final RS2Players PLAYERS = new RS2Players();

	private RS2Players()
	{
	}

	protected List<RS2Player> all(Predicate<? super RS2Player> filter)
	{
		final IndexedObjectSet<? extends Player> players = Static.getClient().getTopLevelWorldView().players();
		return players.stream()
			.map(RS2Player::new)
			.filter(filter)
			.collect(Collectors.toList());
	}

	public static List<RS2Player> getAll(Predicate<RS2Player> filter)
	{
		return PLAYERS.all(filter);
	}

	public static List<RS2Player> getAll()
	{
		return PLAYERS.all(player -> true);
	}

	public static List<RS2Player> getAll(String... names)
	{
		final Set<String> nameSet = new HashSet<>(Arrays.asList(names));
		return PLAYERS.all(p -> nameSet.contains(p.getName()));
	}

	public static RS2Player getNearest()
	{
		return getNearest(player -> true);
	}

	public static RS2Player getNearest(Predicate<RS2Player> filter)
	{
		return getNearest(getLocal().getWorldLocation(), filter);
	}

	public static RS2Player getNearest(String... names)
	{
		return getNearest(getLocal().getWorldLocation(), names);
	}

	public static RS2Player getNearest(WorldPoint to, Predicate<RS2Player> filter)
	{
		return getAll(filter.and(p -> p != getLocal())).stream().min(Comparator.comparingInt(p -> p.getWorldLocation().distanceTo2D(to))).orElse(null);
	}

	public static RS2Player getNearest(WorldPoint to, String... names)
	{
		final Set<String> nameSet = new HashSet<>(Arrays.asList(names));
		return getNearest(to, p -> nameSet.contains(p.getName()));
	}

	public static RS2Player getHintArrowPlayer()
	{
		return new RS2Player(Static.getClient().getHintArrowPlayer());
	}

	public static RS2Player getLocal()
	{
		final Player local = Static.getClient().getLocalPlayer();
		if (local == null)
		{
			throw new IllegalStateException("Local player was null, are you logged in?");
		}

		return new RS2Player(local);
	}
}

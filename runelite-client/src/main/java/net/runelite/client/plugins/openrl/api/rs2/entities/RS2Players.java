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
		return PLAYERS.all(Predicates.names(names));
	}

	protected RS2Player nearest(WorldPoint to, Predicate<RS2Player> filter)
	{
		return PLAYERS.all(filter).stream().min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo2D(to))).orElse(null);
	}

	public static RS2Player getNearest(WorldPoint to, Predicate<RS2Player> filter)
	{
		return PLAYERS.nearest(to, filter);
	}

	public static RS2Player getNearest(WorldPoint to, int... ids)
	{
		return PLAYERS.nearest(to, Predicates.ids(ids));
	}

	public static RS2Player getNearest(WorldPoint to, String... names)
	{
		return PLAYERS.nearest(to, Predicates.names(names));
	}

	public static RS2Player getNearest(Predicate<RS2Player> filter)
	{
		return PLAYERS.nearest(RS2Players.getLocal().getWorldLocation(), filter);
	}

	public static RS2Player getNearest()
	{
		return getNearest(x -> true);
	}

	public static RS2Player getNearest(int... ids)
	{
		return getNearest(Predicates.ids(ids));
	}

	public static RS2Player getNearest(String... names)
	{
		return getNearest(Predicates.names(names));
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

	public static class Predicates
	{
		public static Predicate<RS2Player> ids(int... ids)
		{
			final Set<Integer> idSet = Arrays.stream(ids).boxed().collect(Collectors.toSet());
			return x -> idSet.contains(x.getId());
		}

		public static Predicate<RS2Player> names(String... names)
		{
			final Set<String> nameSet = new HashSet<>(Arrays.asList(names));
			return x -> nameSet.contains(x.getName());
		}
	}
}

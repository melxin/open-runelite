package net.runelite.client.plugins.openrl.api.rs2.providers.entities;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static java.lang.Math.abs;
import net.runelite.api.IndexedObjectSet;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Player;

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

	public static List<RS2Player> getAll(int... ids)
	{
		return PLAYERS.all(Predicates.ids(ids));
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

	protected RS2Player farthest(WorldPoint to, Predicate<RS2Player> filter)
	{
		return PLAYERS.all(filter).stream().max(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo2D(to))).orElse(null);
	}

	public static RS2Player getFarthest(WorldPoint to, Predicate<RS2Player> filter)
	{
		return PLAYERS.farthest(to, filter);
	}

	public static RS2Player getFarthest(WorldPoint to, int... ids)
	{
		return PLAYERS.farthest(to, Predicates.ids(ids));
	}

	public static RS2Player getFarthest(WorldPoint to, String... names)
	{
		return PLAYERS.farthest(to, Predicates.names(names));
	}

	public static RS2Player getFarthest(Predicate<RS2Player> filter)
	{
		return PLAYERS.farthest(RS2Players.getLocal().getWorldLocation(), filter);
	}

	public static RS2Player getFarthest()
	{
		return getFarthest(x -> true);
	}

	public static RS2Player getFarthest(int... ids)
	{
		return getFarthest(Predicates.ids(ids));
	}

	public static RS2Player getFarthest(String... names)
	{
		return getFarthest(Predicates.names(names));
	}

	public static RS2Player getFirst(Predicate<RS2Player> filter)
	{
		return PLAYERS.all(filter).stream().findFirst().orElse(null);
	}

	public static RS2Player getFirst()
	{
		return getFirst(x -> true);
	}

	public static RS2Player getFirst(int... ids)
	{
		return getAll(ids).stream().findFirst().orElse(null);
	}

	public static RS2Player getFirst(String... names)
	{
		return getAll(names).stream().findFirst().orElse(null);
	}

	protected List<RS2Player> at(WorldPoint worldPoint, Predicate<RS2Player> filter)
	{
		return getAll(x -> filter.test(x) && x.getWorldLocation().equals(worldPoint));
	}

	public static List<RS2Player> getAt(WorldPoint worldPoint, Predicate<RS2Player> filter)
	{
		return PLAYERS.at(worldPoint, filter);
	}

	public static List<RS2Player> getAt(WorldPoint worldPoint, int... ids)
	{
		return PLAYERS.at(worldPoint, Predicates.ids(ids));
	}

	public static List<RS2Player> getAt(WorldPoint worldPoint, String... names)
	{
		return PLAYERS.at(worldPoint, Predicates.names(names));
	}

	public static List<RS2Player> getAt(WorldPoint worldPoint)
	{
		return PLAYERS.at(worldPoint, x -> true);
	}

	public static RS2Player getFirstAt(WorldPoint worldPoint, Predicate<RS2Player> filter)
	{
		return PLAYERS.at(worldPoint, filter).stream().findFirst().orElse(null);
	}

	public static RS2Player getFirstAt(WorldPoint worldPoint, int... ids)
	{
		return getFirstAt(worldPoint, Predicates.ids(ids));
	}

	public static RS2Player getFirstAt(WorldPoint worldPoint, String... names)
	{
		return getFirstAt(worldPoint, Predicates.names(names));
	}

	public static RS2Player getFirstAt(WorldPoint worldPoint)
	{
		return getFirstAt(worldPoint, x -> true);
	}

	protected List<RS2Player> at(LocalPoint localPoint, Predicate<RS2Player> filter)
	{
		return getAll(x -> filter.test(x) && x.getLocalLocation().equals(localPoint));
	}

	public static List<RS2Player> getAt(LocalPoint localPoint, Predicate<RS2Player> filter)
	{
		return PLAYERS.at(localPoint, filter);
	}

	public static List<RS2Player> getAt(LocalPoint localPoint, int... ids)
	{
		return PLAYERS.at(localPoint, Predicates.ids(ids));
	}

	public static List<RS2Player> getAt(LocalPoint localPoint, String... names)
	{
		return PLAYERS.at(localPoint, Predicates.names(names));
	}

	public static List<RS2Player> getAt(LocalPoint localPoint)
	{
		return PLAYERS.at(localPoint, x -> true);
	}

	public static RS2Player getFirstAt(LocalPoint localPoint, Predicate<RS2Player> filter)
	{
		return PLAYERS.at(localPoint, filter).stream().findFirst().orElse(null);
	}

	public static RS2Player getFirstAt(LocalPoint localPoint, int... ids)
	{
		return getFirstAt(localPoint, Predicates.ids(ids));
	}

	public static RS2Player getFirstAt(LocalPoint localPoint, String... names)
	{
		return getFirstAt(localPoint, Predicates.names(names));
	}

	public static RS2Player getFirstAt(LocalPoint localPoint)
	{
		return getFirstAt(localPoint, x -> true);
	}

	protected List<RS2Player> in(WorldArea area, Predicate<? super RS2Player> filter)
	{
		return PLAYERS.all(x -> filter.test(x) && area.contains(x.getWorldLocation()));
	}

	public static List<RS2Player> within(WorldArea area, Predicate<RS2Player> filter)
	{
		return PLAYERS.in(area, filter);
	}

	public static List<RS2Player> within(WorldArea area, int... ids)
	{
		return PLAYERS.in(area, Predicates.ids(ids));
	}

	public static List<RS2Player> within(WorldArea area, String... names)
	{
		return PLAYERS.in(area, Predicates.names(names));
	}

	public static List<RS2Player> within(WorldArea area)
	{
		return PLAYERS.in(area, x -> true);
	}

	public static List<RS2Player> withinArea(WorldPoint from, int area, Predicate<RS2Player> filter)
	{
		return PLAYERS.all(x -> filter.test(x) && abs(x.getWorldLocation().getX() - from.getX()) <= area && abs(x.getWorldLocation().getY() - from.getY()) <= area);
	}

	public static List<RS2Player> withinArea(WorldPoint from, int area, int... ids)
	{
		return withinArea(from, area, Predicates.ids(ids));
	}

	public static List<RS2Player> withinArea(WorldPoint from, int area, String... names)
	{
		return withinArea(from, area, Predicates.names(names));
	}

	public static List<RS2Player> withinArea(WorldPoint from, int area)
	{
		return withinArea(from, area, x -> true);
	}

	public static List<RS2Player> withinArea(LocalPoint from, int area, Predicate<RS2Player> filter)
	{
		return PLAYERS.all(x -> filter.test(x) && abs(x.getLocalLocation().getX() - from.getX()) <= area && abs(x.getLocalLocation().getY() - from.getY()) <= area);
	}

	public static List<RS2Player> withinArea(LocalPoint from, int area, int... ids)
	{
		return withinArea(from, area, Predicates.ids(ids));
	}

	public static List<RS2Player> withinArea(LocalPoint from, int area, String... names)
	{
		return withinArea(from, area, Predicates.names(names));
	}

	public static List<RS2Player> withinArea(LocalPoint from, int area)
	{
		return withinArea(from, area, x -> true);
	}

	public static List<RS2Player> withinDistance(WorldPoint to, int distance, Predicate<RS2Player> filter)
	{
		return PLAYERS.all(x -> filter.test(x) && x.getWorldLocation().distanceTo2D(to) <= distance);
	}

	public static List<RS2Player> withinDistance(WorldPoint to, int distance, int... ids)
	{
		return withinDistance(to, distance, Predicates.ids(ids));
	}

	public static List<RS2Player> withinDistance(WorldPoint to, int distance, String... names)
	{
		return withinDistance(to, distance, Predicates.names(names));
	}

	public static List<RS2Player> withinDistance(WorldPoint to, int distance)
	{
		return withinDistance(to, distance, x -> true);
	}

	public static List<RS2Player> withinDistance(LocalPoint to, int distance, Predicate<RS2Player> filter)
	{
		return PLAYERS.all(x -> filter.test(x) && x.getLocalLocation().distanceTo(to) <= distance);
	}

	public static List<RS2Player> withinDistance(LocalPoint to, int distance, int... ids)
	{
		return withinDistance(to, distance, Predicates.ids(ids));
	}

	public static List<RS2Player> withinDistance(LocalPoint to, int distance, String... names)
	{
		return withinDistance(to, distance, Predicates.names(names));
	}

	public static List<RS2Player> withinDistance(LocalPoint to, int distance)
	{
		return withinDistance(to, distance, x -> true);
	}

	protected List<RS2Player> sort(Predicate<RS2Player> filter, Comparator<RS2Player> comparator)
	{
		return PLAYERS.all(filter).stream()
			.sorted(comparator)
			.collect(Collectors.toList());
	}

	public static List<RS2Player> getSorted(Predicate<RS2Player> filter, Comparator<RS2Player> comparator)
	{
		return PLAYERS.sort(filter, comparator);
	}

	public static List<RS2Player> getSorted(Comparator<RS2Player> comparator, int... ids)
	{
		return getSorted(Predicates.ids(ids), comparator);
	}

	public static List<RS2Player> getSorted(Comparator<RS2Player> comparator, String... names)
	{
		return getSorted(Predicates.names(names), comparator);
	}

	public static List<RS2Player> getSorted(Comparator<RS2Player> comparator)
	{
		return getSorted(x -> true, comparator);
	}

	public static List<RS2Player> getSortedByDistance(WorldPoint to, Predicate<RS2Player> filter)
	{
		return getSorted(filter, Comparator.comparingInt(x -> x.getWorldLocation().distanceTo2D(to)));
	}

	public static List<RS2Player> getSortedByDistance(WorldPoint to, int... ids)
	{
		return getSortedByDistance(to, Predicates.ids(ids));
	}

	public static List<RS2Player> getSortedByDistance(WorldPoint to, String... names)
	{
		return getSortedByDistance(to, Predicates.names(names));
	}

	public static List<RS2Player> getSortedByDistance(WorldPoint to)
	{
		return getSortedByDistance(to, x -> true);
	}

	public static List<RS2Player> getByCombatLevels(Predicate<RS2Player> filter, int... levels)
	{
		return PLAYERS.all(filter).stream().filter(Predicates.combatLevels(levels)).collect(Collectors.toList());
	}

	public static List<RS2Player> getByCombatLevels(int... levels)
	{
		return getByCombatLevels(x -> true, levels);
	}

	public static List<RS2Player> getByCombatLevelsInRange(int min, int max)
	{
		return PLAYERS.all(Predicates.combatLevelsInRange(min, max));
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

		public static Predicate<RS2Player> combatLevels(int... levels)
		{
			final Set<Integer> levelSet = Arrays.stream(levels).boxed().collect(Collectors.toSet());
			return x -> levelSet.contains(x.getCombatLevel());
		}

		public static Predicate<RS2Player> combatLevelsInRange(int min, int max)
		{
			return x -> x.getCombatLevel() >= min && x.getCombatLevel() <= max;
		}
	}
}
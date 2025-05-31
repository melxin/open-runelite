package net.runelite.client.plugins.openrl.api.rs2.providers.entities;

import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.IndexedObjectSet;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2NPC;

@Slf4j
public class RS2NPCs
{
	private static final RS2NPCs NPCS = new RS2NPCs();

	private RS2NPCs()
	{
	}

	protected List<RS2NPC> all(Predicate<? super RS2NPC> filter)
	{
		final IndexedObjectSet<? extends NPC> npcs = Static.getClient().getTopLevelWorldView().npcs();
		return npcs.stream()
			.map(RS2NPC::new)
			.filter(filter)
			.collect(Collectors.toList());
	}

	public static List<RS2NPC> getAll(Predicate<RS2NPC> filter)
	{
		return NPCS.all(filter);
	}

	public static List<RS2NPC> getAll()
	{
		return NPCS.all(x -> true);
	}

	public static List<RS2NPC> getAll(int... ids)
	{
		return NPCS.all(Predicates.ids(ids));
	}

	public static List<RS2NPC> getAll(String... names)
	{
		return NPCS.all(Predicates.names(names));
	}

	protected RS2NPC nearest(WorldPoint to, Predicate<RS2NPC> filter)
	{
		return NPCS.all(filter).stream().min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo2D(to))).orElse(null);
	}

	public static RS2NPC getNearest(WorldPoint to, Predicate<RS2NPC> filter)
	{
		return NPCS.nearest(to, filter);
	}

	public static RS2NPC getNearest(WorldPoint to, int... ids)
	{
		return NPCS.nearest(to, Predicates.ids(ids));
	}

	public static RS2NPC getNearest(WorldPoint to, String... names)
	{
		return NPCS.nearest(to, Predicates.names(names));
	}

	public static RS2NPC getNearest(Predicate<RS2NPC> filter)
	{
		return NPCS.nearest(RS2Players.getLocal().getWorldLocation(), filter);
	}

	public static RS2NPC getNearest()
	{
		return getNearest(x -> true);
	}

	public static RS2NPC getNearest(int... ids)
	{
		return getNearest(Predicates.ids(ids));
	}

	public static RS2NPC getNearest(String... names)
	{
		return getNearest(Predicates.names(names));
	}

	public static List<RS2NPC> actionEquals(Predicate<RS2NPC> filter, String... actions)
	{
		return NPCS.all(RS2NPCs.Predicates.actions(actions).and(x -> filter.test(x)));
	}

	public static List<RS2NPC> actionEquals(String... actions)
	{
		return actionEquals(x -> true, actions);
	}

	public static class Predicates
	{
		public static Predicate<RS2NPC> ids(int... ids)
		{
			final Set<Integer> idSet = Arrays.stream(ids).boxed().collect(Collectors.toSet());
			return x -> idSet.contains(x.getId());
		}

		public static Predicate<RS2NPC> names(String... names)
		{
			final Set<String> nameSet = new HashSet<>(Arrays.asList(names));
			return x -> nameSet.contains(x.getName());
		}

		private static Predicate<RS2NPC> actions(String... actions)
		{
			final Set<String> actionSet = new HashSet<>(Arrays.asList(actions));
			return x ->
			{
				final String[] npcActions = x.getActions();
				if (npcActions == null)
				{
					return false;
				}

				for (String npcAction : npcActions)
				{
					if (npcAction != null && actionSet.contains(npcAction))
					{
						return true;
					}
				}
				return false;
			};
		}
	}
}
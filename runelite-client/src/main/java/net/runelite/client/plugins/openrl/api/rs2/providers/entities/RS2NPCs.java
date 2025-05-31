package net.runelite.client.plugins.openrl.api.rs2.providers.entities;

import lombok.extern.slf4j.Slf4j;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.IndexedObjectSet;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Predicates;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.RS2NPCQuery;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2NPC;

@Slf4j
public class RS2NPCs
{
	private static final RS2NPCs NPCS = new RS2NPCs();

	private RS2NPCs()
	{
	}

	public static RS2NPCQuery query()
	{
		return RS2NPCQuery.query();
	}

	protected List<RS2NPC> all(Predicate<? super RS2NPC> filter)
	{
		final IndexedObjectSet<? extends NPC> npcs = Static.getClient().getTopLevelWorldView().npcs();
		return npcs.stream()
			.map(RS2NPC::new)
			.filter(filter)
			.collect(Collectors.toList());
	}

	public static List<RS2NPC> getAll(int... ids)
	{
		return NPCS.all(Predicates.idEquals(ids));
	}

	public static List<RS2NPC> getAll(String... names)
	{
		return NPCS.all(Predicates.nameEquals(names));
	}

	public static List<RS2NPC> getAll()
	{
		return NPCS.all(x -> true);
	}

	public static List<RS2NPC> getAll(Predicate<RS2NPC> filter)
	{
		return NPCS.all(filter);
	}

	protected RS2NPC nearest(WorldPoint to, Predicate<RS2NPC> filter)
	{
		return NPCS.all(filter).stream().min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo2D(to))).orElse(null);
	}

	public static RS2NPC getNearest(WorldPoint to, int... ids)
	{
		return NPCS.nearest(to, Predicates.idEquals(ids));
	}

	public static RS2NPC getNearest(WorldPoint to, String... names)
	{
		return NPCS.nearest(to, Predicates.nameEquals(names));
	}

	public static RS2NPC getNearest(WorldPoint to, Predicate<RS2NPC> filter)
	{
		return NPCS.nearest(to, filter);
	}

	public static RS2NPC getNearest(int... ids)
	{
		return getNearest(Predicates.idEquals(ids));
	}

	public static RS2NPC getNearest(String... names)
	{
		return getNearest(Predicates.nameEquals(names));
	}

	public static RS2NPC getNearest()
	{
		return getNearest(x -> true);
	}

	public static RS2NPC getNearest(Predicate<RS2NPC> filter)
	{
		return NPCS.nearest(RS2Players.getLocal().getWorldLocation(), filter);
	}
}
package net.runelite.client.plugins.openrl.api.rs2;

import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.IndexedObjectSet;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;

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
		return NPCS.all(n -> Arrays.asList(ids).contains(n.getId()));
	}

	public static List<RS2NPC> getAll(String... names)
	{
		return NPCS.all(n -> Arrays.asList(names).contains(n.getName()));
	}

	public static RS2NPC getNearest()
	{
		return getNearest(npc -> true);
	}

	public static RS2NPC getNearest(Predicate<RS2NPC> filter)
	{
		return getNearest(RS2Players.getLocal().getWorldLocation(), filter);
	}

	public static RS2NPC getNearest(int... ids)
	{
		return getNearest(RS2Players.getLocal().getWorldLocation(), ids);
	}

	public static RS2NPC getNearest(String... names)
	{
		return getNearest(RS2Players.getLocal().getWorldLocation(), names);
	}

	public static RS2NPC getNearest(WorldPoint to, Predicate<RS2NPC> filter)
	{
		return getAll(filter).stream().min(Comparator.comparingInt(n -> n.getWorldLocation().distanceTo2D(to))).orElse(null);
	}

	public static RS2NPC getNearest(WorldPoint to, int... ids)
	{
		return getNearest(to, n -> Arrays.asList(ids).contains(n.getId()));
	}

	public static RS2NPC getNearest(WorldPoint to, String... names)
	{
		return getNearest(to, n -> Arrays.asList(names).contains(n.getName()));
	}
}
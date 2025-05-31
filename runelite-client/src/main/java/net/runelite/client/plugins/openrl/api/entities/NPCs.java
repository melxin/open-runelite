package net.runelite.client.plugins.openrl.api.entities;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.IndexedObjectSet;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;
import net.runelite.client.plugins.openrl.api.input.utils.Randomizer;

@Slf4j
public class NPCs
{
	private static final NPCs NPCS = new NPCs();

	private NPCs()
	{
	}

	protected List<NPC> all(Predicate<? super NPC> filter)
	{
		List<NPC> out = new ArrayList<>();
		IndexedObjectSet<? extends NPC> npcs = Static.getClient().getTopLevelWorldView().npcs();
		for (NPC npc : npcs)
		{
			if (filter.test(npc))
			{
				out.add(npc);
			}
		}

		return out;
	}

	public static List<NPC> getAll()
	{
		return getAll(x -> true);
	}

	public static List<NPC> getAll(Predicate<NPC> filter)
	{
		return NPCS.all(filter);
	}

	public static List<NPC> getAll(int... ids)
	{
		return NPCS.all(n -> Arrays.asList(ids).contains(n.getId()));
	}

	public static List<NPC> getAll(String... names)
	{
		return NPCS.all(n -> Arrays.asList(names).contains(n.getName()));
	}

	public static NPC getNearest()
	{
		return getNearest(npc -> true);
	}

	public static NPC getNearest(Predicate<NPC> filter)
	{
		return getNearest(Players.getLocal().getWorldLocation(), filter);
	}

	public static NPC getNearest(int... ids)
	{
		return getNearest(Players.getLocal().getWorldLocation(), ids);
	}

	public static NPC getNearest(String... names)
	{
		return getNearest(Players.getLocal().getWorldLocation(), names);
	}

	public static NPC getNearest(WorldPoint to, Predicate<NPC> filter)
	{
		return getAll(filter).stream().min(Comparator.comparingInt(n -> n.getWorldLocation().distanceTo2D(to))).orElse(null);
	}

	public static NPC getNearest(WorldPoint to, int... ids)
	{
		return getNearest(to, n -> Arrays.asList(ids).contains(n.getId()));
	}

	public static NPC getNearest(WorldPoint to, String... names)
	{
		return getNearest(to, n -> Arrays.asList(names).contains(n.getName()));
	}

	 public static void interact(NPC npc, MenuAction menuAction)
	 {
		 final int param0 = 0;
		 final int param1 = 0;
		 final int index = npc.getIndex();
		 final int itemId = -1;
		 final int worldViewId = -1;
		 final String option = "";
		 final String target = "";
		 final Point clickPoint = getClickPoint(npc);
		 final int x = clickPoint.getX();
		 final int y = clickPoint.getY();
		 Static.getEventBus().post(new MenuAutomated(param0, param1, menuAction, index, itemId, worldViewId, option, target, x, y));
	}

	public static void interact(NPC npc, int index)
	{
		if (index == -1)
		{
			return;
		}
		interact(npc, getMenuAction(index));
	}

	public static void interact(NPC npc, String action)
	{
		interact(npc, getActionIndex(npc, action));
	}

	@Nullable
	public static MenuAction getMenuAction(int index)
	{
		if (Static.getClient().isWidgetSelected())
		{
			return MenuAction.WIDGET_TARGET_ON_NPC;
		}

		switch (index)
		{
			case 0:
				return MenuAction.NPC_FIRST_OPTION;
			case 1:
				return MenuAction.NPC_SECOND_OPTION;
			case 2:
				return MenuAction.NPC_THIRD_OPTION;
			case 3:
				return MenuAction.NPC_FOURTH_OPTION;
			case 4:
				return MenuAction.NPC_FIFTH_OPTION;
			default:
				return null;
		}
	}

	@Nullable
	public static String[] getActions(NPC npc)
	{
		final NPCComposition npcComposition = Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getNpcDefinition(npc.getId())).orElse(null);
		if (npcComposition == null)
		{
			return null;
		}
		final String[] actions = npcComposition.getActions();
		return actions != null ? actions : null;
	}

	public static int getActionIndex(NPC npc, String action)
	{
		final String[] actions = getActions(npc);
		if (actions == null)
		{
			return -1;
		}
		return Arrays.asList(actions).indexOf(action);
	}

	public static boolean hasAction(NPC npc, String action)
	{
		final String[] actions = getActions(npc);
		if (actions == null)
		{
			return false;
		}

		return Arrays.asList(actions).contains(action);
	}

	public static String getName(NPC npc)
	{
		return Static.getClientThread().runOnClientThreadOptional(() -> npc.getName()).orElse(null);
	}

	public static NPCComposition getComposition(NPC npc)
	{
		return Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getNpcDefinition(npc.getId())).orElse(null);
	}

	public static Point getClickPoint(NPC npc)
	{
		final Shape convexHull = npc.getConvexHull();
		return convexHull != null ? Randomizer.getRandomPointIn(convexHull.getBounds()) : null;
	}
}

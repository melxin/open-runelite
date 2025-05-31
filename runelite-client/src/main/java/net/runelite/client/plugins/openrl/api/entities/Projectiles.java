package net.runelite.client.plugins.openrl.api.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.Actor;
import net.runelite.api.Projectile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;

public class Projectiles
{
	private Projectiles()
	{
	}

	public static List<Projectile> getAll(Predicate<Projectile> filter)
	{
		List<Projectile> out = new ArrayList<>();
		Static.getClient().getProjectiles().forEach(p ->
		{
			if (filter.test(p))
			{
				out.add(p);
			}
		});

		return out;
	}

	public static List<Projectile> getAll(int... ids)
	{
		return getAll(p -> Arrays.asList(ids).contains(p.getId()));
	}

	public static Projectile getNearest(Predicate<Projectile> filter)
	{
		return getAll(filter).stream()
				.min(Comparator.comparingInt(p ->
						WorldPoint.fromLocal(Static.getClient(), (int) p.getX(), (int) p.getY(), Static.getClient().getPlane())
								.distanceTo(Players.getLocal().getWorldLocation()))
				)
				.orElse(null);
	}

	public static Projectile getNearest(int... ids)
	{
		return getNearest(p -> Arrays.asList(ids).contains(p.getId()));
	}

	public static Projectile getNearest(Actor target)
	{
		return getNearest(x -> x.getInteracting() != null && x.getInteracting().equals(target));
	}

	public static Projectile getNearest(WorldPoint startPoint)
	{
		LocalPoint localPoint = LocalPoint.fromWorld(Static.getClient(), startPoint);
		if (localPoint == null)
		{
			return null;
		}

		return getNearest(x -> x.getX1() == localPoint.getX() && x.getY1() == localPoint.getY());
	}
}

package net.runelite.client.plugins.openrl.api.rs2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.Actor;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;

public class RS2Projectiles
{
	private RS2Projectiles()
	{
	}

	public static List<RS2Projectile> getAll(Predicate<RS2Projectile> filter)
	{
		final List<RS2Projectile> out = new ArrayList<>();
		Static.getClient().getProjectiles().forEach(p ->
		{
			final RS2Projectile rs2Projectile = new RS2Projectile(p);
			if (filter.test(rs2Projectile))
			{
				out.add(rs2Projectile);
			}
		});

		return out;
	}

	public static List<RS2Projectile> getAll(int... ids)
	{
		return getAll(p -> Arrays.asList(ids).contains(p.getId()));
	}

	public static RS2Projectile getNearest(Predicate<RS2Projectile> filter)
	{
		return getAll(filter).stream()
			.min(Comparator.comparingInt(p ->
				WorldPoint.fromLocal(Static.getClient(), (int) p.getX(), (int) p.getY(), Static.getClient().getPlane())
					.distanceTo(RS2Players.getLocal().getWorldLocation()))
			)
			.orElse(null);
	}

	public static RS2Projectile getNearest(int... ids)
	{
		return getNearest(p -> Arrays.asList(ids).contains(p.getId()));
	}

	public static RS2Projectile getNearest(Actor target)
	{
		return getNearest(x -> x.getInteracting() != null && x.getInteracting().equals(target));
	}

	public static RS2Projectile getNearest(WorldPoint startPoint)
	{
		LocalPoint localPoint = LocalPoint.fromWorld(Static.getClient(), startPoint);
		if (localPoint == null)
		{
			return null;
		}

		return getNearest(x -> x.getX1() == localPoint.getX() && x.getY1() == localPoint.getY());
	}
}
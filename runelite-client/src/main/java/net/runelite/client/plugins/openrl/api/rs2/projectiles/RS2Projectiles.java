package net.runelite.client.plugins.openrl.api.rs2.projectiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.Actor;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2Players;

public class RS2Projectiles
{
	private static final RS2Projectiles PROJECTILES = new RS2Projectiles();

	private RS2Projectiles()
	{
	}

	protected static List<RS2Projectile> all(Predicate<RS2Projectile> filter)
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

	public static List<RS2Projectile> getAll(Predicate<RS2Projectile> filter)
	{
		return PROJECTILES.all(filter);
	}

	public static List<RS2Projectile> getAll(int... ids)
	{
		return PROJECTILES.all(Predicates.ids(ids));
	}

	public static RS2Projectile getNearest(Predicate<RS2Projectile> filter)
	{
		return PROJECTILES.all(filter).stream()
			.min(Comparator.comparingInt(p ->
				WorldPoint.fromLocal(Static.getClient(), (int) p.getX(), (int) p.getY(), Static.getClient().getPlane())
					.distanceTo(RS2Players.getLocal().getWorldLocation()))
			)
			.orElse(null);
	}

	public static RS2Projectile getNearest(int... ids)
	{
		return getNearest(Predicates.ids(ids));
	}

	public static RS2Projectile getNearest(Actor target)
	{
		return getNearest(x -> x.getTargetActor() != null && x.getTargetActor().equals(target));
	}

	public static RS2Projectile getNearest(WorldPoint startPoint)
	{
		final LocalPoint localPoint = LocalPoint.fromWorld(Static.getClient(), startPoint);
		if (localPoint == null)
		{
			return null;
		}

		return getNearest(x -> x.getX1() == localPoint.getX() && x.getY1() == localPoint.getY());
	}

	public static class Predicates
	{
		public static Predicate<RS2Projectile> ids(int... ids)
		{
			final Set<Integer> idSet = Arrays.stream(ids).boxed().collect(Collectors.toSet());
			return x -> idSet.contains(x.getId());
		}
	}
}
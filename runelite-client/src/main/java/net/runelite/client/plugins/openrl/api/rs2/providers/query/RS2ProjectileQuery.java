/*
 * Copyright (c) 2025, Melxin <https://github.com/melxin>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.openrl.api.rs2.providers.query;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.Actor;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.abstraction.AbstractQuery;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Projectile;

public class RS2ProjectileQuery extends AbstractQuery<RS2Projectile, RS2ProjectileQuery>
{
	private RS2ProjectileQuery()
	{
	}

	public static RS2ProjectileQuery query()
	{
		return new RS2ProjectileQuery();
	}

	@Override
	protected List<RS2Projectile> all(Predicate<? super RS2Projectile> filter)
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

	// Queries

	public RS2ProjectileQuery withId(int id)
	{
		return and(x -> x.getId() == id);
	}

	public RS2ProjectileQuery withIds(int... ids)
	{
		return and(x ->
		{
			final int projectileId = x.getId();
			for (int id : ids)
			{
				if (projectileId == id)
				{
					return true;
				}
			}
			return false;
		});
	}

	public RS2ProjectileQuery withAnimation(int id)
	{
		return and(x -> x.getAnimation().getId() == id);
	}

	// Results

	public RS2Projectile getNearest(Predicate<RS2Projectile> filter)
	{
		return stream().filter(filter)
			.min(Comparator.comparingInt(p ->
				WorldPoint.fromLocal(Static.getClient().getTopLevelWorldView(), (int) p.getX(), (int) p.getY(), Static.getClient().getTopLevelWorldView().getPlane())
					.distanceTo(getLocal().getWorldLocation()))
			)
			.orElse(null);
	}

	public RS2Projectile getNearest(WorldPoint source)
	{
		final LocalPoint localPoint = LocalPoint.fromWorld(Static.getClient(), source);
		if (localPoint == null)
		{
			return null;
		}

		return getNearest(x -> x.getX1() == localPoint.getX() && x.getY1() == localPoint.getY());
	}

	public RS2Projectile getNearest(Actor target)
	{
		return getNearest(x -> x.getTargetActor() != null && x.getTargetActor().equals(target));
	}

	public RS2Projectile getNearest()
	{
		return getNearest(x -> true);
	}
}
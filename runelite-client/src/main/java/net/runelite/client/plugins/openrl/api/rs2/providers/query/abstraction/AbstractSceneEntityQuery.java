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
package net.runelite.client.plugins.openrl.api.rs2.providers.query.abstraction;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.api.commons.Predicates;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.api.Locatable;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.api.SceneEntity;

public abstract class AbstractSceneEntityQuery<T extends SceneEntity, Q extends AbstractSceneEntityQuery<T, Q>> extends AbstractQuery<T, Q>
{
	// Queries

	public Q idEquals(int id)
	{
		return and(Predicates.idEquals(id));
	}

	public Q idEquals(int... ids)
	{
		return and(Predicates.idEquals(ids));
	}

	public Q idEquals(Collection<Integer> ids)
	{
		return and(Predicates.idEquals(ids));
	}

	public Q nameEquals(String name)
	{
		return and(Predicates.nameEquals(name));
	}

	public Q nameEquals(String... names)
	{
		return and(Predicates.nameEquals(names));
	}

	public Q nameEquals(Collection<String> names)
	{
		return and(Predicates.nameEquals(names));
	}

	public Q nameContains(String name)
	{
		return and(Predicates.nameContains(name));
	}

	public Q nameContains(String... names)
	{
		return and(Predicates.nameContains(names));
	}

	public Q actionEquals(String action)
	{
		return and(Predicates.actionEquals(action));
	}

	public Q actionEquals(String... actions)
	{
		return and(Predicates.actionEquals(actions));
	}

	public Q actionContains(String action)
	{
		return and(Predicates.actionContains(action));
	}

	public Q actionContains(String... actions)
	{
		return and(Predicates.actionContains(actions));
	}

	public Q byType(Class<?>... types)
	{
		return and(x ->
		{
			for (Class<?> type : types)
			{
				if (type.isAssignableFrom(x.getClass()))
				{
					return true;
				}
			}
			return false;
		});
	}

	public Q notByType(Class<?>... types)
	{
		return not(x ->
		{
			for (Class<?> type : types)
			{
				if (type.isAssignableFrom(x.getClass()))
				{
					return true;
				}
			}
			return false;
		});
	}

	public Q at(LocalPoint localPoint)
	{
		return and(x -> x.getLocalLocation().equals(localPoint));
	}

	public Q at(WorldPoint worldPoint)
	{
		return and(x -> x.getWorldLocation().equals(worldPoint));
	}

	public Q at(Locatable locatable)
	{
		return and(x -> x.getWorldLocation().equals(locatable.getWorldLocation()));
	}

	public Q at(LocalPoint... localPoints)
	{
		return and(x ->
		{
			final LocalPoint lp = x.getLocalLocation();
			for (LocalPoint localPoint : localPoints)
			{
				if (lp.equals(localPoint))
				{
					return true;
				}
			}
			return false;
		});
	}

	public Q at(WorldPoint... worldPoints)
	{
		return and(x ->
		{
			final WorldPoint wp = x.getWorldLocation();
			for (WorldPoint worldPoint : worldPoints)
			{
				if (wp.equals(worldPoint))
				{
					return true;
				}
			}
			return false;
		});
	}

	public Q at(Locatable... locatables)
	{
		return and(x ->
		{
			final WorldPoint wp = x.getWorldLocation();
			for (Locatable locatable : locatables)
			{
				if (wp.equals(locatable.getWorldLocation()))
				{
					return true;
				}
			}
			return false;
		});
	}

	public Q notAt(LocalPoint localPoint)
	{
		return not(x -> x.getLocalLocation().equals(localPoint));
	}

	public Q notAt(WorldPoint worldPoint)
	{
		return not(x -> x.getWorldLocation().equals(worldPoint));
	}

	public Q notAt(Locatable locatable)
	{
		return not(x -> x.getWorldLocation().equals(locatable.getWorldLocation()));
	}

	public Q notAt(LocalPoint... localPoints)
	{
		return not(x ->
		{
			final LocalPoint lp = x.getLocalLocation();
			for (LocalPoint localPoint : localPoints)
			{
				if (lp.equals(localPoint))
				{
					return true;
				}
			}
			return false;
		});
	}

	public Q notAt(WorldPoint... worldPoints)
	{
		return not(x ->
		{
			final WorldPoint wp = x.getWorldLocation();
			for (WorldPoint worldPoint : worldPoints)
			{
				if (wp.equals(worldPoint))
				{
					return true;
				}
			}
			return false;
		});
	}

	public Q notAt(Locatable... locatables)
	{
		return not(x ->
		{
			final WorldPoint wp = x.getWorldLocation();
			for (Locatable locatable : locatables)
			{
				if (wp.equals(locatable.getWorldLocation()))
				{
					return true;
				}
			}
			return false;
		});
	}

	public Q within(WorldArea area)
	{
		return and(x ->
		{
			final WorldPoint wp = x.getWorldLocation();
			return wp != null && area.contains(wp);
		});
	}

	public Q within(WorldArea... areas)
	{
		return and(x ->
		{
			final WorldPoint wp = x.getWorldLocation();
			for (WorldArea area : areas)
			{
				if (area.contains(wp))
				{
					return true;
				}
			}
			return false;
		});
	}

	public Q notWithin(WorldArea area)
	{
		return not(x ->
		{
			final WorldPoint wp = x.getWorldLocation();
			return wp != null && area.contains(wp);
		});
	}

	public Q notWithin(WorldArea... areas)
	{
		return not(x ->
		{
			final WorldPoint wp = x.getWorldLocation();
			for (WorldArea area : areas)
			{
				if (area.contains(wp))
				{
					return true;
				}
			}
			return false;
		});
	}

	public Q withinBoundingBox(WorldPoint min, WorldPoint max)
	{
		return and(x ->
		{
			final WorldPoint loc = x.getWorldLocation();
			return loc != null && (loc.getX() >= min.getX() && loc.getX() <= max.getX()
				&& loc.getY() >= min.getY() && loc.getY() <= max.getY());
		});
	}

	public Q withinArea(LocalPoint source, int area)
	{
		return and(x -> java.lang.Math.abs(x.getLocalLocation().getX() - source.getX()) <= area && java.lang.Math.abs(x.getLocalLocation().getY() - source.getY()) <= area);
	}

	public Q withinArea(WorldPoint source, int area)
	{
		return and(x -> java.lang.Math.abs(x.getWorldLocation().getX() - source.getX()) <= area && java.lang.Math.abs(x.getWorldLocation().getY() - source.getY()) <= area);
	}

	public Q withinArea(Locatable source, int area)
	{
		return and(x -> java.lang.Math.abs(x.getWorldLocation().getX() - source.getWorldX()) <= area && java.lang.Math.abs(x.getWorldLocation().getY() - source.getWorldY()) <= area);
	}

	public Q withinDistance(LocalPoint source, int distance)
	{
		return and(x ->
		{
			final LocalPoint lp = x.getLocalLocation();
			return lp != null && lp.distanceTo(source) <= distance;
		});
	}

	public Q withinDistance(WorldPoint source, int distance)
	{
		return and(x ->
		{
			final WorldPoint wp = x.getWorldLocation();
			return wp != null && wp.distanceTo2D(source) <= distance;
		});
	}

	public Q withinDistance(Locatable source, int distance)
	{
		return and(x ->
		{
			final WorldPoint wp = x.getWorldLocation();
			return wp != null && source.distanceTo2D(wp) <= distance;
		});
	}

	public Q withinDistance(int distance)
	{
		return withinDistance(getLocal().getWorldLocation(), distance);
	}

	public Q sortByDistance(LocalPoint source)
	{
		return sort(Comparator.comparingInt(x -> x.distanceTo(source)));
	}

	public Q sortByDistance(WorldPoint source)
	{
		return sort(Comparator.comparingInt(x -> x.distanceTo2D(source)));
	}

	public Q sortByDistance(Locatable source)
	{
		return sort(Comparator.comparingInt(x -> x.distanceTo2D(source)));
	}

	public Q sortByDistance()
	{
		return sortByDistance(getLocal().getWorldLocation());
	}

	// Results

	public T getNearest(LocalPoint source)
	{
		return stream().min(Comparator.comparingInt(x -> x.distanceTo(source))).orElse(null);
	}

	public T getNearest(WorldPoint source)
	{
		return stream().min(Comparator.comparingInt(x -> x.distanceTo2D(source))).orElse(null);
	}

	public T getNearest(Locatable source)
	{
		return stream().min(Comparator.comparingInt(x -> x.distanceTo2D(source))).orElse(null);
	}

	public T getNearest()
	{
		return getNearest(getLocal().getWorldLocation());
	}

	public T getFarthest(LocalPoint source)
	{
		return stream().max(Comparator.comparingInt(x -> x.distanceTo(source))).orElse(null);
	}

	public T getFarthest(WorldPoint source)
	{
		return stream().max(Comparator.comparingInt(x -> x.distanceTo2D(source))).orElse(null);
	}

	public T getFarthest(Locatable source)
	{
		return stream().max(Comparator.comparingInt(x -> x.distanceTo2D(source))).orElse(null);
	}

	public T getFarthest()
	{
		return getFarthest(getLocal().getWorldLocation());
	}

	// Varargs
	public T getNearest(LocalPoint... sources)
	{
		return stream().min(Comparator.comparingInt(x -> maxDistance(x, sources, Locatable::distanceTo))).orElse(null);
	}

	public T getNearest(WorldPoint... sources)
	{
		return stream().min(Comparator.comparingInt(x -> maxDistance(x, sources, Locatable::distanceTo2D))).orElse(null);
	}

	public T getNearest(Locatable... sources)
	{
		return stream().min(Comparator.comparingInt(x -> maxDistance(x, sources, (entity, source) -> entity.distanceTo2D(source.getWorldLocation())))).orElse(null);
	}

	public T getFarthest(LocalPoint... sources)
	{
		return stream().max(Comparator.comparingInt(x -> maxDistance(x, sources, Locatable::distanceTo))).orElse(null);
	}

	public T getFarthest(WorldPoint... sources)
	{
		return stream().max(Comparator.comparingInt(x -> maxDistance(x, sources, Locatable::distanceTo2D))).orElse(null);
	}

	public T getFarthest(Locatable... sources)
	{
		return stream().max(Comparator.comparingInt(x -> maxDistance(x, sources, (entity, source) -> entity.distanceTo2D(source.getWorldLocation())))).orElse(null);
	}

	private <S> int maxDistance(T entity, S[] sources, java.util.function.BiFunction<T, S, Integer> distanceFunction)
	{
		int maxDist = Integer.MIN_VALUE;
		for (S source : sources)
		{
			if (source != null)
			{
				final int dist = distanceFunction.apply(entity, source);
				if (dist > maxDist)
				{
					maxDist = dist;
				}
			}
		}
		return maxDist;
	}

	/**
	 * Calculates the centroid.
	 */
	public WorldPoint getCentroid()
	{
		int sumX = 0, sumY = 0;
		int count = 0;
		final List<T> result = result();
		for (T entity : result)
		{
			final WorldPoint wp = entity.getWorldLocation();
			if (wp != null)
			{
				sumX += wp.getX();
				sumY += wp.getY();
				count++;
			}
		}

		if (count == 0)
		{
			return null;
		}

		return new WorldPoint(sumX / count, sumY / count, 0);
	}
}
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

import java.util.Comparator;
import java.util.List;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Actor;
import net.runelite.client.util.Text;

public abstract class AbstractActorQuery<T extends RS2Actor, Q extends AbstractActorQuery<T, Q>> extends AbstractQuery<T, Q>
{
	// Queries

	public Q idEquals(int... ids)
	{
		return and(x ->
		{
			final int actorId = x instanceof NPC ? ((NPC) x).getId() : x instanceof Player ? ((Player) x).getId() : -1;
			for (int id : ids)
			{
				if (actorId == id)
				{
					return true;
				}
			}
			return false;
		});
	}

	public Q nameEquals(String... names)
	{
		return and(x ->
		{
			final String actorName = x.getName();
			if (actorName != null)
			{
				for (String name : names)
				{
					if (Text.sanitize(actorName).equalsIgnoreCase(name))
					{
						return true;
					}
				}
			}
			return false;
		});
	}

	public Q nameContains(String... names)
	{
		return and(x ->
		{
			final String actorName = x.getName();
			if (actorName != null)
			{
				for (String name : names)
				{
					if (Text.sanitize(actorName).toLowerCase().contains(name.toLowerCase()))
					{
						return true;
					}
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

	public Q withinDistance(WorldPoint source, int distance)
	{
		return and(x ->
		{
			final WorldPoint wp = x.getWorldLocation();
			return wp != null && wp.distanceTo2D(source) <= distance;
		});
	}

	public Q withinDistance(int distance)
	{
		return withinDistance(getLocal().getWorldLocation(), distance);
	}

	public Q withCombatLevels(int... levels)
	{
		return and(x ->
		{
			for (int level : levels)
			{
				if (x.getCombatLevel() == level)
				{
					return true;
				}
			}
			return false;
		});
	}

	public Q withCombatLevelsInRange(int min, int max)
	{
		return and(x -> x.getCombatLevel() >= min && x.getCombatLevel() <= max);
	}

	// Results

	public T getNearest(LocalPoint source)
	{
		return stream().min(Comparator.comparingInt(x -> x.getLocalLocation().distanceTo(source))).orElse(null);
	}

	public T getNearest(WorldPoint source)
	{
		return stream().min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo2D(source))).orElse(null);
	}

	public T getNearest()
	{
		return getNearest(getLocal().getWorldLocation());
	}

	public T getFarthest(LocalPoint source)
	{
		return stream().max(Comparator.comparingInt(x -> x.getLocalLocation().distanceTo(source))).orElse(null);
	}

	public T getFarthest(WorldPoint source)
	{
		return stream().max(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo2D(source))).orElse(null);
	}

	public T getFarthest()
	{
		return getFarthest(getLocal().getWorldLocation());
	}

	public List<T> getSortedByDistance(WorldPoint source)
	{
		return sorted(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo2D(source)));
	}

	public List<T> getSortedByDistance()
	{
		return getSortedByDistance(getLocal().getWorldLocation());
	}
}
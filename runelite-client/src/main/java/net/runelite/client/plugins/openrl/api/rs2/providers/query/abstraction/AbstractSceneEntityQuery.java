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
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.api.Locatable;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.api.SceneEntity;
import net.runelite.client.util.Text;

public abstract class AbstractSceneEntityQuery<T extends SceneEntity, Q extends AbstractSceneEntityQuery<T, Q>> extends AbstractQuery<T, Q>
{
	// Queries

	public Q idEquals(int... ids)
	{
		return and(x ->
		{
			final int entityId = x.getId();
			for (int id : ids)
			{
				if (entityId == id)
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
			final String entityName = x.getName();
			if (entityName != null)
			{
				for (String name : names)
				{
					if (Text.sanitize(entityName).equalsIgnoreCase(name))
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
			final String entityName = x.getName();
			if (entityName != null)
			{
				for (String name : names)
				{
					if (Text.sanitize(entityName).toLowerCase().contains(name.toLowerCase()))
					{
						return true;
					}
				}
			}
			return false;
		});
	}

	public Q actionEquals(String... actions)
	{
		return and(x ->
		{
			final String[] entityActions = x.getActions();
			if (entityActions != null)
			{
				for (String action : actions)
				{
					for (String entityAction : entityActions)
					{
						if (entityAction != null && entityAction.equalsIgnoreCase(action))
						{
							return true;
						}
					}
				}
			}
			return false;
		});
	}

	public Q actionContains(String... actions)
	{
		return and(x ->
		{
			final String[] entityActions = x.getActions();
			if (entityActions != null)
			{
				for (String action : actions)
				{
					for (String entityAction : entityActions)
					{
						if (entityAction != null)
						{
							if (entityAction.toLowerCase().contains(action.toLowerCase()))
							{
								return true;
							}
						}
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
}
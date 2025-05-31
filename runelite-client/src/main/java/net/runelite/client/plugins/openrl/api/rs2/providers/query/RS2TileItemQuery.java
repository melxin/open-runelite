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
import java.util.stream.Collectors;
import net.runelite.api.TileItem;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.abstraction.AbstractQuery;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Tile;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2TileItem;

public class RS2TileItemQuery extends AbstractQuery<RS2TileItem, RS2TileItemQuery>
{
	private RS2TileItemQuery()
	{
	}

	public static RS2TileItemQuery query()
	{
		return new RS2TileItemQuery();
	}

	@Override
	protected List<RS2TileItem> all(Predicate<? super RS2TileItem> filter)
	{
		return RS2TileQuery.query().stream()
			.flatMap(tile -> at(tile, filter).stream())
			.collect(Collectors.toList());
	}

	private List<RS2TileItem> at(RS2Tile tile, Predicate<? super RS2TileItem> filter)
	{
		final List<RS2TileItem> out = new ArrayList<>();
		if (tile == null)
		{
			return out;
		}

		if (tile.getGroundItems() != null)
		{
			for (TileItem item : tile.getGroundItems())
			{
				if (item == null || item.getId() == -1)
				{
					continue;
				}

				final RS2TileItem rs2TileItem = new RS2TileItem(item);
				if (!filter.test(rs2TileItem))
				{
					continue;
				}

				out.add(rs2TileItem);
			}
		}

		return out;
	}

	// Query

	public RS2TileItemQuery idEquals(int... ids)
	{
		return and(x ->
		{
			final int itemId = x.getId();
			for (int id : ids)
			{
				if (itemId == id)
				{
					return true;
				}
			}
			return false;
		});
	}

	public RS2TileItemQuery nameEquals(String... names)
	{
		return and(x ->
		{
			final String itemName = x.getName();
			if (itemName != null)
			{
				for (String name : names)
				{
					if (itemName.equalsIgnoreCase(name))
					{
						return true;
					}
				}
			}
			return false;
		});
	}

	public RS2TileItemQuery nameContains(String... names)
	{
		return and(x ->
		{
			final String itemName = x.getName();
			if (itemName != null)
			{
				for (String name : names)
				{
					if (itemName.toLowerCase().contains(name.toLowerCase()))
					{
						return true;
					}
				}
			}
			return false;
		});
	}

	// Results

	public RS2TileItem getNearest(LocalPoint source)
	{
		return stream().min(Comparator.comparingInt(x -> x.getLocalLocation().distanceTo(source))).orElse(null);
	}

	public RS2TileItem getNearest()
	{
		return getNearest(getLocal().getLocalLocation());
	}

	public RS2TileItem getFarthest(LocalPoint source)
	{
		return stream().max(Comparator.comparingInt(x -> x.getLocalLocation().distanceTo(source))).orElse(null);
	}

	public RS2TileItem getFarthest()
	{
		return getFarthest(getLocal().getLocalLocation());
	}

	public List<RS2TileItem> getSortedByDistance(LocalPoint source)
	{
		return sorted(Comparator.comparingInt(x -> x.getLocalLocation().distanceTo(source)));
	}

	public List<RS2TileItem> getSortedByDistance()
	{
		return getSortedByDistance(getLocal().getLocalLocation());
	}
}
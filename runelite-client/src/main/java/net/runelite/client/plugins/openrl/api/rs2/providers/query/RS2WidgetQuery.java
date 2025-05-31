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
import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.abstraction.AbstractQuery;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;

public class RS2WidgetQuery extends AbstractQuery<RS2Widget, RS2WidgetQuery>
{
	private RS2WidgetQuery()
	{
	}

	public static RS2WidgetQuery query()
	{
		return new RS2WidgetQuery();
	}

	@Override
	protected List<RS2Widget> all(Predicate<? super RS2Widget> filter)
	{
		final List<RS2Widget> out = new ArrayList<>();
		final Widget[] widgetRoots = Static.getClient().getWidgetRoots();
		for (Widget widgetRoot : widgetRoots)
		{
			final RS2Widget widget = new RS2Widget(widgetRoot);
			if (filter.test(widget))
			{
				out.add(widget);
			}
		}
		return out;
	}

	// Queries

	public RS2WidgetQuery idEquals(int... ids)
	{
		return and(x ->
		{
			final int widgetId = x.getId();
			for (int id : ids)
			{
				if (widgetId == id)
				{
					return true;
				}
			}
			return false;
		});
	}

	public RS2WidgetQuery nameEquals(String... names)
	{
		return and(x ->
		{
			final String widgetName = x.getName();
			if (widgetName != null)
			{
				for (String name : names)
				{
					if (widgetName.equalsIgnoreCase(name))
					{
						return true;
					}
				}
			}
			return false;
		});
	}

	public RS2WidgetQuery nameContains(String... names)
	{
		return and(x ->
		{
			final String widgetName = x.getName();
			if (widgetName != null)
			{
				for (String name : names)
				{
					if (widgetName.toLowerCase().contains(name.toLowerCase()))
					{
						return true;
					}
				}
			}
			return false;
		});
	}

	public RS2WidgetQuery isVisible()
	{
		return and(x -> !x.isHidden());
	}

	// Results
}
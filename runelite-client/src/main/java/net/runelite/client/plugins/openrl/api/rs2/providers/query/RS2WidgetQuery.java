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

import org.apache.commons.lang3.ArrayUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.ScriptID;
import net.runelite.api.annotations.Component;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Predicates;
import net.runelite.client.plugins.openrl.api.rs2.providers.client.RS2ClientScript;
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

	public RS2WidgetQuery idEquals(int id)
	{
		return and(Predicates.idEquals(id));
	}

	public RS2WidgetQuery idEquals(int... ids)
	{
		return and(Predicates.idEquals(ids));
	}

	public RS2WidgetQuery idEquals(Collection<Integer> ids)
	{
		return and(Predicates.idEquals(ids));
	}

	public RS2WidgetQuery nameEquals(String name)
	{
		return and(Predicates.nameEquals(name));
	}

	public RS2WidgetQuery nameEquals(String... names)
	{
		return and(Predicates.nameEquals(names));
	}

	public RS2WidgetQuery nameEquals(Collection<String> names)
	{
		return and(Predicates.nameEquals(names));
	}

	public RS2WidgetQuery nameContains(String name)
	{
		return and(Predicates.nameContains(name));
	}

	public RS2WidgetQuery nameContains(String... names)
	{
		return and(Predicates.nameContains(names));
	}

	public RS2WidgetQuery actionEquals(String action)
	{
		return and(Predicates.actionEquals(action));
	}

	public RS2WidgetQuery actionEquals(String... actions)
	{
		return and(Predicates.actionEquals(actions));
	}

	public RS2WidgetQuery actionContains(String action)
	{
		return and(Predicates.actionContains(action));
	}

	public RS2WidgetQuery actionContains(String... actions)
	{
		return and(Predicates.actionContains(actions));
	}

	public RS2WidgetQuery textEquals(String text)
	{
		return and(x -> x.getText().equalsIgnoreCase(text));
	}

	public RS2WidgetQuery textEquals(String... text)
	{
		return and(x ->
		{
			final String widgetText = x.getText();
			if (widgetText == null)
			{
				return false;
			}
			for (String s : text)
			{
				if (s != null && widgetText.equalsIgnoreCase(s))
				{
					return true;
				}
			}
			return false;
		});
	}

	public RS2WidgetQuery textContains(String text)
	{
		return and(x -> x.getText().toLowerCase().contains(text.toLowerCase()));
	}

	public RS2WidgetQuery textContains(String... text)
	{
		return and(x ->
		{
			final String widgetText = x.getText();
			if (widgetText == null)
			{
				return false;
			}
			for (String s : text)
			{
				if (s != null && widgetText.toLowerCase().contains(s.toLowerCase()))
				{
					return true;
				}
			}
			return false;
		});
	}

	public RS2WidgetQuery itemIdEquals(int itemId)
	{
		return and(x -> x.getItemId() == itemId);
	}

	public RS2WidgetQuery itemIdEquals(int... itemIds)
	{
		return and(x -> ArrayUtils.contains(itemIds, x.getItemId()));
	}

	public RS2WidgetQuery itemIdEquals(Collection<Integer> itemIds)
	{
		return and(x -> itemIds.contains(x.getId()));
	}

	public RS2WidgetQuery parentIdEquals(int parentId)
	{
		return and(x -> x.getParentId() == parentId);
	}

	public RS2WidgetQuery parentIdEquals(int... parentIds)
	{
		return and(x -> ArrayUtils.contains(parentIds, x.getParentId()));
	}

	public RS2WidgetQuery parentIdEquals(Collection<Integer> parentIds)
	{
		return and(x -> parentIds.contains(x.getId()));
	}

	public RS2WidgetQuery withType(int type)
	{
		return and(w -> w.getType() == type);
	}

	public RS2WidgetQuery withType(int... types)
	{
		return and(w -> ArrayUtils.contains(types, w.getType()));
	}

	public RS2WidgetQuery notWithType(int type)
	{
		return not(w -> w.getType() == type);
	}

	public RS2WidgetQuery notWithType(int... types)
	{
		return not(w -> ArrayUtils.contains(types, w.getType()));
	}

	public RS2WidgetQuery isVisible()
	{
		return and(x -> x != null && x.isVisible());
	}

	// Results

	public RS2Widget get(WidgetInfo widgetInfo)
	{
		final Widget widget = Static.getClient().getWidget(widgetInfo);
		return widget != null ? new RS2Widget(widget) : null;
	}

	public RS2Widget get(int group, int id)
	{
		final Widget widget = Static.getClient().getWidget(group, id);
		return widget != null ? new RS2Widget(widget) : null;
	}

	public RS2Widget get(int group, int id, int child)
	{
		final RS2Widget widget = get(group, id);
		if (widget == null)
		{
			return null;
		}
		final RS2Widget childWidget = widget.getChild(child);
		return childWidget != null ? childWidget : null;
	}

	public List<RS2Widget> get(int group)
	{
		final List<RS2Widget> widgets = all(w -> w.getId() == group);
		if (widgets == null || widgets.isEmpty())
		{
			return Collections.emptyList();
		}
		final List<RS2Widget> out = new ArrayList<>();
		for (RS2Widget widget : widgets)
		{
			out.add(widget);
		}

		return out;
	}

	public List<RS2Widget> getChildren(RS2Widget widget)
	{
		if (widget == null)
		{
			return Collections.emptyList();
		}

		final RS2Widget[] children = widget.getChildren();
		if (children == null || children.length == 0)
		{
			return Collections.emptyList();
		}

		return Arrays.stream(children)
			.collect(Collectors.toList());
	}

	public List<RS2Widget> getChildren(WidgetInfo widgetInfo)
	{
		return getChildren(get(widgetInfo));
	}

	public List<RS2Widget> getChildren(int group, int child)
	{
		return getChildren(get(group, child));
	}

	public RS2Widget getWidget(@Component int componentId)
	{
		final Widget widget = Static.getClient().getWidget(componentId);
		return widget != null ? new RS2Widget(widget) : null;
	}

	public RS2Widget getWidget(@Component int componentId, int childId)
	{
		final Widget widget = Static.getClient().getWidget(componentId);
		final Widget child = widget != null ? widget.getChild(childId) : null;
		return child != null ? new RS2Widget(child) : null;
	}

	public boolean isVisible(RS2Widget widget)
	{
		return widget != null ? widget.isVisible() : false;
	}

	/**
	 * Scroll to widget {@link net.runelite.client.plugins.cluescrolls.ClueScrollPlugin}
	 *
	 * @param list
	 * @param scrollbar
	 * @param widgets
	 */
	public void scrollToWidget(@Component int list, @Component int scrollbar, RS2Widget... widgets)
	{
		final RS2Widget parent = RS2WidgetQuery.query().getWidget(list);
		int averageCentralY = 0;
		int nonnullCount = 0;
		for (Widget widget : widgets)
		{
			if (widget != null)
			{
				averageCentralY += widget.getRelativeY() + widget.getHeight() / 2;
				nonnullCount += 1;
			}
		}
		if (nonnullCount == 0)
		{
			return;
		}
		averageCentralY /= nonnullCount;
		final int newScroll = Math.max(0, Math.min(parent.getScrollHeight(),
			averageCentralY - parent.getHeight() / 2));

		RS2ClientScript.runScript(
			ScriptID.UPDATE_SCROLLBAR,
			scrollbar,
			list,
			newScroll
		);
	}
}
package net.runelite.client.plugins.openrl.api.rs2.providers.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.ScriptID;
import net.runelite.api.annotations.Component;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.rs2.providers.client.RS2ClientScript;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.RS2WidgetQuery;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;

public class RS2Widgets
{
	private static final RS2Widgets WIDGETS = new RS2Widgets();

	private RS2Widgets()
	{
	}

	public static RS2WidgetQuery query()
	{
		return RS2WidgetQuery.query();
	}

	public static RS2Widget get(WidgetInfo widgetInfo)
	{
		final Widget widget = Static.getClient().getWidget(widgetInfo);
		return widget != null ? new RS2Widget(widget) : null;
	}

	public static RS2Widget get(int group, int id)
	{
		final Widget widget = Static.getClient().getWidget(group, id);
		return widget != null ? new RS2Widget(widget) : null;
	}

	public static RS2Widget get(int group, int id, int child)
	{
		return get(group, id) == null ? null : get(group, id).getChild(child);
	}

	public static List<RS2Widget> get(int group)
	{
		final List<RS2Widget> widgets = WIDGETS.all(w -> w.getId() == group);
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

	public static RS2Widget get(int group, Predicate<RS2Widget> filter)
	{
		return get(group).stream().filter(filter).findFirst().orElse(null);
	}

	public static List<RS2Widget> getChildren(RS2Widget widget, Predicate<RS2Widget> filter)
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
			.filter(filter)
			.collect(Collectors.toList());
	}

	public static List<RS2Widget> getChildren(WidgetInfo widgetInfo, Predicate<RS2Widget> filter)
	{
		return getChildren(get(widgetInfo), filter);
	}

	public static List<RS2Widget> getChildren(int group, int child, Predicate<RS2Widget> filter)
	{
		return getChildren(get(group, child), filter);
	}

	public static List<RS2Widget> getAll(int group, Predicate<RS2Widget> filter)
	{
		return get(group).stream().filter(filter).collect(Collectors.toList());
	}

	protected List<RS2Widget> all(Predicate<RS2Widget> filter)
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

	public static List<RS2Widget> getAll()
	{
		return WIDGETS.all(x -> true);
	}

	public static List<RS2Widget> getAll(Predicate<RS2Widget> filter)
	{
		return WIDGETS.all(filter);
	}

	public static RS2Widget getWidget(@Component int componentId)
	{
		final Widget widget = Static.getClient().getWidget(componentId);
		return widget != null ? new RS2Widget(widget) : null;
	}

	public static RS2Widget fromId(@Component int packedId)
	{
		final Widget widget = Static.getClient().getWidget(packedId);
		return widget != null ? new RS2Widget(widget) : null;
	}

	public static boolean isVisible(RS2Widget widget)
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
	public static void scrollToWidget(@Component int list, @Component int scrollbar, RS2Widget... widgets)
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
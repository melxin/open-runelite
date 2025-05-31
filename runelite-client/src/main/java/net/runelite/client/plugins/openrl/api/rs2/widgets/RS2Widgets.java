package net.runelite.client.plugins.openrl.api.rs2.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.annotations.Component;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.openrl.Static;

public class RS2Widgets
{
	private static final RS2Widgets WIDGETS = new RS2Widgets();

	private RS2Widgets()
	{
	}

	public static RS2Widget get(WidgetInfo widgetInfo)
	{
		return new RS2Widget(Static.getClient().getWidget(widgetInfo));
	}

	public static RS2Widget get(int group, int id)
	{
		return new RS2Widget(Static.getClient().getWidget(group, id));
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

	public static List<RS2Widget> getAll(Predicate<RS2Widget> filter)
	{
		return WIDGETS.all(filter);
	}

	public static List<RS2Widget> getAll()
	{
		return WIDGETS.all(x -> true);
	}

	public static RS2Widget getWidget(@Component int componentId)
	{
		final Widget widget = Static.getClient().getWidget(componentId);
		return widget != null ? new RS2Widget(widget) : null;
	}

	public static RS2Widget fromId(@Component int packedId)
	{
		return new RS2Widget(Static.getClient().getWidget(packedId));
	}

	public static boolean isVisible(RS2Widget widget)
	{
		if (widget == null)
		{
			return false;
		}
		final boolean isVisible = Static.getClientThread().runOnClientThreadOptional(() -> !widget.isHidden()).orElse(false);
		return isVisible;
	}
}
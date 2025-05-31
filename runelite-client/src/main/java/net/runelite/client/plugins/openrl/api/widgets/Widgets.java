package net.runelite.client.plugins.openrl.api.widgets;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.plugins.openrl.Static;

public class Widgets
{
	public static Widget get(WidgetInfo widgetInfo)
	{
		return Static.getClient().getWidget(widgetInfo);
	}

	public static Widget get(int group, int id)
	{
		return Static.getClient().getWidget(group, id);
	}

	public static Widget get(int group, int id, int child)
	{
		return get(group, id) == null ? null : get(group, id).getChild(child);
	}

	public static List<Widget> get(int group)
	{
		final List<Widget> widgets = Arrays.asList(Static.getClient().getWidgetRoots()).stream().filter(w -> w.getId() == group).collect(Collectors.toList());
		if (widgets == null || widgets.isEmpty())
		{
			return Collections.emptyList();
		}

		return widgets;
	}

	public static Widget get(int group, Predicate<Widget> filter)
	{
		return get(group).stream().filter(filter).findFirst().orElse(null);
	}

	public static List<Widget> getChildren(Widget widget, Predicate<Widget> filter)
	{
		if (widget == null)
		{
			return Collections.emptyList();
		}

		Widget[] children = widget.getChildren();
		if (children == null)
		{
			return Collections.emptyList();
		}

		return Arrays.stream(children).filter(filter).collect(Collectors.toList());
	}

	public static List<Widget> getChildren(WidgetInfo widgetInfo, Predicate<Widget> filter)
	{
		return getChildren(get(widgetInfo), filter);
	}

	public static List<Widget> getChildren(int group, int child, Predicate<Widget> filter)
	{
		return getChildren(get(group, child), filter);
	}

	public static List<Widget> getAll(int group, Predicate<Widget> filter)
	{
		return get(group).stream().filter(filter).collect(Collectors.toList());
	}

	public static Widget fromId(int packedId)
	{
		return Static.getClient().getWidget(packedId);
	}

	public static boolean isVisible(Widget widget)
	{
		return widget != null && !widget.isHidden();
	}

	public static int getMenuIdentifier(Widget widget, int actionIndex)
	{
		switch (widget.getType())
		{
			case WidgetType.GRAPHIC:
				return widget.getTargetVerb() == null || widget.getTargetVerb().isEmpty() || Arrays.asList(widget.getActions()).contains("Cast")  && !widget.getTargetVerb().startsWith("Cast") ? actionIndex + 1 : 0;
			case WidgetType.TEXT:
				return 0;
			default:
				return actionIndex + 1;
		}
	}
}

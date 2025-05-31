package net.runelite.client.plugins.openrl.api.widgets;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.MenuAction;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;

public class Widgets
{
	private static final Widgets WIDGETS = new Widgets();

	private Widgets()
	{
	}

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
		final List<Widget> widgets = WIDGETS.all(w -> w.getId() == group);
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

	public static List<Widget> getAll()
	{
		return getAll(x -> true);
	}

	public static List<Widget> getAll(Predicate<Widget> filter)
	{
		return WIDGETS.all(filter);
	}

	protected List<Widget> all(Predicate<Widget> filter)
	{
		return Arrays.stream(Static.getClient().getWidgetRoots())
			.filter(filter)
			.collect(Collectors.toList());
	}

	public static Widget getWidget(int componentId)
	{
		return Static.getClient().getWidget(componentId);
	}

	public static Widget fromId(int packedId)
	{
		return Static.getClient().getWidget(packedId);
	}

	public static boolean isVisible(Widget widget)
	{
		if (widget == null)
		{
			return false;
		}
		final boolean isVisible = Static.getClientThread().runOnClientThreadOptional(() -> !widget.isHidden()).orElse(false);
		return isVisible;
	}

	public static void interact(Widget widget, int index)
	{
		interact(widget, getAction(widget, index));
	}

	public static void interact(Widget widget, String action)
	{
		final int param0 = widget.getIndex(); // -1
		final int param1 = widget.getId();
		final MenuAction menuAction = getMenuAction(action);
		final String[] actions = widget.getActions();
		final int actionIndex = Arrays.asList(actions).indexOf(action) + 1;
		final int itemId = widget.getItemId();
		final int worldViewId = -1;
		final String option = "";
		final String target = "";
		final Rectangle bounds = widget.getBounds();
		final int x = (int) bounds.getX();
		final int y = (int) bounds.getY();
		Static.getEventBus().post(new MenuAutomated(param0, param1, menuAction, actionIndex, itemId, worldViewId, option, target, x, y));
	}

	public static String getAction(Widget widget, int index)
	{
		final String[] actions = Arrays.stream(widget.getActions())
			.filter(a -> a != null && !a.equals("null"))
			.toArray(String[]::new);
		if (index >= 0 && index < actions.length)
		{
			return actions[index];
		}
		return "null";
	}

	public static MenuAction getMenuAction(String action)
	{
		final MenuAction menuAction = Static.getClient().isWidgetSelected() ? MenuAction.WIDGET_TARGET_ON_WIDGET
			: action.equalsIgnoreCase("use") ? MenuAction.WIDGET_TARGET
			: action.equalsIgnoreCase("cast") ? MenuAction.WIDGET_TARGET_ON_WIDGET
			: MenuAction.CC_OP;
		return menuAction;
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

package net.runelite.client.plugins.openrl.api.managers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.openrl.OpenRuneLiteConfig;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Time;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;
import net.runelite.client.plugins.openrl.api.input.Mouse;
import net.runelite.client.plugins.openrl.api.input.naturalmouse.NaturalMouse;
import net.runelite.client.plugins.openrl.api.widgets.Widgets;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Point;
import java.awt.Rectangle;

@Singleton
@Slf4j
public class InteractionManager
{
	private static final int MINIMAP_WIDTH = 250;
	private static final int MINIMAP_HEIGHT = 180;

	@Inject
	private NaturalMouse naturalMouse;

	@Inject
	private OpenRuneLiteConfig config;

	@Inject
	private Client client;

	@Inject
	InteractionManager(EventBus eventBus)
	{
		eventBus.register(this);
	}

	@Subscribe
	public void onMenuAutomated(MenuAutomated event)
	{
		log.info("{}", event);
		interact(event.getParam0(), event.getParam1(), event.getMenuAction(), event.getIndex(), event.getItemId(), event.getWorldViewId(), event.getOption(), event.getTarget(), event.getCanvasX(), event.getCanvasY());
	}

	private void interact(int param0, int param1, MenuAction menuAction, int index, int itemId, int worldViewId, String option, String target, int canvasX, int canvasY)
	{
		switch (config.interactionMethod())
		{
			case INVOKE:
				Static.invokeMenuAction(param0, param1, menuAction, index, itemId, worldViewId, option, target, canvasX, canvasY);
				break;
			case MOUSE_EVENTS:
				naturalMouse.moveTo(canvasX, canvasY);
				Point mousePosition = Mouse.getPosition();
				if (mousePosition.getX() == canvasX && mousePosition.getY() == canvasY)
				{
					Mouse.click(canvasX, canvasY, true);
				}
				break;
			case BOTH:
				naturalMouse.moveTo(canvasX, canvasY);
				mousePosition = Mouse.getPosition();
				if (mousePosition.getX() == canvasX && mousePosition.getY() == canvasY)
				{
					Mouse.click(canvasX, canvasY, true);
					Time.sleep(50, 100);
					Static.invokeMenuAction(param0, param1, menuAction, index, itemId, worldViewId, option, target, canvasX, canvasY);
				}
				break;
		}
	}

	public boolean clickInsideMinimap(Point point)
	{
		return getMinimap().contains(point);
	}

	private Rectangle getMinimap()
	{
		Widget minimap = Widgets.get(WidgetInfo.FIXED_VIEWPORT_MINIMAP_DRAW_AREA);
		if (Widgets.isVisible(minimap))
		{
			return minimap.getBounds();
		}

		Widget minimap1 = Widgets.get(WidgetInfo.RESIZABLE_MINIMAP_DRAW_AREA);
		if (Widgets.isVisible(minimap1))
		{
			return minimap1.getBounds();
		}

		Widget minimap2 = Widgets.get(WidgetInfo.RESIZABLE_MINIMAP_STONES_DRAW_AREA);
		if (Widgets.isVisible(minimap2))
		{
			return minimap2.getBounds();
		}

		Rectangle bounds = client.getCanvas().getBounds();
		return new Rectangle(bounds.width - MINIMAP_WIDTH, 0, MINIMAP_WIDTH, MINIMAP_HEIGHT);
	}

	private boolean clickOffScreen(Point point)
	{
		return point.x < 0 || point.y < 0
			|| point.x > client.getViewportWidth() || point.y > client.getViewportHeight();
	}

	@Getter
	@AllArgsConstructor
	public enum InteractMethod
	{
		INVOKE, MOUSE_EVENTS, BOTH
	}
}

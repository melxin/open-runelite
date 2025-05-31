package net.runelite.client.plugins.openrl.api.managers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
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
import net.runelite.client.plugins.openrl.api.reflection.Reflection;
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
	private Client client;

	@Inject
	private OpenRuneLiteConfig config;

	@Inject
	private NaturalMouse naturalMouse;

	@Inject
	InteractionManager(EventBus eventBus)
	{
		eventBus.register(this);
	}

	private MenuAutomated menuAutomated;
	private MenuEntry targetMenu;

	@Subscribe
	public void onMenuAutomated(MenuAutomated event)
	{
		log.info("{}", event);
		this.menuAutomated = event;
		this.targetMenu = event.getMenuEntry();
		interact((MenuAutomated) event);
	}

	private void interact(MenuAutomated menuAutomated)
	{
		interact(menuAutomated.getParam0(), menuAutomated.getParam1(), menuAutomated.getMenuAction(), menuAutomated.getIndex(), menuAutomated.getItemId(), menuAutomated.getWorldViewId(), menuAutomated.getOption(), menuAutomated.getTarget(), menuAutomated.getCanvasX(), menuAutomated.getCanvasY());
	}

	private void interact(MenuEntry menuEntry)
	{
		interact(menuEntry.getParam0(), menuEntry.getParam1(), menuEntry.getType(), menuEntry.getIdentifier(), menuEntry.getItemId(), menuEntry.getWorldViewId(), menuEntry.getOption(), menuEntry.getTarget(), -1, -1);
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

	@Getter
	@AllArgsConstructor
	public enum InteractMethod
	{
		INVOKE, MOUSE_EVENTS, BOTH
	}

	@Subscribe(priority = Integer.MAX_VALUE)
	private void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (targetMenu != null && event.getType() != targetMenu.getType().getId())
		{
			this.client.getMenu().setMenuEntries(new MenuEntry[]{});
		}

		if (targetMenu != null)
		{
			if (targetMenu.getItemId() > 0)
			{
				Reflection.setItemId(targetMenu, targetMenu.getItemId());
			}
			this.client.getMenu().setMenuEntries(new MenuEntry[]{targetMenu});
		}
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked event)
	{
		this.targetMenu = null;
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
}

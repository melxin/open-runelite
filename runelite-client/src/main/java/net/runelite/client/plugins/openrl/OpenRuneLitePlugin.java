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
package net.runelite.client.plugins.openrl;

import lombok.extern.slf4j.Slf4j;
import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.openrl.api.events.Draw;
import net.runelite.client.plugins.openrl.api.plugin.SettingsPlugin;
import net.runelite.client.plugins.openrl.api.reflection.Reflection;
import net.runelite.client.plugins.openrl.ui.OpenRuneLitePanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ImageUtil;

@PluginDescriptor(
	name = "Open RuneLite Plugin",
	hidden = true
)
@Slf4j
public class OpenRuneLitePlugin extends SettingsPlugin
{
	@Inject
	private OpenRuneLiteConfig config;

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (configChanged.getGroup().equals(OpenRuneLiteConfig.GROUP))
		{
			if (configChanged.getKey().equals("printMenuActions"))
			{
				Reflection.setPrintMenuActions(config.printMenuActions());
			}
		}
	}

	@Subscribe
	protected void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (config.printMenuActions())
		{
			/* The RuneScape client may deprioritize an action in the menu by incrementing the opcode with 2000,
			 * undo it here so we can get the correct opcode
			 */
			/*boolean decremented = false;
			int opcode = event.getMenuEntry().getIdentifier();
			if (opcode >= 2000)
			{
				decremented = true;
				opcode -= 2000;
			}

			log.info("|MenuAction|: MenuOption={} MenuTarget={} Id={} Opcode={}/{} Param0={} Param1={} CanvasX={} CanvasY={} ItemId={} WorldViewId={}",
				event.getMenuOption(), event.getMenuTarget(), event.getId(),
				event.getMenuAction(), opcode + (decremented ? 2000 : 0),
				event.getParam0(), event.getParam1(), "canvasX", "canvasY", event.getItemId(), -1
			);

			MenuEntry menuEntry = event.getMenuEntry();
			if (menuEntry != null)
			{
				log.info(
					"|MenuEntry|: Idx={} MenuOption={} MenuTarget={} Id={} MenuAction={} Param0={} Param1={} Consumed={} IsItemOp={} ItemOp={} ItemID={} WorldViewId={} Widget={}",
					event.getId(), menuEntry.getOption(), menuEntry.getTarget(), menuEntry.getIdentifier(), menuEntry.getType(), menuEntry.getParam0(), menuEntry.getParam1(), event.isConsumed(), menuEntry.isItemOp(), menuEntry.getItemOp(), menuEntry.getItemId(), menuEntry.getWorldViewId(), menuEntry.getWidget()
				);
			}*/
		}
	}

	private static Robot robot;

	static
	{
		try
		{
			robot = new Robot();
		}
		catch (AWTException e)
		{
			log.error("", e);
		}
	}

	@Inject
	private EventBus eventBus;
	@Inject
	private ClientToolbar clientToolbar;
	@Inject
	private ConfigManager configManager;
	private OpenRuneLitePanel openRuneLitePanel;
	private NavigationButton navButton;

	private final Point mousePosition = new Point();
	private boolean isClicked;

	@Override
	protected void startUp()
	{
		openRuneLitePanel = new OpenRuneLitePanel(config, configManager);

		eventBus.register(openRuneLitePanel);

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/openrl.png");

		navButton = NavigationButton.builder()
			.tooltip("Open RuneLite Client")
			.icon(icon)
			.priority(-1)
			.panel(openRuneLitePanel)
			.build();

		clientToolbar.addNavigation(navButton);

		Reflection.setPrintMenuActions(config.printMenuActions());

		final Canvas canvas = Static.getClient().getCanvas();
		canvas.addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
				mousePosition.setLocation(e.getX(), e.getY());
				canvas.repaint();
			}
		});

		canvas.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				isClicked = true;
				canvas.repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				isClicked = false;
				canvas.repaint();
			}
		});
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
		//eventBus.unregister(regionHandler);
		eventBus.unregister(openRuneLitePanel);
	}

	@Subscribe
	protected void onDraw(Draw event)
	{
		final Graphics graphics = event.getGraphics();

		if (graphics == null)
		{
			return;
		}

		if (config.overlayEnabled())
		{
			graphics.setColor(config.overlayColor());
			//graphics.drawString("Time running: " + this.getTimeRunning(), 10, 20);
			graphics.drawString("Test: " + "test", 10, 35);
		}

		if (config.mousePositionOverlay())
		{
			// Render mouse cross
			final Graphics2D g2d = (Graphics2D) graphics;
			g2d.setColor(isClicked ? Color.RED : Color.YELLOW);
			final float strokeWidth = 4.0f;
			final Stroke oldStroke = g2d.getStroke();
			g2d.setStroke(new BasicStroke(strokeWidth));

			final int size = 10;
			final int halfSize = size / 2;
			final int x = mousePosition.x;
			final int y = mousePosition.y;

			g2d.drawLine(x - halfSize, y - halfSize, x + halfSize, y + halfSize);
			g2d.drawLine(x - halfSize, y + halfSize, x + halfSize, y - halfSize);
			g2d.setStroke(oldStroke);

			// Render mouse position
			OverlayUtil.renderTextLocation(g2d,
				new net.runelite.api.Point(10, 20),
				"x: " + mousePosition.getY() + " y: " + mousePosition.getY(),
				Color.YELLOW);

			// Render pixel hex color
			if (robot != null)
			{
				final Color color = robot.getPixelColor(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);

				OverlayUtil.renderTextLocation(g2d,
					new net.runelite.api.Point(10, 35),
					"hex: " + String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()),
					Color.YELLOW);
			}
		}
	}

	@Override
	public Config getConfig()
	{
		return config;
	}

	@Override
	public String getPluginName()
	{
		return "Open RuneLite";
	}

	@Override
	public String getPluginDescription()
	{
		return "Open RuneLite Settings";
	}

	@Override
	public String[] getPluginTags()
	{
		return new String[]{"Open RuneLite"};
	}
}
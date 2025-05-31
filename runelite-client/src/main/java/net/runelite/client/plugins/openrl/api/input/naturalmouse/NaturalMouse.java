package net.runelite.client.plugins.openrl.api.input.naturalmouse;

import lombok.Getter;
import lombok.Setter;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Menu;
import net.runelite.client.plugins.openrl.api.commons.Time;
import net.runelite.client.plugins.openrl.api.input.Mouse;
import net.runelite.client.plugins.openrl.api.input.naturalmouse.api.MouseInfoAccessor;
import net.runelite.client.plugins.openrl.api.input.naturalmouse.api.MouseMotionFactory;
import net.runelite.client.plugins.openrl.api.input.naturalmouse.api.SystemCalls;
import net.runelite.client.plugins.openrl.api.input.naturalmouse.support.DefaultMouseMotionNature;
import net.runelite.client.plugins.openrl.api.input.naturalmouse.support.DefaultNoiseProvider;
import net.runelite.client.plugins.openrl.api.input.naturalmouse.support.DefaultOvershootManager;
import net.runelite.client.plugins.openrl.api.input.naturalmouse.support.DefaultSpeedManager;
import net.runelite.client.plugins.openrl.api.input.naturalmouse.support.Flow;
import net.runelite.client.plugins.openrl.api.input.naturalmouse.support.MouseMotionNature;
import net.runelite.client.plugins.openrl.api.input.naturalmouse.support.SinusoidalDeviationProvider;
import net.runelite.client.plugins.openrl.api.input.naturalmouse.util.FlowTemplates;
import net.runelite.client.plugins.openrl.api.input.naturalmouse.util.Pair;

@Singleton
public class NaturalMouse
{
	private final MouseMotionNature nature;
	private final ThreadLocalRandom random = ThreadLocalRandom.current();
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	@Inject
	private Client client;
	@Getter
	@Setter
	private List<Flow> flows = List.of(
			new Flow(FlowTemplates.variatingFlow()),
			new Flow(FlowTemplates.slowStartupFlow()),
			new Flow(FlowTemplates.slowStartup2Flow()),
			new Flow(FlowTemplates.jaggedFlow()),
			new Flow(FlowTemplates.interruptedFlow()),
			new Flow(FlowTemplates.interruptedFlow2()),
			new Flow(FlowTemplates.stoppingFlow()),
			new Flow(FlowTemplates.adjustingFlow()),
			new Flow(FlowTemplates.random())
	);

	@Inject
	private NaturalMouse()
	{
		nature = new DefaultMouseMotionNature();
		nature.setSystemCalls(new SystemCallsImpl());
		nature.setMouseInfo(new MouseInfoImpl());
	}

	public synchronized void moveTo(int dx, int dy)
	{
		if (!client.isClientThread())
		{
			move(dx, dy);
		}
		else
		{
			executorService.submit(() -> move(dx, dy));
		}
	}

	private synchronized void move(int dx, int dy)
	{
		var motion = getFactory().build(dx, dy);
		try
		{
			motion.move();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public MouseMotionFactory getFactory()
	{
		var factory = new MouseMotionFactory();
		factory.setNature(nature);
		factory.setRandom(random);

		var manager = new SpeedManagerImpl(flows);
		factory.setDeviationProvider(new SinusoidalDeviationProvider(10.0));
		factory.setNoiseProvider(new DefaultNoiseProvider(2.0));
		factory.getNature().setReactionTimeVariationMs(75);
		manager.setMouseMovementBaseTimeMs(150);

		var overshootManager = (DefaultOvershootManager) factory.getOvershootManager();
		overshootManager.setOvershoots(4);
		factory.setSpeedManager(manager);

		return factory;
	}

	public void moveOffScreen()
	{
		// 1 in 4 chance of moving off screen
		if (random.nextInt(4) == 0)
		{
			// Edges of the screen
			int horizontal = random.nextBoolean() ? -1 : client.getCanvasWidth() + 1;
			int vertical = random.nextBoolean() ? -1 : client.getCanvasHeight() + 1;

			boolean exitHorizontally = random.nextBoolean();
			if (exitHorizontally)
			{
				moveTo(horizontal, random.nextInt(0, client.getCanvasHeight() + 1));
			}
			else
			{
				moveTo(random.nextInt(0, client.getCanvasWidth() + 1), vertical);
			}

			if (random.nextInt(4) == 0)
			{
				//client.setFocused(false);
			}
		}
	}

	public void moveOutsideOpenMenu()
	{
		if (!client.isMenuOpen())
		{
			return;
		}

		final Menu menu = client.getMenu();
		final int menuX = menu.getMenuX();
		final int menuY = menu.getMenuY();
		final int menuWidth = menu.getMenuWidth();
		final int menuHeight = menu.getMenuHeight();

		final Canvas canvas = client.getCanvas();
		final int canvasWidth = canvas.getWidth();
		final int canvasHeight = canvas.getHeight();

		int x, y;
		while (client.isMenuOpen())
		{
			x = (int) (Math.random() * canvasWidth);
			y = (int) (Math.random() * canvasHeight);
			if (!(x >= menuX && x <= menuX + menuWidth && y >= menuY && y <= menuY + menuHeight))
			{
				moveTo(x, y);
				break;
			}
		}
	}

	private static class SpeedManagerImpl extends DefaultSpeedManager
	{
		private SpeedManagerImpl(Collection<Flow> flows)
		{
			super(flows);
		}

		@Override
		public Pair<Flow, Long> getFlowWithTime(double distance)
		{
			var pair = super.getFlowWithTime(distance);
			return new Pair<>(pair.x, pair.y);
		}
	}

	private static class MouseInfoImpl implements MouseInfoAccessor
	{
		@Override
		public Point getMousePosition()
		{
			return Mouse.getPosition();
		}
	}

	private class SystemCallsImpl implements SystemCalls
	{
		@Override
		public long currentTimeMillis()
		{
			return System.currentTimeMillis();
		}

		@Override
		public void sleep(long time)
		{
			Time.sleep(time);
		}

		@Override
		public Dimension getScreenSize()
		{
			return client.getCanvas().getSize();
		}

		@Override
		public void setMousePosition(int x, int y)
		{
			Mouse.moved(x, y, client.getCanvas(), currentTimeMillis());
		}
	}
}

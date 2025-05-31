package net.runelite.client.plugins.openrl.api.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.Canvas;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import net.runelite.api.Client;
import net.runelite.api.Menu;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Rand;
import net.runelite.client.plugins.openrl.api.commons.Time;

public class Mouse
{
	private static final Logger log = LoggerFactory.getLogger(Mouse.class);
	private static final int MENU_REPLACE_DELAY = 80;
	public static final Supplier<Point> CLICK_POINT_SUPPLIER = () -> new Point(Rand.nextInt(520, 568), Rand.nextInt(55, 70));

	private static boolean exited = true;
	private static final Executor CLICK_EXECUTOR = Executors.newSingleThreadExecutor();

	public static void clickRandom(boolean rightClick)
	{
		click(CLICK_POINT_SUPPLIER.get(), rightClick);
	}

	public static void click(Rectangle rectangle, boolean rightClick)
	{
		click(rectangle.x, rectangle.y, rightClick);
	}

	public static void click(Point point, boolean rightClick)
	{
		click(point.x, point.y, rightClick);
	}

	public static void click(int x, int y, boolean rightClick)
	{
		if (Static.getClient().isClientThread())
		{
			CLICK_EXECUTOR.execute(() -> handleClick(x, y, rightClick));
		}
		else
		{
			handleClick(x, y, rightClick);
		}
	}

	private static void handleClick(int x, int y, boolean rightClick)
	{
		final long start = System.currentTimeMillis();

		final Canvas canvas = Static.getClient().getCanvas();

		if (Static.getInputManager().isMouseExited())
		{
			entered(x, y, canvas, System.currentTimeMillis());
		}

		moved(x, y, canvas, System.currentTimeMillis());
		Time.sleep(8, 60);
		pressed(x, y, canvas, System.currentTimeMillis(), rightClick ? MouseEvent.BUTTON3 : MouseEvent.BUTTON1);
		if (rightClick)
		{
			Time.sleep(80, 250);
		}
		else
		{
			Time.sleep(60, 180);
		}
		released(x, y, canvas, System.currentTimeMillis(), rightClick ? MouseEvent.BUTTON3 : MouseEvent.BUTTON1);
		clicked(x, y, canvas, System.currentTimeMillis(), rightClick ? MouseEvent.BUTTON3 : MouseEvent.BUTTON1);

		/*if (Rand.nextBool() && !exited && !rightClick)
		{
			exited(x, y, canvas, System.currentTimeMillis());
		}*/

		final long sleep = MENU_REPLACE_DELAY - (System.currentTimeMillis() - start);
		if (sleep > 0)
		{
			Time.sleep(sleep);
		}
		else
		{
			Time.sleep(MENU_REPLACE_DELAY);
		}
	}

	public static void drag(Rectangle startRectangle, Rectangle endRectangle, boolean rightClick)
	{
		drag(startRectangle.x, startRectangle.y, endRectangle.x, endRectangle.y, rightClick);
	}

	public static void drag(Point startPoint, Point endPoint, boolean rightClick)
	{
		drag(startPoint.x, startPoint.y, endPoint.x, endPoint.y, rightClick);
	}

	public static void drag(int startX, int startY, int endX, int endY, boolean rightClick)
	{
		if (Static.getClient().isClientThread())
		{
			CLICK_EXECUTOR.execute(() -> handleDrag(startX, startY, endX, endY, rightClick));
		}
		else
		{
			handleDrag(startX, startY, endX, endY, rightClick);
		}
	}

	private static void handleDrag(int startX, int startY, int endX, int endY, boolean rightClick)
	{
		final Canvas canvas = Static.getClient().getCanvas();

		if (Static.getInputManager().isMouseExited())
		{
			entered(startX, startY, canvas, System.currentTimeMillis());
		}

		Static.getNaturalMouse().moveTo(startX, startY);
		moved(startX, startY, canvas, System.currentTimeMillis());
		Time.sleep(8, 60);
		pressed(startX, startY, canvas, System.currentTimeMillis(), rightClick ? MouseEvent.BUTTON3 : MouseEvent.BUTTON1);
		Time.sleep(600, 800);
		Static.getNaturalMouse().moveTo(endX, endY);
		moved(endX, endY, canvas, System.currentTimeMillis());
		//dragged(endX, endY, canvas, System.currentTimeMillis());
		Time.sleep(600, 800);
		released(endX, endY, canvas, System.currentTimeMillis(), rightClick ? MouseEvent.BUTTON3 : MouseEvent.BUTTON1);
		//clicked(startX, startY, canvas, System.currentTimeMillis(), rightClick ? MouseEvent.BUTTON3 : MouseEvent.BUTTON1);
		Time.sleep(600, 800);

		/*if (Rand.nextBool() && !exited && !rightClick)
		{
			exited(endX, endY, canvas, System.currentTimeMillis());
		}*/
	}

	public static void moveTo(Rectangle rectangle)
	{
		moveTo(rectangle.x, rectangle.y);
	}

	public static void moveTo(Point point)
	{
		moveTo(point.x, point.y);
	}

	public static void moveTo(int x, int y)
	{
		if (Static.getClient().isClientThread())
		{
			CLICK_EXECUTOR.execute(() -> handleMove(x, y));
		}
		else
		{
			handleMove(x, y);
		}
	}

	private static void handleMove(int x, int y)
	{
		final long start = System.currentTimeMillis();

		final Canvas canvas = Static.getClient().getCanvas();
		if (Static.getInputManager().isMouseExited())
		{
			entered(x, y, canvas, System.currentTimeMillis());
		}
		moved(x, y, canvas, System.currentTimeMillis());

		final long sleep = MENU_REPLACE_DELAY - (System.currentTimeMillis() - start);
		if (sleep > 0)
		{
			Time.sleep(sleep);
		}
		else
		{
			Time.sleep(MENU_REPLACE_DELAY);
		}
	}

	public static void scrollUp(Rectangle rectangle)
	{
		scrollUp(rectangle.x, rectangle.y);
	}

	public static void scrollUp(Point point)
	{
		scrollUp(point.x, point.y);
	}

	public static void scrollUp(int x, int y)
	{
		scroll(x, y, true);
	}

	public static void scrollDown(Rectangle rectangle)
	{
		scrollDown(rectangle.x, rectangle.y);
	}

	public static void scrollDown(Point point)
	{
		scrollDown(point.x, point.y);
	}

	public static void scrollDown(int x, int y)
	{
		scroll(x, y, false);
	}

	public static void scroll(int x, int y, boolean up)
	{
		if (Static.getClient().isClientThread())
		{
			CLICK_EXECUTOR.execute(() -> handleScroll(x, y, up));
		}
		else
		{
			handleScroll(x, y, up);
		}
	}

	private static void handleScroll(int x, int y, boolean up)
	{
		final Canvas canvas = Static.getClient().getCanvas();

		if (Static.getInputManager().isMouseExited())
		{
			entered(x, y, canvas, System.currentTimeMillis());
		}

		moved(x, y, canvas, System.currentTimeMillis());

		scrolled(x, y, canvas, System.currentTimeMillis(), up ? -10 : 10, up ? -2 : 2);
	}

	public static synchronized void scrolled(int x, int y, Canvas canvas, long time, int unitsToScroll, int scrollAmount)
	{
		final MouseEvent event = new MouseWheelEvent(canvas, MouseEvent.MOUSE_WHEEL, time, 0, x, y, 0, false, 0, unitsToScroll, scrollAmount);
		event.setSource("openrl");
		canvas.dispatchEvent(event);
	}

	public static synchronized void pressed(int x, int y, Canvas canvas, long time, int button)
	{
		final MouseEvent event = new MouseEvent(canvas, MouseEvent.MOUSE_PRESSED, time, 0, x, y, 1, false, button);
		event.setSource("openrl");
		canvas.dispatchEvent(event);
	}

	public static synchronized void released(int x, int y, Canvas canvas, long time, int button)
	{
		final MouseEvent event = new MouseEvent(canvas, MouseEvent.MOUSE_RELEASED, time, 0, x, y, 1, false, button);
		event.setSource("openrl");
		canvas.dispatchEvent(event);
	}

	public static synchronized void clicked(int x, int y, Canvas canvas, long time, int button)
	{
		final MouseEvent event = new MouseEvent(canvas, MouseEvent.MOUSE_CLICKED, time, 0, x, y, 1, false, button);
		event.setSource("openrl");
		canvas.dispatchEvent(event);
	}

	public static synchronized void released(int x, int y, Canvas canvas, long time)
	{
		final MouseEvent event = new MouseEvent(canvas, MouseEvent.MOUSE_RELEASED, time, 0, x, y, 1, false);
		event.setSource("openrl");
		canvas.dispatchEvent(event);
	}

	public static synchronized void clicked(int x, int y, Canvas canvas, long time)
	{
		final MouseEvent event = new MouseEvent(canvas, MouseEvent.MOUSE_CLICKED, time, 0, x, y, 1, false);
		event.setSource("openrl");
		canvas.dispatchEvent(event);
	}

	public static synchronized void exited(int x, int y, Canvas canvas, long time)
	{
		final MouseEvent event = new MouseEvent(canvas, MouseEvent.MOUSE_EXITED, time, 0, x, y, 0, false);
		event.setSource("openrl");
		canvas.dispatchEvent(event);
		exited = true;
	}

	public static synchronized void entered(int x, int y, Canvas canvas, long time)
	{
		final MouseEvent event = new MouseEvent(canvas, MouseEvent.MOUSE_ENTERED, time, 0, x, y, 0, false);
		event.setSource("openrl");
		canvas.dispatchEvent(event);
		exited = false;
	}

	public static synchronized void moved(int x, int y, Canvas canvas, long time)
	{
		final MouseEvent event = new MouseEvent(canvas, MouseEvent.MOUSE_MOVED, time, 0, x, y, 0, false);
		event.setSource("openrl");
		canvas.dispatchEvent(event);
	}

	public static synchronized void dragged(int x, int y, Canvas canvas, long time)
	{
		final MouseEvent event = new MouseEvent(canvas, MouseEvent.MOUSE_DRAGGED, time, 0, x, y, 1, false);
		event.setSource("openrl");
		canvas.dispatchEvent(event);
	}

	public static Point getPosition()
	{
		final net.runelite.api.Point mouseCanvasPosition = Static.getClient().getMouseCanvasPosition();
		return new java.awt.Point(mouseCanvasPosition.getX(), mouseCanvasPosition.getY());
	}

	public static void moveOutsideOpenMenu()
	{
		final Client client = Static.getClient();

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
}
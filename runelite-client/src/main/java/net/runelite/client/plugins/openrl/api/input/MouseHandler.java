package net.runelite.client.plugins.openrl.api.input;

import java.awt.Canvas;
import java.awt.event.MouseEvent;
import net.runelite.client.plugins.openrl.Static;

public class MouseHandler
{
	public synchronized void sendClickMouseEvent(int x, int y, int button)
	{
		long time = System.currentTimeMillis();
		Canvas canvas = Static.getClient().getCanvas();
		MouseEvent press = new MouseEvent(canvas, MouseEvent.MOUSE_PRESSED, time, 0, x, y, 1, false, button);
		press.setSource("openrl");
		canvas.dispatchEvent(press);
		MouseEvent release = new MouseEvent(canvas, MouseEvent.MOUSE_RELEASED, time, 0, x, y, 1, false, button);
		release.setSource("openrl");
		canvas.dispatchEvent(release);
	}

	public synchronized void sendMovementMouseEvent(int x, int y)
	{
		Canvas canvas = Static.getClient().getCanvas();
		MouseEvent move = new MouseEvent(canvas, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, x, y, 0, false);
		move.setSource("openrl");
		canvas.dispatchEvent(move);
	}
}

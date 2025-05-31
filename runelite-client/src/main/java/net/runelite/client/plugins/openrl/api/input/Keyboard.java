package net.runelite.client.plugins.openrl.api.input;

import java.awt.Canvas;
import java.awt.event.KeyEvent;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Time;

public class Keyboard
{
	public static void pressed(int keyCode)
	{
		pressed(keyCode, KeyEvent.CHAR_UNDEFINED);
	}

	public static void pressed(int keyCode, char keyChar)
	{
		final Canvas canvas = Static.getClient().getCanvas();
		final long time = System.currentTimeMillis();
		final KeyEvent event = new KeyEvent(canvas, KeyEvent.KEY_PRESSED, time, 0, keyCode, keyChar, KeyEvent.KEY_LOCATION_STANDARD);
		canvas.dispatchEvent(event);
	}

	public static void typed(int keyCode)
	{
		typed(keyCode, KeyEvent.CHAR_UNDEFINED);
	}

	public static void typed(int keyCode, char keyChar)
	{
		final Canvas canvas = Static.getClient().getCanvas();
		final long time = System.currentTimeMillis();
		final KeyEvent event = new KeyEvent(canvas, KeyEvent.KEY_TYPED, time, 0, keyCode, keyChar, KeyEvent.KEY_LOCATION_UNKNOWN);
		canvas.dispatchEvent(event);
	}

	public static void released(int keyCode)
	{
		released(keyCode, KeyEvent.CHAR_UNDEFINED);
	}

	public static void released(int keyCode, char keyChar)
	{
		final Canvas canvas = Static.getClient().getCanvas();
		final long time = System.currentTimeMillis();
		final KeyEvent event = new KeyEvent(canvas, KeyEvent.KEY_RELEASED, time, 0, keyCode, keyChar, KeyEvent.KEY_LOCATION_STANDARD);
		canvas.dispatchEvent(event);
	}

	public static void type(char c)
	{
		final Canvas canvas = Static.getClient().getCanvas();
		final long time = System.currentTimeMillis();
		final int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
		final KeyEvent pressed = new KeyEvent(canvas, KeyEvent.KEY_PRESSED, time, 0, keyCode, c, KeyEvent.KEY_LOCATION_STANDARD);
		final KeyEvent typed = new KeyEvent(canvas, KeyEvent.KEY_TYPED, time, 0, 0, c, KeyEvent.KEY_LOCATION_UNKNOWN);
		canvas.dispatchEvent(pressed);
		canvas.dispatchEvent(typed);
		Time.sleep(10);
		final KeyEvent released = new KeyEvent(
			canvas,
			KeyEvent.KEY_RELEASED,
			System.currentTimeMillis(),
			0,
			keyCode,
			c,
			KeyEvent.KEY_LOCATION_STANDARD
		);

		canvas.dispatchEvent(released);
	}

	public static void type(int number)
	{
		type(String.valueOf(number));
	}

	public static void type(String text)
	{
		type(text, false);
	}

	public static void type(String text, boolean sendEnter)
	{
		final char[] chars = text.toCharArray();
		for (char c : chars)
		{
			type(c);
		}

		if (sendEnter)
		{
			sendEnter();
		}
	}

	public static void sendEnter()
	{
		type((char) KeyEvent.VK_ENTER);
	}

	public static void sendSpace()
	{
		type((char) KeyEvent.VK_SPACE);
	}
}
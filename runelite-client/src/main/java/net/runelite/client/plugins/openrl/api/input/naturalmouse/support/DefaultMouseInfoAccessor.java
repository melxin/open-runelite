package net.runelite.client.plugins.openrl.api.input.naturalmouse.support;

import java.awt.MouseInfo;
import java.awt.Point;
import net.runelite.client.plugins.openrl.api.input.naturalmouse.api.MouseInfoAccessor;

public class DefaultMouseInfoAccessor implements MouseInfoAccessor
{
	@Override
	public Point getMousePosition()
	{
		return MouseInfo.getPointerInfo().getLocation();
	}
}

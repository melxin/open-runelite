package net.runelite.client.plugins.openrl.plugins.devtools;

import com.google.common.base.Strings;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.List;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.ui.overlay.OverlayUtil;

public class DrawUtils
{
	public static void drawPath(Graphics2D graphics2D, List<WorldPoint> path)
	{
		path.forEach(wp -> outlineWorldPoint(Static.getClient(), graphics2D, Color.RED, wp));
	}

	public static void outlineWorldPoint(Client client, Graphics2D graphics2D, Color color, WorldPoint wp)
	{
		outlineWorldPoint(client, graphics2D, color, wp);
	}

	public static void outlineWorldPoint(Client client, Graphics2D graphics, Color color, String text, WorldPoint wp)
	{
		final LocalPoint localPoint = LocalPoint.fromWorld(client, wp);
		if (localPoint == null)
		{
			return;
		}

		final Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
		if (poly == null)
		{
			return;
		}

		if (text != null)
		{
			final var stringX = (int) (poly.getBounds().getCenterX() -
				graphics.getFont().getStringBounds(text, graphics.getFontRenderContext()).getWidth() / 2);
			final var stringY = (int) poly.getBounds().getCenterY();
			graphics.setColor(color);
			graphics.drawString(text, stringX, stringY);
		}

		graphics.setColor(color);
		final Stroke originalStroke = graphics.getStroke();
		graphics.setStroke(new BasicStroke(2));
		graphics.draw(poly);
		graphics.setColor(new Color(0, 0, 0, 50));
		graphics.fill(poly);
		graphics.setStroke(originalStroke);
	}

	public static void renderParagraphLocation(Graphics2D graphics, Point txtLoc, String text, Color color)
	{
		if (Strings.isNullOrEmpty(text))
		{
			return;
		}

		final int x = txtLoc.getX();
		int y = txtLoc.getY();

		for (String line : text.split("\n"))
		{
			graphics.setColor(Color.BLACK);
			graphics.drawString(line, x + 1, y + 1);

			graphics.setColor(color);
			graphics.drawString(line, x, y);

			y += 10;
		}
	}

	public static void renderActorParagraph(Graphics2D graphics, Actor actor, String text, Color color)
	{
		final Polygon poly = actor.getCanvasTilePoly();
		if (poly != null)
		{
			OverlayUtil.renderPolygon(graphics, poly, color);
		}

		final String[] lines = text.split("</br>");
		int lineLength = Integer.MIN_VALUE;
		String largestLine = "";

		for (String line : lines)
		{
			if (line.length() > lineLength)
			{
				lineLength = line.length();
				largestLine = line;
			}
		}

		final Point textLocation = actor.getCanvasTextLocation(graphics, largestLine, actor.getLogicalHeight() + 40);
		if (textLocation != null)
		{
			renderParagraphLocation(graphics, textLocation, text, color);
		}
	}

	public static void renderTileOverlayParagraph(Graphics2D graphics, TileObject tileObject, String text, Color color)
	{
		final Polygon poly = tileObject.getCanvasTilePoly();
		if (poly != null)
		{
			OverlayUtil.renderPolygon(graphics, poly, color);
		}

		final Point minimapLocation = tileObject.getMinimapLocation();
		if (minimapLocation != null)
		{
			OverlayUtil.renderMinimapLocation(graphics, minimapLocation, color);
		}

		final String[] lines = text.split("\n");
		int lineLength = Integer.MIN_VALUE;
		String largestLine = "";

		for (String line : lines)
		{
			if (line.length() > lineLength)
			{
				lineLength = line.length();
				largestLine = line;
			}
		}

		final Point textLocation = tileObject.getCanvasTextLocation(graphics, largestLine, 0);
		if (textLocation != null)
		{
			renderParagraphLocation(graphics, textLocation, text, color);
		}
	}

	public static void fillTile(Graphics2D graphics, Client client, WorldPoint point, Color color)
	{
		if (point.getPlane() != client.getPlane())
		{
			return;
		}

		final LocalPoint lp = LocalPoint.fromWorld(client, point);
		if (lp == null)
		{
			return;
		}

		final Polygon poly = Perspective.getCanvasTilePoly(client, lp);
		if (poly == null)
		{
			return;
		}

		graphics.setColor(color);
		graphics.fill(poly);
	}
}
package net.runelite.client.plugins.openrl.plugins.devtools.utils;

import com.google.common.base.Strings;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.List;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.worldmap.WorldMap;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.CollisionMap;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.TransportLoader;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.Transport;
import net.runelite.client.plugins.openrl.api.rs2.providers.scene.RS2Tiles;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Tile;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2WorldPoint;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.api.Locatable;
import net.runelite.client.ui.overlay.OverlayUtil;

public class DrawUtils
{
	private static final Color TRANSPORT_COLOR = new Color(0, 255, 0, 128);
	private static final Color TILE_BLOCKED_COLOR = new Color(0, 128, 255, 128);

	public static void drawOnMap(Graphics2D graphics, Locatable locatable, Color color)
	{
		drawOnMap(graphics, locatable.getWorldLocation(), color);
	}

	public static void drawOnMap(Graphics2D graphics, Tile tile, Color color)
	{
		drawOnMap(graphics, tile.getWorldLocation(), color);
	}

	public static void drawOnMap(Graphics2D graphics, WorldPoint point, Color color)
	{
		final WorldMap wm = Static.getClient().getWorldMap();

		final float pixelsPerTile = wm.getWorldMapZoom();
		final int tileCenterPixel = (int) Math.ceil(pixelsPerTile / 2);

		final Point tile = CoordUtils.worldPointToWorldMap(point);
		final Point bottomRightTile = CoordUtils.worldPointToWorldMap(point.dx(1).dy(-1));

		if (tile == null || bottomRightTile == null)
		{
			return;
		}

		final Point topLeft = CoordUtils.offset(tile, -tileCenterPixel, -tileCenterPixel);
		final Point bottomRight = CoordUtils.offset(bottomRightTile, -tileCenterPixel, -tileCenterPixel);

		graphics.setColor(color);
		graphics.fillRect(topLeft.getX(), topLeft.getY(), bottomRight.getX() - topLeft.getX(), bottomRight.getY() - topLeft.getY());
	}

	public static void drawTransports(Graphics2D graphics2D)
	{
		final Client client = Static.getClient();
		final List<Transport> transports = TransportLoader.buildTransports();

		for (Transport transport : transports)
		{
			WorldPoint instanceSource = transport.getSource();
			WorldPoint instanceDestination = transport.getDestination();

			if (client.isInInstancedRegion())
			{
				instanceSource = WorldPoint.toLocalInstance(client, instanceSource).stream().findFirst().orElse(instanceSource);
				instanceDestination = WorldPoint.toLocalInstance(client, instanceDestination).stream().findFirst().orElse(instanceDestination);
			}

			fillTile(graphics2D, client, instanceSource, TRANSPORT_COLOR);
			Point center = tileCenter(client, instanceSource);
			if (center == null)
			{
				continue;
			}

			Point linkCenter = tileCenter(client, instanceDestination);
			if (linkCenter == null)
			{
				continue;
			}

			graphics2D.drawLine(center.getX(), center.getY(), linkCenter.getX(), linkCenter.getY());
		}
	}

	public static Point tileCenter(Client client, WorldPoint b)
	{
		if (b.getPlane() != client.getPlane())
		{
			return null;
		}

		LocalPoint lp = LocalPoint.fromWorld(client, b);
		if (lp == null)
		{
			return null;
		}

		Polygon poly = Perspective.getCanvasTilePoly(client, lp);
		if (poly == null)
		{
			return null;
		}

		int cx = poly.getBounds().x + poly.getBounds().width / 2;
		int cy = poly.getBounds().y + poly.getBounds().height / 2;
		return new Point(cx, cy);
	}

	public static void drawPath(Graphics2D graphics2D, List<WorldPoint> path, Color color)
	{
		path.forEach(wp -> new RS2WorldPoint(wp).outline(Static.getClient(), graphics2D, color));
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

	public static void drawCollisions(Graphics2D graphics2D, CollisionMap collisionMap)
	{
		final Client client = Static.getClient();
		final List<RS2Tile> tiles = RS2Tiles.getAll();

		if (tiles.isEmpty())
		{
			return;
		}

		if (collisionMap == null)
		{
			return;
		}

		for (RS2Tile tile : tiles)
		{
			final Polygon poly = Perspective.getCanvasTilePoly(client, tile.getLocalLocation());
			if (poly == null)
			{
				continue;
			}

			final StringBuilder sb = new StringBuilder("");
			graphics2D.setColor(Color.WHITE);
			if (!collisionMap.n(tile.getWorldLocation()))
			{
				sb.append("n");
			}

			if (!collisionMap.s(tile.getWorldLocation()))
			{
				sb.append("s");
			}

			if (!collisionMap.w(tile.getWorldLocation()))
			{
				sb.append("w");
			}

			if (!collisionMap.e(tile.getWorldLocation()))
			{
				sb.append("e");
			}

			final String s = sb.toString();
			if (s.isEmpty())
			{
				continue;
			}

			if (!s.equals("nswe"))
			{
				graphics2D.setColor(Color.WHITE);
				if (s.contains("n"))
				{
					graphics2D.drawLine(poly.xpoints[3], poly.ypoints[3], poly.xpoints[2], poly.ypoints[2]);
				}

				if (s.contains("s"))
				{
					graphics2D.drawLine(poly.xpoints[0], poly.ypoints[0], poly.xpoints[1], poly.ypoints[1]);
				}

				if (s.contains("w"))
				{
					graphics2D.drawLine(poly.xpoints[0], poly.ypoints[0], poly.xpoints[3], poly.ypoints[3]);
				}

				if (s.contains("e"))
				{
					graphics2D.drawLine(poly.xpoints[1], poly.ypoints[1], poly.xpoints[2], poly.ypoints[2]);
				}

				continue;
			}

			graphics2D.setColor(TILE_BLOCKED_COLOR);
			graphics2D.fill(poly);
		}
	}

	public static void drawCollisions(Graphics2D graphics2D)
	{
		drawCollisions(graphics2D, Static.getGlobalCollisionMap());
	}
}
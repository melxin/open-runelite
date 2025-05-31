package net.runelite.client.plugins.openrl.plugins.devtools.utils;

import java.awt.Rectangle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.worldmap.WorldMap;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.RS2Widgets;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;
import static net.runelite.api.Perspective.COSINE;
import static net.runelite.api.Perspective.SINE;

public class CoordUtils
{
	public static Point worldPointToWorldMap(WorldPoint worldPoint)
	{
		final WorldMap wm = Static.getClient().getWorldMap();

		if (!wm.getWorldMapData().surfaceContainsPosition(worldPoint.getX(), worldPoint.getY()))
		{
			return null;
		}

		float pixelsPerTile = wm.getWorldMapZoom();

		final RS2Widget map = RS2Widgets.get(WidgetInfo.WORLD_MAP_VIEW);
		if (map != null)
		{
			Rectangle worldMapRect = map.getBounds();

			int widthInTiles = (int) Math.ceil(worldMapRect.getWidth() / pixelsPerTile);
			int heightInTiles = (int) Math.ceil(worldMapRect.getHeight() / pixelsPerTile);

			Point worldMapPosition = wm.getWorldMapPosition();

			//Offset in tiles from anchor sides
			int yTileMax = worldMapPosition.getY() - heightInTiles / 2;
			int yTileOffset = (yTileMax - worldPoint.getY() - 1) * -1;
			int xTileOffset = worldPoint.getX() + widthInTiles / 2 - worldMapPosition.getX();

			int xGraphDiff = ((int) (xTileOffset * pixelsPerTile));
			int yGraphDiff = (int) (yTileOffset * pixelsPerTile);

			//Center on tile.
			yGraphDiff -= pixelsPerTile - Math.ceil(pixelsPerTile / 2);
			xGraphDiff += pixelsPerTile - Math.ceil(pixelsPerTile / 2);

			yGraphDiff = worldMapRect.height - yGraphDiff;
			yGraphDiff += (int) worldMapRect.getY();
			xGraphDiff += (int) worldMapRect.getX();

			return new Point(xGraphDiff, yGraphDiff);
		}

		return null;
	}

	public static WorldPoint worldMapToWorldPoint(Point point)
	{
		float zoom = Static.getClient().getWorldMap().getWorldMapZoom();
		WorldMap worldMap = Static.getClient().getWorldMap();
		final WorldPoint mapPoint = new WorldPoint(worldMap.getWorldMapPosition().getX(), worldMap.getWorldMapPosition().getY(), 0);
		final Point middle = worldPointToWorldMap(mapPoint);
		if (middle == null)
		{
			return null;
		}

		final int dx = (int) ((point.getX() - middle.getX()) / zoom);
		final int dy = (int) ((-(point.getY() - middle.getY())) / zoom);

		return mapPoint.dx(dx).dy(dy);
	}

	@Nullable
	public static Point localToMinimap(@Nonnull Client client, @Nonnull LocalPoint point, int distance)
	{
		final LocalPoint localLocation = client.getLocalPlayer().getLocalLocation();
		final int x = point.getX() / 32 - localLocation.getX() / 32;
		final int y = point.getY() / 32 - localLocation.getY() / 32;

		final int dist = x * x + y * y;
		if (dist < distance)
		{
			Widget minimapDrawWidget;
			if (client.isResized())
			{
				final Widget minimap1 = client.getWidget(WidgetInfo.RESIZABLE_MINIMAP_DRAW_AREA);
				if (minimap1 != null)
				{
					minimapDrawWidget = minimap1;
				}
				else
				{
					minimapDrawWidget = client.getWidget(WidgetInfo.RESIZABLE_MINIMAP_STONES_DRAW_AREA);
				}
			}
			else
			{
				minimapDrawWidget = client.getWidget(WidgetInfo.FIXED_VIEWPORT_MINIMAP_DRAW_AREA);
			}

			if (minimapDrawWidget == null || !RS2Widgets.isVisible(new RS2Widget(minimapDrawWidget)))
			{
				return null;
			}

			final int angle = client.getCameraYawTarget() & 0x7FF;

			final int sin = SINE[angle];
			final int cos = COSINE[angle];

			final int xx = y * sin + cos * x >> 16;
			final int yy = sin * x - y * cos >> 16;

			final Point loc = minimapDrawWidget.getCanvasLocation();
			final int miniMapX = loc.getX() + xx + minimapDrawWidget.getWidth() / 2;
			final int miniMapY = minimapDrawWidget.getHeight() / 2 + loc.getY() + yy;
			return new Point(miniMapX, miniMapY);
		}

		return null;
	}

	/**
	 * Returns a new point offset by xOff and yOff
	 *
	 * @param xOff X offset to apply
	 * @param yOff Y offset to apply
	 * @return A new instance of Point, offset by x + xOff and y + yOff
	 */
	public static Point offset(Point point, int xOff, int yOff)
	{
		return new Point(point.getX() + xOff, point.getY() + yOff);
	}
}
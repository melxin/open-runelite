package net.runelite.client.plugins.openrl.api.rs2.providers.minimap;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.SpriteID;
import net.runelite.api.Varbits;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.game.Vars;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.RS2Widgets;

public class RS2MiniMap
{
	/**
	 * Converts a {@link LocalPoint} to a minimap coordinate {@link Point}.
	 *
	 * @param localPoint The local point to convert.
	 * @return The corresponding minimap point, or {@code null} if conversion fails.
	 */
	@Nullable
	public static Point localToMinimap(LocalPoint localPoint)
	{
		return localPoint == null ? null : Static.getClientThread().runOnClientThreadOptional(() -> Perspective.localToMinimap(Static.getClient(), localPoint))
			.orElse(null);
	}

	/**
	 * Converts a {@link WorldPoint} to a minimap coordinate {@link Point}.
	 *
	 * @param worldPoint The world point to convert.
	 * @return The corresponding minimap point, or {@code null} if conversion fails.
	 */
	@Nullable
	public static Point worldToMinimap(WorldPoint worldPoint)
	{
		if (worldPoint == null)
		{
			return null;
		}

		LocalPoint localPoint = LocalPoint.fromWorld(Static.getClient().getTopLevelWorldView(), worldPoint);

		/*if (Static.getClient().getTopLevelWorldView().isInstance() && localPoint == null) {
			localPoint = Rs2LocalPoint.fromWorldInstance(worldPoint);
		}

		if (localPoint == null) {
			Microbot.log("Tried to walk worldpoint " + worldPoint + " using the canvas but localpoint returned null");
			return null;
		}*/

		final LocalPoint lp = localPoint;
		return Static.getClientThread().runOnClientThreadOptional(() -> Perspective.localToMinimap(Static.getClient(), lp))
			.orElse(null);
	}

	/**
	 * Retrieves the minimap draw widget based on the current game view mode.
	 *
	 * @return The minimap draw widget, or {@code null} if not found.
	 */
	public static Widget getMinimapDrawWidget()
	{
		if (Static.getClient().isResized())
		{
			if (Vars.getBit(Varbits.SIDE_PANELS) == 1)
			{
				return RS2Widgets.getWidget(ComponentID.RESIZABLE_VIEWPORT_BOTTOM_LINE_MINIMAP_DRAW_AREA);
			}
			return RS2Widgets.getWidget(ComponentID.RESIZABLE_VIEWPORT_MINIMAP_DRAW_AREA);
		}
		return RS2Widgets.getWidget(ComponentID.FIXED_VIEWPORT_MINIMAP_DRAW_AREA);
	}

	/**
	 * Returns a simple elliptical clip area for the minimap.
	 *
	 * @return A {@link Shape} representing the minimap clip area.
	 */
	private static Shape getMinimapClipAreaSimple()
	{
		final Widget minimapDrawArea = getMinimapDrawWidget();
		if (minimapDrawArea == null)
		{
			return null;
		}
		final Rectangle bounds = minimapDrawArea.getBounds();
		return new Ellipse2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}

	/**
	 * Retrieves the minimap clipping area as a polygon derived from the minimap alpha mask sprite,
	 * and scales it inward to avoid overlapping edge elements.
	 *
	 * @param scale The scale factor to shrink the polygon (e.g., 0.94 for 94% of the original size).
	 * @return A {@link Shape} representing the scaled minimap clickable area, or a fallback shape if the sprite is unavailable.
	 */
	public static Shape getMinimapClipArea(double scale)
	{
		final Widget minimapWidget = getMinimapDrawWidget();
		if (minimapWidget == null)
		{
			return null;
		}

		final boolean isResized = Static.getClient().isResized();

		final BufferedImage minimapSprite = Static.getClientThread().runOnClientThreadOptional(() ->
			Static.getSpriteManager().getSprite(
				isResized ? SpriteID.RESIZEABLE_MODE_MINIMAP_ALPHA_MASK : SpriteID.FIXED_MODE_MINIMAP_ALPHA_MASK, 0)).orElse(null);

		if (minimapSprite == null)
		{
			return getMinimapClipAreaSimple();
		}

		final Shape rawClipArea = bufferedImageToPolygon(minimapSprite, minimapWidget.getBounds());
		return shrinkShape(rawClipArea, scale);
	}

	/**
	 * Retrieves the minimap clipping area as a {@link Shape}, scaled to slightly reduce its size.
	 * <p>
	 * This is useful for rendering overlays within the minimap without overlapping UI elements such as the globe icon.
	 *
	 * @return A {@link Shape} representing the scaled minimap clip area, or {@code null} if the minimap widget is unavailable.
	 */
	public static Shape getMinimapClipArea()
	{
		return getMinimapClipArea(Static.getClient().isResized() ? 0.94 : 1.0);
	}

	/**
	 * Converts a BufferedImage to a polygon by detecting the border based on the outside color.
	 *
	 * @param image         The image to convert.
	 * @param minimapBounds The bounds of the minimap widget.
	 * @return A polygon representing the minimap's clickable area.
	 */
	private static Polygon bufferedImageToPolygon(BufferedImage image, Rectangle minimapBounds)
	{
		Color outsideColour = null;
		Color previousColour;
		final int width = image.getWidth();
		final int height = image.getHeight();
		final List<java.awt.Point> points = new ArrayList<>();

		for (int y = 0; y < height; y++)
		{
			previousColour = outsideColour;
			for (int x = 0; x < width; x++)
			{
				int rgb = image.getRGB(x, y);
				int a = (rgb & 0xff000000) >>> 24;
				int r = (rgb & 0x00ff0000) >> 16;
				int g = (rgb & 0x0000ff00) >> 8;
				int b = (rgb & 0x000000ff);
				Color colour = new Color(r, g, b, a);
				if (x == 0 && y == 0)
				{
					outsideColour = colour;
					previousColour = colour;
				}
				if (!colour.equals(outsideColour) && previousColour.equals(outsideColour))
				{
					points.add(new java.awt.Point(x, y));
				}
				if ((colour.equals(outsideColour) || x == (width - 1)) && !previousColour.equals(outsideColour))
				{
					points.add(0, new java.awt.Point(x, y));
				}
				previousColour = colour;
			}
		}

		final int offsetX = minimapBounds.x;
		final int offsetY = minimapBounds.y;
		final Polygon polygon = new Polygon();
		for (java.awt.Point point : points)
		{
			polygon.addPoint(point.x + offsetX, point.y + offsetY);
		}
		return polygon;
	}

	/**
	 * Shrinks the given shape toward its center by the specified scale factor.
	 *
	 * @param shape The original shape to shrink.
	 * @param scale The scale factor (e.g., 0.94 = 94% size). Must be > 0 and < 1 to reduce the shape.
	 * @return A new {@link Shape} that is scaled inward toward its center.
	 */
	private static Shape shrinkShape(Shape shape, double scale)
	{
		final Rectangle bounds = shape.getBounds();
		final double centerX = bounds.getCenterX();
		final double centerY = bounds.getCenterY();

		final AffineTransform shrink = AffineTransform.getTranslateInstance(centerX, centerY);
		shrink.scale(scale, scale);
		shrink.translate(-centerX, -centerY);

		return shrink.createTransformedShape(shape);
	}

	/**
	 * Checks if a given point is inside the minimap clipping area.
	 *
	 * @param point The point to check.
	 * @return {@code true} if the point is within the minimap bounds, {@code false} otherwise.
	 */
	public static boolean isPointInsideMinimap(Point point)
	{
		final Shape minimapClipArea = getMinimapClipArea();
		return minimapClipArea != null && minimapClipArea.contains(point.getX(), point.getY());
	}

	// @TODO FIX/REMOVE
	/*private static final int MINIMAP_WIDTH = 250;
	private static final int MINIMAP_HEIGHT = 180;

	private Rectangle getMinimap()
	{
		final RS2Widget minimap = RS2Widgets.get(WidgetInfo.FIXED_VIEWPORT_MINIMAP_DRAW_AREA);
		if (RS2Widgets.isVisible(minimap))
		{
			return minimap.getWidget().getBounds();
		}

		final RS2Widget minimap1 = RS2Widgets.get(WidgetInfo.RESIZABLE_MINIMAP_DRAW_AREA);
		if (RS2Widgets.isVisible(minimap1))
		{
			return minimap1.getWidget().getBounds();
		}

		final RS2Widget minimap2 = RS2Widgets.get(WidgetInfo.RESIZABLE_MINIMAP_STONES_DRAW_AREA);
		if (RS2Widgets.isVisible(minimap2))
		{
			return minimap2.getWidget().getBounds();
		}

		final Rectangle bounds = Static.getClient().getCanvas().getBounds();
		return new Rectangle(bounds.width - MINIMAP_WIDTH, 0, MINIMAP_WIDTH, MINIMAP_HEIGHT);
	}

	public boolean isClickInsideMinimap(java.awt.Point point)
	{
		return getMinimap().contains(point);
	}

	private boolean isClickOffScreen(java.awt.Point point)
	{
		return point.x < 0 || point.y < 0
			|| point.x > Static.getClient().getViewportWidth() || point.y > Static.getClient().getViewportHeight();
	}*/
}
package net.runelite.client.plugins.openrl.api.rs2.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.MenuAction;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.WallObject;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;
import net.runelite.client.plugins.openrl.api.input.utils.Randomizer;
import net.runelite.client.plugins.openrl.api.rs2.camera.RS2Camera;

@RequiredArgsConstructor
public class RS2TileObject implements TileObject
{
	@NonNull
	@Getter(AccessLevel.PUBLIC)
	private final TileObject tileObject;

	@Override
	public int getId()
	{
		return tileObject.getId();
	}

	@Override
	public long getHash()
	{
		return tileObject.getHash();
	}

	@Override
	public int getX()
	{
		return tileObject.getX();
	}

	@Override
	public int getY()
	{
		return tileObject.getY();
	}

	@Override
	public int getZ()
	{
		return tileObject.getZ();
	}

	@Override
	public int getPlane()
	{
		return tileObject.getPlane();
	}

	@Override
	public WorldView getWorldView()
	{
		return tileObject.getWorldView();
	}

	@Nullable
	public String getName()
	{
		final ObjectComposition composition = getComposition();
		return composition != null ? composition.getName() : null;
	}

	private ObjectComposition objectComposition;

	@Nullable
	public ObjectComposition getComposition()
	{
		if (objectComposition == null)
		{
			this.objectComposition = Static.getGameDataCached().getObjectComposition(getId());
		}
		return objectComposition;
	}

	@Override
	public WorldPoint getWorldLocation()
	{
		return tileObject.getWorldLocation();
	}

	@Nonnull
	@Override
	public LocalPoint getLocalLocation()
	{
		return tileObject.getLocalLocation();
	}

	@Nullable
	@Override
	public Point getCanvasLocation()
	{
		return tileObject.getCanvasLocation();
	}

	@Nullable
	@Override
	public Point getCanvasLocation(int zOffset)
	{
		return tileObject.getCanvasLocation();
	}

	@Nullable
	@Override
	public Polygon getCanvasTilePoly()
	{
		return tileObject.getCanvasTilePoly();
	}

	@Nullable
	@Override
	public Point getCanvasTextLocation(Graphics2D graphics, String text, int zOffset)
	{
		return tileObject.getCanvasTextLocation(graphics, text, zOffset);
	}

	@Nullable
	@Override
	public Point getMinimapLocation()
	{
		return tileObject.getMinimapLocation();
	}

	@Nullable
	@Override
	public Shape getClickbox()
	{
		return tileObject.getClickbox();
	}

	public void interact(MenuAction menuAction)
	{
		RS2Camera.turnToSceneEntityIfOutsideClickableViewport(this);

		int param0;
		int param1;
		final ObjectComposition objectComposition = getComposition();
		if (objectComposition == null)
		{
			return;
		}

		if (tileObject instanceof GameObject)
		{
			final GameObject gameObject = (GameObject) tileObject;
			param0 = gameObject.sizeX() > 1 ? gameObject.getLocalLocation().getSceneX() - gameObject.sizeX() / 2 : gameObject.getLocalLocation().getSceneX();
			param1 = gameObject.sizeY() > 1 ? gameObject.getLocalLocation().getSceneY() - gameObject.sizeY() / 2 : gameObject.getLocalLocation().getSceneY();
		}
		else
		{
			param0 = tileObject.getLocalLocation().getSceneX();
			param1 = tileObject.getLocalLocation().getSceneY();
		}
		final int index = tileObject.getId();
		final int itemId = -1;
		final int worldViewId = -1;
		final String option = "";
		final String target = "";
		final Point clickPoint = getClickPoint();
		final int x = clickPoint.getX();
		final int y = clickPoint.getY();

		Static.getEventBus().post(new MenuAutomated(param0, param1, menuAction, index, itemId, worldViewId, option, target, x, y));
	}

	public void interact(int index)
	{
		if (index == -1)
		{
			return;
		}
		interact(getMenuAction(index));
	}

	public void interact(String action)
	{
		interact(getActionIndex(action));
	}

	@Nullable
	public static MenuAction getMenuAction(int index)
	{
		if (Static.getClient().isWidgetSelected())
		{
			return MenuAction.WIDGET_TARGET_ON_GAME_OBJECT;
		}

		switch (index)
		{
			case 0:
				return MenuAction.GAME_OBJECT_FIRST_OPTION;
			case 1:
				return MenuAction.GAME_OBJECT_SECOND_OPTION;
			case 2:
				return MenuAction.GAME_OBJECT_THIRD_OPTION;
			case 3:
				return MenuAction.GAME_OBJECT_FOURTH_OPTION;
			case 4:
				return MenuAction.GAME_OBJECT_FIFTH_OPTION;
			default:
				return null;
		}
	}

	@Nullable
	public String[] getActions()
	{
		final ObjectComposition objectComposition = getComposition();
		if (objectComposition == null)
		{
			return null;
		}
		final String[] actions = objectComposition.getActions();
		return actions != null ? actions : null;
	}

	public int getActionIndex(String action)
	{
		final String[] actions = getActions();
		if (actions == null)
		{
			return -1;
		}
		return Arrays.asList(actions).indexOf(action);
	}

	public boolean hasAction(String action)
	{
		final String[] actions = getActions();
		if (actions == null)
		{
			return false;
		}

		return Arrays.asList(actions).contains(action);
	}

	public Point getClickPoint()
	{
		Shape shape;
		if (tileObject instanceof GameObject)
		{
			shape = ((GameObject) tileObject).getConvexHull();
		}
		else if (tileObject instanceof WallObject)
		{
			shape = ((WallObject) tileObject).getConvexHull();
		}
		else if (tileObject instanceof DecorativeObject)
		{
			shape = ((DecorativeObject) tileObject).getConvexHull();
		}
		else if (tileObject instanceof GroundObject)
		{
			shape = ((GroundObject) tileObject).getConvexHull();
		}
		else
		{
			shape = tileObject.getCanvasTilePoly();
		}

		return shape != null ? Randomizer.getRandomPointIn(shape.getBounds()) : null;
	}
}
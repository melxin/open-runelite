package net.runelite.client.plugins.openrl.api.rs2.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import java.util.Arrays;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuAction;
import net.runelite.api.Model;
import net.runelite.api.Node;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.TileItem;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;
import net.runelite.client.plugins.openrl.api.reflection.Reflection;
import net.runelite.client.plugins.openrl.api.rs2.camera.RS2Camera;
import net.runelite.client.plugins.openrl.api.rs2.scene.RS2Tile;
import net.runelite.client.plugins.openrl.api.rs2.scene.RS2Tiles;

@RequiredArgsConstructor
public class RS2TileItem implements TileItem
{
	@NonNull
	@Getter(AccessLevel.PUBLIC)
	private final TileItem tileItem;

	@Override
	public int getId()
	{
		return tileItem.getId();
	}

	@Override
	public int getQuantity()
	{
		return tileItem.getQuantity();
	}

	@Override
	public int getVisibleTime()
	{
		return tileItem.getVisibleTime();
	}

	@Override
	public int getDespawnTime()
	{
		return tileItem.getDespawnTime();
	}

	@Override
	public int getOwnership()
	{
		return tileItem.getOwnership();
	}

	@Override
	public boolean isPrivate()
	{
		return tileItem.isPrivate();
	}

	@Override
	public Model getModel()
	{
		return tileItem.getModel();
	}

	@Override
	public int getModelHeight()
	{
		return tileItem.getModelHeight();
	}

	@Override
	public void setModelHeight(int modelHeight)
	{
		tileItem.setModelHeight(modelHeight);
	}

	@Override
	public int getAnimationHeightOffset()
	{
		return tileItem.getAnimationHeightOffset();
	}

	@Override
	public Node getNext()
	{
		return tileItem.getNext();
	}

	@Override
	public Node getPrevious()
	{
		return tileItem.getPrevious();
	}

	@Override
	public long getHash()
	{
		return tileItem.getHash();
	}

	@Nullable
	public String getName()
	{
		final ItemComposition composition = getComposition();
		return composition != null ? composition.getName() : null;
	}

	private ItemComposition itemComposition;

	@Nullable
	public ItemComposition getComposition()
	{
		if (itemComposition == null)
		{
			this.itemComposition = Static.getGameDataCached().getItemComposition(getId());
		}
		return itemComposition;
	}

	@Nullable
	public LocalPoint getLocalLocation()
	{
		if (getTile() == null)
		{
			return null;
		}
		return getTile().getLocalLocation();
	}

	public void interact(MenuAction menuAction)
	{
		RS2Camera.turnToSceneEntityIfOutsideClickableViewport(this);

		final LocalPoint localPoint = LocalPoint.fromWorld(Static.getClient().getTopLevelWorldView(), getTile().getWorldLocation());
		if (localPoint == null)
		{
			return;
		}
		final int param0 = localPoint.getSceneX();
		final int param1 = localPoint.getSceneY();

		final ItemComposition itemComposition = getComposition();
		if (itemComposition == null)
		{
			return;
		}

		//final MenuAction menuAction = getMenuAction(getActionIndex(action));

		final int identifier = tileItem.getId();
		final int itemId = -1;
		final int worldViewId = -1;
		final String option = "";
		final String target = "<col=ff9040>" + itemComposition.getName();

		final Point clickPoint = getClickPoint();
		final int x = clickPoint.getX();
		final int y = clickPoint.getY();

		Static.getEventBus().post(new MenuAutomated(param0, param1, menuAction, identifier, itemId, worldViewId, option, target, x, y));
	}

	public void interact(int index)
	{
		interact(getAction(index));
	}

	public void interact(String action)
	{
		interact(getMenuAction(getActionIndex(action)));
	}

	@Nullable
	public MenuAction getMenuAction(int index)
	{
		if (Static.getClient().isWidgetSelected())
		{
			return MenuAction.WIDGET_TARGET_ON_GROUND_ITEM;
		}
		switch (index)
		{
			case 0:
				return MenuAction.GROUND_ITEM_FIRST_OPTION;
			case 1:
				return MenuAction.GROUND_ITEM_SECOND_OPTION;
			case 2:
				return MenuAction.GROUND_ITEM_THIRD_OPTION;
			case 3:
				return MenuAction.GROUND_ITEM_FOURTH_OPTION;
			case 4:
				return MenuAction.GROUND_ITEM_FIFTH_OPTION;
			default:
				return null;
		}
	}

	public int getActionIndex(String action)
	{
		final ItemComposition itemComposition = getComposition();
		if (itemComposition == null)
		{
			return -1;
		}

		final String[] groundActions = Reflection.getGroundItemActions(itemComposition);
		for (int i = 0; i < groundActions.length; i++)
		{
			final String groundAction = groundActions[i];
			if (groundAction != null && groundAction.equalsIgnoreCase(action))
			{
				return i;
			}
		}

		return -1;
	}

	public String getAction(int index)
	{
		final ItemComposition itemComposition = getComposition();
		if (itemComposition == null)
		{
			return null;
		}

		final String[] groundActions = Arrays.stream(Reflection.getGroundItemActions(itemComposition))
			.filter(a -> a != null && !a.equals("null"))
			.toArray(String[]::new);

		if (index >= 0 && index < groundActions.length)
		{
			return groundActions[index];
		}

		return "null";
	}

	public RS2Tile getTile()
	{
		for (RS2Tile tile : RS2Tiles.getAll())
		{
			if (tile.getGroundItems() != null)
			{
				for (TileItem item : tile.getGroundItems())
				{
					if (item == tileItem)
					{
						return tile;
					}
				}
			}
		}
		return null;
	}

	public Point getClickPoint()
	{
		final LocalPoint localLocation = getLocalLocation();
		/*final Shape shape = Perspective.getClickbox(Static.getClient(), getModel(), 0, localLocation.getX(), localLocation.getY(), Static.getClient().getPlane());
		return shape != null ? Randomizer.getRandomPointIn(shape.getBounds()) : Perspective.localToCanvas(Static.getClient(), localLocation, Static.getClient().getPlane());*/
		return Perspective.localToCanvas(Static.getClient(), localLocation, Static.getClient().getPlane());
	}
}
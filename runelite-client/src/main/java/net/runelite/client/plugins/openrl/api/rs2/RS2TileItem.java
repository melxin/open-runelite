package net.runelite.client.plugins.openrl.api.rs2;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuAction;
import net.runelite.api.Model;
import net.runelite.api.Node;
import net.runelite.api.TileItem;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;
import net.runelite.client.plugins.openrl.api.reflection.Reflection;

@Getter
@RequiredArgsConstructor
public class RS2TileItem implements TileItem
{
	@NonNull
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

	public String getName()
	{
		final ItemComposition composition = getComposition();
		return composition != null ? composition.getName() : null;
	}

	public ItemComposition getComposition()
	{
		return Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getItemDefinition(tileItem.getId())).orElse(null);
	}

	public void interact(String action)
	{
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

		final MenuAction menuAction = getMenuAction(getActionIndex(action));

		final int identifier = tileItem.getId();
		final int itemId = -1;
		final int worldViewId = -1;
		final String option = "";
		final String target = "<col=ff9040>" + itemComposition.getName();

		/*final Rectangle bounds = PointRandomizer.getBoundsFor(tileItem);
		final int x = (int) bounds.getX();
		final int y = (int) bounds.getY();*/

		Static.getEventBus().post(new MenuAutomated(param0, param1, menuAction, identifier, itemId, worldViewId, option, target, -1, -1));
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
}

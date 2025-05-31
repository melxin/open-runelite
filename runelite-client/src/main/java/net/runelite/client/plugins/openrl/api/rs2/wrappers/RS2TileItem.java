/*
 * Copyright (c) 2025, Melxin <https://github.com/melxin>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.openrl.api.rs2.wrappers;

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
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;
import net.runelite.client.plugins.openrl.api.reflection.Reflection;
import net.runelite.client.plugins.openrl.api.rs2.providers.camera.RS2Camera;
import net.runelite.client.plugins.openrl.api.rs2.providers.scene.RS2Tiles;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.api.SceneEntity;

@RequiredArgsConstructor
public class RS2TileItem implements TileItem, SceneEntity
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
	public int getRenderMode()
	{
		return tileItem.getRenderMode();
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

	public LocalPoint getLocalLocation()
	{
		return getTile().getLocalLocation();
	}

	public WorldPoint getWorldLocation()
	{
		return getTile().getWorldLocation();
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

	@Override
	public void interact(int index)
	{
		interact(getAction(index));
	}

	@Override
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

	@Override
	public String[] getActions()
	{
		final ItemComposition itemComposition = getComposition();
		if (itemComposition == null)
		{
			return null;
		}

		final String[] groundActions = Arrays.stream(Reflection.getGroundItemActions(itemComposition))
			.filter(a -> a != null && !a.equals("null"))
			.toArray(String[]::new);

		return groundActions;
	}

	public int getActionIndex(String action)
	{
		final String[] groundActions = getActions();
		if (groundActions == null)
		{
			return -1;
		}
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
		final String[] groundActions = getActions();
		if (groundActions == null)
		{
			return "null";
		}

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
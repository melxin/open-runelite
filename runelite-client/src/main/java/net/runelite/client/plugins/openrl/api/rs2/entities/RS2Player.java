package net.runelite.client.plugins.openrl.api.rs2.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import net.runelite.api.Actor;
import net.runelite.api.ActorSpotAnim;
import net.runelite.api.HeadIcon;
import net.runelite.api.IterableHashTable;
import net.runelite.api.MenuAction;
import net.runelite.api.Model;
import net.runelite.api.Node;
import net.runelite.api.Player;
import net.runelite.api.PlayerComposition;
import net.runelite.api.Point;
import net.runelite.api.SpritePixels;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.events.MenuAutomated;
import net.runelite.client.plugins.openrl.api.input.utils.Randomizer;
import net.runelite.client.plugins.openrl.api.rs2.camera.RS2Camera;

@RequiredArgsConstructor
public class RS2Player implements Player
{
	@NonNull
	@Getter(AccessLevel.PUBLIC)
	private final Player player;

	@Override
	public int getId()
	{
		return player.getId();
	}

	@Override
	public WorldView getWorldView()
	{
		return player.getWorldView();
	}

	@Override
	public LocalPoint getCameraFocus()
	{
		return player.getCameraFocus();
	}

	@Override
	public int getCombatLevel()
	{
		return player.getCombatLevel();
	}

	@Override
	public PlayerComposition getPlayerComposition()
	{
		return player.getPlayerComposition();
	}

	@Override
	public Polygon[] getPolygons()
	{
		return player.getPolygons();
	}

	@Override
	public int getTeam()
	{
		return player.getTeam();
	}

	@Override
	public boolean isFriendsChatMember()
	{
		return player.isFriendsChatMember();
	}

	@Override
	public boolean isFriend()
	{
		return player.isFriend();
	}

	@Override
	public boolean isClanMember()
	{
		return player.isClanMember();
	}

	@Override
	public HeadIcon getOverheadIcon()
	{
		return player.getOverheadIcon();
	}

	@Override
	public int getSkullIcon()
	{
		return player.getSkullIcon();
	}

	@Override
	public void setSkullIcon(int skullIcon)
	{
		player.setSkullIcon(skullIcon);
	}

	@Override
	public int getFootprintSize()
	{
		return player.getFootprintSize();
	}

	@Nullable
	@Override
	public String getName()
	{
		return Static.getClientThread().runOnClientThreadOptional(() -> player.getName()).orElse(null);
	}

	@Override
	public boolean isInteracting()
	{
		return player.isInteracting();
	}

	@Override
	public Actor getInteracting()
	{
		return player.getInteracting();
	}

	@Override
	public int getHealthRatio()
	{
		return player.getHealthRatio();
	}

	@Override
	public int getHealthScale()
	{
		return player.getHealthScale();
	}

	private PlayerComposition playerComposition;

	@Nullable
	public PlayerComposition getComposition()
	{
		if (playerComposition == null)
		{
			playerComposition = Static.getGameDataCached().getPlayerComposition(getPlayer());
		}
		return playerComposition;
	}

	@Override
	public WorldPoint getWorldLocation()
	{
		return player.getWorldLocation();
	}

	@Override
	public LocalPoint getLocalLocation()
	{
		return player.getLocalLocation();
	}

	@Override
	public int getOrientation()
	{
		return player.getOrientation();
	}

	@Override
	public int getCurrentOrientation()
	{
		return player.getCurrentOrientation();
	}

	@Override
	public int getAnimation()
	{
		return player.getAnimation();
	}

	@Override
	public int getPoseAnimation()
	{
		return player.getPoseAnimation();
	}

	@Override
	public void setPoseAnimation(int animation)
	{
		player.setPoseAnimation(animation);
	}

	@Override
	public int getPoseAnimationFrame()
	{
		return player.getPoseAnimationFrame();
	}

	@Override
	public void setPoseAnimationFrame(int frame)
	{
		player.setPoseAnimationFrame(frame);
	}

	@Override
	public int getIdlePoseAnimation()
	{
		return player.getIdlePoseAnimation();
	}

	@Override
	public void setIdlePoseAnimation(int animation)
	{
		player.setIdlePoseAnimation(animation);
	}

	@Override
	public int getIdleRotateLeft()
	{
		return player.getIdleRotateLeft();
	}

	@Override
	public void setIdleRotateLeft(int animationID)
	{
		player.setIdleRotateLeft(animationID);
	}

	@Override
	public int getIdleRotateRight()
	{
		return player.getIdleRotateRight();
	}

	@Override
	public void setIdleRotateRight(int animationID)
	{
		player.setIdleRotateRight(animationID);
	}

	@Override
	public int getWalkAnimation()
	{
		return player.getWalkAnimation();
	}

	@Override
	public void setWalkAnimation(int animationID)
	{
		player.setWalkAnimation(animationID);
	}

	@Override
	public int getWalkRotateLeft()
	{
		return player.getWalkRotateLeft();
	}

	@Override
	public void setWalkRotateLeft(int animationID)
	{
		player.setWalkRotateLeft(animationID);
	}

	@Override
	public int getWalkRotateRight()
	{
		return player.getWalkRotateRight();
	}

	@Override
	public void setWalkRotateRight(int animationID)
	{
		player.setWalkRotateRight(animationID);
	}

	@Override
	public int getWalkRotate180()
	{
		return player.getWalkRotate180();
	}

	@Override
	public void setWalkRotate180(int animationID)
	{
		player.setWalkRotate180(animationID);
	}

	@Override
	public int getRunAnimation()
	{
		return player.getRunAnimation();
	}

	@Override
	public void setRunAnimation(int animationID)
	{
		player.setRunAnimation(animationID);
	}

	@Override
	public void setAnimation(int animation)
	{
		player.setAnimation(animation);
	}

	@Override
	public int getAnimationFrame()
	{
		return player.getAnimationFrame();
	}

	@Override
	public void setActionFrame(int frame)
	{
		player.setActionFrame(frame);
	}

	@Override
	public void setAnimationFrame(int frame)
	{
		player.setAnimationFrame(frame);
	}

	@Override
	public IterableHashTable<ActorSpotAnim> getSpotAnims()
	{
		return player.getSpotAnims();
	}

	@Override
	public boolean hasSpotAnim(int spotAnimId)
	{
		return player.hasSpotAnim(spotAnimId);
	}

	@Override
	public void createSpotAnim(int id, int spotAnimId, int height, int delay)
	{
		player.createSpotAnim(id, spotAnimId, height, delay);
	}

	@Override
	public void removeSpotAnim(int id)
	{
		player.removeSpotAnim(id);
	}

	@Override
	public void clearSpotAnims()
	{
		player.clearSpotAnims();
	}

	@Override
	public int getGraphic()
	{
		return player.getGraphic();
	}

	@Override
	public void setGraphic(int graphic)
	{
		player.setGraphic(graphic);
	}

	@Override
	public int getGraphicHeight()
	{
		return player.getGraphicHeight();
	}

	@Override
	public void setGraphicHeight(int height)
	{
		 player.setGraphicHeight(height);
	}

	@Override
	public int getSpotAnimFrame()
	{
		return player.getSpotAnimFrame();
	}

	@Override
	public void setSpotAnimFrame(int spotAnimFrame)
	{
		player.setSpotAnimFrame(spotAnimFrame);
	}

	@Override
	public Polygon getCanvasTilePoly()
	{
		return player.getCanvasTilePoly();
	}

	@Nullable
	@Override
	public Point getCanvasTextLocation(Graphics2D graphics, String text, int zOffset)
	{
		return player.getCanvasTextLocation(graphics, text, zOffset);
	}

	@Override
	public Point getCanvasImageLocation(BufferedImage image, int zOffset)
	{
		return player.getCanvasImageLocation(image, zOffset);
	}

	@Override
	public Point getCanvasSpriteLocation(SpritePixels sprite, int zOffset)
	{
		return player.getCanvasSpriteLocation(sprite, zOffset);
	}

	@Override
	public Point getMinimapLocation()
	{
		return player.getMinimapLocation();
	}

	@Override
	public int getLogicalHeight()
	{
		return player.getLogicalHeight();
	}

	@Override
	public Shape getConvexHull()
	{
		return player.getConvexHull();
	}

	@Override
	public WorldArea getWorldArea()
	{
		return player.getWorldArea();
	}

	@Override
	public String getOverheadText()
	{
		return player.getOverheadText();
	}

	@Override
	public void setOverheadText(String overheadText)
	{
		player.setOverheadText(overheadText);
	}

	@Override
	public int getOverheadCycle()
	{
		return player.getOverheadCycle();
	}

	@Override
	public void setOverheadCycle(int cycles)
	{
		player.setOverheadCycle(cycles);
	}

	@Override
	public boolean isDead()
	{
		return player.isDead();
	}

	@Override
	public void setDead(boolean dead)
	{
		player.setDead(dead);
	}

	@Override
	public int getAnimationHeightOffset()
	{
		return player.getAnimationHeightOffset();
	}

	@Override
	public Model getModel()
	{
		return player.getModel();
	}

	@Override
	public int getModelHeight()
	{
		return player.getModelHeight();
	}

	@Override
	public void setModelHeight(int modelHeight)
	{
		player.setModelHeight(modelHeight);
	}

	@Override
	public Node getNext()
	{
		return player.getNext();
	}

	@Override
	public Node getPrevious()
	{
		return player.getPrevious();
	}

	@Override
	public long getHash()
	{
		return player.getHash();
	}

	public void interact(MenuAction menuAction)
	{
		RS2Camera.turnToSceneEntityIfOutsideClickableViewport(this);

		final int param0 = 0;
		final int param1 = 0;
		final int index = player.getId();
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
		interact(getMenuAction(index));
	}

	public void interact(String action)
	{
		interact(getMenuAction(action));
	}

	@Nullable
	public MenuAction getMenuAction(String action)
	{
		if (Static.getClient().isWidgetSelected())
		{
			return MenuAction.WIDGET_TARGET_ON_PLAYER;
		}

		if (action.equalsIgnoreCase("attack"))
		{
			return MenuAction.PLAYER_SECOND_OPTION;
		}
		else if (action.equalsIgnoreCase("walk here"))
		{
			return MenuAction.WALK;
		}
		else if (action.equalsIgnoreCase("follow"))
		{
			return MenuAction.PLAYER_THIRD_OPTION;
		}
		else if (action.equalsIgnoreCase("challenge"))
		{
			return MenuAction.PLAYER_FIRST_OPTION;
		}
		else if (action.equalsIgnoreCase("trade with"))
		{
			return MenuAction.PLAYER_FOURTH_OPTION;
		}
		else if (action.equalsIgnoreCase("cast"))
		{
			return MenuAction.WIDGET_TARGET_ON_PLAYER;
		}
		else if (action.equalsIgnoreCase("use"))
		{
			return MenuAction.WIDGET_TARGET_ON_PLAYER;
		}

		return null;
	}

	@Nullable
	public MenuAction getMenuAction(int index)
	{
		if (Static.getClient().isWidgetSelected())
		{
			return MenuAction.WIDGET_TARGET_ON_PLAYER;
		}

		switch (index)
		{
			case 0:
				return MenuAction.PLAYER_FIRST_OPTION;
			case 1:
				return MenuAction.PLAYER_SECOND_OPTION;
			case 2:
				return MenuAction.PLAYER_THIRD_OPTION;
			case 3:
				return MenuAction.PLAYER_FOURTH_OPTION;
			case 4:
				return MenuAction.PLAYER_FIFTH_OPTION;
			case 5:
				return MenuAction.PLAYER_SIXTH_OPTION;
			case 6:
				return MenuAction.PLAYER_SEVENTH_OPTION;
			case 7:
				return MenuAction.PLAYER_EIGHTH_OPTION;
			default:
				return null;
		}
	}

	public Point getClickPoint()
	{
		final Shape convexHull = player.getConvexHull();
		return convexHull != null ? Randomizer.getRandomPointIn(convexHull.getBounds()) : null;
	}
}
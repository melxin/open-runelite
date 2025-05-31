package net.runelite.client.plugins.openrl.api.rs2.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.annotation.Nullable;
import net.runelite.api.Actor;
import net.runelite.api.ActorSpotAnim;
import net.runelite.api.IterableHashTable;
import net.runelite.api.MenuAction;
import net.runelite.api.Model;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Node;
import net.runelite.api.NpcOverrides;
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
public class RS2NPC implements NPC
{
	@NonNull
	@Getter(AccessLevel.PUBLIC)
	private final NPC npc;

	@Override
	public int getId()
	{
		return npc.getId();
	}

	@Nullable
	@Override
	public String getName()
	{
		return Static.getClientThread().runOnClientThreadOptional(() -> npc.getName()).orElse(getComposition().getName());
	}

	@Override
	public boolean isInteracting()
	{
		return npc.isInteracting();
	}

	@Override
	public Actor getInteracting()
	{
		return npc.getInteracting();
	}

	@Override
	public int getHealthRatio()
	{
		return npc.getHealthRatio();
	}

	@Override
	public int getHealthScale()
	{
		return npc.getHealthScale();
	}

	@Override
	public WorldView getWorldView()
	{
		return npc.getWorldView();
	}

	@Override
	public LocalPoint getCameraFocus()
	{
		return npc.getCameraFocus();
	}

	@Override
	public int getCombatLevel()
	{
		return npc.getCombatLevel();
	}

	@Override
	public int getIndex()
	{
		return npc.getIndex();
	}

	private NPCComposition npcComposition;

	@Nullable
	public NPCComposition getComposition()
	{
		if (npcComposition == null)
		{
			npcComposition = Static.getGameDataCached().getNPCComposition(getId());
		}
		return npcComposition;
	}

	@Nullable
	@Override
	public NPCComposition getTransformedComposition()
	{
		return npc.getTransformedComposition();
	}

	@Nullable
	@Override
	public NpcOverrides getModelOverrides()
	{
		return npc.getModelOverrides();
	}

	@Nullable
	@Override
	public NpcOverrides getChatheadOverrides()
	{
		return npc.getChatheadOverrides();
	}

	@Nullable
	@Override
	public int[] getOverheadArchiveIds()
	{
		return npc.getOverheadArchiveIds();
	}

	@Nullable
	@Override
	public short[] getOverheadSpriteIds()
	{
		return npc.getOverheadSpriteIds();
	}

	@Override
	public WorldPoint getWorldLocation()
	{
		return npc.getWorldLocation();
	}

	@Override
	public LocalPoint getLocalLocation()
	{
		return npc.getLocalLocation();
	}

	@Override
	public int getOrientation()
	{
		return npc.getOrientation();
	}

	@Override
	public int getCurrentOrientation()
	{
		return npc.getCurrentOrientation();
	}

	@Override
	public int getAnimation()
	{
		return npc.getAnimation();
	}

	@Override
	public int getPoseAnimation()
	{
		return npc.getPoseAnimation();
	}

	@Override
	public void setPoseAnimation(int animation)
	{
		npc.setPoseAnimation(animation);
	}

	@Override
	public int getPoseAnimationFrame()
	{
		return npc.getPoseAnimationFrame();
	}

	@Override
	public void setPoseAnimationFrame(int frame)
	{
		npc.setPoseAnimationFrame(frame);
	}

	@Override
	public int getIdlePoseAnimation()
	{
		return npc.getIdlePoseAnimation();
	}

	@Override
	public void setIdlePoseAnimation(int animation)
	{
		npc.setIdlePoseAnimation(animation);
	}

	@Override
	public int getIdleRotateLeft()
	{
		return npc.getIdleRotateLeft();
	}

	@Override
	public void setIdleRotateLeft(int animationID)
	{
		npc.setIdleRotateLeft(animationID);
	}

	@Override
	public int getIdleRotateRight()
	{
		return npc.getIdleRotateRight();
	}

	@Override
	public void setIdleRotateRight(int animationID)
	{
		npc.setIdleRotateRight(animationID);
	}

	@Override
	public int getWalkAnimation()
	{
		return npc.getWalkAnimation();
	}

	@Override
	public void setWalkAnimation(int animationID)
	{
		npc.setWalkAnimation(animationID);
	}

	@Override
	public int getWalkRotateLeft()
	{
		return npc.getWalkRotateLeft();
	}

	@Override
	public void setWalkRotateLeft(int animationID)
	{
		npc.setWalkRotateLeft(animationID);
	}

	@Override
	public int getWalkRotateRight()
	{
		return npc.getWalkRotateRight();
	}

	@Override
	public void setWalkRotateRight(int animationID)
	{
		npc.setWalkRotateRight(animationID);
	}

	@Override
	public int getWalkRotate180()
	{
		return npc.getWalkRotate180();
	}

	@Override
	public void setWalkRotate180(int animationID)
	{
		npc.setWalkRotate180(animationID);
	}

	@Override
	public int getRunAnimation()
	{
		return npc.getRunAnimation();
	}

	@Override
	public void setRunAnimation(int animationID)
	{
		npc.setRunAnimation(animationID);
	}

	@Override
	public void setAnimation(int animation)
	{
		npc.setRunAnimation(animation);
	}

	@Override
	public int getAnimationFrame()
	{
		return npc.getAnimationFrame();
	}

	@Override
	public void setActionFrame(int frame)
	{
		npc.setActionFrame(frame);
	}

	@Override
	public void setAnimationFrame(int frame)
	{
		npc.setAnimationFrame(frame);
	}

	@Override
	public IterableHashTable<ActorSpotAnim> getSpotAnims()
	{
		return npc.getSpotAnims();
	}

	@Override
	public boolean hasSpotAnim(int spotAnimId)
	{
		return npc.hasSpotAnim(spotAnimId);
	}

	@Override
	public void createSpotAnim(int id, int spotAnimId, int height, int delay)
	{
		npc.createSpotAnim(id, spotAnimId, height, delay);
	}

	@Override
	public void removeSpotAnim(int id)
	{
		npc.removeSpotAnim(id);
	}

	@Override
	public void clearSpotAnims()
	{
		npc.clearSpotAnims();
	}

	@Override
	public int getGraphic()
	{
		return npc.getGraphic();
	}

	@Override
	public void setGraphic(int graphic)
	{
		npc.setGraphic(graphic);
	}

	@Override
	public int getGraphicHeight()
	{
		return npc.getGraphicHeight();
	}

	@Override
	public void setGraphicHeight(int height)
	{
		npc.setGraphicHeight(height);
	}

	@Override
	public int getSpotAnimFrame()
	{
		return npc.getSpotAnimFrame();
	}

	@Override
	public void setSpotAnimFrame(int spotAnimFrame)
	{
		npc.setSpotAnimFrame(spotAnimFrame);
	}

	@Override
	public Polygon getCanvasTilePoly()
	{
		return npc.getCanvasTilePoly();
	}

	@Nullable
	@Override
	public Point getCanvasTextLocation(Graphics2D graphics, String text, int zOffset)
	{
		return npc.getCanvasTextLocation(graphics, text, zOffset);
	}

	@Override
	public Point getCanvasImageLocation(BufferedImage image, int zOffset)
	{
		return npc.getCanvasImageLocation(image, zOffset);
	}

	@Override
	public Point getCanvasSpriteLocation(SpritePixels sprite, int zOffset)
	{
		return npc.getCanvasSpriteLocation(sprite, zOffset);
	}

	@Override
	public Point getMinimapLocation()
	{
		return npc.getMinimapLocation();
	}

	@Override
	public int getLogicalHeight()
	{
		return npc.getLogicalHeight();
	}

	@Override
	public Shape getConvexHull()
	{
		return npc.getConvexHull();
	}

	@Override
	public WorldArea getWorldArea()
	{
		return npc.getWorldArea();
	}

	@Override
	public String getOverheadText()
	{
		return npc.getOverheadText();
	}

	@Override
	public void setOverheadText(String overheadText)
	{
		npc.setOverheadText(overheadText);
	}

	@Override
	public int getOverheadCycle()
	{
		return npc.getOverheadCycle();
	}

	@Override
	public void setOverheadCycle(int cycles)
	{
		npc.setOverheadCycle(cycles);
	}

	@Override
	public boolean isDead()
	{
		return npc.isDead();
	}

	@Override
	public void setDead(boolean dead)
	{
		npc.setDead(dead);
	}

	@Override
	public int getAnimationHeightOffset()
	{
		return npc.getAnimationHeightOffset();
	}

	@Override
	public Model getModel()
	{
		return npc.getModel();
	}

	@Override
	public int getModelHeight()
	{
		return npc.getModelHeight();
	}

	@Override
	public void setModelHeight(int modelHeight)
	{
		npc.setModelHeight(modelHeight);
	}

	@Override
	public Node getNext()
	{
		return npc.getNext();
	}

	@Override
	public Node getPrevious()
	{
		return npc.getPrevious();
	}

	@Override
	public long getHash()
	{
		return npc.getHash();
	}

	public void interact(MenuAction menuAction)
	{
		RS2Camera.turnToSceneEntityIfOutsideClickableViewport(this);

		final int param0 = 0;
		final int param1 = 0;
		final int index = npc.getIndex();
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
	public MenuAction getMenuAction(int index)
	{
		if (Static.getClient().isWidgetSelected())
		{
			return MenuAction.WIDGET_TARGET_ON_NPC;
		}

		switch (index)
		{
			case 0:
				return MenuAction.NPC_FIRST_OPTION;
			case 1:
				return MenuAction.NPC_SECOND_OPTION;
			case 2:
				return MenuAction.NPC_THIRD_OPTION;
			case 3:
				return MenuAction.NPC_FOURTH_OPTION;
			case 4:
				return MenuAction.NPC_FIFTH_OPTION;
			default:
				return null;
		}
	}

	@Nullable
	public String[] getActions()
	{
		final NPCComposition npcComposition = getComposition();
		if (npcComposition == null)
		{
			return null;
		}
		final String[] actions = npcComposition.getActions();
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
		final Shape convexHull = npc.getConvexHull();
		return convexHull != null ? Randomizer.getRandomPointIn(convexHull.getBounds()) : null;
	}
}
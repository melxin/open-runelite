package net.runelite.client.plugins.openrl.api.rs2.projectiles;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import javax.annotation.Nullable;
import net.runelite.api.Actor;
import net.runelite.api.Animation;
import net.runelite.api.Model;
import net.runelite.api.Node;
import net.runelite.api.Projectile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

@RequiredArgsConstructor
public class RS2Projectile implements Projectile
{
	@NonNull
	@Getter(AccessLevel.PUBLIC)
	private final Projectile projectile;

	@Override
	public int getId()
	{
		return projectile.getId();
	}

	@Override
	public int getSourceLevel()
	{
		return projectile.getSourceLevel();
	}

	@Override
	public WorldPoint getSourcePoint()
	{
		return projectile.getSourcePoint();
	}

	@Nullable
	@Override
	public Actor getSourceActor()
	{
		return projectile.getSourceActor();
	}

	@Override
	public int getTargetLevel()
	{
		return projectile.getTargetLevel();
	}

	@Override
	public WorldPoint getTargetPoint()
	{
		return projectile.getTargetPoint();
	}

	@Nullable
	@Override
	public Actor getTargetActor()
	{
		return projectile.getTargetActor();
	}

	@Override
	public LocalPoint getTarget()
	{
		return projectile.getTarget();
	}

	@Override
	public double getX()
	{
		return projectile.getX();
	}

	@Override
	public double getY()
	{
		return projectile.getY();
	}

	@Override
	public double getZ()
	{
		return projectile.getZ();
	}

	@Override
	public int getOrientation()
	{
		return projectile.getOrientation();
	}

	@Nullable
	@Override
	public Animation getAnimation()
	{
		return projectile.getAnimation();
	}

	@Override
	public int getAnimationFrame()
	{
		return projectile.getAnimationFrame();
	}

	@Override
	public int getX1()
	{
		return projectile.getX1();
	}

	@Override
	public int getY1()
	{
		return projectile.getY1();
	}

	@Override
	public int getFloor()
	{
		return projectile.getFloor();
	}

	@Override
	public int getHeight()
	{
		return projectile.getHeight();
	}

	@Override
	public int getEndHeight()
	{
		return projectile.getEndHeight();
	}

	@Override
	public int getStartCycle()
	{
		return projectile.getStartCycle();
	}

	@Override
	public int getEndCycle()
	{
		return projectile.getEndCycle();
	}

	@Override
	public void setEndCycle(int cycle)
	{
		projectile.setEndCycle(cycle);
	}

	@Override
	public int getRemainingCycles()
	{
		return projectile.getRemainingCycles();
	}

	@Override
	public int getSlope()
	{
		return projectile.getSlope();
	}

	@Override
	public int getStartPos()
	{
		return projectile.getStartPos();
	}

	@Override
	public int getStartHeight()
	{
		return projectile.getStartHeight();
	}

	@Override
	public Model getModel()
	{
		return projectile.getModel();
	}

	@Override
	public int getModelHeight()
	{
		return projectile.getModelHeight();
	}

	@Override
	public void setModelHeight(int modelHeight)
	{
		projectile.setModelHeight(modelHeight);
	}

	@Override
	public int getAnimationHeightOffset()
	{
		return projectile.getAnimationHeightOffset();
	}

	@Override
	public Node getNext()
	{
		return projectile.getNext();
	}

	@Override
	public Node getPrevious()
	{
		return projectile.getPrevious();
	}

	@Override
	public long getHash()
	{
		return projectile.getHash();
	}
}
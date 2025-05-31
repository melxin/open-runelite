package net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.requirement;

import lombok.Value;
import net.runelite.client.plugins.openrl.api.rs2.providers.worlds.RS2Worlds;

@Value
public class WorldRequirement implements Requirement
{
	boolean memberWorld;

	@Override
	public Boolean get()
	{
		return !memberWorld || RS2Worlds.inMembersWorld();
	}
}
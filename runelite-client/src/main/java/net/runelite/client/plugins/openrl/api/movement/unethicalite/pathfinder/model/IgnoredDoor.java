package net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.requirement.Requirements;

@Value
@AllArgsConstructor
public class IgnoredDoor
{
	WorldPoint location;
	int id;
	Requirements requirements;

	@Override
	public String toString()
	{
		return "IgnoredDoor{" +
			"location=" + location +
			", id=" + id +
			", requirements=" + requirements.fulfilled() +
			'}';
	}
}
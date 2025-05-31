package net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.dto;

import lombok.Value;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.TransportLoader;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.Transport;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.requirement.Requirements;

@Value
public class TransportDto
{
	WorldPoint source;
	WorldPoint destination;
	String action;
	Integer objectId;
	Requirements requirements;

	public Transport toTransport()
	{
		return TransportLoader.objectTransport(source, destination, objectId, action, requirements);
	}
}
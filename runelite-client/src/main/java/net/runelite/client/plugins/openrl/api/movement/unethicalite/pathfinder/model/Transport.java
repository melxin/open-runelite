package net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.requirement.Requirements;

@Value
@AllArgsConstructor
public class Transport
{
	WorldPoint source;
	WorldPoint destination;
	int sourceRadius;
	int destinationRadius;
	Runnable handler;
	Requirements requirements;

	public Transport(WorldPoint source,
		WorldPoint destination,
		int sourceRadius,
		int destinationRadius,
		Runnable handler
	)
	{
		this.source = source;
		this.destination = destination;
		this.sourceRadius = sourceRadius;
		this.destinationRadius = destinationRadius;
		this.handler = handler;
		this.requirements = new Requirements();
	}
}
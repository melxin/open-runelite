package net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.requirement;

import lombok.Value;
import java.util.List;
import net.runelite.client.plugins.openrl.api.rs2.providers.items.RS2Equipment;
import net.runelite.client.plugins.openrl.api.rs2.providers.items.RS2Inventory;

@Value
public class ItemRequirement implements Requirement
{
	Reduction reduction;
	boolean equipped;
	List<Integer> ids;
	int amount;

	@Override
	public Boolean get()
	{
		switch (reduction)
		{
			case AND:
				if (equipped)
				{
					return ids.stream().allMatch(it -> RS2Equipment.getCount(true, it) >= amount);
				}
				else
				{
					return ids.stream().allMatch(it -> RS2Inventory.getCount(true, it) >= amount);
				}
			case OR:
				if (equipped)
				{
					return ids.stream().anyMatch(it -> RS2Equipment.getCount(true, it) >= amount);
				}
				else
				{
					return ids.stream().anyMatch(it -> RS2Inventory.getCount(true, it) >= amount);
				}
			case NOT:
				if (equipped)
				{
					return ids.stream().noneMatch(it -> RS2Equipment.getCount(true, it) >= amount);
				}
				else
				{
					return ids.stream().noneMatch(it -> RS2Inventory.getCount(true, it) >= amount);
				}
		}
		return false;
	}
}
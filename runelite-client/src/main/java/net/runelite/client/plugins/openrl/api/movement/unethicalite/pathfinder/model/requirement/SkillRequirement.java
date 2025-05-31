package net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.requirement;

import lombok.Value;
import net.runelite.api.Skill;
import net.runelite.client.plugins.openrl.api.game.Skills;

@Value
public class SkillRequirement implements Requirement
{
	Skill skill;
	int level;

	@Override
	public Boolean get()
	{
		return Skills.getLevel(skill) >= level;
	}
}
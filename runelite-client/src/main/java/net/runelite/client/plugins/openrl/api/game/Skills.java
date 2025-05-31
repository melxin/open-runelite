package net.runelite.client.plugins.openrl.api.game;

import net.runelite.api.Skill;
import net.runelite.client.plugins.openrl.Static;

public class Skills
{
	public static int getBoostedLevel(Skill skill)
	{
		return Static.getClient().getBoostedSkillLevel(skill);
	}

	public static int getLevel(Skill skill)
	{
		return Static.getClient().getRealSkillLevel(skill);
	}

	public static int getExperience(Skill skill)
	{
		return Static.getClient().getSkillExperience(skill);
	}
}
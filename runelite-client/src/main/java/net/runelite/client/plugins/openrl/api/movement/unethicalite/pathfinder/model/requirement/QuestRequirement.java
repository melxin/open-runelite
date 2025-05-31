package net.runelite.client.plugins.openrl.api.movement.unethicalite.pathfinder.model.requirement;

import lombok.Value;
import java.util.Set;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.client.plugins.openrl.api.rs2.providers.quests.RS2Quests;

@Value
public class QuestRequirement implements Requirement
{
	Quest quest;
	Set<QuestState> states;

	@Override
	public Boolean get()
	{
		return states.contains(RS2Quests.getState(quest));
	}
}
package net.runelite.client.plugins.openrl.api.rs2.providers.quests;

import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.client.plugins.openrl.api.managers.QuestManager;

public class Quests
{
	public static QuestState getState(Quest quest)
	{
		return QuestManager.getQuestStates().get(quest);
	}

	public static boolean isFinished(Quest quest)
	{
		return QuestManager.getQuestStates().get(quest) == QuestState.FINISHED;
	}
}

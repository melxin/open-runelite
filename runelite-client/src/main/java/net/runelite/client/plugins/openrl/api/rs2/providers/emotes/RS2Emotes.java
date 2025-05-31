package net.runelite.client.plugins.openrl.api.rs2.providers.emotes;

import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Time;
import net.runelite.client.plugins.openrl.api.managers.InteractionManager;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.RS2WidgetQuery;
import net.runelite.client.plugins.openrl.api.rs2.providers.tabs.RS2Tabs;
import net.runelite.client.plugins.openrl.api.rs2.providers.tabs.Tab;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;

public class RS2Emotes
{
	/**
	 * perform an emote by id
	 *
	 * @param emoteId emote id
	 */
	public static void perform(int emoteId)
	{
		perform(emoteId, false);
	}

	/**
	 * Perform an emote by id
	 *
	 * @param emoteId emote id
	 * @param loop whether to loop or perform
	 */
	public static void perform(int emoteId, boolean loop)
	{
		perform(EmoteID.valueOf(emoteId), loop);
	}

	/**
	 * Perform an emote by EmoteID
	 *
	 * @param emoteId emote id
	 */
	public static void perform(EmoteID emoteId)
	{
		perform(emoteId, false);
	}

	/**
	 * Perform an emote by EmoteID
	 *
	 * @param emoteId emote id
	 * @param loop whether to loop or perform
	 */
	public static void perform(EmoteID emoteId, boolean loop)
	{
		if (Static.getOpenRuneLiteConfig().interactionMethod() == InteractionManager.InteractMethod.MOUSE_EVENTS)
		{
			if (!RS2Tabs.isOpen(Tab.EMOTES))
			{
				RS2Tabs.open(Tab.EMOTES);
				Time.sleepTick();
			}
		}

		final RS2Widget emoteWidget = RS2WidgetQuery.query().getWidget(EmoteID.EMOTES_COMPONENT_ID, emoteId.getId());
		if (emoteWidget != null)
		{
			if (Static.getOpenRuneLiteConfig().interactionMethod() == InteractionManager.InteractMethod.MOUSE_EVENTS)
			{
				RS2WidgetQuery.query().scrollToWidget(InterfaceID.Emote.SCROLLABLE, InterfaceID.Emote.SCROLLBAR, emoteWidget);
			}
			emoteWidget.interact(loop ? 1 : 0);
		}
	}
}
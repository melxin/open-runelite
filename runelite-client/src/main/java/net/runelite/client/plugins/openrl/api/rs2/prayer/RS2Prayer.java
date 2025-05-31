package net.runelite.client.plugins.openrl.api.rs2.prayer;

import java.util.Arrays;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.plugins.openrl.api.game.Skills;
import net.runelite.client.plugins.openrl.api.game.Vars;
import net.runelite.client.plugins.openrl.api.rs2.widgets.RS2Widget;
import net.runelite.client.plugins.openrl.api.rs2.widgets.RS2Widgets;

public class RS2Prayer
{
	public static boolean isEnabled(Prayer prayer)
	{
		return Vars.getBit(prayer.getVarbit()) == 1;
	}

	public static void toggle(Prayer prayer)
	{
		RS2Widget widget = RS2Widgets.getWidget(prayer.getInterfaceId());
		if (widget != null)
		{
			widget.interact(0);
		}
	}

	public static int getPoints()
	{
		return Skills.getBoostedLevel(Skill.PRAYER);
	}

	public static void toggleQuickPrayer(boolean enabled)
	{
		RS2Widget widget = RS2Widgets.getWidget(InterfaceID.Orbs.PRAYERBUTTON);
		if (widget != null)
		{
			widget.interact(enabled ? "Activate" : "Deactivate");
		}
	}

	public static boolean isQuickPrayerEnabled()
	{
		return Vars.getBit(Varbits.QUICK_PRAYER) == 1;
	}

	public static boolean anyActive()
	{
		return Arrays.stream(Prayer.values()).anyMatch(RS2Prayer::isEnabled);
	}

	public static void disableAll()
	{
		Arrays.stream(Prayer.values()).filter(RS2Prayer::isEnabled).forEach(RS2Prayer::toggle);
	}
}
package net.runelite.client.plugins.openrl.plugins.bankstander;

import java.awt.Button;
import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(MBankStanderConfig.GROUP)
public interface MBankStanderConfig extends Config
{
	String GROUP = "MBankStander";

	@ConfigItem(
		keyName = "overlayToggle",
		name = "Enable overlay",
		description = "Enable or disable the overlay.",
		position = 0
	)
	default boolean overlayEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "overlayColor",
		name = "Overlay color",
		description = "The overlay color.",
		position = 1
	)
	default Color overlayColor()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		keyName = "destinationLevel",
		name = "Destination level",
		description = "The destination level to stop at (0) to disable.",
		position = 2
	)
	default int destinationLevel()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "itemName1",
		name = "item name1",
		description = "The name of item 1.",
		position = 3
	)
	default String itemName1()
	{
		return "Chisel";
	}

	@ConfigItem(
		keyName = "itemName1Amount",
		name = "item name1 amount",
		description = "The amount of item 1.",
		position = 4
	)
	default int itemAmount1()
	{
		return 1;
	}

	@ConfigItem(
		keyName = "itemName2",
		name = "item name2",
		description = "The name of item 2.",
		position = 5
	)
	default String itemName2()
	{
		return "Chisel";
	}

	@ConfigItem(
		keyName = "itemName2Amount",
		name = "item name2 amount",
		description = "The amount of item 2.",
		position = 6
	)
	default int itemAmount2()
	{
		return 27;
	}


	@ConfigItem(
		keyName = "start",
		name = "Start/Stop",
		description = "Start/Stop button",
		position = 7
	)
	default Button startStopButton()
	{
		return new Button();
	}
}

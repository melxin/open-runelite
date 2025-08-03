package net.runelite.client.plugins.openrl.plugins.crafting;

import java.awt.Button;
import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(MGemCutterConfig.GROUP)
public interface MGemCutterConfig extends Config
{
	String GROUP = "MGemCutter";

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
		keyName = "uncutName",
		name = "Uncut name",
		description = "The name of the uncut to cut.",
		position = 3
	)
	default String uncutName()
	{
		return "Uncut opal";
	}


	@ConfigItem(
		keyName = "start",
		name = "Start/Stop",
		description = "Start/Stop button",
		position = 4
	)
	default Button startStopButton()
	{
		return new Button();
	}
}

package net.runelite.client.plugins.openrl.plugins.woodcutting;

import java.awt.Button;
import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(MChopperConfig.GROUP)
public interface MChopperConfig extends Config
{
	String GROUP = "MChopper";

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
		keyName = "treeName",
		name = "Tree name",
		description = "The name of the tree to chop chop.",
		position = 3
	)
	default String treeName()
	{
		return "Tree";
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

package net.runelite.client.plugins.openrl.api.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.runelite.api.MenuAction;

/**
 * Event class for menu interactions
 */
@Data
@AllArgsConstructor
public class MenuAutomated
{
	private int param0;
	private int param1;
	private MenuAction menuAction;
	private int index;
	private int itemId;
	private int worldViewId;
	private String option;
	private String target;
	private int canvasX;
	private int canvasY;
}

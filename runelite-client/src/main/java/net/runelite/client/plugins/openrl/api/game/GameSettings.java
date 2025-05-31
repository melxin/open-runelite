package net.runelite.client.plugins.openrl.api.game;

import net.runelite.api.VarPlayer;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.plugins.openrl.api.rs2.providers.client.RS2ClientScript;
import net.runelite.client.plugins.openrl.api.rs2.providers.tabs.RS2Tabs;
import net.runelite.client.plugins.openrl.api.rs2.providers.tabs.Tab;
import net.runelite.client.plugins.openrl.api.rs2.providers.widgets.RS2Widgets;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;
import java.util.function.Supplier;

// @TODO FIX
public class GameSettings
{
	public enum Display
	{
		FIXED(() -> RS2Widgets.getWidget(InterfaceID.TOPLEVEL)),
		RESIZABLE_CLASSIC(() -> RS2Widgets.getWidget(InterfaceID.TOPLEVEL_OSRS_STRETCH)),
		RESIZABLE_MODERN(() -> RS2Widgets.getWidget(InterfaceID.TOPLEVEL_PRE_EOC));

		private final Supplier<RS2Widget> widgetSupplier;

		Display(Supplier<RS2Widget> widgetSupplier)
		{
			this.widgetSupplier = widgetSupplier;
		}

		public Supplier<RS2Widget> getWidgetSupplier()
		{
			return widgetSupplier;
		}

		public static void setMode(Display displayMode)
		{
			if (!RS2Tabs.isOpen(Tab.OPTIONS))
			{
				RS2Tabs.open(Tab.OPTIONS);
			}

			switch (displayMode)
			{
				case FIXED:
					RS2ClientScript.runScript(3998, 0);
					break;
				case RESIZABLE_CLASSIC:
					// I have no fuckin idea but it works
					RS2ClientScript.runScript(441, 7602188, 7602213, 7602207, 7602209, 7602214, 7602215, 7602176);
					//Static.getClient().interact(1, 57, 2, 7602213);
					break;
				case RESIZABLE_MODERN:
					RS2ClientScript.runScript(3998, 1);
					break;
			}
		}

		public static Display getCurrentMode()
		{
			for (Display display : values())
			{
				final RS2Widget widget = display.getWidgetSupplier().get();
				if (RS2Widgets.isVisible(widget))
				{
					return display;
				}
			}

			return null;
		}
	}

	public enum Audio
	{
		MUSIC(() -> RS2Widgets.get(116, 41), VarPlayer.MUSIC_VOLUME),
		EFFECTS(() -> RS2Widgets.get(116, 55), VarPlayer.SOUND_EFFECT_VOLUME),
		AREA(() -> RS2Widgets.get(116, 69), VarPlayer.AREA_EFFECT_VOLUME);

		private final Supplier<RS2Widget> widgetSupplier;
		private final int levelVarp;

		Audio(Supplier<RS2Widget> widgetSupplier, int levelVarp)
		{
			this.widgetSupplier = widgetSupplier;
			this.levelVarp = levelVarp;
		}

		public Supplier<RS2Widget> getWidgetSupplier()
		{
			return widgetSupplier;
		}

		public int getLevel()
		{
			return Vars.getVarp(getLevelVarp());
		}

		public void setVolume(int level)
		{
			if (getLevel() != level)
			{
				if (!RS2Tabs.isOpen(Tab.OPTIONS))
				{
					RS2Tabs.open(Tab.OPTIONS);
				}

				final RS2Widget widget = widgetSupplier.get();
				if (widget != null && widget.getChild(level) != null)
				{
					widget.getChild(level).interact(0);
				}
			}
		}

		public static boolean isFullMuted()
		{
			return MUSIC.getLevel() == 0 && AREA.getLevel() == 0 && EFFECTS.getLevel() == 0;
		}

		public static void muteAll()
		{
			for (Audio audio : values())
			{
				audio.setVolume(0);
			}
		}

		public int getLevelVarp()
		{
			return levelVarp;
		}
	}
}
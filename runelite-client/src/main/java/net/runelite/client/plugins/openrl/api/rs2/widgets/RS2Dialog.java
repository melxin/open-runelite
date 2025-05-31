package net.runelite.client.plugins.openrl.api.rs2.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.game.GameThread;
import net.runelite.client.plugins.openrl.api.input.Keyboard;
import net.runelite.client.plugins.openrl.api.commons.Predicates;

public class RS2Dialog
{
	private static final Supplier<RS2Widget> SPRITE_CONT = () -> RS2Widgets.get(193, 0);
	private static final Supplier<RS2Widget> SCROLL_BAR = () -> RS2Widgets.get(162, 557);
	private static final Supplier<RS2Widget> WEIRD_CONT = () -> RS2Widgets.get(193, 3);
	private static final Supplier<RS2Widget> WEIRD_CONT_2 = () -> RS2Widgets.get(633, 0);
	private static final Supplier<RS2Widget> NPC_CONT = () -> RS2Widgets.get(WidgetID.DIALOG_NPC_GROUP_ID, 4);
	private static final Supplier<RS2Widget> NPC_TEXT = () -> RS2Widgets.get(WidgetID.DIALOG_NPC_GROUP_ID, 6);
	private static final Supplier<RS2Widget> PLAYER_CONT = () -> RS2Widgets.get(WidgetID.DIALOG_PLAYER_GROUP_ID, 3);
	private static final Supplier<RS2Widget> PLAYER_NAME = () -> RS2Widgets.get(WidgetID.DIALOG_PLAYER_GROUP_ID, 4);
	private static final Supplier<RS2Widget> PLAYER_TEXT = () -> RS2Widgets.get(WidgetID.DIALOG_PLAYER_GROUP_ID, 6);
	private static final Supplier<RS2Widget> DEATH_CONT = () -> RS2Widgets.get(663, 0);
	private static final Supplier<RS2Widget> TUT_CONT = () -> RS2Widgets.get(229, 2);
	private static final Supplier<RS2Widget> OPTIONS = () -> RS2Widgets.get(WidgetID.DIALOG_OPTION_GROUP_ID, 1);

	// Tutorial island continue dialogs
	public static void continueTutorial()
	{
		GameThread.invoke(() -> Static.getClient().runScript(299, 1, 1, 1));
	}

	public static boolean isOpen()
	{
		return SCROLL_BAR.get() == null || !SCROLL_BAR.get().isVisible();
	}

	public static boolean canContinue()
	{
		return canContinueNPC() || canContinuePlayer() || canContinueDeath()
			|| canSpriteContinue() || canSprite2Continue()
			|| canContinue1() || canContinue2()
			|| canContinueTutIsland() || canContinueTutIsland2()
			|| canContinueTutIsland3() || canLevelUpContinue();
	}

	public static boolean canLevelUpContinue()
	{
		return RS2Widgets.isVisible(RS2Widgets.get(WidgetInfo.LEVEL_UP_LEVEL));
	}

	public static boolean canSpriteContinue()
	{
		return RS2Widgets.isVisible(SPRITE_CONT.get());
	}

	public static boolean canSprite2Continue()
	{
		//return RS2Widgets.isVisible(RS2Widgets.get(WidgetInfo.DIALOG2_SPRITE_CONTINUE));
		return false;
	}

	public static boolean canContinue1()
	{
		return RS2Widgets.isVisible(WEIRD_CONT.get());
	}

	public static boolean canContinue2()
	{
		return RS2Widgets.isVisible(WEIRD_CONT_2.get());
	}

	public static boolean canContinueNPC()
	{
		return RS2Widgets.isVisible(NPC_CONT.get());
	}

	public static boolean canContinuePlayer()
	{
		return RS2Widgets.isVisible(PLAYER_CONT.get());
	}

	public static boolean canContinueDeath()
	{
		RS2Widget widget = DEATH_CONT.get();
		return widget != null && widget.isVisible() && widget.getChild(2) != null && !widget.getChild(2).isVisible();
	}

	public static boolean canContinueTutIsland()
	{
		return RS2Widgets.isVisible(TUT_CONT.get());
	}

	public static boolean canContinueTutIsland2()
	{
		RS2Widget widget = RS2Widgets.get(WidgetInfo.DIALOG_SPRITE);
		return widget != null
			&& widget.isVisible() && widget.getChild(2) != null && widget.getChild(2).isVisible();
	}

	public static boolean canContinueTutIsland3()
	{
		RS2Widget widget = RS2Widgets.get(WidgetInfo.CHATBOX_FULL_INPUT);
		return widget != null && widget.isVisible() && widget.getText().toLowerCase().contains("continue");
	}

	public static boolean isEnterInputOpen()
	{
		//return RS2Widgets.isVisible(RS2Widgets.get(WidgetInfo.CHATBOX_FULL_INPUT)) && !GrandExchange.isSearchingItem();
		return RS2Widgets.isVisible(RS2Widgets.get(WidgetInfo.CHATBOX_FULL_INPUT));
	}

	public static void enterName(String input)
	{
		//GameThread.invoke(() -> DialogPackets.sendNameInput(input));
		Keyboard.type(input, true);
	}

	public static void enterText(String input)
	{
		//GameThread.invoke(() -> DialogPackets.sendTextInput(input));
		Keyboard.type(input, true);
	}

	public static void enterAmount(int input)
	{
		//GameThread.invoke(() -> DialogPackets.sendNumberInput(input));
		Keyboard.type(String.valueOf(input), true);
	}

	public static boolean isViewingOptions()
	{
		return !getOptions().isEmpty();
	}

	public static void continueSpace()
	{
		if (RS2Dialog.isOpen())
		{
			Keyboard.sendSpace();
		}
	}

	public static boolean chooseOption(int index)
	{
		if (isViewingOptions())
		{
			Keyboard.type(index);
			return true;
		}

		return false;
	}

	public static boolean chooseOption(String... options)
	{
		if (isViewingOptions())
		{
			for (int i = 0; i < getOptions().size(); i++)
			{
				Widget widget = getOptions().get(i);
				for (String option : options)
				{
					if (widget.getText().contains(option))
					{
						return chooseOption(i + 1);
					}
				}
			}
		}

		return false;
	}

	public static String getQuestion()
	{
		RS2Widget widget = OPTIONS.get();
		if (!RS2Widgets.isVisible(widget))
		{
			return "";
		}

		RS2Widget[] children = widget.getChildren();
		if (children == null || children.length == 0)
		{
			return "";
		}

		return children[0].getText();
	}

	public static List<RS2Widget> getOptions()
	{
		RS2Widget widget = OPTIONS.get();
		if (!RS2Widgets.isVisible(widget))
		{
			return Collections.emptyList();
		}

		List<RS2Widget> out = new ArrayList<>();
		RS2Widget[] children = widget.getChildren();
		if (children == null)
		{
			return out;
		}

		// Skip first child, it's not a dialog option
		for (int i = 1; i < children.length; i++)
		{
			if (children[i].getText().isBlank())
			{
				continue;
			}

			out.add(children[i]);
		}

		return out;
	}

	public static boolean hasOption(String option)
	{
		return hasOption(Predicates.texts(option));
	}

	public static boolean hasOption(Predicate<String> option)
	{
		return getOptions().stream()
			.map(Widget::getText)
			.filter(Objects::nonNull)
			.anyMatch(option);
	}

		/*public static void invokeDialog(DialogOption... dialogOption)
		{
			GameThread.invokeLater(() ->
			{
				for (DialogOption option : dialogOption)
				{
					Static.getClient().processDialog(option.getWidgetUid(), option.getMenuIndex());
				}

				return true;
			});
		}

		public static void invokeDialog(int widgetId, int menuIndex)
		{
			GameThread.invokeLater(() ->
			{
				Static.getClient().processDialog(widgetId, menuIndex);
				return true;
			});
		}*/

	public static void close()
	{
		GameThread.invoke(() -> Static.getClient().runScript(138));
	}

	public static String getText()
	{
		Widget widget = null;

		if (canContinueNPC())
		{
			widget = NPC_TEXT.get();
		}
		else if (canContinuePlayer())
		{
			widget = PLAYER_TEXT.get();
		}

		return widget == null ? "" : widget.getText();
	}

	public static String getName()
	{
		Widget widget = null;

		if (canContinueNPC())
		{
			widget = NPC_CONT.get();
		}
		else if (canContinuePlayer())
		{
			widget = PLAYER_NAME.get();
		}

		return widget == null ? "" : widget.getText();
	}
}
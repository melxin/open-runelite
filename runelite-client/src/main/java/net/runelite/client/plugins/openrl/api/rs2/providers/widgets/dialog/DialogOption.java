package net.runelite.client.plugins.openrl.api.rs2.providers.widgets.dialog;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import javax.annotation.Nullable;

@Getter
@RequiredArgsConstructor
public enum DialogOption
{
	NPC_CONTINUE(15138821, -1),
	PLAYER_CONTINUE(14221317, -1),
	QUEST(12648448, 0),
	ITEM_ONE(12648448, -1),
	ITEM_TWO(12648448, 0),
	CHAT_OPTION_ONE(14352385, 1),
	CHAT_OPTION_TWO(14352385, 2),
	CHAT_OPTION_THREE(14352385, 3),
	CHAT_OPTION_FOUR(14352385, 4),
	CHAT_OPTION_FIVE(14352385, 5),
	PLAIN_CONTINUE(15007746, -1),
	PLAIN_CONTINUE_TWO(720900, -1),
	LEVEL_UP_CONTINUE(15269891, -1);

	private final int widgetUid;
	private final int menuIndex;

	@Nullable
	public static DialogOption of(int widgetUid, int menuIndex)
	{
		return Arrays.stream(values())
			.filter(option -> option.getWidgetUid() == widgetUid && option.getMenuIndex() == menuIndex)
			.findFirst()
			.orElse(null);
	}
}
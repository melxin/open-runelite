package net.runelite.client.plugins.openrl.api.rs2.tabs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.gameval.InterfaceID;

@AllArgsConstructor
@Getter
public enum RS2Tab
{
	COMBAT(InterfaceID.Toplevel.STONE0, InterfaceID.ToplevelOsrsStretch.STONE0, InterfaceID.ToplevelPreEoc.STONE0, 0),
	SKILLS(InterfaceID.Toplevel.STONE1, InterfaceID.ToplevelOsrsStretch.STONE1, InterfaceID.ToplevelPreEoc.STONE1, 1),
	QUESTS(InterfaceID.Toplevel.STONE2, InterfaceID.ToplevelOsrsStretch.STONE2, InterfaceID.ToplevelPreEoc.STONE2, 2),
	INVENTORY(InterfaceID.Toplevel.STONE3, InterfaceID.ToplevelOsrsStretch.STONE3, InterfaceID.ToplevelPreEoc.STONE3, 3),
	EQUIPMENT(InterfaceID.Toplevel.STONE4, InterfaceID.ToplevelOsrsStretch.STONE4, InterfaceID.ToplevelPreEoc.STONE4, 4),
	PRAYER(InterfaceID.Toplevel.STONE5, InterfaceID.ToplevelOsrsStretch.STONE5, InterfaceID.ToplevelPreEoc.STONE5, 5),
	MAGIC(InterfaceID.Toplevel.STONE6, InterfaceID.ToplevelOsrsStretch.STONE6, InterfaceID.ToplevelPreEoc.STONE6, 6),
	CLAN_CHAT(InterfaceID.Toplevel.STONE7, InterfaceID.ToplevelOsrsStretch.STONE7, InterfaceID.ToplevelPreEoc.STONE7, 7),
	ACCOUNT(InterfaceID.Toplevel.STONE8, InterfaceID.ToplevelOsrsStretch.STONE8, InterfaceID.ToplevelPreEoc.STONE8, 8),
	FRIENDS(InterfaceID.Toplevel.STONE9, InterfaceID.ToplevelOsrsStretch.STONE9, InterfaceID.ToplevelPreEoc.STONE9, 9),
	LOG_OUT(InterfaceID.Toplevel.STONE10, InterfaceID.ToplevelOsrsStretch.STONE10, InterfaceID.ToplevelPreEoc.STONE10, 10),
	OPTIONS(InterfaceID.Toplevel.STONE11, InterfaceID.ToplevelOsrsStretch.STONE11, InterfaceID.ToplevelPreEoc.STONE11, 11),
	EMOTES(InterfaceID.Toplevel.STONE12, InterfaceID.ToplevelOsrsStretch.STONE12, InterfaceID.ToplevelPreEoc.STONE12, 12),
	MUSIC(InterfaceID.Toplevel.STONE13, InterfaceID.ToplevelOsrsStretch.STONE13, InterfaceID.ToplevelPreEoc.STONE13, 13);

	private final int fixedLayoutInterfaceId;
	private final int classicLayoutInterfaceId;
	private final int modernLayoutInterfaceId;
	private final int index;
}
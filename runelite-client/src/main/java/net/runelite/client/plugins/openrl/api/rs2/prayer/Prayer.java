package net.runelite.client.plugins.openrl.api.rs2.prayer;

import net.runelite.api.annotations.Component;
import net.runelite.api.annotations.Varbit;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;

/**
 * An enumeration of prayers.
 */
public enum Prayer
{
	/**
	 * Thick Skin (Level 1, Defence).
	 */
	THICK_SKIN(VarbitID.PRAYER_THICKSKIN, InterfaceID.Prayerbook.PRAYER1),
	/**
	 * Burst of Strength (Level 4, Strength).
	 */
	BURST_OF_STRENGTH(VarbitID.PRAYER_BURSTOFSTRENGTH, InterfaceID.Prayerbook.PRAYER2),
	/**
	 * Clarity of Thought (Level 7, Attack).
	 */
	CLARITY_OF_THOUGHT(VarbitID.PRAYER_CLARITYOFTHOUGHT, InterfaceID.Prayerbook.PRAYER3),
	/**
	 * Sharp Eye (Level 8, Ranging).
	 */
	SHARP_EYE(VarbitID.PRAYER_SHARPEYE, InterfaceID.Prayerbook.PRAYER19),
	/**
	 * Mystic Will (Level 9, Magic).
	 */
	MYSTIC_WILL(VarbitID.PRAYER_MYSTICWILL, InterfaceID.Prayerbook.PRAYER22),
	/**
	 * Rock Skin (Level 10, Defence).
	 */
	ROCK_SKIN(VarbitID.PRAYER_ROCKSKIN, InterfaceID.Prayerbook.PRAYER4),
	/**
	 * Superhuman Strength (Level 13, Strength).
	 */
	SUPERHUMAN_STRENGTH(VarbitID.PRAYER_SUPERHUMANSTRENGTH, InterfaceID.Prayerbook.PRAYER5),
	/**
	 * Improved Reflexes (Level 16, Attack).
	 */
	IMPROVED_REFLEXES(VarbitID.PRAYER_IMPROVEDREFLEXES, InterfaceID.Prayerbook.PRAYER6),
	/**
	 * Rapid Restore (Level 19, Stats).
	 */
	RAPID_RESTORE(VarbitID.PRAYER_RAPIDRESTORE, InterfaceID.Prayerbook.PRAYER7),
	/**
	 * Rapid Heal (Level 22, Hitpoints).
	 */
	RAPID_HEAL(VarbitID.PRAYER_RAPIDHEAL, InterfaceID.Prayerbook.PRAYER8),
	/**
	 * Protect Item (Level 25).
	 */
	PROTECT_ITEM(VarbitID.PRAYER_PROTECTITEM, InterfaceID.Prayerbook.PRAYER9),
	/**
	 * Hawk Eye (Level 26, Ranging).
	 */
	HAWK_EYE(VarbitID.PRAYER_HAWKEYE, InterfaceID.Prayerbook.PRAYER20),
	/**
	 * Mystic Lore (Level 27, Magic).
	 */
	MYSTIC_LORE(VarbitID.PRAYER_MYSTICLORE, InterfaceID.Prayerbook.PRAYER23),
	/**
	 * Steel Skin (Level 28, Defence).
	 */
	STEEL_SKIN(VarbitID.PRAYER_STEELSKIN, InterfaceID.Prayerbook.PRAYER10),
	/**
	 * Ultimate Strength (Level 31, Strength).
	 */
	ULTIMATE_STRENGTH(VarbitID.PRAYER_ULTIMATESTRENGTH, InterfaceID.Prayerbook.PRAYER11),
	/**
	 * Incredible Reflexes (Level 34, Attack).
	 */
	INCREDIBLE_REFLEXES(VarbitID.PRAYER_INCREDIBLEREFLEXES, InterfaceID.Prayerbook.PRAYER12),
	/**
	 * Protect from Magic (Level 37).
	 */
	PROTECT_FROM_MAGIC(VarbitID.PRAYER_PROTECTFROMMAGIC, InterfaceID.Prayerbook.PRAYER13),
	/**
	 * Protect from Missiles (Level 40).
	 */
	PROTECT_FROM_MISSILES(VarbitID.PRAYER_PROTECTFROMMISSILES, InterfaceID.Prayerbook.PRAYER14),
	/**
	 * Protect from Melee (Level 43).
	 */
	PROTECT_FROM_MELEE(VarbitID.PRAYER_PROTECTFROMMELEE, InterfaceID.Prayerbook.PRAYER15),
	/**
	 * Eagle Eye (Level 44, Ranging).
	 */
	EAGLE_EYE(VarbitID.PRAYER_EAGLEEYE, InterfaceID.Prayerbook.PRAYER21),
	/**
	 * Mystic Might (Level 45, Magic).
	 */
	MYSTIC_MIGHT(VarbitID.PRAYER_MYSTICMIGHT, InterfaceID.Prayerbook.PRAYER24),
	/**
	 * Retribution (Level 46).
	 */
	RETRIBUTION(VarbitID.PRAYER_RETRIBUTION, InterfaceID.Prayerbook.PRAYER16),
	/**
	 * Redemption (Level 49).
	 */
	REDEMPTION(VarbitID.PRAYER_REDEMPTION, InterfaceID.Prayerbook.PRAYER17),
	/**
	 * Smite (Level 52).
	 */
	SMITE(VarbitID.PRAYER_SMITE, InterfaceID.Prayerbook.PRAYER18),
	/**
	 * Chivalry (Level 60, Defence/Strength/Attack).
	 */
	CHIVALRY(VarbitID.PRAYER_CHIVALRY, InterfaceID.Prayerbook.PRAYER26),
	/**
	 * Deadeye (Level 62, Ranging/Damage/Defence).
	 */
	DEADEYE(VarbitID.PRAYER_DEADEYE, -1),
	/**
	 * Mystic Vigour (Level 63, Magic/Magic Def./Defence).
	 */
	MYSTIC_VIGOUR(VarbitID.PRAYER_MYSTICVIGOUR, -1),
	/**
	 * Piety (Level 70, Defence/Strength/Attack).
	 */
	PIETY(VarbitID.PRAYER_PIETY, InterfaceID.Prayerbook.PRAYER27),
	/**
	 * Preserve (Level 55).
	 */
	PRESERVE(VarbitID.PRAYER_PRESERVE, InterfaceID.Prayerbook.PRAYER29),
	/**
	 * Rigour (Level 74, Ranging/Damage/Defence).
	 */
	RIGOUR(VarbitID.PRAYER_RIGOUR, InterfaceID.Prayerbook.PRAYER25),
	/**
	 * Augury (Level 77, Magic/Magic Def./Defence).
	 */
	AUGURY(VarbitID.PRAYER_AUGURY, InterfaceID.Prayerbook.PRAYER28),

	/**
	 * Ruinous Powers Rejuvenation (Level 60).
	 */
	RP_REJUVENATION(VarbitID.PRAYER_REJUVENATION, -1),
	/**
	 * Ruinous Powers Ancient Strength (Level 61).
	 */
	RP_ANCIENT_STRENGTH(VarbitID.PRAYER_ANCIENT_STRENGTH, -1),
	/**
	 * Ruinous Powers Ancient Sight (Level 62).
	 */
	RP_ANCIENT_SIGHT(VarbitID.PRAYER_ANCIENT_SIGHT, -1),
	/**
	 * Ruinous Powers Ancient Will (Level 63).
	 */
	RP_ANCIENT_WILL(VarbitID.PRAYER_ANCIENT_WILL, -1),
	/**
	 * Ruinous Powers Protect Item (Level 65).
	 */
	RP_PROTECT_ITEM(VarbitID.PRAYER_PROTECT_ITEM_R, -1),
	/**
	 * Ruinous Powers Ruinous Grace (Level 66).
	 */
	RP_RUINOUS_GRACE(VarbitID.PRAYER_RUINOUS_GRACE, -1),
	/**
	 * Ruinous Powers Dampen Magic (Level 67).
	 */
	RP_DAMPEN_MAGIC(VarbitID.PRAYER_DAMPEN_MAGIC, -1),
	/**
	 * Ruinous Powers Dampen Ranged (Level 69).
	 */
	RP_DAMPEN_RANGED(VarbitID.PRAYER_DAMPEN_RANGED, -1),
	/**
	 * Ruinous Powers Dampen Melee (Level 71).
	 */
	RP_DAMPEN_MELEE(VarbitID.PRAYER_DAMPEN_MELEE, -1),
	/**
	 * Ruinous Powers Trinitas (Level 72).
	 */
	RP_TRINITAS(VarbitID.PRAYER_TRINITAS, -1),
	/**
	 * Ruinous Powers Berserker (Level 74).
	 */
	RP_BERSERKER(VarbitID.PRAYER_BERSERKER, -1),
	/**
	 * Ruinous Powers Purge (Level 75).
	 */
	RP_PURGE(VarbitID.PRAYER_PURGE, -1),
	/**
	 * Ruinous Powers Metabolise (Level 77).
	 */
	RP_METABOLISE(VarbitID.PRAYER_METABOLISE, -1),
	/**
	 * Ruinous Powers Rebuke (Level 78).
	 */
	RP_REBUKE(VarbitID.PRAYER_REBUKE, -1),
	/**
	 * Ruinous Powers Vindication (Level 80).
	 */
	RP_VINDICATION(VarbitID.PRAYER_VINDICATION, -1),
	/**
	 * Ruinous Powers Decimate (Level 82).
	 */
	RP_DECIMATE(VarbitID.PRAYER_DECIMATE, -1),
	/**
	 * Ruinous Powers Annihilate (Level 84).
	 */
	RP_ANNIHILATE(VarbitID.PRAYER_ANNIHILATE, -1),
	/**
	 * Ruinous Powers Vaporise (Level 86).
	 */
	RP_VAPORISE(VarbitID.PRAYER_VAPORISE, -1),
	/**
	 * Ruinous Powers Fumus' Vow (Level 87).
	 */
	RP_FUMUS_VOW(VarbitID.PRAYER_FUMUS_VOW, -1),
	/**
	 * Ruinous Powers Umbra's Vow (Level 88).
	 */
	RP_UMBRA_VOW(VarbitID.PRAYER_UMBRAS_VOW, -1),
	/**
	 * Ruinous Powers Cruor's Vow (Level 89).
	 */
	RP_CRUORS_VOW(VarbitID.PRAYER_CRUORS_VOW, -1),
	/**
	 * Ruinous Powers Glacies' Vow (Level 90).
	 */
	RP_GLACIES_VOW(VarbitID.PRAYER_GLACIES_VOW, -1),
	/**
	 * Ruinous Powers Wrath (Level 91).
	 */
	RP_WRATH(VarbitID.PRAYER_WRATH, -1),
	/**
	 * Ruinous Powers Intensify (Level 92).
	 */
	RP_INTENSIFY(VarbitID.PRAYER_INTENSIFY, -1),
	;

	private final int varbit;
	private final int interfaceId;

	Prayer(@Varbit int varbit, @Component int interfaceId)
	{
		this.varbit = varbit;
		this.interfaceId = interfaceId;
	}

	/**
	 * Gets the varbit that stores whether the prayer is active or not.
	 *
	 * @return the prayer active varbit
	 */
	@Varbit
	public int getVarbit()
	{
		return varbit;
	}

	@Component
	public int getInterfaceId()
	{
		return interfaceId;
	}
}

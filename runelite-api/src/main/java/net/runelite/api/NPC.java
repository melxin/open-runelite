/*
 * Copyright (c) 2016-2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.api;

import javax.annotation.Nullable;

/**
 * Represents a non-player character in the game.
 */
public interface NPC extends Actor
{
	/**
	 * Gets the ID of the NPC.
	 *
	 * @return the ID of the NPC
	 * @see net.runelite.api.gameval.NpcID
	 */
	int getId();

	@Override
	String getName();

	@Override
	int getCombatLevel();

	/**
	 * Gets the index position of this NPC in the clients cached
	 * NPC array.
	 *
	 * @return the NPC index
	 */
	int getIndex();

	/**
	 * Gets the composition of this NPC.
	 *
	 * @return the composition
	 */
	NPCComposition getComposition();

	/**
	 * Get the composition for this NPC and transform it if required
	 *
	 * @return the transformed NPC
	 */
	@Nullable
	NPCComposition getTransformedComposition();

	@Nullable
	NpcOverrides getModelOverrides();

	@Nullable
	NpcOverrides getChatheadOverrides();

	/**
	 * Get the array of overhead icon archive ids.
	 * Used in conjunction with {@link #getOverheadSpriteIds()}
	 * to determine which icons are being rendered overhead.
	 *
	 * @return A sparse array of archive ids. Values of -1 are not used.
	 * @see #getOverheadSpriteIds()
	 */
	@Nullable
	int[] getOverheadArchiveIds();

	/**
	 * Get the array of overhead icon sprite indexes.
	 * Used in conjunction with {@link #getOverheadArchiveIds()}
	 * to determine which icons are being rendered overhead.
	 *
	 * @return A sparse array of archive ids. Values of -1 are not used.
	 * @see #getOverheadArchiveIds()
	 */
	@Nullable
	short[] getOverheadSpriteIds();

	/**
	 * Gets the overhead icon for this NPC.
	 * This is a convenience method that maps the overhead sprite IDs to HeadIcon.
	 *
	 * @return the overhead icon, or null if none
	 */
	@Nullable
	default HeadIcon getOverheadIcon()
	{
		short[] spriteIds = getOverheadSpriteIds();
		if (spriteIds == null || spriteIds.length == 0)
		{
			return null;
		}
		// Map sprite IDs to HeadIcon based on prayer icon sprite IDs
		for (short spriteId : spriteIds)
		{
			if (spriteId == -1)
			{
				continue;
			}
			// These sprite IDs correspond to prayer icon sprites
			// May need adjustment based on actual sprite IDs
			switch (spriteId)
			{
				case 0: // Protect from melee
					return HeadIcon.MELEE;
				case 1: // Protect from ranged
					return HeadIcon.RANGED;
				case 2: // Protect from magic
					return HeadIcon.MAGIC;
				case 3: // Retribution
					return HeadIcon.RETRIBUTION;
				case 4: // Smite
					return HeadIcon.SMITE;
				case 5: // Redemption
					return HeadIcon.REDEMPTION;
			}
		}
		return null;
	}
}

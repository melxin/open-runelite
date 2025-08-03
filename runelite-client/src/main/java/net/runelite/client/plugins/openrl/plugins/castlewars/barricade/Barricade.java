/*
 * Copyright (c) 2022, Melxin <https://github.com/melxin/>
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
package net.runelite.client.plugins.openrl.plugins.castlewars.barricade;

import lombok.Getter;
import lombok.Setter;
import java.time.Duration;
import java.time.Instant;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

/**
 * Wrapper class for a NPC that represents a barricade.
 */
public class Barricade
{
	// A lit barricade stays lit for 7 seconds before collapsing.
	static final Duration TIND_TIME = Duration.ofSeconds(7);

	// Time in milliseconds when the barricade was lit.
	@Getter
	private Instant litOn;

	// State of the barricade.
	@Getter
	@Setter
	public State state;

	// ID of the npc this is representing
	@Getter
	private int npcId;

	// WorldLocation
	@Getter
	private WorldPoint worldLocation;

	// Npc
	@Getter
	private NPC npc;

	/**
	 * The states a barricade can be in.
	 */
	public enum State
	{
		// Lit barricade.
		LIT_BARRICADE,

		// Unlit barricade.
		UNLIT_BARRICADE
	}

	/**
	 * Constructor for a barricade npc
	 *
	 * @param npc The npc.
	 */
	public Barricade(NPC npc)
	{
		this.state = State.LIT_BARRICADE;
		this.litOn = Instant.now();
		this.npcId = npc.getId();
		this.worldLocation = npc.getWorldLocation();
		this.npc = npc;
	}

	/**
	 * Calculates how much time is left before the barricade is collapsing.
	 *
	 * @return Value between 0 and 1. 0 means the barricade was lit moments ago.
	 * 1 is a barricade that's about to collapse.
	 */
	public double getTindTimeRelative()
	{
		Duration duration = Duration.between(litOn, Instant.now());
		return duration.compareTo(TIND_TIME) < 0 ? (double) duration.toMillis() / TIND_TIME.toMillis() : 1;
	}

	/**
	 * Resets the time value when the barricade was placed.
	 */
	public void resetTimer()
	{
		litOn = Instant.now();
	}
}
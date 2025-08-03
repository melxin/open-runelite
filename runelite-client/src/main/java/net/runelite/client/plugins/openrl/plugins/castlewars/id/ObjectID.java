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
package net.runelite.client.plugins.openrl.plugins.castlewars.id;

import com.google.common.collect.Sets;
import java.util.Set;

public class ObjectID
{
	// GameObject IDs
	public static final int SARADOMIN_STANDARD = 4900; // Saradomin standard
	public static final int ZAMORAK_STANDARD = 4901; // Zamorak standard
	public static final int ROCKS_FULL = 4437; // The underground/tunnel rocks full state
	public static final int ROCKS_HALF = 4438; // The underground/tunnel rocks half state

	// WallObject IDs
	public static final int SARADOMIN_SMALL_DOOR_CLOSED = 4465;
	public static final int SARADOMIN_SMALL_DOOR_OPEN = 4466;

	public static final int SARADOMIN_BIG_DOOR_CLOSED_LEFT = 4423;
	public static final int SARADOMIN_BIG_DOOR_CLOSED_RIGHT = 4424;

	public static final int SARADOMIN_BIG_DOOR_OPEN_LEFT = 4425;
	public static final int SARADOMIN_BIG_DOOR_OPEN_RIGHT = 4426;

	public static final int ZAMORAK_SMALL_DOOR_CLOSED = 4467;
	public static final int ZAMORAK_SMALL_DOOR_OPEN = 4468;

	public static final int ZAMORAK_BIG_DOOR_CLOSED_LEFT = 4427;
	public static final int ZAMORAK_BIG_DOOR_CLOSED_RIGHT = 4428;

	public static final int ZAMORAK_BIG_DOOR_OPEN_LEFT = 4429;
	public static final int ZAMORAK_BIG_DOOR_OPEN_RIGHT = 4430;

	public static final Set<Integer> wallObject_Ids_DOORS = Sets.newHashSet(
		SARADOMIN_SMALL_DOOR_CLOSED,
		SARADOMIN_SMALL_DOOR_OPEN,
		SARADOMIN_BIG_DOOR_CLOSED_LEFT,
		SARADOMIN_BIG_DOOR_CLOSED_RIGHT,
		SARADOMIN_BIG_DOOR_OPEN_LEFT,
		SARADOMIN_BIG_DOOR_OPEN_RIGHT,

		ZAMORAK_SMALL_DOOR_CLOSED,
		ZAMORAK_SMALL_DOOR_OPEN,
		ZAMORAK_BIG_DOOR_CLOSED_LEFT,
		ZAMORAK_BIG_DOOR_CLOSED_RIGHT,
		ZAMORAK_BIG_DOOR_OPEN_LEFT,
		ZAMORAK_BIG_DOOR_OPEN_RIGHT
	);

	public static final int CAVE = 4448;

	// Decorative Object IDs
	public static final int TAP = 4482;
	public static final Set<Integer> decorativeObject_Ids = Sets.newHashSet(TAP);
}
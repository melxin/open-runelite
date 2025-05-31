/*
 * Copyright (c) 2025, Melxin <https://github.com/melxin>
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
package net.runelite.client.plugins.openrl.api.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.client.plugins.openrl.Static;

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

	public MenuEntry getMenuEntry()
	{
		return Static.getClientThread().runOnClientThreadOptional(() -> Static.getClient().getMenu().createMenuEntry(-1)
			.setParam0(param0)
			.setParam1(param1)
			.setType(menuAction)
			.setIdentifier(index)
			.setItemId(itemId)
			.setWorldViewId(worldViewId)
			.setOption(option)
			.setTarget(target)
			.setForceLeftClick(false)
			.onClick(null))
			.orElse(null);
	}
}
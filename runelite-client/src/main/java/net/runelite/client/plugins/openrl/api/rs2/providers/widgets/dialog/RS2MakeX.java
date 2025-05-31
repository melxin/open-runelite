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
package net.runelite.client.plugins.openrl.api.rs2.providers.widgets.dialog;

import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarClientID;
import net.runelite.client.plugins.openrl.api.game.Vars;
import net.runelite.client.plugins.openrl.api.input.Keyboard;
import net.runelite.client.plugins.openrl.api.rs2.providers.query.RS2WidgetQuery;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Widget;
import net.runelite.client.util.Text;

@Slf4j
public class RS2MakeX
{
	private static final RS2WidgetQuery widgetQuery = RS2WidgetQuery.query();

	public static boolean isOpen()
	{
		final RS2Widget widget = widgetQuery.getWidget(InterfaceID.Skillmulti.UNIVERSE);
		return widget != null && widget.isVisible();
	}

	public static int getQuantity()
	{
		return Vars.getVarcInt(VarClientID.SKILLMULTI_QUANTITY);
	}

	public static void setQuantity(int quantity)
	{
		final int currentQuantity = getQuantity();
		if (currentQuantity == quantity)
		{
			return;
		}
		Vars.setVarcInt(VarClientID.SKILLMULTI_QUANTITY, quantity);
	}

	public static void chooseNumericOption(int option)
	{
		Keyboard.type(option);
	}

	public static void chooseItemOption(int itemId)
	{
		final RS2Widget option = getFirstOption(x -> x.getItemId() == itemId);
		if (option == null)
		{
			log.warn("Invalid option!");
			return;
		}
		option.interact("Make");
	}

	public static void chooseItemOption(String itemName)
	{
		final RS2Widget option = getFirstOption(x -> Text.removeTags(x.getName()).contains(itemName));
		if (option == null)
		{
			log.warn("Invalid option!");
			return;
		}
		option.interact("Make");
	}

	public static RS2Widget getFirstOption(Predicate<RS2Widget> filter)
	{
		final List<RS2Widget> options = getOptions(filter);
		return options.isEmpty() ? null : options.get(0);
	}

	public static List<RS2Widget> getOptions()
	{
		return getOptions(x -> true);
	}

	public static List<RS2Widget> getOptions(Predicate<RS2Widget> filter)
	{
		final List<RS2Widget> options = new ArrayList<>();
		for (int i = InterfaceID.Skillmulti.A; i < InterfaceID.Skillmulti.R; i++)
		{
			final RS2Widget option = widgetQuery.getWidget(i);
			if (option == null || option.isSelfHidden())
			{
				continue;
			}

			final RS2Widget[] children = option.getChildren();
			if (children == null)
			{
				continue;
			}

			for (RS2Widget child : children)
			{
				final int itemId = child.getItemId();
				if (itemId == -1 || itemId == 6512)
				{
					continue;
				}

				if (filter.test(option) || filter.test(child))
				{
					options.add(option);
				}
			}
		}
		return options;
	}
}
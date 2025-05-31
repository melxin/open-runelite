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
package net.runelite.client.plugins.openrl.api.reflection;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import net.runelite.client.plugins.openrl.Static;

@Slf4j
public class ReflectionVanilla
{
	/**
	 * Find constants in rl injected-client -> client.class -> hopToWorld/menuAction/openWorldHopper
	 *
	 * LAST UPDATED REVISION = 230
	 */
	private static final String menuActionObfClassName = "ce";
	private static final String menuActionObfMethodName = "la";
	private static final int menuActionGarbageValue = -1759043300;

	private static Method menuActionVanilla = null;

	@SneakyThrows
	public static void invokeMenuAction(int param0, int param1, int opcode, int identifier, int itemId, int worldViewId, String option, String target, int canvasX, int canvasY)
	{
		log.info("[invokeMenuActionVanilla] param0: {}, param1: {}, opcode: {}, identifier: {}, itemId: {}, worldViewId: {}, option: {}, target: {}, canvasX: {}, canvasY: {}", param0, param1, opcode, identifier, itemId, worldViewId, option, target, canvasX, canvasY);
		if (menuActionVanilla == null)
		{
			try
			{
				final Class<?> menuActionClazz = Class.forName(menuActionObfClassName);
				menuActionVanilla = Arrays.asList(menuActionClazz.getDeclaredMethods())
					.stream()
					.filter(m -> Modifier.isStatic(m.getModifiers()) && m.getName().equals(menuActionObfMethodName))
					.findFirst()
					.orElse(null);
			}
			catch (ClassNotFoundException e)
			{
				log.error("Menu action vanilla class was not found", e);
			}

			if (menuActionVanilla == null)
			{
				log.error("Menu action vanilla is broken..");
				return;
			}
		}

		menuActionVanilla.setAccessible(true);
		Static.getClientThread().runOnClientThreadOptional(() -> menuActionVanilla.invoke(null, param0, param1, opcode, identifier, itemId, worldViewId, option, target, canvasX, canvasY, menuActionGarbageValue));
		menuActionVanilla.setAccessible(false);
	}
}

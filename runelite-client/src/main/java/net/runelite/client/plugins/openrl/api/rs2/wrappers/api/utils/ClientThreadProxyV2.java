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
package net.runelite.client.plugins.openrl.api.rs2.wrappers.api.utils;

import lombok.NonNull;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.runelite.client.plugins.openrl.Static;

public class ClientThreadProxyV2
{
	private static final Set<String> methodsToProxy = Set.of(
		"Projectile#getSourceActor",
		"Projectile#getTargetActor",
		"Client#addChatMessage",
		"Client#applyTransformations",
		"Client#changeMemoryMode",
		"Client#changeWorld",
		"Client#closeInterface",
		"Client#createItemSprite",
		"Client#createMenuEntry",
		"Client#createProjectile",
		"Client#getAccountType",
		"Client#getCameraFocusEntity",
		"Client#getDBRowConfig",
		"Client#getDBRowsByValue",
		"Client#getDBTableField",
		"Client#getDBTableRows",
		"Client#getEnum",
		"Client#getFollower",
		"Client#getHintArrowNpc",
		"Client#getHintArrowPlayer",
		"Client#getItemContainer",
		"Client#getItemDefinition",
		"Client#getNpcDefinition",
		"Client#getObjectDefinition",
		"Client#getServerVarbitValue",
		"Client#getSprites",
		"Client#getStructComposition",
		"Client#getStructCompositionCache",
		"Client#getVar",
		"Client#getVarbit",
		"Client#getVarbitValue",
		"Client#hopToWorld",
		"Client#isPrayerActive",
		"Client#isRuneLiteObjectRegistered",
		"Client#loadAnimation",
		"Client#loadModel",
		"Client#loadModelData",
		"Client#menuAction",
		"Client#openInterface",
		"Client#openWorldHopper",
		"Client#playSoundEffect",
		"Client#queueChangedVarp",
		"Client#registerRuneLiteObject",
		"Client#removeRuneLiteObject",
		"Client#runScript",
		"Client#setDrawCallbacks",
		"Client#setGameState",
		"Client#setLoginScreen",
		"Client#setMenuEntries",
		"Client#setVarbit",
		"Client#setVarbitValue",
		"Client#setVarcIntValue",
		"Client#setVarcStrValue",
		"Actor#getCanvasImageLocation",
		"Actor#getCanvasSpriteLocation",
		"Actor#getCanvasTextLocation",
		"Actor#getHealthRatio",
		"Actor#getHealthScale",
		"Actor#getInteracting",
		"Actor#getWorldArea",
		"Actor#getWorldLocation",
		"Actor#getWorldView",
		"NPC#getCombatLevel",
		"NPC#getName",
		"WorldView#createProjectile",
		"WorldView#getYellowClickAction",
		"GroundObject#getOpOverride",
		"GroundObject#isOpShown",
		"ItemLayer#getOpOverride",
		"ItemLayer#isOpShown",
		"WallObject#getOpOverride",
		"WallObject#isOpShown",
		"DecorativeObject#getOpOverride",
		"DecorativeObject#isOpShown",
		"GameObject#getOpOverride",
		"GameObject#isOpShown",
		"ClanSettings#titleForRank",
		"Widget#createChild",
		"Widget#getNestedChildren",
		"Widget#getParent",
		"Widget#getParentId",
		"Widget#isHidden",
		"Widget#revalidate",
		"Widget#revalidateScroll",
		"MenuEntry#getActor",
		"MenuEntry#getItemOp",
		"MenuEntry#getNpc",
		"MenuEntry#getPlayer",
		"ParamHolder#getIntValue",
		"ParamHolder#getLongValue",
		"ParamHolder#getStringValue",
		"ParamHolder#setValue",
		"Menu#createMenuEntry",
		"Menu#removeMenuEntry",
		"Menu#setMenuEntries",
		"IndexedObjectSet#byIndex"
	);

	private static final Map<Class<?>, Map<Method, MethodHandle>> unboundHandleCache = new ConcurrentHashMap<>();
	private static final Map<Object, Map<Method, MethodHandle>> boundHandleCache = new WeakHashMap<>();
	private static final Map<Object, Object> proxyCache = new WeakHashMap<>();

	@SuppressWarnings("unchecked")
	public static <T> T create(@NonNull T target, @NonNull Class<T> interfaceClass)
	{
		synchronized (proxyCache)
		{
			if (proxyCache.containsKey(target))
			{
				return (T) proxyCache.get(target);
			}
		}

		final Map<Method, MethodHandle> unboundHandleCache = ClientThreadProxyV2.unboundHandleCache.computeIfAbsent(interfaceClass, cls ->
		{
			final Map<Method, MethodHandle> unboundHandles = new ConcurrentHashMap<>();
			final MethodHandles.Lookup lookup = MethodHandles.lookup();
			for (Method method : cls.getMethods())
			{
				try
				{
					final MethodHandle unboundHandle = lookup.unreflect(method);
					unboundHandles.put(method, unboundHandle);
				}
				catch (IllegalAccessException e)
				{
					throw new RuntimeException("Unable to unreflect method: " + method, e);
				}
			}
			return unboundHandles;
		});

		final Map<Method, MethodHandle> boundHandles = new ConcurrentHashMap<>();
		for (Map.Entry<Method, MethodHandle> entry : unboundHandleCache.entrySet())
		{
			try
			{
				boundHandles.put(entry.getKey(), entry.getValue().bindTo(target));
			}
			catch (Throwable e)
			{
				throw new RuntimeException("Failed to bind handle for method: " + entry.getKey(), e);
			}
		}
		synchronized (boundHandleCache)
		{
			boundHandleCache.put(target, boundHandles);
		}

		final InvocationHandler handler = (proxy, method, args) ->
		{
			Map<Method, MethodHandle> handles;
			synchronized (boundHandleCache)
			{
				handles = boundHandleCache.get(target);
			}
			if (handles == null)
			{
				throw new RuntimeException("Bound handles not found for target: " + target);
			}
			final MethodHandle handle = handles.get(method);
			if (handle == null)
			{
				throw new RuntimeException("Handle not cached for method: " + method);
			}

			if (!methodsToProxy.contains(interfaceClass.getSimpleName() + "#" + method.getName()))
			{
				try
				{
					return handle.invokeWithArguments(args);
				}
				catch (Throwable e)
				{
					throw new RuntimeException(e);
				}
			}

			return Static.getClientThread().invoke(() ->
			{
				try
				{
					return handle.invokeWithArguments(args);
				}
				catch (Throwable e)
				{
					throw new RuntimeException(e);
				}
			});
		};

		final T proxyInstance = (T) Proxy.newProxyInstance(
			interfaceClass.getClassLoader(),
			new Class<?>[]{interfaceClass},
			handler
		);

		synchronized (proxyCache)
		{
			proxyCache.put(target, proxyInstance);
		}

		return proxyInstance;
	}
}
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
import java.util.concurrent.ConcurrentHashMap;
import net.runelite.client.plugins.openrl.Static;

public class ClientThreadProxy
{
	@SuppressWarnings("unchecked")
	public static <T> T create(@NonNull T target, @NonNull Class<T> interfaceClass)
	{
		final Map<Method, MethodHandle> handleCache = new ConcurrentHashMap<>();
		final MethodHandles.Lookup lookup = MethodHandles.lookup();

		for (Method method : interfaceClass.getMethods())
		{
			try
			{
				final MethodHandle handle = lookup.unreflect(method).bindTo(target);
				handleCache.put(method, handle);
			}
			catch (IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
		}

		final InvocationHandler handler = (proxy, method, args) ->
		{
			return Static.getClientThread().invoke(() ->
			{
				try
				{
					final MethodHandle handle = handleCache.get(method);
					if (handle == null)
					{
						throw new RuntimeException("Method not cached: " + method);
					}
					return handle.invokeWithArguments(args);
				}
				catch (Throwable e)
				{
					throw new RuntimeException(e);
				}
			});
		};

		return (T) Proxy.newProxyInstance(
			interfaceClass.getClassLoader(),
			new Class<?>[]{interfaceClass},
			handler
		);
	}

	/*@SuppressWarnings("unchecked")
	public static <T> T create(@NonNull T target, @NonNull Class<T> interfaceClass)
	{
		final InvocationHandler handler = (proxy, method, args) ->
		{
			return Static.getClientThread().invoke(() ->
			{
				try
				{
					return method.invoke(target, args);
				}
				catch (InvocationTargetException | IllegalAccessException e)
				{
					throw new RuntimeException(e);
				}
			});
		};

		return (T) Proxy.newProxyInstance(
			interfaceClass.getClassLoader(),
			new Class<?>[]{interfaceClass},
			handler
		);
	}*/
}
/*
 * Copyright (c) 2026, Melxin <https://github.com/melxin>
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

import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.runelite.client.plugins.openrl.Static;

@Slf4j
public class JagexAccountReflection
{
	/*public static String getAccessToken()
	{
		return getJXProperty("JX_ACCESS_TOKEN");
	}

	public static String getRefreshToken()
	{
		return getJXProperty("JX_REFRESH_TOKEN");
	}*/

	public static String getSessionId()
	{
		return getJXProperty("JX_SESSION_ID");
	}

	public static String getCharacterId()
	{
		return getJXProperty("JX_CHARACTER_ID");
	}

	public static String getDisplayName()
	{
		return getJXProperty("JX_DISPLAY_NAME");
	}

	public static String getJXProperty(String key)
	{
		return getRLJXProperty(key);
	}

	private static Method getRLJXProperty;

	private static String getRLJXProperty(String key)
	{
		if (getRLJXProperty == null)
		{
			try
			{
				final Class<?> clientClazz = Static.getClient().getClass();
				final ClassReader classReader = new ClassReader(clientClazz.getName());
				final ClassNode classNode = new ClassNode(Opcodes.ASM9);
				classReader.accept(classNode, ClassReader.SKIP_FRAMES);
				for (MethodNode method : classNode.methods)
				{
					if (method.name.equals("getLauncherDisplayName") && method.desc.equals("()Ljava/lang/String;"))
					{
						final InsnList ins = method.instructions;
						final AbstractInsnNode ain4 = ins.get(4);
						if (ain4.getOpcode() == Opcodes.INVOKEVIRTUAL)
						{
							final MethodInsnNode minsn = (MethodInsnNode) ain4;
							getRLJXProperty = Class.forName(minsn.owner).getDeclaredMethod(minsn.name, String.class);
							break;
						}
					}
				}
			}
			catch (IOException | ClassNotFoundException | NoSuchMethodException e)
			{
				log.error("failed to resolve getJXProperty", e);
			}
		}

		if (getRLJXProperty == null)
		{
			log.error("getJXProperty is broken!");
			return null;
		}

		getRLJXProperty.setAccessible(true);
		try
		{
			return (String) getRLJXProperty.invoke(Static.getClient(), key);
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			log.error("Failed to get JXProperty: {}", key, e);
		}
		getRLJXProperty.setAccessible(false);

		return null;
	}
}
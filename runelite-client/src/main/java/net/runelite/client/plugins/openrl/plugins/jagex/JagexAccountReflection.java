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
package net.runelite.client.plugins.openrl.plugins.jagex;

import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import net.runelite.client.plugins.openrl.Static;

@Slf4j
public class JagexAccountReflection
{
	private static Field JX_ACCESS_TOKEN;
	private static Field JX_REFRESH_TOKEN;
	private static Field JX_SESSION_ID;
	private static Field JX_CHARACTER_ID;
	private static Method JX_DISPLAY_NAME;
	private static Object JX_DISPLAY_NAME_GARBAGE_VALUE;

	public static void setCredentials(String sessionId, String characterId, String displayName)
	{
		setCredentials("", "", sessionId, characterId, displayName);
	}

	public static void setCredentials(String accessToken, String refreshToken, String sessionId, String characterId, String displayName)
	{
		if (JX_ACCESS_TOKEN == null || JX_REFRESH_TOKEN == null || JX_SESSION_ID == null || JX_CHARACTER_ID == null || JX_DISPLAY_NAME == null)
		{
			try
			{
				final Class<?> clientClazz = Static.getClient().getClass();
				final ClassReader classReader = new ClassReader(clientClazz.getName());
				final ClassNode classNode = new ClassNode(Opcodes.ASM9);
				classReader.accept(classNode, ClassReader.SKIP_FRAMES);

				outer:
				for (MethodNode method : classNode.methods)
				{
					final InsnList ins = method.instructions;
					for (int i = 0; i < ins.size(); i++)
					{
						final AbstractInsnNode ain = ins.get(i);
						if (ain instanceof LdcInsnNode && ((LdcInsnNode) ain).cst instanceof String && ((String) ((LdcInsnNode) ain).cst).startsWith("JX_"))
						{
							final String str = (String) ((LdcInsnNode) ain).cst;
							final AbstractInsnNode ain2 = ins.get(i + 2);

							if (ain2.getOpcode() == Opcodes.PUTSTATIC && ain2 instanceof FieldInsnNode)
							{
								final FieldInsnNode fin = (FieldInsnNode) ain2;

								if (str.equals("JX_ACCESS_TOKEN"))
								{
									JX_ACCESS_TOKEN = Class.forName(fin.owner).getDeclaredField(fin.name);
								}
								else if (str.equals("JX_REFRESH_TOKEN"))
								{
									JX_REFRESH_TOKEN = Class.forName(fin.owner).getDeclaredField(fin.name);
								}
								else if (str.equals("JX_SESSION_ID"))
								{
									JX_SESSION_ID = Class.forName(fin.owner).getDeclaredField(fin.name);
								}
								else if (str.equals("JX_CHARACTER_ID"))
								{
									JX_CHARACTER_ID = Class.forName(fin.owner).getDeclaredField(fin.name);
								}
							}
							else if (str.equals("JX_DISPLAY_NAME"))
							{
								final AbstractInsnNode ain3 = ins.get(i + 3);
								if (ain2 instanceof LdcInsnNode && ain3.getOpcode() == Opcodes.INVOKESTATIC)
								{
									final MethodInsnNode minsn = (MethodInsnNode) ain3;
									JX_DISPLAY_NAME = Arrays.stream(Class.forName(minsn.owner).getDeclaredMethods()).filter(x -> x.getName().equals(minsn.name)).findFirst().orElse(null);
									JX_DISPLAY_NAME_GARBAGE_VALUE = ((LdcInsnNode) ain2).cst;
									break outer;
								}
							}
						}
					}
				}
			}
			catch (IOException | ClassNotFoundException | NoSuchFieldException e)
			{
				log.error("Failed to resolve JX_ credentials", e);
			}
		}

		if (JX_ACCESS_TOKEN == null || JX_REFRESH_TOKEN == null || JX_SESSION_ID == null || JX_CHARACTER_ID == null || JX_DISPLAY_NAME == null)
		{
			log.error("setCredentials method is broken!");
			return;
		}

		JX_ACCESS_TOKEN.setAccessible(true);
		JX_REFRESH_TOKEN.setAccessible(true);
		JX_SESSION_ID.setAccessible(true);
		JX_CHARACTER_ID.setAccessible(true);
		JX_DISPLAY_NAME.setAccessible(true);

		try
		{
			JX_ACCESS_TOKEN.set(null, accessToken);
			JX_REFRESH_TOKEN.set(null, refreshToken);
			JX_SESSION_ID.set(null, sessionId);
			JX_CHARACTER_ID.set(null, characterId);
			JX_DISPLAY_NAME.invoke(null, displayName, JX_DISPLAY_NAME_GARBAGE_VALUE);
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			log.error("Failed to set credentials", e);
		}

		JX_ACCESS_TOKEN.setAccessible(false);
		JX_REFRESH_TOKEN.setAccessible(false);
		JX_SESSION_ID.setAccessible(false);
		JX_CHARACTER_ID.setAccessible(false);
		JX_DISPLAY_NAME.setAccessible(false);
	}

	private static Method setLoginIndex;

	public static void setLoginIndex(int idx)
	{
		if (setLoginIndex == null)
		{
			try
			{
				final Class<?> clientClazz = Static.getClient().getClass();
				final ClassReader classReader = new ClassReader(clientClazz.getName());
				final ClassNode classNode = new ClassNode(Opcodes.ASM9);
				classReader.accept(classNode, ClassReader.SKIP_FRAMES);

				final MethodNode getLoginIndexMethod = classNode.methods.stream().filter(x -> x.name.equals("getLoginIndex")).findFirst().orElse(null);
				if (getLoginIndexMethod == null)
				{
					return;
				}

				final InsnList ins = getLoginIndexMethod.instructions;
				final AbstractInsnNode ain = ins.get(0);
				if (ain != null && ain.getOpcode() == Opcodes.GETSTATIC && ain instanceof FieldInsnNode)
				{
					setLoginIndex = Arrays.stream(Class.forName(((FieldInsnNode) ain).owner).getDeclaredMethods())
						.filter(method -> method.getReturnType() == void.class &&
							method.getParameterCount() == 1 &&
							method.getParameterTypes()[0] == int.class).findFirst().orElse(null);
				}
			}
			catch (IOException | ClassNotFoundException e)
			{
				log.error("Failed to resolve setLoginIndex", e);
			}
		}

		if (setLoginIndex == null)
		{
			log.error("setLoginIndex method is broken!");
			return;
		}

		setLoginIndex.setAccessible(true);
		try
		{
			setLoginIndex.invoke(null, idx);
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			log.error("Failed to invoke setLoginIndex", e);
		}
		setLoginIndex.setAccessible(false);
	}

	private static Method clientReload;
	private static Object clientReloadMethodGarbageValue;

	public static void clientReload()
	{
		if (clientReload == null || clientReloadMethodGarbageValue == null)
		{
			try
			{
				final Class<?> clientClazz = Static.getClient().getClass();
				final ClassReader classReader = new ClassReader(clientClazz.getName());
				final ClassNode classNode = new ClassNode(Opcodes.ASM9);
				classReader.accept(classNode, ClassReader.SKIP_FRAMES);

				outer:
				for (MethodNode method : classNode.methods)
				{
					boolean found = false;

					final InsnList ins = method.instructions;
					for (AbstractInsnNode ain : ins)
					{
						if (ain instanceof LdcInsnNode && ((LdcInsnNode) ain).cst.equals("clientreload"))
						{
							found = true;
							continue;
						}

						if (found)
						{
							if ((ain instanceof LdcInsnNode || ain instanceof IntInsnNode) && ain.getNext().getOpcode() == Opcodes.INVOKESTATIC && ain.getNext() instanceof MethodInsnNode)
							{
								if (ain instanceof LdcInsnNode)
								{
									clientReloadMethodGarbageValue = ((LdcInsnNode) ain).cst;
								}
								else if (ain.getOpcode() == Opcodes.BIPUSH)
								{
									clientReloadMethodGarbageValue = ((byte) ((IntInsnNode) ain).operand);
								}
								else if (ain.getOpcode() == Opcodes.SIPUSH)
								{
									clientReloadMethodGarbageValue = ((short) ((IntInsnNode) ain).operand);
								}

								final MethodInsnNode methodInsn = (MethodInsnNode) ain.getNext();
								clientReload = Arrays.stream(Class.forName(methodInsn.owner).getDeclaredMethods()).filter(x -> x.getName().equals(methodInsn.name) && x.getParameterCount() == 1).findFirst().orElse(null);
								break outer;
							}
						}
					}
				}
			}
			catch (IOException | ClassNotFoundException e)
			{
				log.error("Failed to resolve clientReload", e);
			}
		}

		if (clientReload == null || clientReloadMethodGarbageValue == null)
		{
			log.error("clientReload method is broken!");
			return;
		}

		clientReload.setAccessible(true);
		try
		{
			clientReload.invoke(null, clientReloadMethodGarbageValue);
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			log.error("Failed to invoke client reload", e);
		}
		clientReload.setAccessible(false);
	}
}
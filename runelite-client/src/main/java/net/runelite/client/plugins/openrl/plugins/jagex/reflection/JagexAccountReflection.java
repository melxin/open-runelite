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
package net.runelite.client.plugins.openrl.plugins.jagex.reflection;

import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.utils.ASMUtils;

@Slf4j
public class JagexAccountReflection
{
	private static Method JX_ACCESS_TOKEN;
	private static Method JX_REFRESH_TOKEN;
	private static Method JX_SESSION_ID;
	private static Method JX_CHARACTER_ID;
	private static Field JX_DISPLAY_NAME;

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

							if (ain2.getOpcode() == Opcodes.INVOKESTATIC && ain2 instanceof MethodInsnNode)
							{
								final MethodInsnNode min = (MethodInsnNode) ain2;

								if (str.equals("JX_ACCESS_TOKEN"))
								{
									JX_ACCESS_TOKEN = Class.forName(min.owner).getDeclaredMethod(min.name, String.class);
								}
								else if (str.equals("JX_REFRESH_TOKEN"))
								{
									JX_REFRESH_TOKEN = Class.forName(min.owner).getDeclaredMethod(min.name, String.class);
								}
								else if (str.equals("JX_SESSION_ID"))
								{
									JX_SESSION_ID = Class.forName(min.owner).getDeclaredMethod(min.name, String.class);
								}
								else if (str.equals("JX_CHARACTER_ID"))
								{
									JX_CHARACTER_ID = Class.forName(min.owner).getDeclaredMethod(min.name, String.class);
								}
							}
							else if (str.equals("JX_DISPLAY_NAME"))
							{
								for (int j = i; j < ins.size(); j++)
								{
									AbstractInsnNode in = ins.get(j);
									if (in.getOpcode() == Opcodes.PUTSTATIC)
									{
										FieldInsnNode displayNameField = (FieldInsnNode) in;
										JX_DISPLAY_NAME = Class.forName(displayNameField.owner).getDeclaredField(displayNameField.name);
										break outer;
									}
								}
							}
						}
					}
				}
			}
			catch (IOException | ClassNotFoundException | NoSuchMethodException | NoSuchFieldException e)
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
			JX_ACCESS_TOKEN.invoke(null, accessToken);
			JX_REFRESH_TOKEN.invoke(null, refreshToken);
			JX_SESSION_ID.invoke(null, sessionId);
			JX_CHARACTER_ID.invoke(null, characterId);
			JX_DISPLAY_NAME.set(null, displayName);
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
					log.error("No getLoginIndexMethod present!");
					return;
				}

				final InsnList ins = getLoginIndexMethod.instructions;
				if (ins == null)
				{
					log.error("getLoginIndex method instructions are empty!");
					return;
				}

				final AbstractInsnNode ain = ins.get(0);
				if (ain != null && ain.getOpcode() == Opcodes.GETSTATIC)
				{
					final FieldInsnNode loginIndexFieldInsn = (FieldInsnNode) ain;
					final ClassReader loginClassReader = new ClassReader(loginIndexFieldInsn.owner);
					final ClassNode loginClassNode = new ClassNode(Opcodes.ASM9);
					loginClassReader.accept(loginClassNode, ClassReader.SKIP_FRAMES);

					outer:
					for (MethodNode method : loginClassNode.methods)
					{
						InsnList _ins = method.instructions;
						for (AbstractInsnNode _ain : _ins)
						{
							if (_ain.getOpcode() == Opcodes.GETSTATIC)
							{
								FieldInsnNode targetFieldInsn = (FieldInsnNode) _ain;
								if (targetFieldInsn.owner.equals(loginIndexFieldInsn.owner) && targetFieldInsn.name.equals(loginIndexFieldInsn.name) && targetFieldInsn.desc.equals(loginIndexFieldInsn.desc))
								{
									try
									{
										setLoginIndex = Class.forName(loginClassNode.name).getDeclaredMethod(method.name, int.class);
									}
									catch (ClassNotFoundException | NoSuchMethodException e)
									{
										continue outer;
									}
									break outer;
								}
							}
						}
					}
				}
			}
			catch (IOException e)
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

	private static Field accountMode;
	private static Field jagexAccountMode;

	@Deprecated(forRemoval = true)
	public static void setMode(boolean legacy)
	{
		if (accountMode == null || jagexAccountMode == null)
		{
			try
			{
				final Class<?> clientClazz = Static.getClient().getClass();
				final ClassReader classReader = new ClassReader(clientClazz.getName());
				final ClassNode classNode = new ClassNode(Opcodes.ASM9);
				classReader.accept(classNode, ClassReader.SKIP_FRAMES);

				outer:
				for (FieldNode field : classNode.fields)
				{
					if (!Modifier.isStatic(field.access) || !field.desc.startsWith("L") || !field.desc.endsWith(";"))
					{
						continue;
					}
					final String className = field.desc.replace("L", "").replace(";", "");
					if (className.length() < 2 || className.length() > 3)
					{
						continue;
					}

					final ClassReader targetEnumClassReader = new ClassReader(className);
					final ClassNode targetEnumClassNode = new ClassNode(Opcodes.ASM9);
					targetEnumClassReader.accept(targetEnumClassNode, ClassReader.SKIP_FRAMES);

					for (MethodNode method : targetEnumClassNode.methods)
					{
						if (method.name.equals("<clinit>"))
						{
							final InsnList ins = method.instructions;
							for (int i = 0; i < ins.size(); i++)
							{
								if (i + 5 > ins.size())
								{
									continue outer;
								}

								final AbstractInsnNode current = ins.get(i);
								if (current.getOpcode() != Opcodes.NEW)
								{
									continue;
								}
								final AbstractInsnNode next1 = ins.get(i + 1);
								if (next1.getOpcode() != Opcodes.DUP)
								{
									continue;
								}
								final AbstractInsnNode next2 = ins.get(i + 2);
								final AbstractInsnNode next3 = ins.get(i + 3);
								final int value1 = ASMUtils.getIntValue(next2);
								final int value2 = ASMUtils.getIntValue(next3);
								if (value1 == 4 && value2 == 0)
								{
									accountMode = Class.forName(classNode.name).getDeclaredField(field.name);
									final FieldInsnNode fi = (FieldInsnNode) ins.get(i + 5);
									jagexAccountMode = Class.forName(fi.owner).getDeclaredField(fi.name);
									break outer;
								}
							}
						}
					}
				}
			}
			catch (IOException | ClassNotFoundException | NoSuchFieldException e)
			{
				log.error("Failed to resolve account mode", e);
			}
		}

		if (accountMode == null || jagexAccountMode == null)
		{
			log.error("Set mode method is broken! accountMode: {} jagexAccountMode: {}", accountMode == null ? "false" : true, jagexAccountMode == null ? "false" : true);
			return;
		}

		accountMode.setAccessible(true);
		jagexAccountMode.setAccessible(true);
		try
		{
			accountMode.set(null, jagexAccountMode.get(null));
		}
		catch (IllegalAccessException e)
		{
			log.error("Failed to set mode", e);
		}
		accountMode.setAccessible(false);
		jagexAccountMode.setAccessible(false);
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

	private static Method getRLJXProperty;

	public static String getRLJXProperty(String key)
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

		/*public static void setAccountType(boolean legacy)
	{
		try
		{
			final Field f = Class.forName("client").getDeclaredField("gs");
			f.setAccessible(true);
			f.setInt(null, legacy ? 0 : 5);
			f.setAccessible(false);
		}
		catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	public static void nullOtlTokenRequester()
	{
		try
		{
			final Field f = Arrays.stream(Class.forName("client")
				.getDeclaredFields())
				.filter(field -> field.getType().getSimpleName().equals("OtlTokenRequester"))
				.findFirst()
				.orElse(null);

			f.setAccessible(true);
			f.set(Static.getClient(), null);
			f.setAccessible(false);
		}
		catch (ClassNotFoundException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	public static void setAuthenticationScheme(boolean legacy)
	{
		try
		{
			Method m = Class.forName("client").getDeclaredMethod("lb", boolean.class);
			m.setAccessible(true);
			m.invoke(null, legacy);
			m.setAccessible(false);
		}
		catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}*/

	public static class LoginIndex
	{
		public static int LEGACY = 2;
		public static int JAGEX_ACCOUNT = 10;
	}
}
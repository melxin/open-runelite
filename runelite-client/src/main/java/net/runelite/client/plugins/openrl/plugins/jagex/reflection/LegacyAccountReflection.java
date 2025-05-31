/*
 * Copyright (c) 2025, Melxin <https://github.com/melxin>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.openrl.plugins.jagex.reflection;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import java.lang.reflect.Field;
import net.runelite.client.plugins.openrl.Static;

@Slf4j
public class LegacyAccountReflection
{
	private static Field password;

	@SneakyThrows
	public static String getPassword()
	{
		if (password == null)
		{
			final Class<?> clientClazz = Static.getClient().getClass();
			final ClassReader classReader = new ClassReader(clientClazz.getName());
			final ClassNode classNode = new ClassNode(Opcodes.ASM9);
			classReader.accept(classNode, ClassReader.SKIP_FRAMES);
			final MethodNode method = classNode.methods.stream().filter(m -> m.name.equals("setPassword")).findFirst().orElse(null);
			if (method != null)
			{
				final InsnList ins = method.instructions;
				final AbstractInsnNode ain1 = ins.get(1);
				final FieldInsnNode fin = (FieldInsnNode) ain1;
				password = Class.forName(fin.owner).getDeclaredField(fin.name);
			}
		}

		if (password == null)
		{
			log.error("getPassword method is broken!");
			return null;
		}

		password.setAccessible(true);
		final String pass = (String) password.get(null);
		password.setAccessible(false);
		return pass != null ? pass : null;
	}

	private static Field otp;

	@SneakyThrows
	public static String getOtp()
	{
		if (otp == null)
		{
			final Class<?> clientClazz = Static.getClient().getClass();
			final ClassReader classReader = new ClassReader(clientClazz.getName());
			final ClassNode classNode = new ClassNode(Opcodes.ASM9);
			classReader.accept(classNode, ClassReader.SKIP_FRAMES);
			final MethodNode method = classNode.methods.stream().filter(m -> m.name.equals("setOtp")).findFirst().orElse(null);
			if (method != null)
			{
				final InsnList ins = method.instructions;
				final AbstractInsnNode ain1 = ins.get(1);
				final FieldInsnNode fin = (FieldInsnNode) ain1;
				otp = Class.forName(fin.owner).getDeclaredField(fin.name);
			}
		}

		if (otp == null)
		{
			log.error("getOtp method is broken!");
			return null;
		}

		otp.setAccessible(true);
		final String _otp = (String) otp.get(null);
		otp.setAccessible(false);
		return _otp != null ? _otp : null;
	}
}
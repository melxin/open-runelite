/*
 * Copyright (c) 2024, Melxin <https://github.com/melxin/>
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
package net.runelite.transformers.client;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Create client::getLocalPlayerIndex client::setLocalPlayerIndex(int idx)
 */
@Slf4j
public class LocalPlayerIndexTransformer implements ClassFileTransformer
{
	private static final Set<String> transformedClasses = ConcurrentHashMap.newKeySet();

	private static final String VANILLA_CLIENT_NAME = "client";

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classFileBuffer)
	{
		if (transformedClasses.contains(className) || !className.equals(VANILLA_CLIENT_NAME))
		{
			return classFileBuffer;
		}

		transformedClasses.add(className);

		final ClassReader reader = new ClassReader(classFileBuffer);
		final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);

		Stopwatch stopWatch = Stopwatch.createStarted();

		final ClassNode classNode = new ClassNode();
		reader.accept(classNode, ClassReader.SKIP_FRAMES);

		final AtomicBoolean isInserted = new AtomicBoolean();

		reader.accept(new ClassVisitor(Opcodes.ASM9, writer)
		{
			@Override
			public void visitEnd()
			{
				super.visitEnd();

				outer:
				for (MethodNode methodNode : classNode.methods)
				{
					for (AbstractInsnNode insnNode : methodNode.instructions)
					{
						if (insnNode instanceof LdcInsnNode
							&& insnNode.getNext() instanceof LdcInsnNode
							&& insnNode.getNext().getNext().getOpcode() == Opcodes.GETSTATIC
							&& insnNode.getNext().getNext().getNext().getOpcode() == Opcodes.IMUL
							&& insnNode.getNext().getNext().getNext().getNext().getOpcode() == Opcodes.BIPUSH
							&& insnNode.getNext().getNext().getNext().getNext() instanceof IntInsnNode
							&& ((IntInsnNode) insnNode.getNext().getNext().getNext().getNext()).operand == 8
							&& insnNode.getNext().getNext().getNext().getNext().getNext().getOpcode() == Opcodes.ISHL
							&& insnNode.getNext().getNext().getNext().getNext().getNext().getNext().getOpcode() == Opcodes.IMUL
							&& insnNode.getNext().getNext().getNext().getNext().getNext().getNext().getNext().getOpcode() == Opcodes.PUTSTATIC)
						{
							final FieldInsnNode targetLocalPlayerIndexField = (FieldInsnNode) insnNode.getNext().getNext();
							final LdcInsnNode targetLocalPlayerIndexFieldGetterMultiplier = (LdcInsnNode) insnNode.getNext();
							final LdcInsnNode targetLocalPlayerIndexFieldSetterMultiplier = (LdcInsnNode) insnNode;

							// Create the setter method for the static field
							MethodVisitor setterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "setLocalPlayerIndex", "(I)V", null, null);

							// Method body: set the static field
							setterMv.visitCode();

							// Load the integer parameter (at index 1) onto the stack
							setterMv.visitVarInsn(Opcodes.ILOAD, 1);

							// Multiplication
							setterMv.visitLdcInsn(targetLocalPlayerIndexFieldSetterMultiplier.cst);

							// Multiply
							setterMv.visitInsn(Opcodes.IMUL);

							// Set the static field
							setterMv.visitFieldInsn(Opcodes.PUTSTATIC, targetLocalPlayerIndexField.owner, targetLocalPlayerIndexField.name, targetLocalPlayerIndexField.desc);

							// Return from the method
							setterMv.visitInsn(Opcodes.RETURN);
							setterMv.visitMaxs(1, 2);
							setterMv.visitEnd();

							// Create the getter method for the static field
							MethodVisitor getterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "getLocalPlayerIndex", "()I", null, null);

							// Method body: return the static field
							getterMv.visitCode();

							// Get the value of the static field
							getterMv.visitFieldInsn(Opcodes.GETSTATIC, targetLocalPlayerIndexField.owner, targetLocalPlayerIndexField.name, targetLocalPlayerIndexField.desc);

							// Multiplication
							getterMv.visitLdcInsn(targetLocalPlayerIndexFieldGetterMultiplier.cst);

							// Multiply
							getterMv.visitInsn(Opcodes.IMUL);

							// Return the integer value
							getterMv.visitInsn(Opcodes.IRETURN);
							getterMv.visitMaxs(1, 1);
							getterMv.visitEnd();

							isInserted.set(true);

							break outer;
						}
					}
				}
			}
		}, ClassReader.SKIP_FRAMES);

		if (isInserted.get())
		{
			log.info("Took: {}", stopWatch);
		}
		return writer.toByteArray();
	}
}
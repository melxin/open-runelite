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
package net.runelite.transformers.mousehandler;

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
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.runelite.utils.FieldUtils;

/**
 * Access MouseHandler
 */
@Slf4j
public class MouseHandlerTransformer implements ClassFileTransformer
{
	private static final Set<String> transformedClasses = ConcurrentHashMap.newKeySet();

	private static final String VANILLA_CLIENT_NAME = "client";

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classFileBuffer)
	{
		final ClassReader reader = new ClassReader(classFileBuffer);
		final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);

		if (transformedClasses.contains(className))
		{
			return classFileBuffer;
		}

		transformedClasses.add(className);

		Stopwatch stopwatch = Stopwatch.createStarted();

		final ClassNode classNode = new ClassNode();
		reader.accept(classNode, ClassReader.SKIP_FRAMES);
		//classNode.accept(writer);

		reader.accept(new ClassVisitor(Opcodes.ASM9, writer)
		{
			@Override
			public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
			{
				// Add the new interfaces
				if (className.length() == 2 && Arrays.asList(reader.getInterfaces()).containsAll(List.of("java/awt/event/MouseListener", "java/awt/event/MouseMotionListener", "java/awt/event/FocusListener")))
				{
					// Add the new interface to the existing interfaces
					String[] newInterfaces = Arrays.copyOf(interfaces, interfaces.length + 1);
					newInterfaces[interfaces.length] = "net/runelite/api/MouseHandler";
					super.visit(version, access, name, signature, superName, newInterfaces);
				}
				else
				{
					super.visit(version, access, name, signature, superName, interfaces);
				}
			}

			@Override
			public void visitEnd()
			{
				super.visitEnd();

				// Find mouse handler instance and create client::getMouseHandler();
				if (className.equals(VANILLA_CLIENT_NAME))
				{
					outer:
					for (MethodNode m : classNode.methods)
					{
						// Kill0 method
						if (m.access == (Opcodes.ACC_PROTECTED | Opcodes.ACC_FINAL) && m.desc.equals("(I)V"))
						{
							for (AbstractInsnNode in : m.instructions)
							{
								if (in instanceof FieldInsnNode
									&& in.getNext().getOpcode() == Opcodes.DUP
									&& in.getNext().getNext() instanceof VarInsnNode
									&& ((VarInsnNode) in.getNext().getNext()).var == 2
									&& in.getNext().getNext().getNext().getOpcode() == Opcodes.MONITORENTER)
								{
									FieldInsnNode mouseHandlerInstance = (FieldInsnNode) in;
									log.info("Found MouseHandler_instance: {}.{} | {}", mouseHandlerInstance.owner, mouseHandlerInstance.name, mouseHandlerInstance.desc);

									// Create client::getMouseHandler
									MethodVisitor getterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "getMouseHandler", "()Lnet/runelite/api/MouseHandler;", null, null);
									getterMv.visitCode();
									getterMv.visitFieldInsn(Opcodes.GETSTATIC, mouseHandlerInstance.owner, mouseHandlerInstance.name, mouseHandlerInstance.desc); // Get the value of the static field
									getterMv.visitInsn(Opcodes.ARETURN); // Return the result
									getterMv.visitMaxs(1, 1); // Maximum stack size of 1, 1 local variable (this)
									getterMv.visitEnd();

									break outer;
								}
							}
						}
					}
				}

				// Find MouseHandler class
				if (className.length() == 2 && Arrays.asList(reader.getInterfaces()).containsAll(List.of("java/awt/event/MouseListener", "java/awt/event/MouseMotionListener", "java/awt/event/FocusListener")))
				{
					log.info("Found mouse handler: {} | {}", className, Arrays.toString(reader.getInterfaces()));
				}

				// Handle client
				if (className.equals(VANILLA_CLIENT_NAME))
				{
					MethodNode getMouseLastPressedMillis = classNode.methods.stream()
						.filter(m -> m.access == Opcodes.ACC_PUBLIC && m.name.equals("getMouseLastPressedMillis") && m.desc.equals("()J"))
						.findFirst()
						.orElseThrow(() -> new NoSuchElementException("getMouseLastPressedMillis target method was not found"));

					FieldInsnNode MouseHandler_lastPressedMillis = (FieldInsnNode) getMouseLastPressedMillis.instructions.get(0);

					log.info("Found MouseHandler_mouseLastPressedMillis: {}.{} | {}", MouseHandler_lastPressedMillis.owner, MouseHandler_lastPressedMillis.name, MouseHandler_lastPressedMillis.desc);

					// Create setter for MouseHandler_mouseLastPressedMillis
					MethodVisitor MouseHandler_lastPressedMillisSetterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "setMouseLastPressedMillis", "(J)V", null, null);
					MouseHandler_lastPressedMillisSetterMv.visitCode();
					MouseHandler_lastPressedMillisSetterMv.visitVarInsn(Opcodes.LLOAD, 1); // Load the first parameter (long) onto the stack
					MouseHandler_lastPressedMillisSetterMv.visitFieldInsn(Opcodes.PUTSTATIC, MouseHandler_lastPressedMillis.owner, MouseHandler_lastPressedMillis.name, MouseHandler_lastPressedMillis.desc); // Store the result in the static field
					MouseHandler_lastPressedMillisSetterMv.visitInsn(Opcodes.RETURN); // Return from the method
					MouseHandler_lastPressedMillisSetterMv.visitMaxs(1, 2); // Maximum stack size of 1, 2 local variables (this and the input parameter)
					MouseHandler_lastPressedMillisSetterMv.visitEnd();

					// Find and create setter & getter for mouseLastLastPressedTimeMillis
					outer:
					for (MethodNode methodNode : classNode.methods)
					{
						for (AbstractInsnNode insnNode : methodNode.instructions)
						{
							if (insnNode instanceof FieldInsnNode
								&& insnNode.getOpcode() == Opcodes.GETSTATIC
								&& ((FieldInsnNode) insnNode).owner.equals(MouseHandler_lastPressedMillis.owner)
								&& ((FieldInsnNode) insnNode).name.equals(MouseHandler_lastPressedMillis.name)
								&& ((FieldInsnNode) insnNode).desc.equals(MouseHandler_lastPressedMillis.desc)
								&& insnNode.getNext() instanceof LdcInsnNode
								&& insnNode.getNext().getNext().getOpcode() == Opcodes.LMUL
								&& insnNode.getNext().getNext().getNext() instanceof LdcInsnNode
								&& insnNode.getNext().getNext().getNext().getNext() instanceof FieldInsnNode
								&& insnNode.getNext().getNext().getNext().getNext().getNext().getOpcode() == Opcodes.LMUL)
							{
								FieldInsnNode mouseLastPressedTimeMillis = (FieldInsnNode) insnNode.getNext().getNext().getNext().getNext();
								LdcInsnNode mouseLastPressedTimeMillisGetterMultiplier = (LdcInsnNode) insnNode.getNext().getNext().getNext();
								LdcInsnNode mouseLastPressedTimeMillisSetterMultiplier = FieldUtils.findFieldMultiplication(classNode, mouseLastPressedTimeMillis, true);
								log.info("Found mouseLastPressedTimeMillis: {} getter: {} setter: {}", mouseLastPressedTimeMillis.name, mouseLastPressedTimeMillisGetterMultiplier.cst, mouseLastPressedTimeMillisSetterMultiplier.cst);

								// Create setter for long
								MethodVisitor mouseLastPressedTimeMillisSetterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "setClientMouseLastPressedMillis", "(J)V", null, null);
								mouseLastPressedTimeMillisSetterMv.visitCode();
								mouseLastPressedTimeMillisSetterMv.visitVarInsn(Opcodes.LLOAD, 1); // Load the first parameter long onto the stack
								mouseLastPressedTimeMillisSetterMv.visitLdcInsn(mouseLastPressedTimeMillisSetterMultiplier.cst); // Multiplication
								mouseLastPressedTimeMillisSetterMv.visitInsn(Opcodes.LMUL); // Multiply
								mouseLastPressedTimeMillisSetterMv.visitFieldInsn(Opcodes.PUTSTATIC, mouseLastPressedTimeMillis.owner, mouseLastPressedTimeMillis.name, mouseLastPressedTimeMillis.desc); // Store the result in the static field
								mouseLastPressedTimeMillisSetterMv.visitInsn(Opcodes.RETURN); // Return from the method
								mouseLastPressedTimeMillisSetterMv.visitMaxs(2, 2); // Maximum stack size of 2, 2 local variables (this and the input parameter)
								mouseLastPressedTimeMillisSetterMv.visitEnd();

								// Create getter for long
								MethodVisitor mouseLastPressedTimeMillisGetterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "getClientMouseLastPressedMillis", "()J", null, null);
								mouseLastPressedTimeMillisGetterMv.visitCode();
								mouseLastPressedTimeMillisGetterMv.visitFieldInsn(Opcodes.GETSTATIC, mouseLastPressedTimeMillis.owner, mouseLastPressedTimeMillis.name, mouseLastPressedTimeMillis.desc); // Get the value of the static field
								mouseLastPressedTimeMillisGetterMv.visitLdcInsn(mouseLastPressedTimeMillisGetterMultiplier.cst); // Multiplication
								mouseLastPressedTimeMillisGetterMv.visitInsn(Opcodes.LMUL); // Multiply
								mouseLastPressedTimeMillisGetterMv.visitInsn(Opcodes.LRETURN); // Return the result
								mouseLastPressedTimeMillisGetterMv.visitMaxs(1, 1); // Maximum stack size of 1, 1 local variable (this)
								mouseLastPressedTimeMillisGetterMv.visitEnd();

								break outer;
							}
						}
					}
				}
			}
		}, ClassReader.SKIP_FRAMES);

		log.info("Took: {}", stopwatch);
		return writer.toByteArray();
	}
}
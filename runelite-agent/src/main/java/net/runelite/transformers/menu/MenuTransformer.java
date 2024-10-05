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
package net.runelite.transformers.menu;

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
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import net.runelite.utils.FieldUtils;

/**
 * Create menu setter and getters
 */
@Slf4j
public class MenuTransformer implements ClassFileTransformer
{
	private static final Set<String> transformedClasses = ConcurrentHashMap.newKeySet();

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classFileBuffer)
	{
		if (transformedClasses.contains(className) || className.length() != 2)
		{
			return classFileBuffer;
		}

		transformedClasses.add(className);

		final ClassReader reader = new ClassReader(classFileBuffer);
		final ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);

		if (!Arrays.asList(reader.getInterfaces()).contains("net/runelite/api/Menu"))
		{
			return classFileBuffer;
		}

		log.info("Found menu class: {}", className);

		Stopwatch stopWatch = Stopwatch.createStarted();

		final ClassNode classNode = new ClassNode();
		reader.accept(classNode, ClassReader.SKIP_FRAMES);

		final AtomicBoolean menuOptionsCountInserted = new AtomicBoolean();
		final AtomicBoolean menuArguments1Inserted = new AtomicBoolean();
		final AtomicBoolean menuArguments2Inserted = new AtomicBoolean();
		final AtomicBoolean menuOpcodesInserted = new AtomicBoolean();
		final AtomicBoolean menuIdentifiersInserted = new AtomicBoolean();
		final AtomicBoolean menuItemIdsInserted = new AtomicBoolean();
		final AtomicBoolean menuWorldViewIdsInserted = new AtomicBoolean();
		final AtomicBoolean menuActionsInserted = new AtomicBoolean();
		final AtomicBoolean menuTargetsInserted = new AtomicBoolean();
		final AtomicBoolean subMenusInserted = new AtomicBoolean();
		final AtomicBoolean menuShiftClickInserted = new AtomicBoolean();


		reader.accept(new ClassVisitor(Opcodes.ASM9, writer)
		{
			@Override
			public void visitEnd()
			{
				super.visitEnd();

				MethodNode targetMethod = classNode.methods.stream()
					.filter(m -> m.access == Opcodes.ACC_PUBLIC && m.name.equals("<init>") && m.desc.equals("(Z)V"))
					.findFirst()
					.orElseThrow(() -> new NoSuchElementException("Menu target method was not found in class: " + className));

				for (AbstractInsnNode insnNode : targetMethod.instructions)
				{
					// menuOptionsCount
					if (!menuOptionsCountInserted.get()
						&& insnNode instanceof FieldInsnNode
						&& ((FieldInsnNode) insnNode).desc.equals("I")
						&& insnNode.getOpcode() == Opcodes.PUTFIELD
						&& insnNode.getPrevious().getOpcode() == Opcodes.IMUL
						&& insnNode.getPrevious().getPrevious() instanceof LdcInsnNode)
					{
						FieldInsnNode menuOptionsCount = (FieldInsnNode) insnNode;
						LdcInsnNode menuOptionsCountSetterMultiplier = (LdcInsnNode) insnNode.getPrevious().getPrevious();
						LdcInsnNode menuOptionsCountGetterMultiplier = (LdcInsnNode) FieldUtils.findFieldMultiplication(classNode, menuOptionsCount, false);

						log.info("Found menuOptionsCount: {} setter: {} getter: {}", menuOptionsCount.name, menuOptionsCountSetterMultiplier.cst, menuOptionsCountGetterMultiplier.cst);
						// Create setter
						MethodVisitor setterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "setMenuOptionCount", "(I)V", null, null);
						setterMv.visitCode();
						setterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						setterMv.visitVarInsn(Opcodes.ILOAD, 1); // Load the first parameter (int value) onto the stack
						setterMv.visitLdcInsn(menuOptionsCountSetterMultiplier.cst); // Load the constant multiplier onto the stack
						setterMv.visitInsn(Opcodes.IMUL); // Multiply the two int values on the stack
						setterMv.visitFieldInsn(Opcodes.PUTFIELD, menuOptionsCount.owner, menuOptionsCount.name, menuOptionsCount.desc); // Store the result in the field
						setterMv.visitInsn(Opcodes.RETURN); // Return from the method
						setterMv.visitMaxs(3, 2); // Maximum stack size of 3 (for the two integers plus 'this'), 2 local variables (this and the input parameter)
						setterMv.visitEnd();

						// Create getter
						MethodVisitor getterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "getMenuOptionCount", "()I", null, null);
						getterMv.visitCode();
						getterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						getterMv.visitFieldInsn(Opcodes.GETFIELD, menuOptionsCount.owner, menuOptionsCount.name, menuOptionsCount.desc); // Get the value of the field
						getterMv.visitLdcInsn(menuOptionsCountGetterMultiplier.cst); // Load the multiplier constant onto the stack
						getterMv.visitInsn(Opcodes.IMUL); // Multiply the fetched value with the constant
						getterMv.visitInsn(Opcodes.IRETURN); // Return the result
						getterMv.visitMaxs(2, 1); // Maximum stack size of 2, 1 local variable (this)
						getterMv.visitEnd();

						menuOptionsCountInserted.set(true);
						continue;
					}

					// menuArguments1
					if (!menuArguments1Inserted.get()
						&& insnNode instanceof FieldInsnNode
						&& ((FieldInsnNode) insnNode).desc.equals("[I")
						&& insnNode.getOpcode() == Opcodes.PUTFIELD
						&& insnNode.getPrevious().getOpcode() == Opcodes.NEWARRAY)
					{
						FieldInsnNode menuArguments1 = (FieldInsnNode) insnNode;

						log.info("Found menuArguments1: {}", menuArguments1.name);

						// Create setter for int[]
						MethodVisitor setterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "setMenuArguments1", "([I)V", null, null);
						setterMv.visitCode();
						setterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						setterMv.visitVarInsn(Opcodes.ALOAD, 1); // Load the first parameter (int[] array) onto the stack
						setterMv.visitFieldInsn(Opcodes.PUTFIELD, menuArguments1.owner, menuArguments1.name, menuArguments1.desc); // Store the result in the field
						setterMv.visitInsn(Opcodes.RETURN); // Return from the method
						setterMv.visitMaxs(2, 2); // Maximum stack size of 2 (one for 'this' and one for the int[]), 2 local variables (this and the input parameter)
						setterMv.visitEnd();

						// Create getter for int[]
						MethodVisitor getterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "getMenuArguments1", "()[I", null, null);
						getterMv.visitCode();
						getterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						getterMv.visitFieldInsn(Opcodes.GETFIELD, menuArguments1.owner, menuArguments1.name, menuArguments1.desc); // Get the value of the field
						getterMv.visitInsn(Opcodes.ARETURN); // Return the result
						getterMv.visitMaxs(1, 1); // Maximum stack size of 1, 1 local variable (this)
						getterMv.visitEnd();

						menuArguments1Inserted.set(true);
						continue;
					}

					// menuArguments2
					if (!menuArguments2Inserted.get()
						&& insnNode instanceof FieldInsnNode
						&& ((FieldInsnNode) insnNode).desc.equals("[I")
						&& insnNode.getOpcode() == Opcodes.PUTFIELD
						&& insnNode.getPrevious().getOpcode() == Opcodes.NEWARRAY)
					{
						FieldInsnNode menuArguments2 = (FieldInsnNode) insnNode;

						log.info("Found menuArguments2: {}", menuArguments2.name);

						// Create setter for int[]
						MethodVisitor setterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "setMenuArguments2", "([I)V", null, null);
						setterMv.visitCode();
						setterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						setterMv.visitVarInsn(Opcodes.ALOAD, 1); // Load the first parameter (int[] array) onto the stack
						setterMv.visitFieldInsn(Opcodes.PUTFIELD, menuArguments2.owner, menuArguments2.name, menuArguments2.desc); // Store the result in the field
						setterMv.visitInsn(Opcodes.RETURN); // Return from the method
						setterMv.visitMaxs(2, 2); // Maximum stack size of 2 (one for 'this' and one for the int[]), 2 local variables (this and the input parameter)
						setterMv.visitEnd();

						// Create getter for int[]
						MethodVisitor getterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "getMenuArguments2", "()[I", null, null);
						getterMv.visitCode();
						getterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						getterMv.visitFieldInsn(Opcodes.GETFIELD, menuArguments2.owner, menuArguments2.name, menuArguments2.desc); // Get the value of the field
						getterMv.visitInsn(Opcodes.ARETURN); // Return the result
						getterMv.visitMaxs(1, 1); // Maximum stack size of 1, 1 local variable (this)
						getterMv.visitEnd();

						menuArguments2Inserted.set(true);
						continue;
					}

					// menuOpcodes
					if (!menuOpcodesInserted.get()
						&& insnNode instanceof FieldInsnNode
						&& ((FieldInsnNode) insnNode).desc.equals("[I")
						&& insnNode.getOpcode() == Opcodes.PUTFIELD
						&& insnNode.getPrevious().getOpcode() == Opcodes.NEWARRAY)
					{
						FieldInsnNode menuOpcodes = (FieldInsnNode) insnNode;

						log.info("Found menuOpcodes: {}", menuOpcodes.name);

						// Create setter for int[]
						MethodVisitor setterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "setMenuOpcodes", "([I)V", null, null);
						setterMv.visitCode();
						setterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						setterMv.visitVarInsn(Opcodes.ALOAD, 1); // Load the first parameter (int[] array) onto the stack
						setterMv.visitFieldInsn(Opcodes.PUTFIELD, menuOpcodes.owner, menuOpcodes.name, menuOpcodes.desc); // Store the result in the field
						setterMv.visitInsn(Opcodes.RETURN); // Return from the method
						setterMv.visitMaxs(2, 2); // Maximum stack size of 2 (one for 'this' and one for the int[]), 2 local variables (this and the input parameter)
						setterMv.visitEnd();

						// Create getter for int[]
						MethodVisitor getterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "getMenuOpcodes", "()[I", null, null);
						getterMv.visitCode();
						getterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						getterMv.visitFieldInsn(Opcodes.GETFIELD, menuOpcodes.owner, menuOpcodes.name, menuOpcodes.desc); // Get the value of the field
						getterMv.visitInsn(Opcodes.ARETURN); // Return the result
						getterMv.visitMaxs(1, 1); // Maximum stack size of 1, 1 local variable (this)
						getterMv.visitEnd();

						menuOpcodesInserted.set(true);
						continue;
					}

					// menuIdentifiers
					if (!menuIdentifiersInserted.get()
						&& insnNode instanceof FieldInsnNode
						&& ((FieldInsnNode) insnNode).desc.equals("[I")
						&& insnNode.getOpcode() == Opcodes.PUTFIELD
						&& insnNode.getPrevious().getOpcode() == Opcodes.NEWARRAY)
					{
						FieldInsnNode menuIdentifiers = (FieldInsnNode) insnNode;

						log.info("Found menuIdentifiers: {}", menuIdentifiers.name);

						// Create setter for int[]
						MethodVisitor setterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "setMenuIdentifiers", "([I)V", null, null);
						setterMv.visitCode();
						setterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						setterMv.visitVarInsn(Opcodes.ALOAD, 1); // Load the first parameter (int[] array) onto the stack
						setterMv.visitFieldInsn(Opcodes.PUTFIELD, menuIdentifiers.owner, menuIdentifiers.name, menuIdentifiers.desc); // Store the result in the field
						setterMv.visitInsn(Opcodes.RETURN); // Return from the method
						setterMv.visitMaxs(2, 2); // Maximum stack size of 2 (one for 'this' and one for the int[]), 2 local variables (this and the input parameter)
						setterMv.visitEnd();

						// Create getter for int[]
						MethodVisitor getterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "getMenuIdentifiers", "()[I", null, null);
						getterMv.visitCode();
						getterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						getterMv.visitFieldInsn(Opcodes.GETFIELD, menuIdentifiers.owner, menuIdentifiers.name, menuIdentifiers.desc); // Get the value of the field
						getterMv.visitInsn(Opcodes.ARETURN); // Return the result
						getterMv.visitMaxs(1, 1); // Maximum stack size of 1, 1 local variable (this)
						getterMv.visitEnd();

						menuIdentifiersInserted.set(true);
						continue;
					}

					// menuItemIds
					if (!menuItemIdsInserted.get()
						&& insnNode instanceof FieldInsnNode
						&& ((FieldInsnNode) insnNode).desc.equals("[I")
						&& insnNode.getOpcode() == Opcodes.PUTFIELD
						&& insnNode.getPrevious().getOpcode() == Opcodes.NEWARRAY)
					{
						FieldInsnNode menuItemIds = (FieldInsnNode) insnNode;

						log.info("Found menuItemIds: {}", menuItemIds.name);

						// Create setter for int[]
						MethodVisitor setterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "setMenuItemIds", "([I)V", null, null);
						setterMv.visitCode();
						setterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						setterMv.visitVarInsn(Opcodes.ALOAD, 1); // Load the first parameter (int[] array) onto the stack
						setterMv.visitFieldInsn(Opcodes.PUTFIELD, menuItemIds.owner, menuItemIds.name, menuItemIds.desc); // Store the result in the field
						setterMv.visitInsn(Opcodes.RETURN); // Return from the method
						setterMv.visitMaxs(2, 2); // Maximum stack size of 2 (one for 'this' and one for the int[]), 2 local variables (this and the input parameter)
						setterMv.visitEnd();

						// Create getter for int[]
						MethodVisitor getterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "getMenuItemIds", "()[I", null, null);
						getterMv.visitCode();
						getterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						getterMv.visitFieldInsn(Opcodes.GETFIELD, menuItemIds.owner, menuItemIds.name, menuItemIds.desc); // Get the value of the field
						getterMv.visitInsn(Opcodes.ARETURN); // Return the result
						getterMv.visitMaxs(1, 1); // Maximum stack size of 1, 1 local variable (this)
						getterMv.visitEnd();

						menuItemIdsInserted.set(true);
						continue;
					}

					// menuWorldViewIds
					if (!menuWorldViewIdsInserted.get()
						&& insnNode instanceof FieldInsnNode
						&& ((FieldInsnNode) insnNode).desc.equals("[I")
						&& insnNode.getOpcode() == Opcodes.PUTFIELD
						&& insnNode.getPrevious().getOpcode() == Opcodes.NEWARRAY)
					{
						FieldInsnNode menuWorldViewIds = (FieldInsnNode) insnNode;

						log.info("Found menuWorldViewIds: {}", menuWorldViewIds.name);

						// Create setter for int[]
						MethodVisitor setterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "setMenuWorldViewIds", "([I)V", null, null);
						setterMv.visitCode();
						setterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						setterMv.visitVarInsn(Opcodes.ALOAD, 1); // Load the first parameter (int[] array) onto the stack
						setterMv.visitFieldInsn(Opcodes.PUTFIELD, menuWorldViewIds.owner, menuWorldViewIds.name, menuWorldViewIds.desc); // Store the result in the field
						setterMv.visitInsn(Opcodes.RETURN); // Return from the method
						setterMv.visitMaxs(2, 2); // Maximum stack size of 2 (one for 'this' and one for the int[]), 2 local variables (this and the input parameter)
						setterMv.visitEnd();

						// Create getter for int[]
						MethodVisitor getterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "getMenuWorldViewIds", "()[I", null, null);
						getterMv.visitCode();
						getterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						getterMv.visitFieldInsn(Opcodes.GETFIELD, menuWorldViewIds.owner, menuWorldViewIds.name, menuWorldViewIds.desc); // Get the value of the field
						getterMv.visitInsn(Opcodes.ARETURN); // Return the result
						getterMv.visitMaxs(1, 1); // Maximum stack size of 1, 1 local variable (this)
						getterMv.visitEnd();

						menuWorldViewIdsInserted.set(true);
						continue;
					}

					// menuActions
					if (!menuActionsInserted.get()
						&& insnNode instanceof FieldInsnNode
						&& ((FieldInsnNode) insnNode).desc.equals("[Ljava/lang/String;")
						&& insnNode.getOpcode() == Opcodes.PUTFIELD
						&& insnNode.getPrevious().getOpcode() == Opcodes.ANEWARRAY)
					{
						FieldInsnNode menuActions = (FieldInsnNode) insnNode;

						log.info("Found menuActions: {}", menuActions.name);

						// Create setter for String[]
						MethodVisitor setterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "setMenuOptions", "([Ljava/lang/String;)V", null, null);
						setterMv.visitCode();
						setterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						setterMv.visitVarInsn(Opcodes.ALOAD, 1); // Load the first parameter (String[] array) onto the stack
						setterMv.visitFieldInsn(Opcodes.PUTFIELD, menuActions.owner, menuActions.name, menuActions.desc); // Store the result in the field
						setterMv.visitInsn(Opcodes.RETURN); // Return from the method
						setterMv.visitMaxs(2, 2); // Maximum stack size of 2 (one for 'this' and one for the String[]), 2 local variables (this and the input parameter)
						setterMv.visitEnd();

						// Create getter for String[]
						MethodVisitor getterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "getMenuOptions", "()[Ljava/lang/String;", null, null);
						getterMv.visitCode();
						getterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						getterMv.visitFieldInsn(Opcodes.GETFIELD, menuActions.owner, menuActions.name, menuActions.desc); // Get the value of the field
						getterMv.visitInsn(Opcodes.ARETURN); // Return the result
						getterMv.visitMaxs(1, 1); // Maximum stack size of 1, 1 local variable (this)
						getterMv.visitEnd();

						menuActionsInserted.set(true);
						continue;
					}

					// menuTargets
					if (!menuTargetsInserted.get()
						&& insnNode instanceof FieldInsnNode
						&& ((FieldInsnNode) insnNode).desc.equals("[Ljava/lang/String;")
						&& insnNode.getOpcode() == Opcodes.PUTFIELD
						&& insnNode.getPrevious().getOpcode() == Opcodes.ANEWARRAY)
					{
						FieldInsnNode menuTargets = (FieldInsnNode) insnNode;

						log.info("Found menuTargets: {}", menuTargets.name);

						// Create setter for String[]
						MethodVisitor setterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "setMenuTargets", "([Ljava/lang/String;)V", null, null);
						setterMv.visitCode();
						setterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						setterMv.visitVarInsn(Opcodes.ALOAD, 1); // Load the first parameter (String[] array) onto the stack
						setterMv.visitFieldInsn(Opcodes.PUTFIELD, menuTargets.owner, menuTargets.name, menuTargets.desc); // Store the result in the field
						setterMv.visitInsn(Opcodes.RETURN); // Return from the method
						setterMv.visitMaxs(2, 2); // Maximum stack size of 2 (one for 'this' and one for the String[]), 2 local variables (this and the input parameter)
						setterMv.visitEnd();

						// Create getter for String[]
						MethodVisitor getterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "getMenuTargets", "()[Ljava/lang/String;", null, null);
						getterMv.visitCode();
						getterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						getterMv.visitFieldInsn(Opcodes.GETFIELD, menuTargets.owner, menuTargets.name, menuTargets.desc); // Get the value of the field
						getterMv.visitInsn(Opcodes.ARETURN); // Return the result
						getterMv.visitMaxs(1, 1); // Maximum stack size of 1, 1 local variable (this)
						getterMv.visitEnd();

						menuTargetsInserted.set(true);
						continue;
					}

					// subMenus
					if (!subMenusInserted.get()
						&& insnNode instanceof FieldInsnNode
						&& insnNode.getOpcode() == Opcodes.PUTFIELD
						&& insnNode.getPrevious().getOpcode() == Opcodes.ANEWARRAY)
					{
						FieldInsnNode subMenus = (FieldInsnNode) insnNode;

						log.info("Found subMenus: {}", subMenus.name);

						// Create setter for Menu[]
						MethodVisitor setterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "setSubMenus", "([L" + subMenus.owner + ";)V", null, null);
						setterMv.visitCode();
						setterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						setterMv.visitVarInsn(Opcodes.ALOAD, 1); // Load the first parameter (menu[] array) onto the stack
						setterMv.visitFieldInsn(Opcodes.PUTFIELD, subMenus.owner, subMenus.name, subMenus.desc); // Store the result in the field
						setterMv.visitInsn(Opcodes.RETURN); // Return from the method
						setterMv.visitMaxs(2, 2); // Maximum stack size of 2, 2 local variables (this and the input parameter)
						setterMv.visitEnd();

						// Create getter for Menu[]
						MethodVisitor getterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "getSubMenus", "()[L" + subMenus.owner + ";", null, null);
						getterMv.visitCode();
						getterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						getterMv.visitFieldInsn(Opcodes.GETFIELD, subMenus.owner, subMenus.name, subMenus.desc); // Get the value of the field
						getterMv.visitInsn(Opcodes.ARETURN); // Return the result
						getterMv.visitMaxs(1, 1); // Maximum stack size of 1, 1 local variable (this)
						getterMv.visitEnd();

						subMenusInserted.set(true);
						continue;
					}

					// menuShiftClick
					if (!menuShiftClickInserted.get()
						&& insnNode instanceof FieldInsnNode
						&& ((FieldInsnNode) insnNode).desc.equals("[Z")
						&& insnNode.getOpcode() == Opcodes.PUTFIELD
						&& insnNode.getPrevious().getOpcode() == Opcodes.NEWARRAY)
					{
						FieldInsnNode menuShiftClick = (FieldInsnNode) insnNode;

						log.info("Found menuShiftClick: {}", menuShiftClick.name);

						// Create setter for boolean[]
						MethodVisitor setterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "setMenuForceLeftClick", "([Z)V", null, null);
						setterMv.visitCode();
						setterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						setterMv.visitVarInsn(Opcodes.ALOAD, 1); // Load the first parameter boolean[] onto the stack
						setterMv.visitFieldInsn(Opcodes.PUTFIELD, menuShiftClick.owner, menuShiftClick.name, menuShiftClick.desc); // Store the result in the field
						setterMv.visitInsn(Opcodes.RETURN); // Return from the method
						setterMv.visitMaxs(2, 2); // Maximum stack size of 2, 2 local variables (this and the input parameter)
						setterMv.visitEnd();

						// Create getter for boolean[]
						MethodVisitor getterMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "getMenuForceLeftClick", "()[Z", null, null);
						getterMv.visitCode();
						getterMv.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this' onto the stack
						getterMv.visitFieldInsn(Opcodes.GETFIELD, menuShiftClick.owner, menuShiftClick.name, menuShiftClick.desc); // Get the value of the field
						getterMv.visitInsn(Opcodes.ARETURN); // Return the result
						getterMv.visitMaxs(1, 1); // Maximum stack size of 1, 1 local variable (this)
						getterMv.visitEnd();

						menuShiftClickInserted.set(true);
						break;
					}
				}
			}
		}, ClassReader.SKIP_FRAMES);

		if (menuOptionsCountInserted.get()
			&& menuArguments1Inserted.get()
			&& menuArguments2Inserted.get()
			&& menuOpcodesInserted.get()
			&& menuIdentifiersInserted.get()
			&& menuItemIdsInserted.get()
			&& menuWorldViewIdsInserted.get()
			&& menuActionsInserted.get()
			&& menuTargetsInserted.get()
			&& subMenusInserted.get()
			&& menuShiftClickInserted.get())
		{
			log.info("Took: {}", stopWatch);
		}
		return writer.toByteArray();
	}
}
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
package net.melxin.asm.mapper.mappers;

import java.util.IdentityHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.melxin.asm.mapper.Mapper;
import net.melxin.asm.mapper.Remapper;
import net.melxin.asm.mapper.annotations.AnnotationAdder;
import net.melxin.asm.transformer.JarEntryGroup;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceInterpreter;
import org.objectweb.asm.tree.analysis.SourceValue;

@Slf4j
public class MethodGarbageValueMapper implements Mapper
{
	@Override
	public void map(JarEntryGroup sourceGroup, JarEntryGroup targetGroup, Remapper remapper)
	{
		int count = 0;

		/*
		 * Keep the caller MethodNode as the outer key, and record each call site
		 * and its constant under that method.
		 */
		final Map<MethodNode, Map<MethodInsnNode, Number>> garbageValueMap = new IdentityHashMap<>();

		for (ClassNode classNode : targetGroup.getClassNodes())
		{
			for (MethodNode method : classNode.methods)
			{
				if (method.instructions == null || method.instructions.size() == 0)
				{
					continue;
				}

				Frame<SourceValue>[] frames;
				try
				{
					frames = new Analyzer<>(new SourceInterpreter()).analyze(classNode.name, method);
				}
				catch (AnalyzerException | RuntimeException e)
				{
					log.debug("Failed to analyze {}{}: {}", method.name, method.desc, e.getMessage());
					continue;
				}

				AbstractInsnNode[] instructions = method.instructions.toArray();
				for (int i = 0; i < instructions.length; i++)
				{
					if (!(instructions[i] instanceof MethodInsnNode))
					{
						continue;
					}

					MethodInsnNode call = (MethodInsnNode) instructions[i];
					if (!targetGroup.isObfuscated(call.name))
					{
						continue;
					}

					Type[] arguments = Type.getArgumentTypes(call.desc);
					if (arguments.length == 0)
					{
						continue;
					}

					Type lastArgument = arguments[arguments.length - 1];
					if (lastArgument.getSort() != Type.INT && lastArgument.getSort() != Type.LONG && lastArgument.getSort() != Type.BYTE && lastArgument.getSort() != Type.SHORT)
					{
						continue;
					}

					Frame<SourceValue> frame = frames[i];
					if (frame == null || frame.getStackSize() == 0)
					{
						continue;
					}

					/*
					 * The last argument is the top stack value at the call.
					 * ASM represents long/double values as one stack value.
					 */
					SourceValue value = frame.getStack(frame.getStackSize() - 1);

					/*
					 * Only accept a value produced directly by one constant
					 * instruction. Multiple producers usually indicate a merge
					 * from different control-flow paths.
					 */
					if (value.insns.size() != 1)
					{
						continue;
					}

					AbstractInsnNode producer = value.insns.iterator().next();
					Number constant = readConstant(producer);
					if (constant != null)
					{
						garbageValueMap
							.computeIfAbsent(method, ignored -> new IdentityHashMap<>())
							.put(call, constant);
					}
				}
			}
		}

		for (Map.Entry<MethodNode, Map<MethodInsnNode, Number>> entry : garbageValueMap.entrySet())
		{
			// The outer key preserves the caller MethodNode for mapping/context.
			MethodNode callerMethod = entry.getKey();

			for (Map.Entry<MethodInsnNode, Number> callEntry : entry.getValue().entrySet())
			{
				MethodInsnNode call = callEntry.getKey();
				Number garbageValue = callEntry.getValue();

				MethodNode targetMethod = targetGroup.findMethod(call.owner, call.name, call.desc);

				if (targetMethod == null)
				{
					log.debug("Could not find target method {}.{}{} (called from {}{})", call.owner, call.name, call.desc, callerMethod.name, callerMethod.desc);
					continue;
				}

				if (AnnotationAdder.hasAnnotation(targetMethod.visibleAnnotations, AnnotationAdder.OBFUSCATED_SIGNATURE_ANNOTATION_DESC))
				{
					for (AnnotationNode annotation : targetMethod.visibleAnnotations)
					{
						if (annotation.desc.equals(AnnotationAdder.OBFUSCATED_SIGNATURE_ANNOTATION_DESC))
						{
							if (!annotation.values.contains("garbageValue"))
							{
								annotation.values.add("garbageValue");
								annotation.values.add(garbageValue);
								count++;
							}
						}
					}
					continue;
				}

				AnnotationAdder.addObfuscatedSignatureAnnotation(targetMethod, targetMethod.desc, garbageValue.toString());
				count++;
			}
		}

		log.info("Added: {} obfuscated signature annotations", count);
	}

	private static Number readConstant(AbstractInsnNode insn)
	{
		if (insn == null)
		{
			return null;
		}

		switch (insn.getOpcode())
		{
			case Opcodes.ICONST_M1:
				return -1;
			case Opcodes.ICONST_0:
				return 0;
			case Opcodes.ICONST_1:
				return 1;
			case Opcodes.ICONST_2:
				return 2;
			case Opcodes.ICONST_3:
				return 3;
			case Opcodes.ICONST_4:
				return 4;
			case Opcodes.ICONST_5:
				return 5;
			case Opcodes.LCONST_0:
				return 0L;
			case Opcodes.LCONST_1:
				return 1L;
			case Opcodes.BIPUSH:
			case Opcodes.SIPUSH:
				return ((IntInsnNode) insn).operand;
			case Opcodes.LDC:
				Object constant = ((LdcInsnNode) insn).cst;
				if (constant instanceof Integer || constant instanceof Long)
				{
					return (Number) constant;
				}
				return null;
			default:
				return null;
		}
	}
}
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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.melxin.asm.mapper.Mapper;
import net.melxin.asm.mapper.Remapper;
import net.melxin.asm.mapper.annotations.AnnotationAdder;
import net.melxin.asm.transformer.JarEntryGroup;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceInterpreter;
import org.objectweb.asm.tree.analysis.SourceValue;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class FieldMultiplicationMapper implements Mapper
{
	@Override
	public void map(JarEntryGroup sourceGroup, JarEntryGroup targetGroup, Remapper remapper)
	{
		int count = 0;

		final Map<String, FieldBean> fields = new LinkedHashMap<>();
		final Set<ClassNode> classNodes = targetGroup.getClassNodes();

		for (ClassNode classNode : classNodes)
		{
			for (FieldNode field : classNode.fields)
			{
				if (!"I".equals(field.desc) && !"J".equals(field.desc))
				{
					continue;
				}

				FieldBean fieldBean = new FieldBean(classNode.name, field);
				fields.put(key(classNode.name, field.name, field.desc), fieldBean);
			}
		}

		for (ClassNode classNode : classNodes)
		{
			for (MethodNode method : classNode.methods)
			{
				if (method.instructions == null)
				{
					continue;
				}

				AbstractInsnNode[] instructions = method.instructions.toArray();

				for (int i = 0; i < instructions.length; i++)
				{
					AbstractInsnNode insn = instructions[i];
					if (!(insn instanceof FieldInsnNode))
					{
						continue;
					}

					FieldInsnNode fi = (FieldInsnNode) insn;
					FieldBean bean = fields.get(key(fi.owner, fi.name, fi.desc));

					if (bean == null)
					{
						continue;
					}

					int mulOpcode = mulOpcode(fi.desc);
					if (mulOpcode == -1)
					{
						continue;
					}

					// Getters
					if (insn.getOpcode() == Opcodes.GETFIELD || insn.getOpcode() == Opcodes.GETSTATIC)
					{
						Number leftConstant = null;
						Number rightConstant = null;
						if (i > 0 && i + 1 < instructions.length && instructions[i + 1].getOpcode() == mulOpcode)
						{
							leftConstant = readConstant(instructions[i - 1], fi.desc);
						}
						else if (i + 2 < instructions.length && instructions[i + 2].getOpcode() == mulOpcode)
						{
							rightConstant = readConstant(instructions[i + 1], fi.desc);
						}

						if (leftConstant == null && rightConstant == null)
						{
							if (i > 0)
							{
								leftConstant = readConstant(instructions[i - 1], fi.desc);
							}

							if (i + 1 < instructions.length)
							{
								rightConstant = readConstant(instructions[i + 1], fi.desc);
							}
						}

						if (leftConstant != null && rightConstant == null)
						{
							//log.info("add getter left");
							bean.getters.add(leftConstant);
						}
						if (rightConstant != null && leftConstant == null)
						{
							//log.info("add getter right");
							bean.getters.add(rightConstant);
						}
						if (leftConstant != null && rightConstant != null)
						{
							//log.info("Two getter constants: {} ? {} {}.{} {}", leftConstant.intValue(), rightConstant.intValue(), classNode.name, method.name, method.desc);
						}
						if (leftConstant == null && rightConstant == null)
						{
							//log.info("No getter for: {}.{} {}", fi.owner, fi.name, fi.desc);
							//log.info("No getter for: {}.{} {} left: {} right: {}", fi.owner, fi.name, fi.desc, i - 1 > 0 ? instructions[i - 1] : "null", i + 1 < instructions.length ? instructions[i + 1] : "null");
						}
					}

					// Setters
					if (insn.getOpcode() == Opcodes.PUTFIELD || insn.getOpcode() == Opcodes.PUTSTATIC)
					{
						Number leftConstant = null;
						Number rightConstant = null;
						if (i - 2 >= 0 && instructions[i - 1].getOpcode() == mulOpcode)
						{
							leftConstant = readConstant(instructions[i - 2], fi.desc);
						}

						if (leftConstant == null && rightConstant == null)
						{
							if (i > 0)
							{
								leftConstant = readConstant(instructions[i - 1], fi.desc);
							}

							if (i + 1 < instructions.length)
							{
								rightConstant = readConstant(instructions[i + 1], fi.desc);
							}
						}

						if (leftConstant != null && rightConstant == null)
						{
							bean.setters.add(leftConstant);
						}
						if (rightConstant != null && leftConstant == null)
						{
							bean.setters.add(rightConstant);
						}
						if (leftConstant != null && rightConstant != null)
						{
							//log.info("Two setter constants: {} ? {} {}.{} {}", leftConstant.intValue(), rightConstant.intValue(), classNode.name, method.name, method.desc);
						}
						if (leftConstant == null && rightConstant == null)
						{
							//log.info("No setter for: {}.{} {}", fi.owner, fi.name, fi.desc);
						}
					}
				}
			}
		}

		// Analyzer
		final Analyzer<SourceValue> analyzer = new Analyzer<>(new SourceInterpreter());
		for (ClassNode classNode : classNodes)
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
					frames = analyzer.analyze(classNode.name, method);
				}
				catch (AnalyzerException e)
				{
					log.error("Failed to analyze {}.{} {}", classNode.name, method.name, method.desc, e);
					continue;
				}

				AbstractInsnNode[] instructions = method.instructions.toArray();

				for (int i = 0; i < instructions.length; i++)
				{
					AbstractInsnNode insn = instructions[i];
					int opcode = insn.getOpcode();

					if (opcode != Opcodes.IMUL && opcode != Opcodes.LMUL)
					{
						continue;
					}

					Frame<SourceValue> frame = frames[i];
					if (frame == null || frame.getStackSize() < 2)
					{
						continue;
					}

					SourceValue right = frame.getStack(frame.getStackSize() - 1);
					SourceValue left = frame.getStack(frame.getStackSize() - 2);
					String desc = opcode == Opcodes.IMUL ? "I" : "J";

					Set<Number> leftConstants = new LinkedHashSet<>();
					for (AbstractInsnNode source : left.insns)
					{
						int sourceOpcode = source.getOpcode();
						Number constant = null;

						if ("I".equals(desc))
						{
							if (sourceOpcode >= Opcodes.ICONST_M1 && sourceOpcode <= Opcodes.ICONST_5)
							{
								constant = sourceOpcode - Opcodes.ICONST_0;
							}
							else if (sourceOpcode == Opcodes.BIPUSH || sourceOpcode == Opcodes.SIPUSH)
							{
								constant = ((IntInsnNode) source).operand;
							}
							else if (source instanceof LdcInsnNode && ((LdcInsnNode) source).cst instanceof Integer)
							{
								constant = (Integer) ((LdcInsnNode) source).cst;
							}
						}
						else
						{
							if (sourceOpcode == Opcodes.LCONST_0)
							{
								constant = 0L;
							}
							else if (sourceOpcode == Opcodes.LCONST_1)
							{
								constant = 1L;
							}
							else if (source instanceof LdcInsnNode && ((LdcInsnNode) source).cst instanceof Long)
							{
								constant = (Long) ((LdcInsnNode) source).cst;
							}
						}

						if (constant != null)
						{
							leftConstants.add(constant);
						}
					}

					Set<Number> rightConstants = new LinkedHashSet<>();
					for (AbstractInsnNode source : right.insns)
					{
						int sourceOpcode = source.getOpcode();
						Number constant = null;

						if ("I".equals(desc))
						{
							if (sourceOpcode >= Opcodes.ICONST_M1 && sourceOpcode <= Opcodes.ICONST_5)
							{
								constant = sourceOpcode - Opcodes.ICONST_0;
							}
							else if (sourceOpcode == Opcodes.BIPUSH || sourceOpcode == Opcodes.SIPUSH)
							{
								constant = ((IntInsnNode) source).operand;
							}
							else if (source instanceof LdcInsnNode && ((LdcInsnNode) source).cst instanceof Integer)
							{
								constant = (Integer) ((LdcInsnNode) source).cst;
							}
						}
						else
						{
							if (sourceOpcode == Opcodes.LCONST_0)
							{
								constant = 0L;
							}
							else if (sourceOpcode == Opcodes.LCONST_1)
							{
								constant = 1L;
							}
							else if (source instanceof LdcInsnNode && ((LdcInsnNode) source).cst instanceof Long)
							{
								constant = (Long) ((LdcInsnNode) source).cst;
							}
						}

						if (constant != null)
						{
							rightConstants.add(constant);
						}
					}

					// Getter: field read multiplied by a constant.
					for (AbstractInsnNode source : left.insns)
					{
						if (source instanceof FieldInsnNode && (source.getOpcode() == Opcodes.GETFIELD || source.getOpcode() == Opcodes.GETSTATIC))
						{
							FieldInsnNode fieldInsn = (FieldInsnNode) source;
							if (desc.equals(fieldInsn.desc) && !rightConstants.isEmpty())
							{
								FieldBean bean = fields.get(key(fieldInsn.owner, fieldInsn.name, fieldInsn.desc));
								if (bean != null)
								{
									bean.getters.addAll(rightConstants);
								}
							}
						}
					}

					for (AbstractInsnNode source : right.insns)
					{
						if (source instanceof FieldInsnNode && (source.getOpcode() == Opcodes.GETFIELD || source.getOpcode() == Opcodes.GETSTATIC))
						{
							FieldInsnNode fieldInsn = (FieldInsnNode) source;
							if (desc.equals(fieldInsn.desc) && !leftConstants.isEmpty())
							{
								FieldBean bean = fields.get(key(fieldInsn.owner, fieldInsn.name, fieldInsn.desc));
								if (bean != null)
								{
									bean.getters.addAll(leftConstants);
								}
							}
						}
					}

					// Setter: multiplication result stored into a field.
					AbstractInsnNode next = insn.getNext();
					while (next != null && next.getOpcode() < 0)
					{
						next = next.getNext();
					}

					if (next instanceof FieldInsnNode && (next.getOpcode() == Opcodes.PUTFIELD || next.getOpcode() == Opcodes.PUTSTATIC))
					{
						FieldInsnNode fieldInsn = (FieldInsnNode) next;
						if (desc.equals(fieldInsn.desc))
						{
							FieldBean bean = fields.get(key(fieldInsn.owner, fieldInsn.name, fieldInsn.desc));
							if (bean != null)
							{
								bean.setters.addAll(leftConstants);
								bean.setters.addAll(rightConstants);
							}
						}
					}
				}
			}
		}

		for (Map.Entry<String, FieldBean> entry : fields.entrySet())
		{
			FieldBean bean = entry.getValue();

			// Only setters, should calculate the getters?
			if (bean.getters.isEmpty() && !bean.setters.isEmpty())
			{
				//log.info("No getter: {}.{} {}", bean.owner, bean.fieldNode.name, bean.fieldNode.desc);
			}

			// Only getters, should calculate the setters?
			if (!bean.getters.isEmpty() && bean.setters.isEmpty())
			{
				//log.info("empty!");
			}

			inner:
			for (Number getter : bean.getters)
			{
				for (Number setter : bean.setters)
				{
					if (isValidNumber(getter) && isValidNumber(setter) && areModularInverses(getter, setter))
					{
						//log.info("Field: {} getter: {} setter: {} (modular inverse)", entry.getKey(), getter, setter);
						if ("J".equals(bean.fieldNode.desc))
						{
							AnnotationAdder.addObfuscatedGetterAnnotation(bean.fieldNode, getter.longValue());
						}
						else
						{
							AnnotationAdder.addObfuscatedGetterAnnotation(bean.fieldNode, getter.intValue());
						}
						count++;
						break inner;
					}
				}
			}
		}
		log.info("Added: {} / {} obfuscated getter annotations", count, fields.size());
	}

	private static String key(String owner, String name, String desc)
	{
		return owner + "#" + name + "#" + desc;
	}

	private static int mulOpcode(String desc)
	{
		if ("I".equals(desc))
		{
			return Opcodes.IMUL;
		}
		if ("J".equals(desc))
		{
			return Opcodes.LMUL;
		}
		return -1;
	}

	private static Number readConstant(AbstractInsnNode insn, String desc)
	{
		if ("I".equals(desc))
		{
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
				default:
					break;
			}

			if (insn instanceof IntInsnNode
				&& (insn.getOpcode() == Opcodes.BIPUSH
				|| insn.getOpcode() == Opcodes.SIPUSH))
			{
				return ((IntInsnNode) insn).operand;
			}

			if (insn instanceof LdcInsnNode)
			{
				Object value = ((LdcInsnNode) insn).cst;
				return value instanceof Integer ? (Integer) value : null;
			}
		}
		else if ("J".equals(desc))
		{
			if (insn.getOpcode() == Opcodes.LCONST_0)
			{
				return 0L;
			}
			if (insn.getOpcode() == Opcodes.LCONST_1)
			{
				return 1L;
			}

			if (insn instanceof LdcInsnNode)
			{
				Object value = ((LdcInsnNode) insn).cst;
				return value instanceof Long ? (Long) value : null;
			}
		}

		return null;
	}

	private static boolean isValidNumber(Number value)
	{
		if (value instanceof Integer)
		{
			return value.intValue() != 0 && value.intValue() != 1;
		}
		return value instanceof Long
			&& value.longValue() != 0L
			&& value.longValue() != 1L;
	}

	private static boolean areModularInverses(Number getter, Number setter)
	{
		if (getter instanceof Integer && setter instanceof Integer)
		{
			int g = getter.intValue();
			int s = setter.intValue();
			return (g & 1) != 0 && (s & 1) != 0 && g * s == 1;
		}

		if (getter instanceof Long && setter instanceof Long)
		{
			long g = getter.longValue();
			long s = setter.longValue();
			return (g & 1L) != 0L && (s & 1L) != 0L && g * s == 1L;
		}

		return false;
	}

	@Getter
	@RequiredArgsConstructor
	private static class FieldBean
	{
		private final String owner;
		private final FieldNode fieldNode;
		private final Set<Number> getters = new java.util.LinkedHashSet<>();
		private final Set<Number> setters = new java.util.LinkedHashSet<>();
	}
}
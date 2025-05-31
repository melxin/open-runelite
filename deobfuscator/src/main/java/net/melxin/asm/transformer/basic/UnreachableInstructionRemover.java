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
package net.melxin.asm.transformer.basic;

import lombok.extern.slf4j.Slf4j;
import net.melxin.asm.transformer.JarEntryTransformer;
import net.melxin.asm.transformer.JarEntryGroup;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

@Slf4j
public class UnreachableInstructionRemover implements JarEntryTransformer
{
	@Override
	public void transform(JarEntryGroup jarEntryGroup)
	{
		int removed = 0;
		for (ClassNode classNode : jarEntryGroup.getClassNodes())
		{
			for (MethodNode method : classNode.methods)
			{
				final AbstractInsnNode[] instructions = method.instructions.toArray();
				final Frame<BasicValue>[] frames;
				try
				{
					// Analyze control flow
					frames = new Analyzer<>(new BasicInterpreter()).analyze(classNode.name, method);
				}
				catch (AnalyzerException e)
				{
					log.error("", e);
					continue;
				}

				for (int i = 0; i < frames.length; i++)
				{
					if (frames[i] == null)
					{
						// Remove instruction
						final AbstractInsnNode insn = instructions[i];
						method.instructions.remove(insn);
						removed++;
					}
				}
			}
		}
		log.info("Removed: {} unreachable instructions", removed);
	}
}
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
package net.melxin.asm.diagnostic.reporters;

import lombok.extern.slf4j.Slf4j;
import net.melxin.asm.diagnostic.DiagnosticJarEntryReporter;
import net.melxin.asm.transformer.JarEntryGroup;
import org.objectweb.asm.commons.CodeSizeEvaluator;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

@Slf4j
public class LargeMethodReporter implements DiagnosticJarEntryReporter
{
	@Override
	public void report(JarEntryGroup jarEntryGroup)
	{
		int REPORT_THRESHOLD = 65_535; // 65,535 = JVM limit

		for (ClassNode classNode : jarEntryGroup.getClassNodes())
		{
			for (MethodNode method : classNode.methods)
			{
				CodeSizeEvaluator evaluator = new CodeSizeEvaluator(null);
				method.accept(evaluator);

				int minSize = evaluator.getMinSize();
				int maxSize = evaluator.getMaxSize();

				if (maxSize >= REPORT_THRESHOLD)
				{
					log.warn("Large method: {}.{}{} — estimated {}–{} bytes", classNode.name, method.name, method.desc, minSize, maxSize);
				}
			}
		}
	}
}
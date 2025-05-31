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
package net.melxin.asm.transformer;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.melxin.asm.diagnostic.Diagnostics;
import net.melxin.asm.transformer.runelite.RuneLiteAdjacentTryCatchBlockMerger;
import net.melxin.asm.transformer.basic.DUPPOPRemover;
import net.melxin.asm.transformer.basic.DecompilerTrapRemover;
import net.melxin.asm.transformer.basic.EmptyLocalVarTableRemover;
import net.melxin.asm.transformer.basic.EmptyMethodRemover;
import net.melxin.asm.transformer.basic.EmptyTryCatchExceptionBlockRemover;
import net.melxin.asm.transformer.basic.ExprArgOrder;
import net.melxin.asm.transformer.basic.IllegalVarArgsRemover;
import net.melxin.asm.transformer.runelite.RuneLiteGetPathErrorFixer;
import net.melxin.asm.transformer.basic.IllegalStateExceptionRemover;
import net.melxin.asm.transformer.basic.IntIdentityOperationRemover;
import net.melxin.asm.transformer.basic.NopInstructionRemover;
import net.melxin.asm.transformer.basic.RedundantCastRemover;
import net.melxin.asm.transformer.basic.UnusedLocalVarRemover;
import net.melxin.asm.transformer.basic.UnreachableInstructionRemover;
import net.melxin.asm.transformer.basic.RedundantGotoRemover;
import net.melxin.asm.transformer.basic.RuntimeTryCatchExceptionBlockRemover;
import net.melxin.asm.transformer.basic.UnusedClassRemover;
import net.melxin.asm.transformer.basic.UnusedFieldRemover;
import net.melxin.asm.transformer.basic.UnusedMethodRemover;
import net.melxin.asm.mapper.Mappings;
import net.melxin.asm.transformer.runelite.RuneLiteConstantDynamicTransformer;
import net.melxin.asm.transformer.runelite.RuneLiteNamedAnnotationsRemover;
import net.melxin.asm.transformer.basic.SortMembersByName;
import java.io.File;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Main
{
	@RequiredArgsConstructor
	@Getter
	private enum Option
	{
		INPUT("input", "The input jar file."),
		OUTPUT("output", "The output jar file."),
		MAPPING("mapping", "The previous mappings jar file."),
		MINIMAL("minimal", "The minimal mode.");

		private final String option;
		private final String description;
	}

	public static void main(String[] args)
	{
		final long startTime = System.nanoTime();

		if (args.length < 2)
		{
			args = new String[]{"--input", "input.jar", "--output", "output.jar"/*,"--maping", "mapping.jar"*/ /*, "--minimal"*/};
			log.info("Usage: java -jar asm-transformer.jar --input <input.jar> --output <output.jar> \nOptional: --mapping <mapping.jar> --minimal");
		}

		log.info("Args: {}", Arrays.toString(args));

		final OptionParser parser = new OptionParser(false);
		parser.accepts(Option.INPUT.getOption(), Option.INPUT.getDescription())
			.withRequiredArg()
			.ofType(String.class)
			.required()
			.defaultsTo("input.jar");
		parser.accepts(Option.OUTPUT.getOption(), Option.OUTPUT.getDescription())
			.withRequiredArg()
			.ofType(String.class)
			.required()
			.defaultsTo("output.jar");
		parser.accepts(Option.MAPPING.getOption(), Option.MAPPING.getDescription())
			.withRequiredArg()
			.ofType(String.class)
			.defaultsTo("mapping.jar");
		parser.accepts(Option.MINIMAL.getOption(), Option.MINIMAL.getDescription());

		final OptionSet options = parser.parse(args);

		if (options.has(Option.MINIMAL.getOption()))
		{
			log.info("Minimal mode");
			Minimal.main(args);
			return;
		}

		// Load entries
		final JarEntryGroup jarEntryGroup = new JarEntryGroup(new File((String) options.valueOf(Option.INPUT.getOption())));

		// Apply transformations
		jarEntryTransformers.forEach(transformer ->
		{
			log.debug("Running transformer: {}", transformer.name());
			transformer.transform(jarEntryGroup);
		});

		// Apply mappings
		final Mappings mappings = new Mappings(options.has(Option.MAPPING.getOption()) ? new JarEntryGroup(new File((String) options.valueOf(Option.MAPPING.getOption()))) : null, jarEntryGroup);
		mappings.map();

		// Write output jar
		jarEntryGroup.writeOutputJar(new File((String) options.valueOf(Option.OUTPUT.getOption())));

		log.info("Transformation completed in {} ms!", (System.nanoTime() - startTime) / 1_000_000);

		// Diagnostic reporters on the output jar
		Diagnostics.report(new File((String) options.valueOf(Option.OUTPUT.getOption())));
	}

	private static final List<JarEntryTransformer> jarEntryTransformers = List.of(
		// Basic
		new UnusedClassRemover(),
		new UnusedMethodRemover(),
		new UnusedFieldRemover(),
		new SortMembersByName(),
		new RuntimeTryCatchExceptionBlockRemover(),
		new IllegalStateExceptionRemover(),
		new UnreachableInstructionRemover(),
		new RedundantGotoRemover(),
		new ExprArgOrder(),

		new NopInstructionRemover(),
		new UnusedLocalVarRemover(),
		new DUPPOPRemover(),
		new EmptyLocalVarTableRemover(),
		new IntIdentityOperationRemover(),
		new EmptyTryCatchExceptionBlockRemover(),
		new EmptyMethodRemover(),
		new RedundantCastRemover(),
		new DecompilerTrapRemover(),
		new IllegalVarArgsRemover(),

		// RuneLite
		new RuneLiteNamedAnnotationsRemover(),
		new RuneLiteConstantDynamicTransformer(),
		new RuneLiteGetPathErrorFixer(),
		new RuneLiteAdjacentTryCatchBlockMerger()
	);
}
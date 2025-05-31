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
package net.runelite;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import java.util.HashMap;
import java.util.Map;

public class NonLoadingClassWriter extends ClassWriter
{
	private final Map<String, String> superClassCache = new HashMap<>();

	public NonLoadingClassWriter(int flags)
	{
		super(flags);
	}

	public NonLoadingClassWriter(ClassReader reader, int flags)
	{
		super(reader, flags);
	}

	@Override
	protected String getCommonSuperClass(String type1, String type2)
	{
		// Handle the base case of Object
		if ("java/lang/Object".equals(type1) || "java/lang/Object".equals(type2))
		{
			return "java/lang/Object";
		}

		// Check cache
		String key = type1 + ":" + type2;
		if (superClassCache.containsKey(key))
		{
			return superClassCache.get(key);
		}

		// Infer common superclass heuristically
		String result = inferCommonSuperClass(type1, type2);
		superClassCache.put(key, result);
		return result;
	}

	private String inferCommonSuperClass(String type1, String type2)
	{
		String package1 = getPackageName(type1);
		String package2 = getPackageName(type2);

		if (package1.equals(package2))
		{
			// Same package: assume sibling classes share a common superclass
			return type1;
		}

		// Different packages: default to Object
		return "java/lang/Object";
	}

	private String getPackageName(String internalName)
	{
		int lastSlash = internalName.lastIndexOf('/');
		if (lastSlash == -1)
		{
			return ""; // default package
		}
		return internalName.substring(0, lastSlash);
	}
}
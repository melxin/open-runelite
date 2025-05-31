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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.CheckClassAdapter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import javax.annotation.Nullable;

@Slf4j
public class JarEntryGroup
{
	// Map to hold non-class entries
	@Getter(AccessLevel.PUBLIC)
	private final Map<String, byte[]> nonClassEntries = new HashMap<>();

	// Set to hold class entries as ClassNode objects
	@Getter(AccessLevel.PUBLIC)
	private final Set<ClassNode> classNodes = new HashSet<>();

	/**
	 * Constructor
	 *
	 * @param inputJar
	 */
	public JarEntryGroup(File inputJar)
	{
		this.loadJar(inputJar);
	}

	/**
	 * Load the jar file
	 *
	 * @param inputJar
	 */
	private void loadJar(File inputJar)
	{
		log.info("Load jar: {}", inputJar.getAbsolutePath());
		try (JarFile jarFile = new JarFile(inputJar))
		{
			for (Enumeration<JarEntry> it = jarFile.entries(); it.hasMoreElements(); )
			{
				final JarEntry entry = it.nextElement();
				try (InputStream is = jarFile.getInputStream(entry))
				{
					if (!entry.getName().endsWith(".class"))
					{
						// Store non-class entries
						final byte[] data = is.readAllBytes();
						nonClassEntries.put(entry.getName(), data);
						log.debug("Load non-class entry: {}", entry.getName());
					}
					else
					{
						// Store class entries
						final ClassReader reader = new ClassReader(is);
						final ClassNode classNode = new ClassNode(Opcodes.ASM9);
						reader.accept(classNode, ClassReader.SKIP_FRAMES);
						classNodes.add(classNode);
						log.debug("Load class node: {}", classNode.name);
					}
				}
			}
		}
		catch (IOException e)
		{
			log.error("Failed to load jar: {}", inputJar.getAbsolutePath(), e);
		}
	}

	/**
	 * Write output jar
	 *
	 * @param outputJar
	 */
	public void writeOutputJar(File outputJar)
	{
		log.info("Write output jar: {}", outputJar.getAbsolutePath());
		try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(outputJar)))
		{
			// (Optional) set compression level (0-9)
			jos.setLevel(Deflater.BEST_COMPRESSION);

			// Write non-class entries
			for (Map.Entry<String, byte[]> nonClassEntry : nonClassEntries.entrySet())
			{
				final JarEntry newEntry = new JarEntry(nonClassEntry.getKey());
				newEntry.setMethod(ZipEntry.DEFLATED); // Optional
				jos.putNextEntry(newEntry);
				jos.write(nonClassEntry.getValue());
				jos.closeEntry();
				log.debug("Write non-class entry: {}", newEntry.getName());
			}

			// Write class entries
			for (ClassNode classNode : classNodes)
			{
				final JarEntry newEntry = new JarEntry(classNode.name.replace('.', '/') + ".class");
				newEntry.setMethod(ZipEntry.DEFLATED); // Optional
				jos.putNextEntry(newEntry);
				final ClassWriter writer = new NonLoadingClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
				classNode.accept(writer);
				final byte[] classFileBuffer = writer.toByteArray();
				this.validateDataFlow(newEntry.getName(), classFileBuffer);
				jos.write(classFileBuffer);
				jos.closeEntry();
				log.debug("Write class entry: {}", newEntry.getName());
			}
		}
		catch (IOException e)
		{
			log.error("Failed to write output jar: {}", outputJar.getAbsolutePath(), e);
		}
	}

	/**
	 * Validate data flow
	 *
	 * @param name the class name
	 * @param data the class file
	 */
	private void validateDataFlow(String name, byte[] data)
	{
		try
		{
			final ClassReader cr = new ClassReader(data);
			final ClassWriter cw = new NonLoadingClassWriter(cr, 0);
			final ClassVisitor cv = new CheckClassAdapter(cw, true);
			cr.accept(cv, 0);
		}
		catch (Exception ex)
		{
			log.warn("Class: {} failed validation", name, ex);
		}
	}

	/**
	 * Find a class by name
	 *
	 * @param name the class name
	 * @return the class as ClassNode object
	 */
	@Nullable
	public ClassNode findClass(String name)
	{
		for (ClassNode classNode : classNodes)
		{
			if (classNode.name.equals(name))
			{
				return classNode;
			}
		}
		return null;
	}

	@Nullable
	public FieldNode findField(String owner, String name, String desc)
	{
		for (ClassNode classNode : classNodes)
		{
			if (!classNode.name.equals(owner))
			{
				continue;
			}

			for (FieldNode fieldNode : classNode.fields)
			{
				if (fieldNode.name.equals(name) && fieldNode.desc.equals(desc))
				{
					return fieldNode;
				}
			}
		}
		return null;
	}

	@Deprecated(forRemoval = true)
	@Nullable
	public FieldNode findField(ClassNode classNode, String name, boolean isStatic)
	{
		final List<FieldNode> fieldNodes = classNode.fields;
		for (FieldNode fieldNode : fieldNodes)
		{
			final boolean isStaticField = (fieldNode.access & Opcodes.ACC_STATIC) != 0;
			if (isStatic != isStaticField)
			{
				continue;
			}

			if (fieldNode.name.equals(name))
			{
				return fieldNode;
			}
		}
		return null;
	}

	@Nullable
	public MethodNode findMethod(String owner, String name, String desc)
	{
		for (ClassNode classNode : classNodes)
		{
			if (!classNode.name.equals(owner))
			{
				continue;
			}

			for (MethodNode methodNode : classNode.methods)
			{
				if (methodNode.name.equals(name) && methodNode.desc.equals(desc))
				{
					return methodNode;
				}
			}
		}
		return null;
	}

	@Deprecated(forRemoval = true)
	@Nullable
	public MethodNode findMethod(ClassNode classNode, String name, boolean isStatic)
	{
		final List<MethodNode> methodNodes = classNode.methods;
		for (MethodNode methodNode : methodNodes)
		{
			final boolean isStaticMethod = (methodNode.access & Opcodes.ACC_STATIC) != 0;
			if (isStatic != isStaticMethod)
			{
				continue;
			}

			if (methodNode.name.equals(name))
			{
				return methodNode;
			}
		}
		return null;
	}

	@Nullable
	public ClassNode getOwner(FieldNode fieldNode)
	{
		for (ClassNode classNode : classNodes)
		{
			for (FieldNode field : classNode.fields)
			{
				if (field.equals(fieldNode))
				{
					return classNode;
				}
			}
		}
		return null;
	}

	@Nullable
	public ClassNode getOwner(MethodNode methodNode)
	{
		for (ClassNode classNode : classNodes)
		{
			for (MethodNode method : classNode.methods)
			{
				if (method.equals(methodNode))
				{
					return classNode;
				}
			}
		}
		return null;
	}

	public Set<FieldNode> getAllFields()
	{
		final Set<FieldNode> fields = new HashSet<>();
		for (ClassNode classNode : classNodes)
		{
			fields.addAll(classNode.fields);
		}
		return fields;
	}

	public Set<MethodNode> getAllMethods()
	{
		final Set<MethodNode> methods = new HashSet<>();
		for (ClassNode classNode : classNodes)
		{
			methods.addAll(classNode.methods);
		}
		return methods;
	}

	public boolean isObfuscated(String name)
	{
		if (name.length() <= 3 || name.startsWith("rl"))
		{
			return !name.equals("run")
				&& !name.equals("add")
				&& !name.equals("get")
				&& !name.equals("set")
				&& !name.equals("put");
		}
		return name.startsWith("method")
			|| name.startsWith("vmethod")
			|| name.startsWith("field")
			|| name.startsWith("class")
			|| name.startsWith("__");
	}
}
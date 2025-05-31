package net.melxin.asm.transformer.unused;

import net.melxin.asm.transformer.JarEntryGroup;
import net.melxin.asm.transformer.JarEntryTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class UnusedMethodParametersRemover implements JarEntryTransformer
{
	@Override
	public void transform(JarEntryGroup jarEntryGroup)
	{
		for (ClassNode classNode : jarEntryGroup.getClassNodes())
		{
			if (classNode.methods != null)
			{
				for (MethodNode method : classNode.methods)
				{
					// Skip constructors, abstract, native, bridge, synthetic
					if ((method.access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE | Opcodes.ACC_BRIDGE | Opcodes.ACC_SYNTHETIC)) != 0)
						continue;

					List<Integer> unusedParams = findUnusedParameters(method);
					if (!unusedParams.isEmpty())
					{
						removeUnusedParameters(classNode, method, unusedParams);
					}
				}
			}
		}
	}

	private List<Integer> findUnusedParameters(MethodNode method)
	{
		Type methodType = Type.getMethodType(method.desc);
		Type[] argTypes = methodType.getArgumentTypes();

		int paramCount = argTypes.length;
		boolean[] used = new boolean[paramCount];

		boolean isStatic = (method.access & Opcodes.ACC_STATIC) != 0;
		int paramOffset = isStatic ? 0 : 1;

		for (AbstractInsnNode insn : method.instructions.toArray())
		{
			if (insn instanceof VarInsnNode)
			{
				VarInsnNode varInsn = (VarInsnNode) insn;
				int index = varInsn.var;
				if (!isStatic && index == 0) continue; // 'this'
				int paramIndex = index - paramOffset;
				if (paramIndex >= 0 && paramIndex < paramCount)
				{
					used[paramIndex] = true;
				}
			}
		}

		List<Integer> unused = new ArrayList<>();
		for (int i = 0; i < paramCount; i++)
		{
			if (!used[i])
			{
				unused.add(i);
			}
		}
		return unused;
	}

	private void removeUnusedParameters(ClassNode classNode, MethodNode method, List<Integer> unusedParams)
	{
		Type methodType = Type.getMethodType(method.desc);
		Type[] argTypes = methodType.getArgumentTypes();

		int paramCount = argTypes.length;
		boolean isStatic = (method.access & Opcodes.ACC_STATIC) != 0;
		int paramOffset = isStatic ? 0 : 1;

		// Map from old index to new index
		Map<Integer, Integer> oldToNewIndex = new HashMap<>();
		List<Type> newArgTypes = new ArrayList<>();
		int newIndex = 0;

		for (int i = 0; i < paramCount; i++)
		{
			if (unusedParams.contains(i))
				continue;
			oldToNewIndex.put(i, newIndex++);
			newArgTypes.add(argTypes[i]);
		}

		// Update method descriptor
		String newDesc = Type.getMethodDescriptor(methodType.getReturnType(), newArgTypes.toArray(new Type[0]));
		method.desc = newDesc;

		// Remove local variables for unused params
		if (method.localVariables != null)
		{
			Iterator<LocalVariableNode> it = method.localVariables.iterator();
			while (it.hasNext())
			{
				LocalVariableNode localVar = it.next();
				int index = localVar.index;
				if (!isStatic && index == 0) continue; // 'this'
				int paramIdx = index - paramOffset;
				if (unusedParams.contains(paramIdx))
					it.remove();
			}
		}

		// Remove instructions for unused parameters and update variables
		ListIterator<AbstractInsnNode> insnIt = method.instructions.iterator();
		while (insnIt.hasNext())
		{
			AbstractInsnNode insn = insnIt.next();

			if (insn instanceof VarInsnNode)
			{
				VarInsnNode varInsn = (VarInsnNode) insn;
				int index = varInsn.var;
				if (!isStatic && index == 0) continue; // 'this'
				int paramIdx = index - paramOffset;
				if (unusedParams.contains(paramIdx))
				{
					insnIt.remove(); // Remove instruction referencing unused param
				}
				else if (oldToNewIndex.containsKey(paramIdx))
				{
					// Update index to new index
					varInsn.var = oldToNewIndex.get(paramIdx) + (isStatic ? 0 : 1);
				}
			}
		}
	}
}
package net.runelite.client.plugins.openrl.api.reflection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Field;

@Slf4j
public class PlatformInfoReflection
{
	@AllArgsConstructor
	@Getter
	private enum PlatformInfo
	{
		OPERATING_SYSTEM("ba", -1628705917, 1),
		ARCH_64("bh", -1, 2),
		OPERATING_SYSTEM_VERSION("bg", -1759820623, 3),

		JAVA_VENDOR("bt", 272699405, 4),
		JAVA_MAJOR("bs", -206669209, 5),
		JAVA_MINOR("bu", 381637045, 6),
		JAVA_PATCH("bp", -1719395127, 7),

		BOOLEAN_1("bz", -1, 8),
		MAX_MEMORY("bk", 1747448955, 9),
		CPU_CORES("bb", 398288957, 10),
		VAR_11("by", 340316445, 11),
		CLOCK_SPEED("cg", -826920955, 12),
		VAR_13("cs", -1, 13),
		VAR_14("ce", -1, 14),
		VAR_15("ci", -1, 15),
		VAR_16("cq", -1, 16),
		VAR_17("cj", 121894137, 17),
		VAR_18("cm", 1023852999, 18),
		VAR_19("cu", 1320473145, 19),

		VAR_20("cl", 1775238883, 20),
		VAR_21("ck", -1, 21),
		VAR_22("cn", -1, 22),
		VAR_23("ch", -1, 23),
		VAR_24("cx", -603081979, 24),
		VAR_25("cv", -1, 25),
		VAR_26("cc", -1, 26);

		private final String fieldName;
		private final int multiplier;
		private final int varIndex;
	}

	public static void print()
	{
		try
		{
			final Field staticPlatformInfoInstanceField = Class.forName("cl").getDeclaredField("wo");
			staticPlatformInfoInstanceField.setAccessible(true);
			final Object platformInfoInstance = staticPlatformInfoInstanceField.get(null);
			staticPlatformInfoInstanceField.setAccessible(false);
			final Class<?> platformInfoClazz = Class.forName("ut");

			for (PlatformInfo platformInfo : PlatformInfo.values())
			{
				final int multiplier = platformInfo.getMultiplier();
				final Field f = platformInfoClazz.getDeclaredField(platformInfo.getFieldName());
				//boolean isStatic = Modifier.isStatic(f.getModifiers());
				if (f == null)
				{
					log.warn("Failed to find field: {}", platformInfo.getFieldName());
					continue;
				}

				f.setAccessible(true);
				final Object result;
				if (multiplier == -1)
				{
					result = f.get(platformInfoInstance);
				}
				else
				{
					result = f.getInt(platformInfoInstance) * multiplier;
				}
				f.setAccessible(false);
				log.info("Found: {} = {}", platformInfo, result);
			}
		}
		catch (Exception e)
		{
			log.error("Failed to print platform info", e);
		}
	}
}

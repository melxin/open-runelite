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
		OPERATING_SYSTEM("be", 1524586385, 1), // WINDOWS=1, MAC_OS=2, LINUX=3, OTHER=4
		ARCH_64("bh", -1, 2),
		OPERATING_SYSTEM_VERSION("bk", -1387405423, 3),

		JAVA_VENDOR("bp", -1498585167, 4), // SUN=1, MICROSOFT=2, APPLE=3, ORACLE=5, OTHER=4
		JAVA_MAJOR("br", 1021463221, 5),
		JAVA_MINOR("bv", -109020011, 6),
		JAVA_PATCH("bj", -459953697, 7),

		VAR_8("bm", -1, 8),
		MAX_MEMORY("bf", 145813101, 9),
		CPU_CORES("bw", -539507735, 10),
		VAR_11("bc", -1721208821, 11),
		CLOCK_SPEED("cw", -747966881, 12),
		VAR_13("cz", -1, 13),
		VAR_14("cf", -1, 14),
		VAR_15("cg", -1, 15),
		VAR_16("ci", -1, 16),
		VAR_17("cp", 1443667205, 17),
		VAR_18("cm", 580651873, 18),
		VAR_19("cc", -986889351, 19),

		VAR_20("cj", 847732193, 20),
		VAR_21("ce", -1, 21),
		VAR_22("cr", -1, 22),
		VAR_23("cb", -1, 23),
		VAR_24("cu", 638903767, 24),
		VAR_25("cq", -1, 25),
		VAR_26("cn", -1, 26);

		private final String fieldName;
		private final int multiplier;
		private final int varIndex;
	}

	public static void print()
	{
		try
		{
			final Field staticPlatformInfoInstanceField = Class.forName("le").getDeclaredField("wc");
			staticPlatformInfoInstanceField.setAccessible(true);
			final Object platformInfoInstance = staticPlatformInfoInstanceField.get(null);
			staticPlatformInfoInstanceField.setAccessible(false);
			final Class<?> platformInfoClazz = Class.forName("vj");

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

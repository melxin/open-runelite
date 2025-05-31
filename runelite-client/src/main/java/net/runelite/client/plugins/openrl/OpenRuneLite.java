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
package net.runelite.client.plugins.openrl;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import net.runelite.client.RuneLite;

@Slf4j
public class OpenRuneLite
{
	public static final String uuid = UUID.randomUUID().toString();

	public static final File ROOT_DIR = new File(RuneLite.RUNELITE_DIR, "openrl");
	public static final File EXTERNAL_PLUGINS_DIR = new File(ROOT_DIR, "plugins");
	public static final String PLUGIN_DEVELOPMENT_PATH = "plugin.development.path";

	@Getter(AccessLevel.PACKAGE)
	private static final Properties properties = new Properties();
	public static final String SYSTEM_VERSION;
	public static final String SYSTEM_API_VERSION;

	static
	{
		try
		{
			properties.load(OpenRuneLite.class.getResourceAsStream("/open-runelite.properties"));
		}
		catch (IOException e)
		{
			log.error("Failed to load properties", e);
		}

		SYSTEM_VERSION = properties.getProperty("oprl.version", "0.0.1");
		SYSTEM_API_VERSION = properties.getProperty("oprl.api.version", "1.0.0");
	}

	public static String[] getPluginDevelopmentPath()
	{
		// First check if property supplied as environment variable PLUGIN_DEVELOPMENT_PATHS
		String developmentPluginPaths = System.getenv(PLUGIN_DEVELOPMENT_PATH.replace('.', '_').toUpperCase());

		if (Strings.isNullOrEmpty(developmentPluginPaths))
		{
			// Otherwise check the property file
			developmentPluginPaths = properties.getProperty(PLUGIN_DEVELOPMENT_PATH);
		}

		return Strings.isNullOrEmpty(developmentPluginPaths) ? new String[0] : developmentPluginPaths.split(";");
	}

	public static void main(String[] args)
	{
		init();
	}

	public static void init()
	{
		log.info("Initializing Open RuneLite..");
		final Stopwatch stopwatch = Stopwatch.createStarted();
		createDirectories(ROOT_DIR, EXTERNAL_PLUGINS_DIR);
		log.info("Open RuneLite has been setup in {}.", stopwatch.stop());
	}

	private static void createDirectories(File... files)
	{
		for (File f : files)
		{
			if (f.exists())
			{
				continue;
			}

			if (f.mkdirs())
			{
				log.info("Created directory: {}", f.getAbsolutePath());
			}
			else
			{
				log.error("Failed to create directory: {}", f.getAbsolutePath());
				throw new RuntimeException("Failed to create directory: " + f.getAbsolutePath());
			}
		}
	}
}
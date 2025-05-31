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
import org.apache.commons.lang3.ArrayUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;
import javax.annotation.Nullable;
import net.runelite.client.RuneLite;
import net.runelite.client.plugins.openrl.utils.FileUtils;

@Slf4j
public class OpenRuneLite
{
	public static final String uuid = UUID.randomUUID().toString();

	public static final File ROOT_DIR = new File(RuneLite.RUNELITE_DIR, "openrl");
	public static final File EXTERNAL_PLUGINS_DIR = new File(ROOT_DIR, "plugins");
	public static final String PLUGIN_DEVELOPMENT_PATH = "plugin.development.path";

	@Nullable
	public static final File JAR_FILE;
	public static final boolean IS_IDEA;

	@Getter(AccessLevel.PACKAGE)
	private static final Properties properties = new Properties();
	public static final String SYSTEM_VERSION;
	public static final String SYSTEM_API_VERSION;

	static
	{
		log.info("Initializing Open RuneLite.");
		final Stopwatch stopwatch = Stopwatch.createStarted();
		FileUtils.createDirectories(ROOT_DIR, EXTERNAL_PLUGINS_DIR);

		JAR_FILE = FileUtils.getJarFile();
		IS_IDEA = JAR_FILE == null || !JAR_FILE.exists() || !JAR_FILE.getName().endsWith(".jar");
		log.info("Code source path: {} | idea: {}", JAR_FILE != null && JAR_FILE.exists() ? JAR_FILE.getAbsolutePath() : "null", IS_IDEA);

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

		log.info("Open RuneLite {} api {} has been setup in {}.", SYSTEM_VERSION, SYSTEM_API_VERSION, stopwatch.stop());
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

	private static String[] getClientArgs()
	{
		final File clientArgsFile = new File(ROOT_DIR, "client-args.properties");
		final Properties clientArgsProperties = new Properties();
		if (!clientArgsFile.exists())
		{
			clientArgsProperties.setProperty("clientArgs", "");
			try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(clientArgsFile.toPath()), StandardCharsets.UTF_8))
			{
				clientArgsProperties.store(outputStreamWriter, "client arguments");
			}
			catch (IOException e)
			{
				log.error("Unable to create client args file", e);
			}
		}

		try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(clientArgsFile), StandardCharsets.UTF_8))
		{
			clientArgsProperties.load(inputStreamReader);
		}
		catch (IOException e)
		{
			log.warn("Unable to load client arguments from disk", e);
		}

		final String clientArgs = clientArgsProperties.getProperty("clientArgs");
		return clientArgs != null ? clientArgs.split(" ") : null;
	}

	public static void main(String[] args) throws Exception
	{
		args = ArrayUtils.addAll(args, getClientArgs());

		if (Arrays.asList(args).contains("--minimal"))
		{
			System.setProperty("openrl.minimal", "true");
			//MinimalClient.main(args);
		}
		else
		{
			RuneLite.main(args);
		}
	}
}
/*
 * Copyright (c) 2022, Melxin <https://github.com/melxin>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.openrl.plugins.jagex;

import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;
import net.runelite.client.plugins.openrl.OpenRuneLite;

@Slf4j
public class RuneLiteCredentialsManager
{
	private static final File propertiesFile;
	private static final Properties properties;

	private static String[] propertyKeys = new String[]{"JX_ACCESS_TOKEN", "JX_REFRESH_TOKEN", "JX_CHARACTER_ID", "JX_SESSION_ID", "JX_DISPLAY_NAME"};

	static
	{
		propertiesFile = new File(OpenRuneLite.ROOT_DIR.getParent(), "credentials.properties");
		//propertiesFile = new File(System.getProperty("runelite.credentials.path", "credentials.properties"));
		properties = new Properties();
		if (propertiesFile.exists())
		{
			try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(propertiesFile), StandardCharsets.UTF_8))
			{
				properties.load(inputStreamReader);
			}
			catch (IOException e)
			{
				log.warn("Unable to load properties from disk", e);
			}
		}
	}

	public static void setAccount(JagexOAuthClient.Account account)
	{
		properties.setProperty("JX_ACCESS_TOKEN", "");
		properties.setProperty("JX_REFRESH_TOKEN", "");
		properties.setProperty("JX_SESSION_ID", account.sessionId);
		properties.setProperty("JX_CHARACTER_ID", account.accountId);
		properties.setProperty("JX_DISPLAY_NAME", account.displayName);
		try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(propertiesFile.toPath()), StandardCharsets.UTF_8))
		{
			properties.store(outputStreamWriter, "Do not share this file with anyone");
		}
		catch (IOException e)
		{
			log.error("Unable to create properties file", e);
		}

		/*if (propertiesFile.exists())
		{
			propertiesFile.deleteOnExit();
		}*/
	}

	public static void delete()
	{
		if (!properties.isEmpty())
		{
			properties.clear();
		}

		if (propertiesFile.exists())
		{
			propertiesFile.delete();
		}
	}

	public static JagexOAuthClient.Account getAccount() throws IOException
	{
		if (properties == null || properties.isEmpty())
		{
			return null;
		}

		final String charactedId = properties.getProperty("JX_CHARACTER_ID");
		if (charactedId == null)
		{
			return null;
		}

		final List<JagexOAuthClient.Account> accounts = JagexAccountManager.getAccounts();
		for (JagexOAuthClient.Account account : accounts)
		{
			final String accountId = account.getAccountId();
			if (accountId != null && accountId.equals(charactedId))
			{
				return account;
			}
		}
		return null;
	}
}
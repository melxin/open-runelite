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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class JagexAccountManager
{
	private static final Path ACCOUNTS_FILE = Paths.get(System.getProperty("user.home"), ".runelite", "openrl", "accounts.json");
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static List<JagexOAuthClient.Account> getAccounts() throws IOException
	{
		if (!ACCOUNTS_FILE.toFile().exists())
		{
			log.warn("No account file found: {}", ACCOUNTS_FILE.toString());
			return Collections.emptyList();
		}
		return gson.fromJson(Files.readString(ACCOUNTS_FILE), new TypeToken<List<JagexOAuthClient.Account>>()
		{
		}.getType());
	}

	public static void deleteAll()
	{
		if (ACCOUNTS_FILE.toFile().exists())
		{
			ACCOUNTS_FILE.toFile().delete();
		}
	}

	public static void delete(JagexOAuthClient.Account account) throws IOException
	{
		final List<JagexOAuthClient.Account> accounts = getAccounts();
		if (accounts == null || accounts.isEmpty())
		{
			return;
		}

		final Iterator<JagexOAuthClient.Account> iterator = accounts.iterator();
		while (iterator.hasNext())
		{
			JagexOAuthClient.Account acc = iterator.next();
			if (acc.getAccountId().equals(account.getAccountId()))
			{
				iterator.remove();
				break;
			}
		}
		Files.writeString(ACCOUNTS_FILE, gson.toJson(accounts));
	}

	public static void main(String[] args) throws IOException
	{
		final List<JagexOAuthClient.Account> accounts = getAccounts();
		if (accounts == null || accounts.isEmpty())
		{
			return;
		}

		for (JagexOAuthClient.Account acc : accounts)
		{
			log.info("acc: {}", acc.accountId);
			if (acc.displayName != null)
			{
				//RuneLiteCredentialsManager.setAccount(acc);
			}
		}
	}
}
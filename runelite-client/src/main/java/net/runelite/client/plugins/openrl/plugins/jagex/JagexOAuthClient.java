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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.util.LinkBrowser;

/**
 * 1. Open browser
 * 2. Login
 * 3. Paste the url containing `code=`
 * 3. Open browser 2
 * 4. Paste the url containing `id_token=`
 * 5. Fetch and save accounts
 */
@Slf4j
public class JagexOAuthClient
{
	private static final String CLIENT_ID = "com_jagex_auth_desktop_launcher";
	private static final String REDIRECT_URI = "https://secure.runescape.com/m=weblogin/launcher-redirect";
	private static final String TOKEN_URL = "https://account.jagex.com/oauth2/token";
	private static final String SESSION_URL = "https://auth.jagex.com/game-session/v1/sessions";
	private static final String ACCOUNTS_URL = "https://auth.jagex.com/game-session/v1/accounts";

	private static final Path ACCOUNTS_FILE = Paths.get(System.getProperty("user.home"), ".runelite", "openrl", "accounts.json");
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static final HttpClient httpClient = HttpClient.newHttpClient();

	private String state;
	private String codeVerifier;
	private String codeChallenge;

	public static void main(String[] args)
	{
		performOAuthDialog();
	}

	public static void performOAuthCommandLine() throws Exception
	{
		final JagexOAuthClient jagexOAuthClient = new JagexOAuthClient();
		jagexOAuthClient.startOAuthFlow();

		int commandCount = 0;
		final Scanner scanner = new Scanner(System.in);
		while (commandCount < 2)
		{
			final String command = scanner.nextLine();
			log.info("Received command: {}", command);
			commandCount++;
			if (commandCount == 1)
			{
				final String token = jagexOAuthClient.getToken(extractParameter(command, "code"));
				log.info("TOKEN: {}", token);
				jagexOAuthClient.getIdToken(token);
			}
			if (commandCount == 2)
			{
				final String sessionId = jagexOAuthClient.getSessionId(extractParameter(command, "id_token"));
				log.info("SESSION ID: {}", sessionId);
				jagexOAuthClient.fetchAndSaveAccounts(sessionId);
			}
		}
		scanner.close();
	}

	/**
	 * Perform OAuth dialog
	 *
	 * 1. Open Browser
	 * 2. Login
	 * 3. Paste the url containing `code=` in the input dialog
	 * 3. Open Browser 2
	 * 4. Paste the url containing `id_token=` in the input dialog
	 */
	public static void performOAuthDialog()
	{
		try
		{
			final JagexOAuthClient jagexOAuthClient = new JagexOAuthClient();
			jagexOAuthClient.startOAuthFlow();

			// Handle url containing 'code'
			while (true)
			{
				final String url = JOptionPane.showInputDialog(ClientUI.getFrame(),
					"Paste the URL containing the 'code' parameter",
					"Add Account", JOptionPane.PLAIN_MESSAGE);

				// Canceled
				if (url == null)
				{
					return;
				}

				if (url.trim().isEmpty())
				{
					JOptionPane.showMessageDialog(ClientUI.getFrame(),
						"URL cannot be empty. Please try again.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
					continue;
				}

				final String codeParam = JagexOAuthClient.extractParameter(url, "code");
				if (codeParam == null)
				{
					JOptionPane.showMessageDialog(ClientUI.getFrame(),
						"The URL does not contain a 'code' parameter. Please try again.", "Invalid URL", JOptionPane.ERROR_MESSAGE);
					continue;
				}

				final String token = jagexOAuthClient.getToken(codeParam);
				jagexOAuthClient.getIdToken(token);
				break;
			}

			// Handle url containing 'id_token'
			while (true)
			{
				final String url = JOptionPane.showInputDialog(ClientUI.getFrame(),
					"Paste the URL containing 'id_token' parameter",
					"Add Account", JOptionPane.PLAIN_MESSAGE);

				// Canceled
				if (url == null)
				{
					return;
				}

				if (url.trim().isEmpty())
				{
					JOptionPane.showMessageDialog(ClientUI.getFrame(),
						"URL cannot be empty. Please try again.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
					continue;
				}

				final String idTokenParam = JagexOAuthClient.extractParameter(url, "id_token");
				if (idTokenParam == null)
				{
					JOptionPane.showMessageDialog(ClientUI.getFrame(),
						"The URL does not contain an 'id_token' parameter. Please try again.", "Invalid URL", JOptionPane.ERROR_MESSAGE);
					continue;
				}

				final String sessionId = jagexOAuthClient.getSessionId(idTokenParam);
				jagexOAuthClient.fetchAndSaveAccounts(sessionId);
				break;
			}
		}
		catch (Exception e)
		{
			log.error("Error during OAuth process", e);
			JOptionPane.showMessageDialog(ClientUI.getFrame(),
				"An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Open browser with the login url
	 *
	 * @throws Exception
	 */
	public void startOAuthFlow() throws Exception
	{
		generatePKCE();

		final String authUrl = "https://account.jagex.com/oauth2/auth?"
			+ "auth_method=" + URIEncoder("")
			+ "&login_type=" + URIEncoder("")
			+ "&flow=" + URIEncoder("launcher")
			+ "&response_type=" + URIEncoder("code")
			+ "&client_id=" + URIEncoder(CLIENT_ID)
			+ "&redirect_uri=" + URIEncoder(REDIRECT_URI)
			+ "&code_challenge=" + URIEncoder(codeChallenge)
			+ "&code_challenge_method=" + URIEncoder("S256")
			+ "&prompt=" + URIEncoder("login")
			+ "&scope=" + URIEncoder("openid offline gamesso.token.create user.profile.read")
			+ "&state=" + URIEncoder(state);

		log.info("Please complete login in the browser window that opens.");

		LinkBrowser.browse(authUrl);
	}

	private void generatePKCE() throws Exception
	{
		this.state = generateRandomString(8);
		this.codeVerifier = generateRandomString(45);
		this.codeChallenge = base64UrlEncode(sha256Digest(codeVerifier));
		log.info("state: {} codeVerifier: {} codeChallenge:{}", state, codeVerifier, codeChallenge);
	}

	/**
	 * Gets the token for {@link #getIdToken(String)}
	 *
	 * @param code the code from {@link #startOAuthFlow()} opened browser url
	 * @return the code needed for {@link #getIdToken(String)}
	 * @throws Exception
	 */
	public String getToken(String code) throws Exception
	{
		final String body = "grant_type=authorization_code"
			+ "&client_id=" + CLIENT_ID
			+ "&code=" + code
			+ "&redirect_uri=" + URIEncoder(REDIRECT_URI)
			+ "&code_verifier=" + codeVerifier;

		final HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(TOKEN_URL))
			.header("Content-Type", "application/x-www-form-urlencoded")
			.POST(HttpRequest.BodyPublishers.ofString(body))
			.build();

		final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() == HttpURLConnection.HTTP_OK)
		{
			final Map<String, Object> json = gson.fromJson(response.body(), new TypeToken<Map<String, Object>>()
			{
			}.getType());
			return (String) json.get("id_token");
		}
		else
		{
			log.error("Token request failed: {}", response.body());
		}
		return null;
	}

	/**
	 * Get id token
	 *
	 * @param idToken the id token from {@link #getToken(String)}
	 * @throws Exception
	 */
	public void getIdToken(String idToken) throws Exception
	{
		final String nonce = generateRandomString(48);

		final String url = "https://account.jagex.com/oauth2/auth?" +
			"id_token_hint=" + URIEncoder(idToken) +
			"&nonce=" + URIEncoder(nonce) +
			"&prompt=" + URIEncoder("consent") +
			"&redirect_uri=" + URIEncoder("http://localhost") +
			"&response_type=" + URIEncoder("id_token code") +
			"&scope=" + URIEncoder("openid offline") +
			"&client_id=" + URIEncoder("1fddee4e-b100-4f4e-b2b0-097f9088f9d2") +
			"&state=" + URIEncoder(state);

		log.info("The url {} to get your 'id_token'\n", url);

		LinkBrowser.browse(url);
	}

	/**
	 * Get session id
	 *
	 * @param idToken the id token from {@link #getIdToken(String)} opened browser url
	 * @return the session id
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public String getSessionId(String idToken) throws IOException, InterruptedException
	{
		final Map<String, String> jsonMap = new HashMap<>();
		jsonMap.put("idToken", idToken);
		final String jsonBody = gson.toJson(jsonMap);

		final HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(SESSION_URL))
			.header("Content-Type", "application/json")
			.POST(HttpRequest.BodyPublishers.ofString(jsonBody))
			.build();

		final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() == HttpURLConnection.HTTP_OK)
		{
			final Map<String, Object> json = gson.fromJson(response.body(), new TypeToken<Map<String, Object>>()
			{
			}.getType());
			return (String) json.get("sessionId");
		}
		else
		{
			log.info("Session request failed: {}", response.body());
		}
		return null;
	}

	/**
	 * Fetch and save accounts
	 *
	 * @param sessionId the session id from {@link #getSessionId(String)}
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void fetchAndSaveAccounts(String sessionId) throws IOException, InterruptedException
	{
		final HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(ACCOUNTS_URL))
			.header("Authorization", "Bearer " + sessionId)
			.GET()
			.build();

		final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() == HttpURLConnection.HTTP_OK)
		{
			final List<Account> accounts = gson.fromJson(response.body(), new TypeToken<List<Account>>()
			{
			}.getType());
			Files.createDirectories(ACCOUNTS_FILE.getParent());

			List<Account> existingAccounts = new ArrayList<>();
			if (Files.exists(ACCOUNTS_FILE))
			{
				try
				{
					existingAccounts = gson.fromJson(Files.readString(ACCOUNTS_FILE), new TypeToken<List<Account>>()
					{
					}.getType());
				}
				catch (Exception e)
				{
					log.error("Error reading existing accounts", e);
				}
			}

			final Set<String> existingIds = existingAccounts.stream()
				.map(acc -> acc.accountId)
				.collect(Collectors.toSet());

			final List<Account> newAccounts = accounts.stream()
				.filter(acc -> !existingIds.contains(acc.accountId))
				.collect(Collectors.toList());

			for (Account account : newAccounts)
			{
				account.sessionId = sessionId;
				account.createdOn = new Date();
			}

			existingAccounts.addAll(newAccounts);

			Files.writeString(ACCOUNTS_FILE, gson.toJson(existingAccounts));
			log.info("Accounts saved to: {}", ACCOUNTS_FILE.toString());
		}
		else
		{
			log.info("Failed to fetch accounts: {}", response.body());
		}
	}

	// Utilities
	private static String generateRandomString(int length)
	{
		final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		final SecureRandom rnd = new SecureRandom();
		final StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++)
		{
			sb.append(chars.charAt(rnd.nextInt(chars.length())));
		}
		return sb.toString();
	}

	private static byte[] sha256Digest(String input) throws Exception
	{
		final MessageDigest md = MessageDigest.getInstance("SHA-256");
		return md.digest(input.getBytes(StandardCharsets.US_ASCII));
	}

	private static String base64UrlEncode(byte[] bytes)
	{
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	private static String URIEncoder(String input) throws Exception
	{
		return URLEncoder.encode(input, StandardCharsets.UTF_8.name());
	}

	private static String URIDecoder(String input) throws Exception
	{
		return URLDecoder.decode(input, StandardCharsets.UTF_8.name());
	}

	public static String extractParameter(String url, String param)
	{
		try
		{
			final URI uri = new URI(url);
			// Query parameters
			final String query = uri.getQuery();
			if (query != null)
			{
				for (String pair : query.split("&"))
				{
					String[] parts = pair.split("=", 2);
					String key = URIDecoder(parts[0]);
					if (key.equals(param))
					{
						String value = parts.length > 1 ? URIDecoder(parts[1]) : null;
						return value;
					}
				}
			}
			// Fragment parameters
			final String fragment = uri.getFragment();
			if (fragment != null)
			{
				for (String pair : fragment.split("&"))
				{
					String[] parts = pair.split("=", 2);
					String key = URIDecoder(parts[0]);
					if (key.equals(param))
					{
						String value = parts.length > 1 ? URIDecoder(parts[1]) : null;
						return value;
					}
				}
			}
		}
		catch (Exception e)
		{
			log.error("Failed to extract param: {} -> {}", url, param, e);
		}
		return null;
	}

	// Account bean
	@Getter
	public static class Account
	{
		String sessionId;
		String accountId;
		String displayName = "Not set";
		String userHash;
		Date createdOn;
	}
}
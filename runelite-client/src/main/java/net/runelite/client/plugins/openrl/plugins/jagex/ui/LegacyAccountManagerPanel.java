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
package net.runelite.client.plugins.openrl.plugins.jagex.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.input.Keyboard;
import net.runelite.client.plugins.openrl.plugins.jagex.JagexAccountManagerPluginConfig;
import net.runelite.client.plugins.openrl.plugins.jagex.reflection.JagexAccountReflection;
import net.runelite.client.plugins.openrl.utils.CryptoUtils;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.PluginPanel;

@Slf4j
@Deprecated(forRemoval = true)
public class LegacyAccountManagerPanel extends PluginPanel
{
	private JComboBox<String> accountComboBox;
	private DefaultComboBoxModel<String> accountComboBoxModel;

	private JagexAccountManagerPluginConfig config;

	public LegacyAccountManagerPanel(JagexAccountManagerPluginConfig config)
	{
		// Configuration
		this.config = config;

		// Accounts
		add(new JLabel("Legacy accounts:"));

		// Account combo box
		accountComboBoxModel = new DefaultComboBoxModel<>();
		accountComboBox = new JComboBox<>(accountComboBoxModel);
		accountComboBox.setPreferredSize(new Dimension(200, 25));

		accountComboBox.addActionListener(a ->
		{
			final LegacyAccount selectedAccount = getSelectedAccount();
			if (selectedAccount == null)
			{
				return;
			}
		});
		add(accountComboBox);

		refreshAccountsComboBox();

		// Add account button
		final JButton addAccountButton = new JButton("Add account");
		addAccountButton.setToolTipText("Add new legacy account");
		addAccountButton.addActionListener(a ->
		{
			final JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

			final JTextField usernameField = new JTextField();
			final JPasswordField passwordField = new JPasswordField();
			final JTextField otpField = new JTextField();

			panel.add(new JLabel("Username:"));
			panel.add(usernameField);
			panel.add(new JLabel("Password:"));
			panel.add(passwordField);
			panel.add(new JLabel("OTP:"));
			panel.add(otpField);

			final int result = JOptionPane.showConfirmDialog(ClientUI.getFrame(), panel,
				"Enter Account Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.OK_OPTION)
			{
				final String username = usernameField.getText().trim();
				final String password = new String(passwordField.getPassword());
				final String otp = otpField.getText().trim();

				if (username.isEmpty() || password.isEmpty() /*|| otp.isEmpty()*/)
				{
					JOptionPane.showMessageDialog(ClientUI.getFrame(),
						"All fields are required.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
					return;
				}

				try
				{
					LegacyAccountManager.addAccount(new LegacyAccount(username, CryptoUtils.encrypt(password, "@P4ssW0rD!@#!0"), otp.isEmpty() ? null : CryptoUtils.encrypt(otp, "@P4ssW0rD!@#!0")));
				}
				catch (Exception e)
				{
					log.error("Error adding account", e);
					JOptionPane.showMessageDialog(ClientUI.getFrame(),
						"An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}

				refreshAccountsComboBox();
			}
		});
		add(addAccountButton);

		// Remove selected account button
		final JButton removeSelectedAccountButton = new JButton("Remove selected account");
		removeSelectedAccountButton.setToolTipText("Remove the selected account from legacy_accounts.json");
		removeSelectedAccountButton.addActionListener(a ->
		{
			final int result = JOptionPane.showConfirmDialog(
				ClientUI.getFrame(),
				"Are you sure you want to delete the selected account?",
				"Confirm Delete",
				JOptionPane.YES_NO_OPTION
			);

			if (result != JOptionPane.YES_OPTION)
			{
				return;
			}

			try
			{
				final LegacyAccount selectedAccount = getSelectedAccount();
				if (selectedAccount != null)
				{
					LegacyAccountManager.delete(selectedAccount);
					refreshAccountsComboBox();
				}
			}
			catch (IOException e)
			{
				log.error("Failed to remove selected account", e);
			}
		});
		add(removeSelectedAccountButton);

		// Remove all accounts button
		final JButton removeAllAccountsButton = new JButton("Remove all accounts");
		removeAllAccountsButton.setToolTipText("Remove legacy_accounts.json file from disk");
		removeAllAccountsButton.addActionListener(a ->
		{
			final int result = JOptionPane.showConfirmDialog(
				ClientUI.getFrame(),
				"Are you sure you want to delete all accounts?",
				"Confirm Delete",
				JOptionPane.YES_NO_OPTION
			);

			if (result != JOptionPane.YES_OPTION)
			{
				return;
			}
			LegacyAccountManager.deleteAll();
			refreshAccountsComboBox();
		});
		add(removeAllAccountsButton);

		// Set login button
		final JButton setLoginButton = new JButton("Set login");
		setLoginButton.setToolTipText("Set new login credentials");
		setLoginButton.addActionListener(a ->
		{
			Static.getClientThread().invoke(() ->
			{
				if (Static.getClient().getGameState() != GameState.LOGIN_SCREEN)
				{
					JOptionPane.showMessageDialog(ClientUI.getFrame(),
						"Must be on login screen to set login.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
					return;
				}

				final LegacyAccount selectedAccount = getSelectedAccount();
				if (selectedAccount == null)
				{
					Static.getClient().setUsername("");
					Static.getClient().getPreferences().setRememberedUsername("");
					Static.getClient().setPassword("");
					return;
				}
				Static.getClient().setUsername(selectedAccount.getUsername());
				Static.getClient().getPreferences().setRememberedUsername(selectedAccount.getUsername());
				Static.getClient().setPassword(CryptoUtils.decrypt(selectedAccount.getPassword(), "@P4ssW0rD!@#!0"));
				if (selectedAccount.getOtp() != null)
				{
					Static.getClient().setOtp(CryptoUtils.decrypt(selectedAccount.getOtp(), "@P4ssW0rD!@#!0"));
				}
				JagexAccountReflection.setMode(true);
				JagexAccountReflection.setLoginIndex(JagexAccountReflection.LoginIndex.LEGACY);
				if (config.autoLoginLegacy())
				{
					Keyboard.sendEnter();
					Keyboard.sendEnter();
				}
			});
		});
		add(setLoginButton);

		// Auto login checkbox
		final JCheckBox checkBox = new JCheckBox("Auto login");
		checkBox.setToolTipText("Auto login");
		checkBox.setSelected(Static.getConfigManager().getConfiguration(JagexAccountManagerPluginConfig.GROUP, "autoLoginLegacy", Boolean.class));
		checkBox.addChangeListener(l -> Static.getConfigManager().setConfiguration(JagexAccountManagerPluginConfig.GROUP, "autoLoginLegacy",
			((JCheckBox) l.getSource()).isSelected()));
		add(checkBox);
	}

	// Method to update the combo box items
	private void refreshAccountsComboBox()
	{
		accountComboBoxModel.removeAllElements();
		accountComboBoxModel.addElement("None");
		try
		{
			final List<LegacyAccount> accounts = LegacyAccountManager.getAccounts();
			for (LegacyAccount account : accounts)
			{
				accountComboBoxModel.addElement(account.getUsername());
			}

			String currentUsername = Static.getClient().getUsername();
			if (currentUsername == null)
			{
				currentUsername = Static.getClient().getPreferences().getRememberedUsername();
			}

			if (currentUsername != null && !currentUsername.isEmpty())
			{
				for (LegacyAccount account : accounts)
				{
					if (account.getUsername().equalsIgnoreCase(currentUsername))
					{
						accountComboBoxModel.setSelectedItem(currentUsername);
						break;
					}
				}
			}
		}
		catch (IOException e)
		{
			log.error("Failed to refresh accounts combo box", e);
		}
	}

	// Method to get the selected account
	private LegacyAccount getSelectedAccount()
	{
		final Object selected = accountComboBox.getSelectedItem();
		if (selected == null || selected.equals("None"))
		{
			return null;
		}

		try
		{
			final List<LegacyAccount> accounts = LegacyAccountManager.getAccounts();
			for (LegacyAccount account : accounts)
			{
				if (account.getUsername().equalsIgnoreCase(selected.toString()))
				{
					return account;
				}
			}
		}
		catch (IOException e)
		{
			log.error("Failed to get selected account", e);
		}
		return null;
	}

	private static class LegacyAccountManager
	{
		private static final Path ACCOUNTS_FILE = Paths.get(System.getProperty("user.home"), ".runelite", "openrl", "legacy_accounts.json");
		private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

		public static List<LegacyAccount> getAccounts() throws IOException
		{
			if (!ACCOUNTS_FILE.toFile().exists())
			{
				log.warn("No account file found: {}", ACCOUNTS_FILE.toString());
				return Collections.emptyList();
			}
			return gson.fromJson(Files.readString(ACCOUNTS_FILE), new TypeToken<List<LegacyAccount>>()
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

		public static void delete(LegacyAccount account) throws IOException
		{
			final List<LegacyAccount> accounts = getAccounts();
			if (accounts == null || accounts.isEmpty())
			{
				return;
			}

			final Iterator<LegacyAccount> iterator = accounts.iterator();
			while (iterator.hasNext())
			{
				LegacyAccount acc = iterator.next();
				if (acc.getUsername().equalsIgnoreCase(account.username))
				{
					iterator.remove();
					break;
				}
			}
			Files.writeString(ACCOUNTS_FILE, gson.toJson(accounts));
		}

		public static void addAccount(LegacyAccount account)
		{
			try
			{
				Files.createDirectories(ACCOUNTS_FILE.getParent());

				List<LegacyAccount> accounts = getAccounts();
				if (accounts == null || accounts.isEmpty())
				{
					accounts = new ArrayList<>();
				}

				account.createdOn = new Date();

				if (accounts.stream().anyMatch(x -> x.getUsername().equalsIgnoreCase(account.getUsername())))
				{
					log.warn("Account already exists: {}", account.username);
					return;
				}
				accounts.add(account);
				Files.writeString(ACCOUNTS_FILE, gson.toJson(accounts));
			}
			catch (IOException e)
			{
				log.error("Failed to add account", e);
			}
		}
	}

	@Subscribe
	protected void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals(JagexAccountManagerPluginConfig.GROUP))
		{
			return;
		}

		if (configChanged.getKey().equals("autoLoginLegacy"))
		{
			if (!config.autoLoginLegacy() || Static.getClient().getGameState() != GameState.LOGIN_SCREEN)
			{
				return;
			}

			final LegacyAccount selectedAccount = getSelectedAccount();
			if (selectedAccount == null)
			{
				return;
			}

			Static.getClient().setUsername(selectedAccount.getUsername());
			Static.getClient().getPreferences().setRememberedUsername(selectedAccount.getUsername());
			Static.getClient().setPassword(CryptoUtils.decrypt(selectedAccount.getPassword(), "@P4ssW0rD!@#!0"));
			if (selectedAccount.getOtp() != null)
			{
				Static.getClient().setOtp(CryptoUtils.decrypt(selectedAccount.getOtp(), "@P4ssW0rD!@#!0"));
			}
			JagexAccountReflection.setMode(true);
			JagexAccountReflection.setLoginIndex(JagexAccountReflection.LoginIndex.LEGACY);
			Keyboard.sendEnter();
			Keyboard.sendEnter();
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.LOGIN_SCREEN_AUTHENTICATOR)
		{
			final LegacyAccount selectedAccount = getSelectedAccount();
			if (selectedAccount == null)
			{
				return;
			}

			if (event.getGameState() == GameState.LOGIN_SCREEN_AUTHENTICATOR)
			{
				if (selectedAccount.getOtp() != null)
				{
					Static.getClient().setOtp(CryptoUtils.decrypt(selectedAccount.getOtp(), "@P4ssW0rD!@#!0"));
					Keyboard.sendEnter();
					Keyboard.sendEnter();
				}
				return;
			}

			if (!config.autoLoginLegacy())
			{
				return;
			}

			Static.getClient().setUsername(selectedAccount.getUsername());
			Static.getClient().getPreferences().setRememberedUsername(selectedAccount.getUsername());
			Static.getClient().setPassword(CryptoUtils.decrypt(selectedAccount.getPassword(), "@P4ssW0rD!@#!0"));
			if (selectedAccount.getOtp() != null)
			{
				Static.getClient().setOtp(CryptoUtils.decrypt(selectedAccount.getOtp(), "@P4ssW0rD!@#!0"));
			}
			JagexAccountReflection.setMode(true);
			JagexAccountReflection.setLoginIndex(JagexAccountReflection.LoginIndex.LEGACY);
			Keyboard.sendEnter();
			Keyboard.sendEnter();
		}
	}

	// Legacy account bean
	@Getter
	private static class LegacyAccount
	{
		String username;
		String password;
		String otp = null;
		Date createdOn;

		public LegacyAccount(String username, String password)
		{
			this.username = username;
			this.password = password;
		}

		public LegacyAccount(String username, String password, String otp)
		{
			this.username = username;
			this.password = password;
			this.otp = otp;
		}
	}
}
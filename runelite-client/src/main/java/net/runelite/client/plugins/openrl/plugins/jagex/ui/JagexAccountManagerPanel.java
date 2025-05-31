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

import lombok.extern.slf4j.Slf4j;
import java.awt.Dimension;
import java.io.IOException;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.input.Keyboard;
import net.runelite.client.plugins.openrl.plugins.jagex.JagexAccountManager;
import net.runelite.client.plugins.openrl.plugins.jagex.JagexAccountManagerPluginConfig;
import net.runelite.client.plugins.openrl.plugins.jagex.reflection.JagexAccountReflection;
import net.runelite.client.plugins.openrl.plugins.jagex.JagexOAuthClient;
import net.runelite.client.plugins.openrl.plugins.jagex.RuneLiteCredentialsManager;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.PluginPanel;

@Slf4j
public class JagexAccountManagerPanel extends PluginPanel
{
	private JComboBox<String> accountComboBox;
	private DefaultComboBoxModel<String> accountComboBoxModel;

	private JagexAccountManagerPluginConfig config;

	public JagexAccountManagerPanel(JagexAccountManagerPluginConfig config)
	{
		// Configuration
		this.config = config;

		// Accounts
		add(new JLabel("Accounts:"));

		// Account combo box
		accountComboBoxModel = new DefaultComboBoxModel<>();
		accountComboBox = new JComboBox<>(accountComboBoxModel);
		accountComboBox.setToolTipText("Accounts");
		accountComboBox.setPreferredSize(new Dimension(200, 25));

		accountComboBox.addActionListener(a ->
		{
			final JagexOAuthClient.Account selectedAccount = getSelectedAccount();
			if (selectedAccount == null)
			{
				//RuneLiteCredentialsManager.delete();
				return;
			}

			//log.info("Selected: {}", selectedAccount.getDisplayName() + " - " + selectedAccount.getAccountId());
			//RuneLiteCredentialsManager.setAccount(selectedAccount);
		});
		add(accountComboBox);

		refreshAccountsComboBox();

		// Add accounts button
		final JButton addAccountsButton = new JButton("Add accounts");
		addAccountsButton.setToolTipText("Add new accounts using jagex oauth flow");
		addAccountsButton.addActionListener(a ->
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

				refreshAccountsComboBox();
			}
			catch (Exception e)
			{
				log.error("Error during OAuth process", e);
				JOptionPane.showMessageDialog(ClientUI.getFrame(),
					"An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		add(addAccountsButton);

		// Remove selected account button
		final JButton removeSelectedAccountButton = new JButton("Remove selected account");
		removeSelectedAccountButton.setToolTipText("Remove the selected account from accounts.json");
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
				final JagexOAuthClient.Account selectedAccount = getSelectedAccount();
				if (selectedAccount != null)
				{
					JagexAccountManager.delete(selectedAccount);
					RuneLiteCredentialsManager.delete();
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
		removeAllAccountsButton.setToolTipText("Remove accounts.json file from disk");
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
			JagexAccountManager.deleteAll();
			RuneLiteCredentialsManager.delete();
			refreshAccountsComboBox();
		});
		add(removeAllAccountsButton);

		// Remove runelite credentials button
		final JButton removeCredentialsButton = new JButton("Remove runelite credentials");
		removeCredentialsButton.setToolTipText("Remove credentials.properties file from disk");
		removeCredentialsButton.addActionListener(a ->
		{
			RuneLiteCredentialsManager.delete();
			refreshAccountsComboBox();
		});
		add(removeCredentialsButton);

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

				final JagexOAuthClient.Account selectedAccount = getSelectedAccount();
				if (selectedAccount == null)
				{
					RuneLiteCredentialsManager.delete();
					JagexAccountReflection.setCredentials("", "", "", "", "");
					JagexAccountReflection.setMode(true);
					JagexAccountReflection.setLoginIndex(JagexAccountReflection.LoginIndex.LEGACY);
					return;
				}
				RuneLiteCredentialsManager.setAccount(selectedAccount);
				JagexAccountReflection.setCredentials(selectedAccount.getSessionId(), selectedAccount.getAccountId(), selectedAccount.getDisplayName());
				JagexAccountReflection.setLoginIndex(JagexAccountReflection.LoginIndex.JAGEX_ACCOUNT);
				if (config.autoLogin())
				{
					Keyboard.sendEnter();
				}
			});
		});
		add(setLoginButton);

		// Auto login checkbox
		final JCheckBox checkBox = new JCheckBox("Auto login");
		checkBox.setToolTipText("Auto login");
		checkBox.setSelected(Static.getConfigManager().getConfiguration(JagexAccountManagerPluginConfig.GROUP, "autoLogin", Boolean.class));
		checkBox.addChangeListener(l -> Static.getConfigManager().setConfiguration(JagexAccountManagerPluginConfig.GROUP, "autoLogin",
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
			final List<JagexOAuthClient.Account> accounts = JagexAccountManager.getAccounts();
			for (JagexOAuthClient.Account account : accounts)
			{
				accountComboBoxModel.addElement(account.getDisplayName() + " - " + account.getAccountId());
			}

			final JagexOAuthClient.Account storedAccount = RuneLiteCredentialsManager.getAccount();
			if (storedAccount != null)
			{
				accountComboBoxModel.setSelectedItem(storedAccount.getDisplayName() + " - " + storedAccount.getAccountId());
			}
		}
		catch (IOException e)
		{
			log.error("Failed to refresh accounts combo box", e);
		}
	}

	// Method to get the selected account
	private JagexOAuthClient.Account getSelectedAccount()
	{
		final Object selected = accountComboBox.getSelectedItem();
		if (selected == null || selected.equals("None"))
		{
			return null;
		}

		try
		{
			final List<JagexOAuthClient.Account> accounts = JagexAccountManager.getAccounts();
			for (JagexOAuthClient.Account account : accounts)
			{
				if ((account.getDisplayName() + " - " + account.getAccountId()).equals(selected.toString()))
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

	@Subscribe
	protected void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals(JagexAccountManagerPluginConfig.GROUP))
		{
			return;
		}

		if (configChanged.getKey().equals("autoLogin"))
		{
			if (!config.autoLogin() || Static.getClient().getGameState() != GameState.LOGIN_SCREEN)
			{
				return;
			}

			final JagexOAuthClient.Account selectedAccount = getSelectedAccount();
			if (selectedAccount == null)
			{
				return;
			}

			RuneLiteCredentialsManager.setAccount(selectedAccount);
			JagexAccountReflection.setCredentials(selectedAccount.getSessionId(), selectedAccount.getAccountId(), selectedAccount.getDisplayName());
			JagexAccountReflection.setLoginIndex(JagexAccountReflection.LoginIndex.JAGEX_ACCOUNT);
			Keyboard.sendEnter();
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN)
		{
			if (!config.autoLogin())
			{
				return;
			}

			final JagexOAuthClient.Account selectedAccount = getSelectedAccount();
			if (selectedAccount == null)
			{
				return;
			}

			RuneLiteCredentialsManager.setAccount(selectedAccount);
			JagexAccountReflection.setCredentials(selectedAccount.getSessionId(), selectedAccount.getAccountId(), selectedAccount.getDisplayName());
			JagexAccountReflection.setLoginIndex(JagexAccountReflection.LoginIndex.JAGEX_ACCOUNT);
			Keyboard.sendEnter();
		}
	}
}
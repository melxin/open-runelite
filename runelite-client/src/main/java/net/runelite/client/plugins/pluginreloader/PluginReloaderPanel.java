/*
 * Copyright (c) 2026, Orvian
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
package net.runelite.client.plugins.pluginreloader;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

class PluginReloaderPanel extends PluginPanel
{
	private static final int INSETS = 5;

	private final PluginReloaderPlugin plugin;
	private final PluginReloaderConfig config;

	private final JLabel statusLabel;

	@Inject
	PluginReloaderPanel(PluginReloaderPlugin plugin, PluginReloaderConfig config)
	{
		super(false);

		this.plugin = plugin;
		this.config = config;

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel contentPanel = new JPanel(new GridBagLayout());
		contentPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(INSETS, INSETS, INSETS, INSETS);
		gbc.weightx = 1;
		gbc.gridx = 0;

		// Status label
		gbc.gridy = 0;
		statusLabel = new JLabel("Ready to reload");
		statusLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		contentPanel.add(statusLabel, gbc);

		// Reload button
		gbc.gridy = 1;
		JButton reloadButton = new JButton("Reload Plugins");
		reloadButton.addActionListener(e -> reloadPlugins());
		contentPanel.add(reloadButton, gbc);

		add(contentPanel, BorderLayout.NORTH);
	}

	private void reloadPlugins()
	{
		String input = config.selectedPluginsToReload().trim();
		if (input.isEmpty())
		{
			updateStatusLabel("✗ Enter plugin names in config");
			return;
		}

		updateStatusLabel("Reloading...");
		boolean autoEnable = config.autoEnableAfterReload();
		plugin.reloadPlugins(input, autoEnable, this::updateStatusLabel);
	}

	void updateStatusLabel(String text)
	{
		SwingUtilities.invokeLater(() ->
		{
			statusLabel.setText(text);
			repaint();
		});
	}
}

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
package net.runelite.client.plugins.openrl.ui;

import lombok.extern.slf4j.Slf4j;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import net.runelite.api.Constants;
import net.runelite.client.ui.ClientUI;

@Slf4j
public class LogConsole
{
	/**
	 * Constructor
	 */
	public LogConsole()
	{
		initComponents();
	}

	private JScrollPane scrollPane;
	private JTextArea textArea;
	private JPanel consolePanel;
	private JPanel clientPanel;

	// Initialize components
	private void initComponents()
	{
		scrollPane = new JScrollPane();
		textArea = new JTextArea();
		consolePanel = new JPanel(new BorderLayout());
		clientPanel = (JPanel) ClientUI.getFrame().getContentPane().getComponents()[0];

		// Settings
		textArea.setEditable(false);
		textArea.setColumns(20);
		textArea.setRows(5);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);

		textArea.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 14));
		textArea.setForeground(Color.GREEN);
		textArea.setCaretColor(Color.GREEN);
		textArea.setSelectedTextColor(Color.DARK_GRAY);
		textArea.setSelectionColor(Color.GREEN);
		textArea.setDisabledTextColor(Color.RED);

		scrollPane.setViewportView(textArea);
		consolePanel.add(scrollPane, BorderLayout.CENTER);

		// Add to client panel
		//final JPanel clientPanel = (JPanel) ClientUI.getFrame().getContentPane().getComponents()[0];
		//clientPanel.add(new JScrollPane(textArea), BorderLayout.SOUTH);

		// Set print stream and redirect system.out & err
		final PrintStream stdOut = new PrintStream(new MultiOutputStream(System.out, textArea));
		final PrintStream stdErr = new PrintStream(new MultiOutputStream(System.err, textArea));

		System.setOut(stdOut);
		System.setErr(stdErr);
	}

	public void setVisible(boolean visible)
	{
		SwingUtilities.invokeLater(() ->
		{
			if (visible)
			{
				// Add to client panel
				clientPanel.add(consolePanel, BorderLayout.SOUTH);
				clientPanel.setMinimumSize(new Dimension(Constants.GAME_FIXED_WIDTH, Constants.GAME_FIXED_HEIGHT + 125));
				clientPanel.revalidate();
				clientPanel.repaint();
				return;
			}
			clientPanel.remove(consolePanel);
			clientPanel.setMinimumSize(Constants.GAME_FIXED_SIZE);
			clientPanel.revalidate();
			clientPanel.repaint();
		});
	}

	/**
	 * Multi outputStream
	 */
	private class MultiOutputStream extends OutputStream
	{
		private final OutputStream outputStream;
		private final JTextArea textArea;

		public MultiOutputStream(OutputStream outputStream, JTextArea textArea)
		{
			this.outputStream = outputStream;
			this.textArea = textArea;
		}

		@Override
		public void write(int b) throws IOException
		{
			// Write to outputStream
			outputStream.write(b);

			// Clear text from textArea if too many characters
			if (textArea.getText().length() > 100000)
			{
				textArea.setText(String.valueOf((char) b));
				return;
			}

			SwingUtilities.invokeLater(() ->
			{
				// redirects data to the text area
				textArea.append(String.valueOf((char) b));

				// scrolls the text area to the end of data
				textArea.setCaretPosition(textArea.getDocument().getLength());
			});
		}
	}
}
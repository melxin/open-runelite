package net.runelite.client.plugins.openrl.external.ui;

import org.pf4j.update.PluginInfo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import net.runelite.client.plugins.openrl.external.OPRLExternalPluginManager;
import net.runelite.client.plugins.openrl.external.OPRLUpdateRepository;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;

public class RepositoryBox extends JPanel
{
	private static final Font normalFont = FontManager.getRunescapeFont();
	private static final Font smallFont = FontManager.getRunescapeSmallFont();
	private static final ImageIcon DELETE_ICON;
	private static final ImageIcon DELETE_HOVER_ICON;
	private static final ImageIcon DISCORD_ICON;
	private static final ImageIcon DISCORD_HOVER_ICON;

	static
	{
		final BufferedImage deleteImg =
			ImageUtil.recolorImage(
				ImageUtil.resizeCanvas(
					ImageUtil.loadImageResource(ExternalPluginManagerPanel.class, "delete_icon.png"), 14, 14
				), ColorScheme.GREEN
			);
		DELETE_ICON = new ImageIcon(deleteImg);
		DELETE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(deleteImg, 0.53f));

		final BufferedImage discordImg =
			ImageUtil.recolorImage(
				ImageUtil.resizeCanvas(
					ImageUtil.loadImageResource(ExternalPluginManagerPanel.class, "discord_icon.png"), 14, 14
				), Color.WHITE
			);
		DISCORD_ICON = new ImageIcon(discordImg);
		DISCORD_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(discordImg, 0.53f));
	}

	RepositoryBox(OPRLExternalPluginManager externalPluginManager, OPRLUpdateRepository updateRepository)
	{
		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		final String name = updateRepository.getId();
		String urlString = updateRepository.getUrl().toString();
		if (urlString.startsWith("/"))
		{
			urlString = urlString.substring(1);
		}

		final JPanel titleWrapper = new JPanel(new BorderLayout());
		titleWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		titleWrapper.setBorder(new CompoundBorder(
			BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
			BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR)
		));

		final JLabel title = new JLabel();
		title.setText(name);
		title.setFont(normalFont);
		title.setBorder(null);
		title.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		title.setPreferredSize(new Dimension(0, 24));
		title.setForeground(Color.WHITE);
		title.setBorder(new EmptyBorder(0, 8, 0, 0));

		final JPanel titleActions = new JPanel(new BorderLayout(3, 0));
		titleActions.setBorder(new EmptyBorder(0, 0, 0, 8));
		titleActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		final Optional<PluginInfo> firstPlugin = updateRepository.getPlugins().values().stream().findFirst();

		if (firstPlugin.isPresent() && !firstPlugin.get().projectUrl.equals(""))
		{
			final JLabel support = new JLabel();
			support.setIcon(DISCORD_ICON);
			support.setToolTipText("Support");
			support.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent e)
				{
					LinkBrowser.browse(firstPlugin.get().projectUrl);
				}

				@Override
				public void mouseEntered(MouseEvent e)
				{
					support.setIcon(DISCORD_HOVER_ICON);
				}

				@Override
				public void mouseExited(MouseEvent e)
				{
					support.setIcon(DISCORD_ICON);
				}
			});

			titleActions.add(support, BorderLayout.WEST);
		}

		if (!name.equals("OpenRuneLite") && !name.equals("Plugin-Hub"))
		{
			final JLabel install = new JLabel();
			install.setIcon(DELETE_ICON);
			install.setToolTipText("Remove");
			install.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent e)
				{
					externalPluginManager.removeRepository(updateRepository.getId());
				}

				@Override
				public void mouseEntered(MouseEvent e)
				{
					install.setIcon(DELETE_HOVER_ICON);
				}

				@Override
				public void mouseExited(MouseEvent e)
				{
					install.setIcon(DELETE_ICON);
				}
			});

			titleActions.add(install, BorderLayout.EAST);
		}

		titleWrapper.add(title, BorderLayout.CENTER);
		titleWrapper.add(titleActions, BorderLayout.EAST);

		final JMultilineLabel repository = new JMultilineLabel();
		repository.setText(formatURL(urlString));
		repository.setFont(smallFont);
		repository.setDisabledTextColor(Color.WHITE);

		final String finalUrlString = urlString;
		repository.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				LinkBrowser.browse(formatURL(finalUrlString));
			}
		});

		add(titleWrapper, BorderLayout.NORTH);
		add(repository, BorderLayout.CENTER);
	}

	private String formatURL(String url)
	{
		if (url.contains("githubusercontent"))
		{
			url = url.replace("raw.githubusercontent", "github").replace("/master/", "");
		}

		return url;
	}
}
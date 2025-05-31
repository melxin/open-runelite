package net.runelite.client.plugins.openrl.ui;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.openrl.OpenRuneLiteConfig;
import net.runelite.client.ui.PluginPanel;

@Slf4j
public class OpenRuneLitePanel extends PluginPanel
{
	private final List<PanelContainer> containers = new ArrayList<>();
	private final JTabbedPane tabbedPane = new JTabbedPane();

	public OpenRuneLitePanel(OpenRuneLiteConfig config, ConfigManager configManager)
	{
		setLayout(new MigLayout());

		InteractionContainer interactionContainer = new InteractionContainer(config, configManager);

		containers.add(interactionContainer);

		add(tabbedPane);

		tabbedPane.addTab(interactionContainer.getTitle(), interactionContainer);
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged e)
	{
		if (!e.getGroup().equals(OpenRuneLiteConfig.GROUP))
		{
			return;
		}

		SwingUtilities.invokeLater(() -> containers.forEach(PanelContainer::rebuild));
	}
}
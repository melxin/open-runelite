package net.runelite.client.plugins.openrl.api.managers;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.openrl.OpenRuneLiteConfig;
import net.runelite.client.plugins.openrl.api.plugin.LoopedPluginManager;

@Singleton
@Slf4j
public class InputManager implements MouseListener, KeyListener
{
	private static boolean debug = false;

	private final Client client;
	private final LoopedPluginManager loopedPluginManager;
	private final OpenRuneLiteConfig interactionConfig;

	@Getter
	private int lastClickX = -1;
	@Getter
	private int lastClickY = -1;
	@Getter
	private int lastMoveX = -1;
	@Getter
	private int lastMoveY = -1;

	@Getter
	private boolean mouseExited;

	@Inject
	public InputManager(
		MouseManager mouseManager,
		KeyManager keyManager,
		EventBus eventBus,
		Client client,
		LoopedPluginManager loopedPluginManager,
		OpenRuneLiteConfig interactionConfig
	)
	{
		this.client = client;
		this.loopedPluginManager = loopedPluginManager;
		this.interactionConfig = interactionConfig;
		eventBus.register(this);
		mouseManager.registerMouseListener(this);
		keyManager.registerKeyListener(this);
	}

	@Override
	public MouseEvent mouseClicked(MouseEvent mouseEvent)
	{
		if (debug)
		{
			log.info("Mouse clicked: {}", mouseEvent);
		}
		checkIfAutomated(mouseEvent);
		setLastClick(mouseEvent.getX(), mouseEvent.getY());
		return mouseEvent;
	}

	@Override
	public MouseEvent mousePressed(MouseEvent mouseEvent)
	{
		if (debug)
		{
			log.info("Mouse pressed: {}", mouseEvent);
		}
		checkIfAutomated(mouseEvent);
		setLastClick(mouseEvent.getX(), mouseEvent.getY());
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseReleased(MouseEvent mouseEvent)
	{
		if (debug)
		{
			log.info("Mouse released: {}", mouseEvent);
		}
		checkIfAutomated(mouseEvent);
		setLastClick(mouseEvent.getX(), mouseEvent.getY());
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseEntered(MouseEvent mouseEvent)
	{
		if (debug)
		{
			log.info("Mouse entered: {}", mouseEvent);
		}
		checkIfAutomated(mouseEvent);
		setLastMove(mouseEvent.getX(), mouseEvent.getY());
		this.mouseExited = false;
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseExited(MouseEvent mouseEvent)
	{
		if (debug)
		{
			log.info("Mouse exited: {}", mouseEvent);
		}
		checkIfAutomated(mouseEvent);
		setLastMove(mouseEvent.getX(), mouseEvent.getY());
		this.mouseExited = true;
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseDragged(MouseEvent mouseEvent)
	{
		if (debug)
		{
			log.info("Mouse dragged: {}", mouseEvent);
		}
		checkIfAutomated(mouseEvent);
		setLastMove(mouseEvent.getX(), mouseEvent.getY());
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseMoved(MouseEvent mouseEvent)
	{
		if (debug)
		{
			log.info("Mouse moved: {}", mouseEvent);
		}
		checkIfAutomated(mouseEvent);
		setLastMove(mouseEvent.getX(), mouseEvent.getY());
		return mouseEvent;
	}

	/*@Subscribe
	private void onMouseAutomated(MouseAutomated event)
	{
		switch (event.getEventType())
		{
			case PRESS:
			case RELEASE:
				setLastClick(event.getX(), event.getY());
				break;

			case EXIT:
			case MOVE:
				setLastMove(event.getX(), event.getY());
				break;
		}
	}*/

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("openrl"))
		{
			return;
		}

		if (!event.getKey().equals("interactMethod"))
		{
			return;
		}

		/*if (Objects.equals(event.getNewValue(), "MOUSE_FORWARDING"))
		{
			GlobalScreen.addNativeMouseListener(this);
			GlobalScreen.addNativeMouseMotionListener(this);
			GlobalScreen.addNativeMouseWheelListener(this);
			GlobalScreen.addNativeKeyListener(this);
		}
		else if (Objects.equals(event.getOldValue(), "MOUSE_FORWARDING"))
		{
			GlobalScreen.removeNativeMouseListener(this);
			GlobalScreen.removeNativeMouseMotionListener(this);
			GlobalScreen.removeNativeMouseWheelListener(this);
			GlobalScreen.removeNativeKeyListener(this);
		}*/
	}

	private void setLastClick(int x, int y)
	{
		lastClickX = x;
		lastClickY = y;
	}

	private void setLastMove(int x, int y)
	{
		lastMoveX = x;
		lastMoveY = y;
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		if (debug)
		{
			log.info("Key typed: {}", e);
		}
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (debug)
		{
			log.info("Key pressed: {}", e);
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (debug)
		{
			log.info("Key released: {}", e);
		}
	}

	private void checkIfAutomated(MouseEvent mouseEvent)
	{
		if (!interactionConfig.disableMouse())
		{
			return;
		}

		if (/*loopedPluginManager.isPluginRegistered() &&*/ mouseEvent.getSource() != "openrl")
		{
			mouseEvent.consume();
		}

		/*if ((loopedPluginManager.isPluginRegistered() || minimalPluginManager.isScriptRunning())
			&& mouseEvent.getSource() != "openrl")
		{
			mouseEvent.consume();
		}*/
	}
}
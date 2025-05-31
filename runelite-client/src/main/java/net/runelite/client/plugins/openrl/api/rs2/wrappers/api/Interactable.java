package net.runelite.client.plugins.openrl.api.rs2.wrappers.api;

public interface Interactable
{
	String[] getActions();

	void interact(int index);

	void interact(String action);
}
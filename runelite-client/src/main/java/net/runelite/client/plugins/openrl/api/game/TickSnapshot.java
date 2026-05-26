package net.runelite.client.plugins.openrl.api.game;

import lombok.Value;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

/**
 * Immutable snapshot of critical game state captured once per tick on the client thread.
 * Safe to read from any thread — background loops, overlays, and workers all get a
 * consistent, race-free view of the game state as of the last completed tick.
 *
 * Updated exclusively by {@link TickSnapshotManager#onGameTick}.
 * Access the current snapshot via {@link #current()}.
 */
@Value
public class TickSnapshot
{
	/** RS tick counter at the moment this snapshot was captured. */
	int tickCount;

	GameState gameState;
	boolean loggedIn;
	int plane;
	boolean inInstancedRegion;

	/** World-coordinate position of the local player, or {@code null} if not logged in. */
	@Nullable WorldPoint playerLocation;

	int hp;
	int maxHp;
	int prayer;
	int maxPrayer;

	/** Run energy, 0–10000 (RuneLite scale). */
	int runEnergy;

	/** Local player's current animation ID, or {@code -1} if idle. */
	int animation;
	boolean animating;
	boolean moving;

	// ---- static state ----

	private static final AtomicReference<TickSnapshot> CURRENT = new AtomicReference<>(empty());

	/** Returns the most-recently captured snapshot. Never null. */
	public static TickSnapshot current()
	{
		return CURRENT.get();
	}

	/** Called exclusively from {@link TickSnapshotManager} on the client thread. */
	static void update(TickSnapshot snapshot)
	{
		CURRENT.set(snapshot);
	}

	/**
	 * Builds a snapshot from the current client state.
	 * Must be called on the client thread.
	 */
	static TickSnapshot capture(Client client)
	{
		final GameState state = client.getGameState();
		final boolean loggedIn = state == GameState.LOGGED_IN || state == GameState.LOADING;
		final Player player = loggedIn ? client.getLocalPlayer() : null;

		WorldPoint location = null;
		int plane = 0;
		boolean inInstance = false;
		int hp = 1, maxHp = 99;
		int prayer = 1, maxPrayer = 99;
		int runEnergy = 0;
		int animation = -1;
		boolean animating = false;
		boolean moving = false;

		if (loggedIn && player != null)
		{
			location = player.getWorldLocation();
			plane = client.getPlane();
			inInstance = client.isInInstancedRegion();
			hp = client.getBoostedSkillLevel(Skill.HITPOINTS);
			maxHp = client.getRealSkillLevel(Skill.HITPOINTS);
			prayer = client.getBoostedSkillLevel(Skill.PRAYER);
			maxPrayer = client.getRealSkillLevel(Skill.PRAYER);
			runEnergy = client.getEnergy();
			animation = player.getAnimation();
			animating = animation != -1;
			moving = player.getPoseAnimation() != player.getIdlePoseAnimation();
		}

		return new TickSnapshot(
			client.getTickCount(), state, loggedIn,
			plane, inInstance, location,
			hp, maxHp, prayer, maxPrayer, runEnergy,
			animation, animating, moving
		);
	}

	/** Returns a zero-state snapshot used before the first tick fires. */
	static TickSnapshot empty()
	{
		return new TickSnapshot(0, GameState.UNKNOWN, false, 0, false, null, 1, 99, 1, 99, 0, -1, false, false);
	}
}

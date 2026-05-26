package net.runelite.client.plugins.openrl.api.managers;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Central circuit breaker for all reflection-based game interactions.
 *
 * <h3>Principle</h3>
 * <pre>unknown state → do nothing</pre>
 *
 * If hook mappings shift, reflection targets become inaccessible, or interactions
 * fail repeatedly, this class opens a circuit that stops all automation from
 * executing. Without this guard, a broken hook silently produces spam clicks,
 * impossible menu actions, or desynchronised game state — all of which are far
 * more detectable than the mapping change itself.
 *
 * <h3>Circuit-breaker triggers</h3>
 * <ul>
 *   <li>A {@link HookSeverity#CRITICAL} hook fails to resolve or becomes
 *       inaccessible ({@link #reportHookFailed}).</li>
 *   <li>{@value #MAX_CONSECUTIVE_FAILURES} or more consecutive interaction
 *       attempts fail without a single success ({@link #reportInteractionAttemptFailed}).</li>
 * </ul>
 *
 * <h3>Recovery</h3>
 * The circuit auto-closes after {@value #MIN_AUTO_CLOSE_INTERVAL_MS} ms when all
 * CRITICAL hooks have been re-resolved ({@link #reportHookResolved}).
 * For immediate recovery during development, call {@link #forceReset()}.
 *
 * <h3>Usage at every interaction entry point</h3>
 * <pre>{@code
 * if (!interactionSafety.isInteractionSafe()) {
 *     log.warn("Interaction blocked: {}", interactionSafety.getUnsafeReason());
 *     return;
 * }
 * }</pre>
 */
@Singleton
@Slf4j
public class InteractionSafety
{
	public enum HookSeverity
	{
		/** Hook is required for core interaction. Failure opens the circuit. */
		CRITICAL,
		/** Hook is used for auxiliary features. Failure is logged but does not open the circuit. */
		NON_CRITICAL
	}

	@Value
	public static class HookStatus
	{
		String id;
		HookSeverity severity;
		boolean valid;
		@Nullable String failureReason;
		long updatedAtMs;
	}

	private static final int MAX_CONSECUTIVE_FAILURES = 5;
	private static final long MIN_AUTO_CLOSE_INTERVAL_MS = 30_000;

	private final ConcurrentHashMap<String, HookStatus> hookStatuses = new ConcurrentHashMap<>();
	private final AtomicInteger consecutiveFailures = new AtomicInteger(0);

	private volatile boolean circuitOpen = false;
	@Nullable
	private volatile String circuitOpenReason = null;
	private volatile long circuitOpenedAtMs = 0;

	@Inject
	InteractionSafety(EventBus eventBus)
	{
		eventBus.register(this);
	}

	// ---- primary guard ----

	/**
	 * Returns {@code true} only when all of the following hold:
	 * <ul>
	 *   <li>The circuit breaker is not open.</li>
	 *   <li>Consecutive interaction failures are below the threshold.</li>
	 *   <li>No {@link HookSeverity#CRITICAL} hook is in a failed state.</li>
	 * </ul>
	 * This is the single predicate that all interaction code paths must check.
	 */
	public boolean isInteractionSafe()
	{
		if (circuitOpen)
		{
			return false;
		}
		if (consecutiveFailures.get() >= MAX_CONSECUTIVE_FAILURES)
		{
			return false;
		}
		for (final HookStatus status : hookStatuses.values())
		{
			if (status.getSeverity() == HookSeverity.CRITICAL && !status.isValid())
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a human-readable description of why interactions are blocked,
	 * or {@code null} when {@link #isInteractionSafe()} returns {@code true}.
	 */
	@Nullable
	public String getUnsafeReason()
	{
		if (circuitOpen)
		{
			return "circuit-open: " + circuitOpenReason;
		}
		final int failures = consecutiveFailures.get();
		if (failures >= MAX_CONSECUTIVE_FAILURES)
		{
			return "too-many-consecutive-failures: " + failures;
		}
		for (final HookStatus status : hookStatuses.values())
		{
			if (status.getSeverity() == HookSeverity.CRITICAL && !status.isValid())
			{
				return "hook-invalid: " + status.getId() + " — " + status.getFailureReason();
			}
		}
		return null;
	}

	// ---- hook lifecycle reporting ----

	/**
	 * Called by reflection code when a hook is successfully discovered and verified.
	 * Re-registers the hook as valid; triggers auto-close logic if the circuit is open.
	 */
	public void reportHookResolved(String id, HookSeverity severity)
	{
		hookStatuses.put(id, new HookStatus(id, severity, true, null, System.currentTimeMillis()));
		log.debug("Hook resolved: {} ({})", id, severity);
		tryAutoClose();
	}

	/**
	 * Called by reflection code when a hook fails to discover, or throws
	 * {@link IllegalAccessException} (indicating the target became inaccessible).
	 * A {@link HookSeverity#CRITICAL} failure immediately opens the circuit.
	 */
	public void reportHookFailed(String id, HookSeverity severity, String reason)
	{
		final HookStatus prev = hookStatuses.get(id);
		final boolean wasValid = prev == null || prev.isValid();

		hookStatuses.put(id, new HookStatus(id, severity, false, reason, System.currentTimeMillis()));

		if (severity == HookSeverity.CRITICAL && wasValid)
		{
			openCircuit("critical hook '" + id + "' failed: " + reason);
		}
		else
		{
			log.warn("Non-critical hook failed: {} — {}", id, reason);
		}
	}

	/**
	 * Called when a previously failed hook's cache reference has been cleared
	 * so it can be re-discovered on the next invocation attempt.
	 */
	public void reportHookInvalidated(String id)
	{
		final HookStatus existing = hookStatuses.get(id);
		if (existing != null)
		{
			hookStatuses.put(id,
				new HookStatus(id, existing.getSeverity(), false, "invalidated for re-discovery",
					System.currentTimeMillis()));
		}
	}

	// ---- interaction outcome reporting ----

	/**
	 * Called when a game interaction completed without any detected error.
	 * Resets the consecutive failure counter.
	 */
	public void reportInteractionSuccess()
	{
		consecutiveFailures.set(0);
	}

	/**
	 * Called when a game interaction attempt failed (invalid menu entry, reflection
	 * invocation error, menu did not open, etc.). Increments the consecutive failure
	 * counter; opens the circuit after {@value #MAX_CONSECUTIVE_FAILURES} failures.
	 *
	 * @param context short description of where the failure occurred, for logging
	 */
	public void reportInteractionAttemptFailed(String context)
	{
		final int failures = consecutiveFailures.incrementAndGet();
		log.warn("Interaction attempt failed [{}]: {} consecutive failure(s)", context, failures);
		if (failures >= MAX_CONSECUTIVE_FAILURES)
		{
			openCircuit(failures + " consecutive interaction failures, last in: " + context);
		}
	}

	// ---- circuit management ----

	private void openCircuit(String reason)
	{
		if (!circuitOpen)
		{
			circuitOpen = true;
			circuitOpenReason = reason;
			circuitOpenedAtMs = System.currentTimeMillis();
			log.error("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
			log.error("  INTERACTION CIRCUIT OPEN");
			log.error("  reason: {}", reason);
			log.error("  All automation is now suspended.");
			log.error("  Call forceReset() to recover after investigation.");
			log.error("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		}
	}

	private void tryAutoClose()
	{
		if (!circuitOpen)
		{
			return;
		}
		if (System.currentTimeMillis() - circuitOpenedAtMs < MIN_AUTO_CLOSE_INTERVAL_MS)
		{
			return;
		}
		final boolean allCriticalValid = hookStatuses.values().stream()
			.filter(s -> s.getSeverity() == HookSeverity.CRITICAL)
			.allMatch(HookStatus::isValid);
		if (allCriticalValid && consecutiveFailures.get() < MAX_CONSECUTIVE_FAILURES)
		{
			circuitOpen = false;
			circuitOpenReason = null;
			log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
			log.info("  INTERACTION CIRCUIT CLOSED");
			log.info("  All critical hooks are valid again.");
			log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		}
	}

	/**
	 * Manually resets the circuit breaker and clears all failure counters.
	 * Use after investigating a suspension to resume automation.
	 */
	public void forceReset()
	{
		circuitOpen = false;
		circuitOpenReason = null;
		consecutiveFailures.set(0);
		log.warn("InteractionSafety: force-reset. Ensure hooks are valid before resuming.");
	}

	// ---- diagnostics ----

	public Map<String, HookStatus> getHookStatuses()
	{
		return Collections.unmodifiableMap(hookStatuses);
	}

	public boolean isCircuitOpen()
	{
		return circuitOpen;
	}

	@Nullable
	public String getCircuitOpenReason()
	{
		return circuitOpenReason;
	}

	public int getConsecutiveFailures()
	{
		return consecutiveFailures.get();
	}

	// ---- event subscriptions ----

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		// Transient failures from being logged out should not permanently block
		// automation on the next login. Only reset if the circuit is not open
		// (circuit-open conditions require explicit hook re-resolution or forceReset).
		if (event.getGameState() == GameState.LOGGED_IN && !circuitOpen)
		{
			if (consecutiveFailures.getAndSet(0) > 0)
			{
				log.debug("InteractionSafety: reset consecutive failure counter on login");
			}
		}
	}
}

package net.runelite.client.plugins.openrl.api.plugin;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Time;
import net.runelite.client.plugins.openrl.api.game.Game;
import net.runelite.client.plugins.openrl.api.game.TickSnapshot;
import net.runelite.client.plugins.openrl.api.managers.InteractionSafety;
import javax.swing.SwingUtilities;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class for automation plugins that execute a repeating {@link #loop()} body.
 *
 * <h3>Thread model</h3>
 * {@link #loop()} runs on a dedicated per-plugin background thread — never on the
 * game client thread. Because of this:
 * <ul>
 *   <li>Never read live client state (inventories, NPCs, tiles) directly inside
 *       {@code loop()} — use {@link #getSnapshot()} for safe off-thread reads.</li>
 *   <li>To perform a game action, post a {@code MenuAutomated} event or call
 *       {@link net.runelite.client.plugins.openrl.api.game.GameThread#invokeAndWait}.</li>
 *   <li>{@link Time#sleep} / {@link Time#sleepUntil} are safe inside {@code loop()}.</li>
 * </ul>
 *
 * <h3>Loop return values</h3>
 * <ul>
 *   <li>Positive value → sleep that many ms before the next iteration.</li>
 *   <li>Negative value → wait that many ticks before the next iteration
 *       (e.g. {@code return -3} waits 3 game ticks).</li>
 * </ul>
 */
@Slf4j
public abstract class LoopedPlugin extends Plugin implements Runnable
{
	private final AtomicInteger ticks = new AtomicInteger(0);

	private volatile int nextSleepMs = 1000;
	private int currentSleep = 1000;
	private int sleepUntilTick = 0;

	private volatile ScheduledFuture<?> task;

	/** Per-instance executor — each plugin gets its own daemon thread. */
	private final ScheduledExecutorService executor = new LoggableExecutor(1, getClass().getSimpleName());

	// ---- abstract contract ----

	/**
	 * Called repeatedly on the plugin's background thread.
	 * Return a positive ms delay or a negative tick count (see class javadoc).
	 *
	 * <p><b>Do not read live client state here.</b> Use {@link #getSnapshot()} instead.
	 */
	protected abstract int loop();

	// ---- snapshot access ----

	/**
	 * Returns the most recently captured immutable game-state snapshot.
	 * Safe to read from the loop background thread at any time.
	 */
	protected final TickSnapshot getSnapshot()
	{
		return TickSnapshot.current();
	}

	// ---- lifecycle ----

	@Override
	public void run()
	{
		task = executor.schedule(loopTask(), nextSleepMs, TimeUnit.MILLISECONDS);

		while (isRunning())
		{
			if (task.isDone())
			{
				// A cancelled task also reports isDone() == true.
				// Don't reschedule — stop() has already shut down the executor.
				if (task.isCancelled())
				{
					break;
				}
				try
				{
					task = executor.schedule(loopTask(), nextSleepMs, TimeUnit.MILLISECONDS);
				}
				catch (RejectedExecutionException ignored)
				{
					// stop() shut down the executor between isDone() and schedule() — exit cleanly.
					break;
				}
				continue;
			}
			Time.sleep(10);
		}

		task = null;
	}

	public boolean isRunning()
	{
		return task != null && !task.isCancelled();
	}

	public void stop()
	{
		final ScheduledFuture<?> current = task;
		if (current != null)
		{
			current.cancel(true);
		}
		executor.shutdownNow();
	}

	// ---- internals ----

	private Runnable loopTask()
	{
		return () ->
		{
			try
			{
				// Abort the loop iteration if the system is in an unknown or unsafe state.
				// Continuing while hooks are invalid risks producing suspicious behaviour
				// (spam clicks, impossible actions, state desync).
				final InteractionSafety safety = Static.getInteractionSafety();
				if (safety != null && !safety.isInteractionSafe())
				{
					log.debug("Loop iteration paused — system not safe: {}", safety.getUnsafeReason());
					nextSleepMs = 1000;
					return;
				}

				final int currentTick = ticks.get();
				if (sleepUntilTick > 0 && sleepUntilTick > currentTick && Game.isLoggedIn())
				{
					return;
				}

				sleepUntilTick = 0;
				currentSleep = loop();
			}
			catch (PluginStoppedException e)
			{
				SwingUtilities.invokeLater(() -> Plugins.stopPlugin(this));
			}
			finally
			{
				if (sleepUntilTick == 0)
				{
					if (currentSleep < 0 && Game.isLoggedIn())
					{
						sleepUntilTick = ticks.get() + Math.abs(currentSleep);
						nextSleepMs = 0;
					}
					else
					{
						nextSleepMs = currentSleep < 0 ? 1000 : currentSleep;
					}
				}
			}
		};
	}

	@Subscribe
	private void onGameTick(GameTick event)
	{
		ticks.incrementAndGet();
	}

	// ---- executor ----

	private static final class LoggableExecutor extends ScheduledThreadPoolExecutor
	{
		LoggableExecutor(int corePoolSize, String pluginName)
		{
			super(corePoolSize, newDaemonThreadFactory(pluginName));
		}

		private static ThreadFactory newDaemonThreadFactory(String name)
		{
			return r ->
			{
				final Thread t = new Thread(r, "openrl-loop-" + name);
				t.setDaemon(true);
				return t;
			};
		}

		@Override
		protected void afterExecute(Runnable r, Throwable t)
		{
			super.afterExecute(r, t);

			if (t == null && r instanceof Future<?>)
			{
				try
				{
					final Future<?> future = (Future<?>) r;
					if (future.isDone())
					{
						future.get();
					}
				}
				catch (CancellationException ignored)
				{
					// expected on stop()
				}
				catch (ExecutionException ee)
				{
					t = ee.getCause();
				}
				catch (InterruptedException ie)
				{
					Thread.currentThread().interrupt();
				}
			}

			if (t != null)
			{
				log.error("Unhandled error in loop", t);
			}
		}
	}
}

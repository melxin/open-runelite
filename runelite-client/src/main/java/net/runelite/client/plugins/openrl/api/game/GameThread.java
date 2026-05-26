package net.runelite.client.plugins.openrl.api.game;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.runelite.client.plugins.openrl.Static;

/**
 * Utilities for safely crossing the game-thread boundary.
 *
 * <h3>Thread model</h3>
 * The RS client processes all game state on a single game thread. Any read or
 * write of live client state from another thread risks race conditions,
 * ConcurrentModificationExceptions, and partially-updated objects.
 *
 * <h3>Usage summary</h3>
 * <ul>
 *   <li>{@link #invoke(Runnable)} — fire-and-forget. Executes inline if already
 *       on the client thread; otherwise enqueues.</li>
 *   <li>{@link #invokeOnClientThread(Runnable)} — always enqueues, never inline.
 *       Use when ordering relative to already-enqueued work matters.</li>
 *   <li>{@link #invokeAndWait(Callable)} — blocks the calling thread until the
 *       callable completes on the client thread and returns its result.
 *       Never call this from a thread the client thread must wait on.</li>
 *   <li>{@link #assertClientThread()} / {@link #isOnClientThread()} — for
 *       guarding methods that require client-thread context.</li>
 * </ul>
 */
@Slf4j
public class GameThread
{
	private static final long TIMEOUT_MS = 5000;

	/**
	 * Runs {@code runnable} on the client thread.
	 * If the current thread is already the client thread, executes immediately.
	 * Otherwise enqueues for the next available client-thread slot (fire-and-forget).
	 */
	public static void invoke(Runnable runnable)
	{
		if (Static.getClient().isClientThread())
		{
			runnable.run();
		}
		else
		{
			Static.getClientThread().invokeLater(runnable);
		}
	}

	/**
	 * Enqueues {@code runnable} to run on the client thread.
	 * Unlike {@link #invoke(Runnable)}, this method never executes inline,
	 * even if the caller is already on the client thread.
	 * Useful when ordering relative to already-queued work must be preserved.
	 */
	public static void invokeOnClientThread(Runnable runnable)
	{
		Static.getClientThread().invokeLater(runnable);
	}

	/**
	 * Executes {@code callable} on the client thread and blocks until the result
	 * is available, then returns it. If the caller is already on the client thread,
	 * executes inline without scheduling.
	 *
	 * <p>Returns {@code null} if the callable throws, the wait is interrupted, or
	 * the timeout ({@value TIMEOUT_MS} ms) is exceeded.
	 *
	 * <p><b>Never call this from a thread that the client thread must wait on</b>
	 * (e.g. the EDT while holding a lock the client thread needs), or a deadlock
	 * will occur.
	 */
	public static <T> T invokeAndWait(Callable<T> callable)
	{
		if (Static.getClient().isClientThread())
		{
			try
			{
				return callable.call();
			}
			catch (Exception e)
			{
				log.error("invokeAndWait: callable threw on client thread", e);
				return null;
			}
		}

		final FutureTask<T> task = new FutureTask<>(callable);
		Static.getClientThread().invokeLater(task);

		try
		{
			return task.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
		}
		catch (TimeoutException te)
		{
			log.error("invokeAndWait: timed out after {}ms — check for client-thread contention", TIMEOUT_MS);
			task.cancel(true);
		}
		catch (InterruptedException ie)
		{
			Thread.currentThread().interrupt();
			log.warn("invokeAndWait: interrupted");
		}
		catch (ExecutionException ee)
		{
			log.error("invokeAndWait: execution failed", ee.getCause());
		}
		return null;
	}

	/** Returns {@code true} if the current thread is the RS game thread. */
	public static boolean isOnClientThread()
	{
		return Static.getClient().isClientThread();
	}

	/**
	 * Asserts that the calling thread is the RS game thread.
	 *
	 * @throws IllegalStateException if called from any other thread
	 */
	public static void assertClientThread()
	{
		if (!Static.getClient().isClientThread())
		{
			throw new IllegalStateException("Must be called from the client game thread");
		}
	}
}

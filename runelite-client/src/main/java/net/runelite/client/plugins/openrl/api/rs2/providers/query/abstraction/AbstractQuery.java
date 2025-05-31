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
package net.runelite.client.plugins.openrl.api.rs2.providers.query.abstraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.runelite.api.Player;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.rs2.wrappers.RS2Player;

public abstract class AbstractQuery<T, Q extends AbstractQuery<T, Q>>
{
	protected RS2Player getLocal()
	{
		final Player local = Static.getClient().getLocalPlayer();
		if (local == null)
		{
			throw new IllegalStateException("Local player was null, are you logged in?");
		}

		return new RS2Player(local);
	}

	// Subclasses must implement this to provide data
	protected abstract List<T> all(Predicate<? super T> filter);

	// Queries

	protected Predicate<T> predicate = x -> true;
	private final List<Comparator<T>> comparators = new ArrayList<>();
	private final List<Consumer<T>> peekActions = new ArrayList<>();
	private boolean parallel = false;
	private long skipCount = 0;
	private long limitCount = -1;
	private boolean distinct = false;
	private boolean reverse = false;
	private boolean shuffle = false;
	private Function<List<T>, List<T>> postProcessor = null;

	protected final Q self()
	{
		return (Q) this;
	}

	public Q and(Predicate<T> predicate)
	{
		this.predicate = this.predicate.and(predicate);
		return self();
	}

	public Q or(Predicate<T> predicate)
	{
		this.predicate = this.predicate.or(predicate);
		return self();
	}

	public Q not()
	{
		this.predicate = this.predicate.negate();
		return self();
	}

	public Q not(Predicate<T> predicate)
	{
		this.predicate = this.predicate.and(predicate.negate());
		return self();
	}

	public Q test(T type)
	{
		this.predicate = this.predicate.and(x -> this.predicate.test(type));
		return self();
	}

	public Q parallel()
	{
		this.parallel = true;
		return self();
	}

	public Q skip(long count)
	{
		this.skipCount = count;
		return self();
	}

	public Q limit(long count)
	{
		this.limitCount = count;
		return self();
	}

	public Q distinct()
	{
		this.distinct = true;
		return self();
	}

	public Q peek(Consumer<T> action)
	{
		peekActions.add(action);
		return self();
	}

	public Q sort(Comparator<T> comparator)
	{
		comparators.add(comparator);
		return self();
	}

	public <R extends Comparable<? super R>> Q sortBy(Function<? super T, ? extends R> keyExtractor)
	{
		return sort(Comparator.comparing(keyExtractor));
	}

	public <R extends Comparable<? super R>> Q sortByDescending(Function<? super T, ? extends R> keyExtractor)
	{
		return sort((Comparator<T>) Comparator.comparing(keyExtractor).reversed());
	}

	public Q reverse()
	{
		this.reverse = true;
		return self();
	}

	public Q shuffle()
	{
		this.shuffle = true;
		return self();
	}

	public Q postProcess(Function<List<T>, List<T>> processor)
	{
		this.postProcessor = processor;
		return self();
	}

	public Q filter(Predicate<T> predicate)
	{
		this.and(predicate);
		return self();
	}

	// Results

	public List<T> result()
	{
		if (comparators.isEmpty()
			&& peekActions.isEmpty()
			&& !parallel
			&& skipCount == 0
			&& limitCount == -1
			&& !distinct
			&& !reverse
			&& !shuffle
			&& postProcessor == null)
		{
			return all(predicate);
		}

		Stream<T> stream = all(predicate).stream();

		if (parallel)
		{
			stream = stream.parallel();
		}

		for (Consumer<T> peekAction : peekActions)
		{
			stream = stream.peek(peekAction);
		}

		if (distinct)
		{
			stream = stream.distinct();
		}

		if (!comparators.isEmpty())
		{
			stream = stream.sorted(comparators.stream()
				.reduce(Comparator::thenComparing)
				.orElse((a, b) -> 0));
		}

		if (skipCount > 0)
		{
			stream = stream.skip(skipCount);
		}

		if (limitCount >= 0)
		{
			stream = stream.limit(limitCount);
		}

		List<T> result = stream.collect(Collectors.toList());

		if (reverse)
		{
			Collections.reverse(result);
		}

		if (shuffle)
		{
			Collections.shuffle(result);
		}

		if (postProcessor != null)
		{
			result = postProcessor.apply(result);
		}
		return result;
	}

	public CompletableFuture<List<T>> resultAsync()
	{
		return CompletableFuture.supplyAsync(this::result);
	}

	public CompletableFuture<List<T>> resultAsync(Executor executor)
	{
		return CompletableFuture.supplyAsync(this::result, executor);
	}

	public Stream<T> stream()
	{
		return result().stream();
	}

	public void forEach(Consumer<T> action)
	{
		stream().forEach(action);
	}

	public void forEachOrdered(Consumer<T> action)
	{
		stream().forEachOrdered(action);
	}

	public boolean anyMatch(Predicate<T> predicate)
	{
		return stream().anyMatch(predicate);
	}

	public boolean allMatch(Predicate<T> predicate)
	{
		return stream().allMatch(predicate);
	}

	public boolean noneMatch(Predicate<T> predicate)
	{
		return stream().noneMatch(predicate);
	}

	public long count()
	{
		return result().size();
	}

	public boolean exists()
	{
		return count() > 0;
	}

	public boolean isEmpty()
	{
		return result().isEmpty() || stream().findAny().isEmpty() ? true : false;
	}

	public T first()
	{
		final List<T> result = result();
		return result.isEmpty() ? null : result.get(0);
	}

	public T last()
	{
		final List<T> result = result();
		return result.isEmpty() ? null : result.get(result.size() - 1);
	}

	public T random()
	{
		final List<T> results = result();
		return results.isEmpty() ? null : results.get(ThreadLocalRandom.current().nextInt(results.size()));
	}

	public List<T> sorted(Comparator<T> comparator)
	{
		return stream()
			.sorted(comparator)
			.collect(Collectors.toList());
	}

	public <R> R aggregate(Function<Stream<T>, R> aggregator)
	{
		return aggregator.apply(stream());
	}

	public <R, A> R collect(Collector<? super T, A, R> collector)
	{
		return stream().collect(collector);
	}
}
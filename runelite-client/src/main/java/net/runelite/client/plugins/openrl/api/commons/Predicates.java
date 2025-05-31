package net.runelite.client.plugins.openrl.api.commons;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class Predicates
{
	/**
	 * Returns a predicate that tests if a property extracted from an object is distinct across invocations.
	 * Useful for filtering duplicates based on a specific property.
	 *
	 * @param propertyExtractor Function to extract the property to test for distinctness.
	 * @param <T> Type of input object.
	 * @return Predicate that returns true if the property was not seen before.
	 */
	public static <T> Predicate<T> distinctByProperty(Function<? super T, ?> propertyExtractor)
	{
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(propertyExtractor.apply(t));
	}

	/**
	 * Returns a predicate that tests if a string equals any of the provided texts.
	 *
	 * @param texts Array of strings to compare against.
	 * @return Predicate that tests if input equals any of the provided texts.
	 */
	public static Predicate<String> texts(String... texts)
	{
		return t ->
		{
			if (t == null)
			{
				return false;
			}
			for (String text : texts)
			{
				if (t.equals(text))
				{
					return true;
				}
			}
			return false;
		};
	}

	/**
	 * Returns a predicate that tests if a string contains a given substring, with optional case sensitivity.
	 *
	 * @param subString Substring to look for.
	 * @param caseSensitive Whether the contains check should be case-sensitive.
	 * @return Predicate that tests if the string contains the substring.
	 */
	public static Predicate<String> textContains(String subString, boolean caseSensitive)
	{
		return t ->
		{
			if (t == null || subString == null)
			{
				return false;
			}
			if (caseSensitive)
			{
				return t.contains(subString);
			}
			else
			{
				return t.toLowerCase().contains(subString.toLowerCase());
			}
		};
	}

	/**
	 * Overloaded method for case-sensitive substring containment check.
	 *
	 * @param subString Substring to look for.
	 * @return Predicate that tests if the string contains the substring (case-sensitive).
	 */
	public static Predicate<String> textContains(String subString)
	{
		return textContains(subString, true);
	}

	/**
	 * Predicate that tests if an object is null.
	 *
	 * @param <T> Type of object.
	 * @return Predicate that returns true if the object is null.
	 */
	public static <T> Predicate<T> isNull()
	{
		return Objects::isNull;
	}

	/**
	 * Predicate that tests if an object is not null.
	 *
	 * @param <T> Type of object.
	 * @return Predicate that returns true if the object is not null.
	 */
	public static <T> Predicate<T> nonNull()
	{
		return Objects::nonNull;
	}

	/**
	 * Predicate that tests if an object equals a specified value.
	 *
	 * @param value Value to compare against.
	 * @param <T> Type of object.
	 * @return Predicate that returns true if input equals the specified value.
	 */
	public static <T> Predicate<T> equalsTo(T value)
	{
		return t -> Objects.equals(t, value);
	}

	/**
	 * Predicate that tests if a string starts with a given prefix.
	 *
	 * @param prefix Prefix string.
	 * @param caseSensitive Whether the check should be case-sensitive.
	 * @return Predicate that tests if the string starts with the prefix.
	 */
	public static Predicate<String> startsWith(String prefix, boolean caseSensitive)
	{
		return t ->
		{
			if (t == null || prefix == null)
			{
				return false;
			}
			if (caseSensitive)
			{
				return t.startsWith(prefix);
			}
			else
			{
				return t.toLowerCase().startsWith(prefix.toLowerCase());
			}
		};
	}

	/**
	 * Overloaded method for case-sensitive startsWith.
	 */
	public static Predicate<String> startsWith(String prefix)
	{
		return startsWith(prefix, true);
	}

	/**
	 * Predicate that tests if a string ends with a given suffix.
	 *
	 * @param suffix Suffix string.
	 * @param caseSensitive Whether the check should be case-sensitive.
	 * @return Predicate that tests if the string ends with the suffix.
	 */
	public static Predicate<String> endsWith(String suffix, boolean caseSensitive)
	{
		return t ->
		{
			if (t == null || suffix == null)
			{
				return false;
			}
			if (caseSensitive)
			{
				return t.endsWith(suffix);
			}
			else
			{
				return t.toLowerCase().endsWith(suffix.toLowerCase());
			}
		};
	}

	/**
	 * Overloaded method for case-sensitive endsWith.
	 */
	public static Predicate<String> endsWith(String suffix)
	{
		return endsWith(suffix, true);
	}

	/**
	 * Predicate that matches a string against a regex pattern.
	 *
	 * @param regex Pattern to match.
	 * @return Predicate that returns true if the string matches the pattern.
	 */
	public static Predicate<String> matches(String regex)
	{
		return t ->
		{
			if (t == null || regex == null)
			{
				return false;
			}
			return t.matches(regex);
		};
	}

	/**
	 * Creates a predicate that checks if the input matches any element in the provided collection or array.
	 *
	 * @param collectionOrArray Collection or array of elements.
	 * @param <T> Type of elements.
	 * @return Predicate that returns true if input is in the collection or array.
	 */
	public static <T> Predicate<T> in(Collection<T> collectionOrArray)
	{
		return collectionOrArray == null ? t -> false : collectionOrArray::contains;
	}

	/**
	 * Overloaded method accepting array of elements.
	 */
	@SafeVarargs
	public static <T> Predicate<T> in(T... elements)
	{
		return in(java.util.Arrays.asList(elements));
	}

	/**
	 * Predicate that tests if the input matches any of the provided predicates.
	 *
	 * @param predicates Array of predicates.
	 * @param <T> Type of input.
	 * @return Predicate that returns true if any predicate matches.
	 */
	@SafeVarargs
	public static <T> Predicate<T> anyMatch(Predicate<? super T>... predicates)
	{
		return t ->
		{
			for (Predicate<? super T> predicate : predicates)
			{
				if (predicate.test(t))
				{
					return true;
				}
			}
			return false;
		};
	}

	/**
	 * Predicate that tests if the input matches all of the provided predicates.
	 *
	 * @param predicates Array of predicates.
	 * @param <T> Type of input.
	 * @return Predicate that returns true if all predicates match.
	 */
	@SafeVarargs
	public static <T> Predicate<T> allMatch(Predicate<? super T>... predicates)
	{
		return t ->
		{
			for (Predicate<? super T> predicate : predicates)
			{
				if (!predicate.test(t))
				{
					return false;
				}
			}
			return true;
		};
	}
}
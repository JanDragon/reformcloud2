package systems.reformcloud.reformcloud2.executor.api.common.utility.list;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class Streams {

    private Streams() {
        throw new UnsupportedOperationException();
    }

    /**
     * Makes all strings in the list lower-case
     *
     * @param list The input list
     * @return A new list with all keys of the given list, but lower-case
     */
    @NotNull
    public static List<String> toLowerCase(@NotNull Collection<String> list) {
        List<String> strings = new ArrayList<>();
        list.forEach(string -> strings.add(string.toLowerCase()));

        return strings;
    }

    /**
     * Makes the given list unmodifiable
     *
     * @param in The list which should be unmodifiable
     * @param <T> The object parameter of the values in the list
     * @return A unmodifiable list of all given keys
     */
    @NotNull
    public static <T> List<T> unmodifiable(@NotNull List<T> in) {
        return Collections.unmodifiableList(in);
    }

    /**
     * Creates a new list with all given parameters
     *
     * @param in The given keys which should be in the new list
     * @param <T> The object parameter of the values in the list
     * @return A new list with all values of the input list in it
     */
    @NotNull
    public static <T> List<T> newList(@NotNull Collection<T> in) {
        return new ArrayList<>(in);
    }

    /**
     * Copies a sorted set including the {@link Comparator} and all values in it
     *
     * @param set The incoming set with all values in it
     * @param <T> The object parameter of the values in the list
     * @return A copy of the current set
     */
    @NotNull
    public static <T> SortedSet<T> copySortedSet(@NotNull SortedSet<T> set) {
        SortedSet<T> sortedSet = new TreeSet<>(set.comparator());
        sortedSet.addAll(set);
        return sortedSet;
    }

    /**
     * Applies a function to all values in the given list
     *
     * @param in The incoming list with the key values in it
     * @param function The function which should get applied to the values
     * @param <T> The object parameter of the values in the list
     * @param <F> The object parameter of the values in the outgoing list
     * @return A new list with all values of the incoming list, applied to the function
     */
    @NotNull
    public static <T, F> List<F> apply(@NotNull List<T> in, @NotNull Function<T, F> function) {
        return in.stream().map(function).collect(Collectors.toList());
    }

    /**
     * Filters a specific value out of the given list
     *
     * @param in The incoming list
     * @param predicate The predicate which checks if the current object equals the the filter
     * @param <T> The object parameter of the values in the list
     * @return The first value in the list which equals to the filter
     */
    @Nullable
    public static <T> T filter(@NotNull Collection<T> in, @NotNull Predicate<T> predicate) {
        if (in.isEmpty()) {
            return null;
        }

        return in.stream().filter(predicate).findFirst().orElse(null);
    }

    /**
     * Filters a specific value out of the given list
     *
     * @see #filter(Collection, Predicate)
     * @param in The incoming list
     * @param predicate The predicate which checks if the current object equals the the filter
     * @param <T> The object parameter of the values in the list
     * @return A new {@link ReferencedOptional} with the value or {@code null} if no value in the list equals to the filter
     */
    @NotNull
    public static <T> ReferencedOptional<T> filterToReference(@NotNull Collection<T> in, @NotNull Predicate<T> predicate) {
        return ReferencedOptional.build(filter(in, predicate));
    }

    /**
     * Filters a specific value out of the given map
     *
     * @param in The given map
     * @param predicate The predicate which checks if the current object equals the the filter
     * @param <K> The object parameter of the keys in the map
     * @param <V> The object parameter of the values in the map
     * @return A new {@link ReferencedOptional} with the value or {@code null} if no value in the map equals to the filter
     */
    @NotNull
    public static <K, V> ReferencedOptional<V> filterToReference(@NotNull Map<K, V> in, @NotNull Predicate<K> predicate) {
        if (in.isEmpty()) {
            return ReferencedOptional.empty();
        }

        for (Map.Entry<K, V> entry : in.entrySet()) {
            if (predicate.test(entry.getKey())) {
                return ReferencedOptional.build(entry.getValue());
            }
        }

        return ReferencedOptional.empty();
    }

    /**
     * Filters a value out of the given list and applies the function if the value is non-{@code null}
     *
     * @see #filter(Collection, Predicate)
     * @param in The given list
     * @param predicate The predicate which checks if the current object equals the the filter
     * @param function The function which should get applied to the first value which equals to the filter
     * @param <T> The object parameter of the values in the list
     * @param <F> The object parameter of the outgoing value
     * @return The value which got applied to the function or {@code null} if no value in the list equals to the filter
     */
    @Nullable
    public static <T, F> F filterAndApply(@NotNull List<T> in, @NotNull Predicate<T> predicate, @NotNull Function<T, F> function) {
        if (in.isEmpty()) {
            return null;
        }

        T value = filter(in, predicate);
        return value == null ? null : function.apply(value);
    }

    /**
     * Gets all values in a map which equals to the given filter
     *
     * @param in The given map
     * @param predicate The predicate which checks if the current object equals the the filter
     * @param <F> The object parameter of the keys in the map
     * @param <T> The object parameter of the values in the map
     * @return A list with all values of the map which equaled to the filter
     */
    @NotNull
    public static <F, T> List<T> getValues(@NotNull Map<F, T> in, @NotNull Predicate<F> predicate) {
        if (in.isEmpty()) {
            return new ArrayList<>();
        }

        List<T> out = new ArrayList<>();
        in.forEach((key, value) -> {
            if (predicate.test(key)) {
                out.add(value);
            }
        });
        return out;
    }

    /**
     * Goes trough all values in a map and applies them to a consumer
     *
     * @param map The given map
     * @param consumer The consumer which should accept all values in the map
     * @param <T> The object parameter of the values in the map
     */
    public static <T> void forEachValues(@NotNull Map<?, T> map, @NotNull Consumer<T> consumer) {
        map.values().forEach(consumer);
    }

    /**
     * Goes through all values in á list and applies them to a consumer
     *
     * @param list The given list
     * @param consumer The consumer which should accept all values in the map
     * @param <F> The object parameter of the values in the list
     */
    public static <F> void forEach(@NotNull List<F> list, @NotNull Consumer<F> consumer) {
        list.forEach(consumer);
    }

    /**
     * Applies a function to all keys in the given map
     *
     * @param map The given map
     * @param fxFunction The function which should get applied to all keys in the map
     * @param <F> The object parameter of the keys in the map
     * @param <X> The object parameter of the values in the outgoing list
     * @return The created list with all applied values in the map
     */
    @NotNull
    public static <F, X> List<X> keyApply(@NotNull Map<F, ?> map, @NotNull Function<F, X> fxFunction) {
        return map.keySet().stream().map(fxFunction).collect(Collectors.toList());
    }

    /**
     * Creates a new collection
     *
     * @param function The function which creates the collection
     * @param in The array of the incoming objects
     * @param <S> The object parameter of the array
     * @param <F> The object parameter of the values in the outgoing list
     * @return The created list with all values applied to the function
     */
    @NotNull
    @SafeVarargs
    public static <S, F> Collection<F> newCollection(@NotNull Function<S, F> function, S... in) {
        return newCollection(Arrays.asList(in), function);
    }

    /**
     * Applies all values of the incoming collection to the function and collect the values
     *
     * @param in The incoming collection
     * @param function The function to which the values get applied to
     * @param <S> The object parameter of the array
     * @param <F> The object parameter of the values in the outgoing list
     * @return The created collection with the values applied to the function
     */
    @NotNull
    public static <S, F> Collection<F> newCollection(@NotNull Collection<S> in, @NotNull Function<S, F> function) {
        return newCollection(in, s -> true, function);
    }

    /**
     * Applies all values of the incoming collection to the function and collect the values
     *
     * @param in The incoming collection
     * @param predicate The {@link Predicate} which checks if the value of the list should get into the new one
     * @param function The function which maps the value of the incoming collection to the outgoing one
     * @param <S> The object parameter of the collection
     * @param <F> The object parameter of the values in the outgoing list
     * @return The created collection
     */
    @NotNull
    public static <S, F> Collection<F> newCollection(@NotNull Collection<S> in, @NotNull Predicate<S> predicate, @NotNull Function<S, F> function) {
        return in.stream().filter(predicate).map(function).collect(Collectors.toList());
    }

    /**
     * Filters all values out of the incoming collection which matches to the {@link Predicate}
     *
     * @param list The incoming list
     * @param predicate The predicate which tests the values in the list
     * @param <T> The object parameter of the collection
     * @return The new list with all values matching the {@link Predicate}
     */
    @NotNull
    public static <T> List<T> list(@NotNull Collection<T> list, @NotNull Predicate<T> predicate) {
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * Filters all values out of the incoming collection which not matches to the {@link Predicate}
     *
     * @param list The incoming list
     * @param predicate The predicate which tests the values in the list
     * @param <T> The object parameter of the collection
     * @return The new list with all values not matching the {@link Predicate}
     */
    @NotNull
    public static <T> Collection<T> others(@NotNull Collection<T> list, @NotNull Predicate<T> predicate) {
        return list.stream().filter(predicate.negate()).collect(Collectors.toList());
    }

    /**
     * Filters all values out of the incoming collection which matches to the {@link Predicate}
     *
     * @param collection The incoming collection
     * @param predicate The predicate which tests the values in the list
     * @param <T> The object parameter of the collection
     * @return The new list with all values matching the {@link Predicate}
     */
    @NotNull
    public static <T> Collection<T> allOf(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * Applies a function to the given list
     *
     * @param collection The collection on which the function should get applied
     * @param function The function which should get applied
     * @param <T> The object parameter of the collection
     * @param <F> The object parameter of the values in the outgoing collection
     * @return The new collection with all values applied to the function in it
     */
    @NotNull
    public static <T, F> Collection<F> apply(@NotNull Collection<T> collection, @NotNull Function<T, F> function) {
        return collection.stream().map(function).collect(Collectors.toList());
    }

    /**
     * Filters all values of the map when the key matches to the filter
     *
     * @param in The incoming map
     * @param predicate The predicate which filters the keys of the map
     * @param <F> The object parameter of the keys in the map
     * @param <S> The object parameter of the values in the map
     * @return The collection with all filtered values of the map
     */
    @NotNull
    public static <F, S> Collection<S> deepFilter(@NotNull Map<F, S> in, @NotNull Predicate<Map.Entry<F, S>> predicate) {
        Collection<S> out = new LinkedList<>();
        in.entrySet().forEach(e -> {
            if (predicate.test(e)) {
                out.add(e.getValue());
            }
        });
        return out;
    }

    /**
     * Filters one value of the map which matches the filter
     *
     * @param in The incoming map
     * @param predicate The predicate which filters the keys of the map
     * @param <F> The object parameter of the keys in the map
     * @param <S> The object parameter of the values in the map
     * @return The first {@link Map.Entry} which matches to filter or {@link ReferencedOptional#empty()}
     */
    @NotNull
    public static <F, S> ReferencedOptional<Map.Entry<F, S>> deepFilterToReference(@NotNull Map<F, S> in, @NotNull Predicate<Map.Entry<F, S>> predicate) {
        if (in.isEmpty()) {
            return ReferencedOptional.empty();
        }

        for (Map.Entry<F, S> fsEntry : in.entrySet()) {
            if (predicate.test(fsEntry)) {
                return ReferencedOptional.build(fsEntry);
            }
        }

        return ReferencedOptional.empty();
    }

    /**
     * Contacts two arrays
     *
     * @param first The base array which should get concatenated with the other
     * @param second The array which should be added to the base array
     * @param <T> The type of the arrays which should get concatenated
     * @return The concatenated array of the first and second array
     */
    @NotNull
    public static <T> T[] concat(@NotNull T[] first, @NotNull T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}

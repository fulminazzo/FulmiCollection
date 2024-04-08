package it.fulminazzo.fulmicollection.structures;

import it.fulminazzo.fulmicollection.objects.FieldEquable;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * An implementation of {@link Map} that temporarily stores values.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 */
public class CacheMap<K, V> extends FieldEquable implements Map<K, V> {
    private static final long DEFAULT_PERIOD = 3600 * 1000L;
    private static final long DEFAULT_EXPIRE_TIME = 60 * 1000L;

    private final Map<K, Tuple<V, Long>> internal;
    @Getter
    private final long expirationTime;
    @Getter
    private final long period;
    private long lastCheck;

    /**
     * Instantiates a new Cache map.
     */
    public CacheMap() {
        this(null);
    }

    /**
     * Instantiates a new Cache map.
     *
     * @param map the map to copy from
     */
    public CacheMap(final @Nullable Map<K, V> map) {
        this(map, DEFAULT_PERIOD);
    }

    /**
     * Instantiates a new Cache map.
     *
     * @param period the period in milliseconds to check for expired values
     */
    public CacheMap(final long period) {
        this(period, DEFAULT_EXPIRE_TIME);
    }

    /**
     * Instantiates a new Cache map.
     *
     * @param map        the map to copy from
     * @param expirationTime the time in milliseconds after which key-value pairs are considered expired
     */
    public CacheMap(final @Nullable Map<K, V> map, final long expirationTime) {
        this(map, DEFAULT_PERIOD, expirationTime);
    }

    /**
     * Instantiates a new Cache map.
     *
     * @param period     the period in milliseconds to check for expired values
     * @param expirationTime the time in milliseconds after which key-value pairs are considered expired
     */
    public CacheMap(final long period, final long expirationTime) {
        this(null, period, expirationTime);
    }

    /**
     * Instantiates a new Cache map.
     *
     * @param map        the map to copy from
     * @param period     the period in milliseconds to check for expired values
     * @param expirationTime the time in milliseconds after which key-value pairs are considered expired
     */
    public CacheMap(final @Nullable Map<K, V> map, final long period, final long expirationTime) {
        this.internal = new ConcurrentHashMap<>();
        if (map != null) putAll(map);
        this.period = period;
        this.expirationTime = expirationTime;
    }

    /**
     * Removes all the key-value pairs that have exceeded {@link #expirationTime}.
     */
    public void clearExpired() {
        final long now = now();
        for (K key : this.internal.keySet()) {
            Tuple<V, Long> value = this.internal.get(key);
            if (now - value.getValue() >= this.expirationTime)
                this.internal.remove(key, value);
        }
        this.lastCheck = now;
    }

    /**
     * Checks if the {@link #lastCheck} has surpassed {@link #period}.
     *
     * @return true if it has
     */
    protected boolean shouldCheck() {
        return now() - this.lastCheck >= this.period;
    }

    /**
     * Returns the current time in milliseconds.
     *
     * @return the time
     */
    protected long now() {
        return new Date().getTime();
    }

    @Override
    public int size() {
        return this.internal.size();
    }

    @Override
    public boolean isEmpty() {
        return this.internal.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return this.internal.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return this.internal.values().stream().anyMatch(t -> Objects.equals(t.getValue(), o));
    }

    @Override
    public V get(Object o) {
        Tuple<V, Long> t = this.internal.get(o);
        return t == null ? null : t.getKey();
    }

    @Nullable
    @Override
    public V put(K k, V v) {
        Tuple<V, Long> t = this.internal.put(k, new Tuple<>(v, now()));
        return t == null ? null : t.getKey();
    }

    @Override
    public V remove(Object o) {
        Tuple<V, Long> t = this.internal.remove(o);
        return t == null ? null : t.getKey();
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> map) {
        map.forEach(this::put);
    }

    @Override
    public void clear() {
        this.internal.clear();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return this.internal.keySet();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        return this.internal.values().stream()
                .map(Tuple::getKey)
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.internal.entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue().getKey()))
                .collect(Collectors.toSet());
    }
}

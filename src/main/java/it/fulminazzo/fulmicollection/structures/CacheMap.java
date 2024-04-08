package it.fulminazzo.fulmicollection.structures;

import it.fulminazzo.fulmicollection.objects.FieldEquable;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link Map} that temporarily stores values.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 */
public class CacheMap<K, V> extends FieldEquable {
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
        this.internal = new HashMap<>();
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
}

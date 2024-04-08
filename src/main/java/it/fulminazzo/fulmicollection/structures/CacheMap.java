package it.fulminazzo.fulmicollection.structures;

import it.fulminazzo.fulmicollection.objects.FieldEquable;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CacheMap<K, V> extends FieldEquable {
    private static final long DEFAULT_PERIOD = 3600;
    private static final long DEFAULT_EXPIRE_TIME = 60;

    private final Map<K, Tuple<V, Long>> internal;
    @Getter
    private final long expireTime;
    @Getter
    private final long period;
    private long lastCheck;

    public CacheMap() {
        this(null);
    }

    public CacheMap(final @Nullable Map<K, V> map) {
        this(map, DEFAULT_PERIOD);
    }

    public CacheMap(final long period) {
        this(period, DEFAULT_EXPIRE_TIME);
    }

    public CacheMap(final @Nullable Map<K, V> map, final long expireTime) {
        this(map, DEFAULT_PERIOD, expireTime);
    }

    public CacheMap(final long period, final long expireTime) {
        this(null, period, expireTime);
    }

    public CacheMap(final @Nullable Map<K, V> map, final long period, final long expireTime) {
        this.internal = new HashMap<>();
        if (map != null) putAll(map);
        this.period = period;
        this.expireTime = expireTime;
    }

    public void clearExpired() {
        final long now = now();
        for (K key : this.internal.keySet()) {
            Tuple<V, Long> value = this.internal.get(key);
            if (now - value.getValue() >= this.expireTime)
                this.internal.remove(key, value);
        }
        this.lastCheck = now;
    }

    protected boolean shouldCheck() {
        return now() - this.lastCheck >= this.period;
    }

    protected long now() {
        return new Date().getTime();
    }
}

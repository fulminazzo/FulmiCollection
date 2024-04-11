package it.fulminazzo.fulmicollection.structures;

import it.fulminazzo.fulmicollection.objects.FieldEquable;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class BiOptional<K, V> extends FieldEquable {
    private static final BiOptional<?, ?> EMPTY = new BiOptional<>();
    private final K key;
    private final V value;

    private BiOptional() {
        this.key = null;
        this.value = null;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> BiOptional<K, V> empty() {
        return (BiOptional<K, V>) EMPTY;
    }

    private BiOptional(K key, V value) {
        this.key = Objects.requireNonNull(key);
        this.value = Objects.requireNonNull(value);
    }

    public static <K, V> BiOptional<K, V> of(K key, V value) {
        return new BiOptional<>(key, value);
    }

    public static <K, V> BiOptional<K, V> ofNullable(K key, V value) {
        return key == null || value == null ? empty() : of(key, value);
    }

    public Tuple<K, V> get() {
        if (isPresent()) return new Tuple<>(this.key, this.value);
        else throw new NoSuchElementException("No value present");
    }

    public boolean isPresent() {
        return this.key != null && this.value != null;
    }

    public void ifPresent(BiConsumer<? super K, ? super V> consumer) {
        if (isPresent()) consumer.accept(this.key, this.value);
    }

    public Tuple<K, V> orElse(K key, V value) {
        return isPresent() ? get() : new Tuple<>(key, value);
    }

    public Tuple<K, V> orElseGet(Supplier<Tuple<K, V>> function) {
        return isPresent() ? get() : function.get();
    }

    public <X extends Throwable> Tuple<K, V> orElseThrow(Supplier<? extends X> function) throws X {
        if (isPresent()) return get();
        else throw function.get();
    }

    @Override
    public String toString() {
        final String className = getClass().getSimpleName();
        return isPresent() ? String.format("%s[%s, %s]", className, this.key, this.value) :
                String.format("%s.empty", className);
    }
    
}

package it.fulminazzo.fulmicollection.structures;

import it.fulminazzo.fulmicollection.objects.FieldEquable;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * An implementation of {@link java.util.Optional} for key-value pairs.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 */
public final class BiOptional<K, V> extends FieldEquable {
    private static final BiOptional<?, ?> EMPTY = new BiOptional<>();
    private final K key;
    private final V value;

    private BiOptional() {
        this.key = null;
        this.value = null;
    }

    /**
     * Returns an empty BiOptional.
     *
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @return the BiOptional
     */
    @SuppressWarnings("unchecked")
    public static <K, V> BiOptional<K, V> empty() {
        return (BiOptional<K, V>) EMPTY;
    }

    private BiOptional(K key, V value) {
        this.key = Objects.requireNonNull(key);
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Gets an instance of BiOptional containing the provided key and value.
     * Both MUST be not null.
     *
     * @param <K>   the type of the key
     * @param <V>   the type of the value
     * @param key   the key
     * @param value the value
     * @return the BiOptional
     */
    public static <K, V> BiOptional<K, V> of(K key, V value) {
        return new BiOptional<>(key, value);
    }

    /**
     * Gets an instance of BiOptional containing the provided key and value.
     * If one of them is null, {@link #empty()} is returned.
     *
     * @param <K>   the type of the key
     * @param <V>   the type of the value
     * @param key   the key
     * @param value the value
     * @return the BiOptional
     */
    public static <K, V> BiOptional<K, V> ofNullable(K key, V value) {
        return key == null || value == null ? empty() : of(key, value);
    }

    /**
     * Gets the contents of this BiOptional in the form of {@link Tuple}.
     *
     * @return the tuple
     */
    public Tuple<K, V> get() {
        if (isPresent()) return new Tuple<>(this.key, this.value);
        else throw new NoSuchElementException("Either the key or the value are not present");
    }

    /**
     * Checks if both the key and the value are present.
     *
     * @return true if they are
     */
    public boolean isPresent() {
        return this.key != null && this.value != null;
    }

    /**
     * Executes the given function if {@link #isPresent()} is true.
     *
     * @param function the function
     */
    public void ifPresent(BiConsumer<? super K, ? super V> function) {
        if (isPresent()) function.accept(this.key, this.value);
    }

    /**
     * If {@link #isPresent()} is true, {@link #get()} is returned.
     * Otherwise, a new tuple with the specified values is created.
     *
     * @param key   the key
     * @param value the value
     * @return the tuple
     */
    public Tuple<K, V> orElse(K key, V value) {
        return isPresent() ? get() : new Tuple<>(key, value);
    }

    /**
     * If {@link #isPresent()} is true, {@link #get()} is returned.
     * Otherwise, the return of the given function is returned.
     *
     * @param function the function
     * @return the tuple
     */
    public Tuple<K, V> orElseGet(Supplier<Tuple<K, V>> function) {
        return isPresent() ? get() : function.get();
    }

    /**
     * If {@link #isPresent()} is true, {@link #get()} is returned.
     * Otherwise, the given exception is thrown.
     *
     * @param <X>      the type of the exception
     * @param function the function
     * @return the tuple
     * @throws X the exception
     */
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

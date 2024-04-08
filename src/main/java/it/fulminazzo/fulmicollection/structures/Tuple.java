package it.fulminazzo.fulmicollection.structures;

import it.fulminazzo.fulmicollection.objects.FieldEquable;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * The type Tuple.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 */
@Getter
@Setter
public class Tuple<K, V> extends FieldEquable implements Serializable {
    private K key;
    private V value;

    /**
     * Instantiates a new Tuple.
     */
    public Tuple() {
        
    }

    /**
     * Instantiates a new Tuple.
     *
     * @param key   the key
     * @param value the value
     */
    public Tuple(K key, V value) {
        set(key, value);
    }

    /**
     * Set.
     *
     * @param key   the key
     * @param value the value
     */
    public void set(K key, V value) {
        setKey(key);
        setValue(value);
    }

    /**
     * Check if is empty.
     *
     * @return true if {@link #key} and {@link #value} are null
     */
    public boolean isEmpty() {
        return this.key == null && this.value == null;
    }

    /**
     * Check if contains the given key.
     *
     * @param key the key
     * @return true if it does
     */
    public boolean containsKey(K key) {
        return this.key != null && Objects.equals(this.key, key);
    }

    /**
     * Check the {@link #key}.
     *
     * @return true if it is not null
     */
    public boolean hasKey() {
        return this.key != null;
    }

    /**
     * Check if contains the given value.
     *
     * @param value the value
     * @return true if it does
     */
    public boolean containsValue(V value) {
        return this.value != null && Objects.equals(this.value, value);
    }

    /**
     * Check the {@link #value}.
     *
     * @return true if it is not null
     */
    public boolean hasValue() {
        return this.value != null;
    }

    /**
     * Copy the current tuple into a new one.
     *
     * @return the copy
     */
    public Tuple<K, V> copy() {
        return new Tuple<>(this.key, this.value);
    }

    /**
     * If {@link #isEmpty()} is false, the given function is executed.
     *
     * @param function the function
     * @return this tuple
     */
    public Tuple<K, V> ifPresent(BiConsumer<K, V> function) {
        if (!isEmpty()) function.accept(this.key, this.value);
        return this;
    }

    /**
     * If {@link #isEmpty()} is true, the given function is executed.
     *
     * @param function the function
     * @return this tuple
     */
    public Tuple<K, V> orElse(Runnable function) {
        if (isEmpty()) function.run();
        return this;
    }

    @Override
    public @NotNull String toString() {
        return String.format("%s{key: %s; value: %s}", getClass().getSimpleName(), this.key, this.value);
    }
}

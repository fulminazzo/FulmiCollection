package it.fulminazzo.fulmicollection.structures.tuples;

import it.fulminazzo.fulmicollection.interfaces.functions.BiConsumerException;
import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.utils.ExceptionUtils;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The type Tuple.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 */
@Getter
@Setter
public class Tuple<K, V> extends AbstractTuple<
        Tuple<K, V>,
        BiConsumerException<K, V, Exception>,
        BiFunctionException<K, V, Boolean, Exception>
        > {
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
     * Check if contains the given key.
     *
     * @param key the key
     * @return true if it does
     */
    public boolean containsKey(K key) {
        return Objects.equals(this.key, key);
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
        return Objects.equals(this.value, value);
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
     * Converts the current tuple to a new one using the given function.
     * Executed only if {@link #isPresent()}.
     *
     * @param function the function
     * @return the new tuple
     */
    @SuppressWarnings("unchecked")
    public <S, T> Tuple<S, T> map(@NotNull BiFunctionException<K, V, Tuple<S, T>, Exception> function) {
        if (isPresent())
            try {
                return function.apply(this.key, this.value);
            } catch (Exception e) {
                ExceptionUtils.throwException(e);
            }
        return (Tuple<S, T>) empty();
    }

}

package it.fulminazzo.fulmicollection.structures.tuples;

import it.fulminazzo.fulmicollection.interfaces.functions.ConsumerException;
import it.fulminazzo.fulmicollection.interfaces.functions.FunctionException;
import it.fulminazzo.fulmicollection.utils.ExceptionUtils;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The type Singlet.
 *
 * @param <T> the type parameter
 */
@Getter
@Setter
public class Singlet<T> extends AbstractTuple<Singlet<T>, ConsumerException<T>, FunctionException<T, Boolean>> {
    private T value;

    /**
     * Instantiates a new Singlet.
     */
    public Singlet() {

    }

    /**
     * Instantiates a new Singlet.
     *
     * @param value the value
     */
    public Singlet(T value) {
        setValue(value);
    }

    /**
     * Check if contains the given value.
     *
     * @param value the value
     * @return true if it does
     */
    public boolean containsValue(T value) {
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
     * @param <V>      the type parameter
     * @param function the function
     * @return the new tuple
     */
    @SuppressWarnings("unchecked")
    public <V> Singlet<V> map(@NotNull FunctionException<T, V> function) {
        if (isPresent())
            try {
                return new Singlet<>(function.apply(this.value));
            } catch (Exception e) {
                ExceptionUtils.throwException(e);
            }
        return (Singlet<V>) empty();
    }

    /**
     * Converts the current singlet to a nullable singlet
     *
     * @return the nullable singlet
     */
    public NullableSinglet<T> toNullable() {
        return new NullableSinglet<>(this.value);
    }
}

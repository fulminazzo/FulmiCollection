package it.fulminazzo.fulmicollection.structures.tuples;

import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumerException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriFunctionException;
import it.fulminazzo.fulmicollection.utils.ExceptionUtils;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The type Triple.
 *
 * @param <F> the type parameter
 * @param <S> the type parameter
 * @param <T> the type parameter
 */
@Getter
@Setter
public class Triple<F, S, T> extends AbstractTuple<
        Triple<F, S, T>,
        TriConsumerException<F, S, T, Exception>,
        TriFunctionException<F, S, T, Boolean, Exception>
        > {
    private F first;
    private S second;
    private T third;

    /**
     * Instantiates a new Triple.
     */
    public Triple() {

    }

    /**
     * Instantiates a new Triple.
     *
     * @param first  the first
     * @param second the second
     * @param third  the third
     */
    public Triple(F first, S second, T third) {
        set(first, second, third);
    }

    /**
     * Set.
     *
     * @param first  the first
     * @param second the second
     * @param third  the third
     */
    public void set(F first, S second, T third) {
        setFirst(first);
        setSecond(second);
        setThird(third);
    }

    /**
     * Check if contains the given first.
     *
     * @param first the first
     * @return true if it does
     */
    public boolean containsFirst(F first) {
        return Objects.equals(this.first, first);
    }

    /**
     * Check the {@link #first}.
     *
     * @return true if it is not null
     */
    public boolean hasFirst() {
        return this.first != null;
    }

    /**
     * Check if contains the given second.
     *
     * @param second the second
     * @return true if it does
     */
    public boolean containsSecond(S second) {
        return Objects.equals(this.second, second);
    }

    /**
     * Check the {@link #second}.
     *
     * @return true if it is not null
     */
    public boolean hasSecond() {
        return this.second != null;
    }

    /**
     * Check if contains the given third.
     *
     * @param third the third
     * @return true if it does
     */
    public boolean containsThird(T third) {
        return Objects.equals(this.third, third);
    }

    /**
     * Check the {@link #third}.
     *
     * @return true if it is not null
     */
    public boolean hasThird() {
        return this.third != null;
    }

    /**
     * Converts the current triple to a new one using the given function.
     * Executed only if {@link #isPresent()}.
     *
     * @param function the function
     * @return the new tuple
     */
    @SuppressWarnings("unchecked")
    public <A, B, C> Triple<A, B, C> map(@NotNull TriFunctionException<F, S, T, Triple<A, B, C>, Exception> function) {
        if (isPresent())
            try {
                return function.apply(this.first, this.second, this.third);
            } catch (Exception e) {
                ExceptionUtils.throwException(e);
            }
        return (Triple<A, B, C>) empty();
    }

}

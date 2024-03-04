package it.fulminazzo.fulmicollection.structures;

import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * The type Triple.
 *
 * @param <F> the type parameter
 * @param <S> the type parameter
 * @param <T> the type parameter
 */
@Getter
@Setter
public class Triple<F, S, T> implements Serializable {
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
     * Check if is empty.
     *
     * @return true if {@link #first}, {@link #second} and {@link #third} are null
     */
    public boolean isEmpty() {
        return this.first == null && this.second == null && this.third == null;
    }

    /**
     * Check if contains the given first.
     *
     * @param first the first
     * @return true if it does
     */
    public boolean containsFirst(F first) {
        return this.first != null && Objects.equals(this.first, first);
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
        return this.second != null && Objects.equals(this.second, second);
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
        return this.third != null && Objects.equals(this.third, third);
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
     * Copy the current triple into a new one.
     *
     * @return the copy
     */
    public Triple<F, S, T> copy() {
        return new Triple<>(this.first, this.second, this.third);
    }

    /**
     * If {@link #isEmpty()} is false, the given function is executed.
     *
     * @param function the function
     * @return this triple
     */
    public Triple<F, S, T> ifPresent(TriConsumer<F, S, T> function) {
        if (!isEmpty()) function.accept(this.first, this.second, this.third);
        return this;
    }

    /**
     * If {@link #isEmpty()} is true, the given function is executed.
     *
     * @param function the function
     * @return this triple
     */
    public Triple<F, S, T> orElse(Runnable function) {
        if (isEmpty()) function.run();
        return this;
    }

    /**
     * Compare this triple with the given one.
     *
     * @param triple the triple
     * @return true if they are equal
     */
    public boolean equals(Triple<?, ?, ?> triple) {
        return triple != null &&
                Objects.equals(this.first, triple.first) &&
                Objects.equals(this.second, triple.second) &&
                Objects.equals(this.third, triple.third);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (o instanceof Triple) return equals((Triple<?, ?, ?>) o);
        try {if (containsFirst((F) o)) return true;}
        catch (ClassCastException ignored) {}
        try {if (containsSecond((S) o)) return true;}
        catch (ClassCastException ignored) {}
        try {if (containsThird((T) o)) return true;}
        catch (ClassCastException ignored) {}
        return super.equals(o);
    }

    @Override
    public String toString() {
        return String.format("%s{first: %s; second: %s; third: %s}", getClass().getSimpleName(),
                this.first, this.second, this.third);
    }
}

package it.fulminazzo.fulmicollection.structures.tuples;

import org.jetbrains.annotations.NotNull;

/**
 * An implementation of {@link Singlet} that allows null objects to be passed as values.
 *
 * @param <T> the type parameter
 */
public class NullableSinglet<T> extends Singlet<T> {
    private boolean present;

    /**
     * Instantiates a new Nullable singlet.
     */
    public NullableSinglet() {
        super();
    }

    /**
     * Instantiates a new Nullable singlet.
     *
     * @param value the value
     */
    public NullableSinglet(T value) {
        super(value);
    }

    @Override
    public void setValue(T value) {
        this.present = true;
        super.setValue(value);
    }

    /**
     * Unsets the current value.
     */
    public void unsetValue() {
        setValue(null);
        this.present = false;
    }

    @Override
    public boolean hasValue() {
        return this.present;
    }

    @Override
    public boolean isPresent() {
        return this.present;
    }

    /**
     * Converts the current singlet to a non-nullable singlet
     *
     * @return the singlet
     */
    public Singlet<T> toNonNullable() {
        return new Singlet<>(getValue());
    }

    @Override
    public @NotNull String toString() {
        return super.toString().replaceAll("(, )?present: (true|false)(, )", "");
    }
}

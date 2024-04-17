package it.fulminazzo.fulmicollection.structures.tuples;

import it.fulminazzo.fulmicollection.interfaces.functions.FunctionException;
import it.fulminazzo.fulmicollection.utils.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * An implementation of {@link Singlet} that allows null objects to be passed as values.
 *
 * @param <T> the type parameter
 */
@SuppressWarnings("unchecked")
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

    @Override
    public boolean isEmpty() {
        return !isPresent();
    }

    @Override
    public <V> NullableSinglet<V> map(@NotNull FunctionException<T, V> function) {
        if (isPresent())
            try {
                return new NullableSinglet<>(function.apply(getValue()));
            } catch (Exception e) {
                ExceptionUtils.throwException(e);
            }
        return (NullableSinglet<V>) empty();
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
    Field @NotNull [] getFields() {
        return Arrays.stream(super.getFields()).filter(f -> {
            try {
                return !f.equals(NullableSinglet.class.getDeclaredField("present"));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }).toArray(Field[]::new);
    }

    @Override
    public @NotNull String toString() {
        return super.toString().replaceAll("(, )?present: (true|false)(, )", "");
    }
}

package it.fulminazzo.fulmicollection.structures.tuples;

import it.fulminazzo.fulmicollection.objects.FieldEquable;
import it.fulminazzo.fulmicollection.objects.Refl;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A general class to identify various tuples implementations.
 *
 * @param <T> the type of this tuple
 */
@SuppressWarnings("unchecked")
abstract class AbstractTuple<T extends AbstractTuple<T, C>, C> extends FieldEquable implements Serializable {

    /**
     * Checks if is present.
     *
     * @return true if every value is present
     */
    public boolean isPresent() {
        return Arrays.stream(getFieldObjects()).noneMatch(Objects::isNull);
    }

    /**
     * Checks if is empty.
     *
     * @return true if no value is present
     */
    public boolean isEmpty() {
        return Arrays.stream(getFieldObjects()).allMatch(Objects::isNull);
    }

    /**
     * Copies the current tuple to a new one.
     *
     * @return the copy
     */
    public T copy() {
        return (T) new Refl<>(getClass(), getFieldObjects()).getObject();
    }

    /**
     * If {@link #isPresent()} is true, the given function is executed.
     *
     * @param function the function
     * @return this tuple
     */
    public T ifPresent(C function) {
        if (isPresent()) new Refl<>(function).invokeMethod("accept", getFieldObjects());
        return (T) this;
    }

    /**
     * If {@link #isEmpty()} is true, the given function is executed.
     *
     * @param function the function
     * @return this tuple
     */
    public T orElse(final @NotNull Runnable function) {
        if (isEmpty()) function.run();
        return (T) this;
    }

    private Object[] getFieldObjects() {
        List<Object> fields = new LinkedList<>();
        Refl<?> refl = new Refl<>(this);
        for (Field field : refl.getNonStaticFields())
            fields.add(refl.getFieldObject(field));
        return fields.toArray(new Object[0]);
    }

    @Override
    public @NotNull String toString() {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName() + "{");
        Refl<?> refl = new Refl<>(this);
        for (Field field : refl.getNonStaticFields()) {
            builder.append(field.getName()).append(": ");
            Object object = refl.getFieldObject(field);
            builder.append(object == null ? "null" : object.toString());
        }
        return builder.append("}").toString();
    }

}

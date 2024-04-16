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
 * @param <C> the type of the consumer used in {@link #ifPresent(Object)}. In case of a single value, it would be <code>Consumer&lt;V&gt;</code>
 * @param <P> the type of the predicate used in {@link #filter(Object)}. In case of a single value, it would be <code>Function&lt;V, Boolean&gt;</code>
 */
@SuppressWarnings("unchecked")
abstract class AbstractTuple<T extends AbstractTuple<T, C, P>, C, P> extends FieldEquable implements Serializable {

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
    public @NotNull T copy() {
        return (T) new Refl<>(getClass(), getFieldObjects()).getObject();
    }

    /**
     * If {@link #isPresent()} is true, the given function is executed.
     *
     * @param function the function
     * @return this tuple
     */
    public @NotNull T ifPresent(final @NotNull C function) {
        if (isPresent()) new Refl<>(function).invokeMethod("accept", getFieldObjects());
        return (T) this;
    }

    /**
     * If {@link #isEmpty()} is true, the given function is executed.
     *
     * @param function the function
     * @return this tuple
     */
    public @NotNull T orElse(final @NotNull Runnable function) {
        if (isEmpty()) function.run();
        return (T) this;
    }

    /**
     * Combines {@link #ifPresent(Object)} and {@link #orElse(Runnable)}.
     *
     * @param ifPresent the function for {@link #ifPresent(Object)}
     * @param orElse    the function for {@link #orElse(Runnable)}
     * @return this tuple
     */
    public @NotNull T ifPresentOrElse(final @NotNull C ifPresent, final @NotNull Runnable orElse) {
        return ifPresent(ifPresent).orElse(orElse);
    }

    /**
     * Filters the current tuple using the given function.
     * If it returns {@link Boolean#TRUE}, then it is returned this.
     * Otherwise, {@link #empty()} is returned.
     *
     * @param function the function
     * @return the result
     */
    public @NotNull T filter(final @NotNull P function) {
        if (isPresent() && Boolean.TRUE.equals(new Refl<>(function).invokeMethod("apply", getFieldObjects())))
            return (T) this;
        return empty();
    }

    @NotNull T empty() {
        return (T) new Refl<>(getClass(), new Object[0]).getObject();
    }

    private Object @NotNull [] getFieldObjects() {
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

package it.fulminazzo.fulmicollection.structures.tuples;

import it.fulminazzo.fulmicollection.interfaces.functions.FunctionException;
import it.fulminazzo.fulmicollection.objects.FieldEquable;
import it.fulminazzo.fulmicollection.utils.ExceptionUtils;
import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;

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
        return (T) doPrivileged(() -> getClass().getDeclaredConstructor(getFieldTypes()), c -> {
            c.setAccessible(true);
            return c.newInstance(getFieldObjects());
        });
    }

    /**
     * Empty t.
     *
     * @return the t
     */
    @NotNull T empty() {
        return (T) doPrivileged(() -> getClass().getDeclaredConstructor(), c -> {
            c.setAccessible(true);
            return c.newInstance();
        });
    }

    /**
     * If {@link #isPresent()} is true, the given function is executed.
     *
     * @param function the function
     * @return this tuple
     */
    public @NotNull T ifPresent(final @NotNull C function) {
        if (isPresent())
            doPrivileged(() -> function.getClass().getDeclaredMethod("accept", getFieldTypes()), m -> {
                m.setAccessible(true);
                return m.invoke(function, getFieldObjects());
            });
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
        if (isPresent()) {
            Object test = doPrivileged(() -> function.getClass().getDeclaredMethod("apply", getFieldTypes()), m -> {
                m.setAccessible(true);
                return m.invoke(function, getFieldObjects());
            });
            if (Boolean.TRUE.equals(test)) return (T) this;
        }
        return empty();
    }

    /**
     * Gets all the relevant fields for this tuple.
     *
     * @return the values of the fields
     */
    Object @NotNull [] getFieldObjects() {
        return Arrays.stream(getFields())
                .map(f -> AccessController.doPrivileged((PrivilegedAction<?>) () -> {
                    try {
                        f.setAccessible(true);
                        return f.get(this);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })).toArray(Object[]::new);
    }

    /**
     * Gets all the relevant fields for this tuple.
     *
     * @return the types of the fields
     */
    Class<?> @NotNull [] getFieldTypes() {
        return Arrays.stream(getFields()).map(Field::getType).toArray(Class[]::new);
    }

    /**
     * Gets all the relevant fields for this tuple.
     *
     * @return the fields
     */
    Field @NotNull [] getFields() {
        return ReflectionUtils.getFields(this, f -> !Modifier.isStatic(f.getModifiers())).toArray(new Field[0]);
    }

    private <O, M> O doPrivileged(Callable<M> method, FunctionException<M, O> exceptionAction) {
        try {
            M m = method.call();
            return AccessController.doPrivileged((PrivilegedExceptionAction<O>) () -> exceptionAction.apply(m));
        } catch (Exception e) {
            ExceptionUtils.throwException(e);
            throw new IllegalStateException("Unreachable code");
        }
    }

    @Override
    public @NotNull String toString() {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName() + "{");
        for (Field field : getFields()) {
            builder.append(field.getName()).append(": ");
            Object object = doPrivileged(() -> field, f -> {
                f.setAccessible(true);
                return f.get(this);
            });
            builder.append(object == null ? "null" : object.toString())
                    .append(", ");
        }
        String output = builder.append("}").toString();
        if (output.endsWith(", }"))
            output = output.substring(0, output.length() - 3) + "}";
        return output;
    }

}

package it.fulminazzo.fulmicollection.objects;


import it.fulminazzo.fulmicollection.utils.ExceptionUtils;
import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A class that acts as a wrapper for an object.
 * It provides various utilities to work with reflections.
 *
 * @param <T> the object
 */
@SuppressWarnings("unchecked")
@Getter
public class Refl<T> {
    private final T object;

    /**
     * Instantiates a new Refl.
     *
     * @param className  the class name
     * @param parameters the parameters
     */
    public Refl(final @NotNull String className, Object @Nullable ... parameters) {
        this(className, ReflectionUtils.objectsToClasses(parameters), parameters);
    }

    /**
     * Instantiates a new Refl.
     *
     * @param className      the class name
     * @param parameterTypes the parameter types
     * @param parameters     the parameters
     */
    public Refl(final @NotNull String className, final Class<?> @Nullable [] parameterTypes, Object @Nullable ... parameters) {
        this(ReflectionUtils.getClass(className), parameterTypes, parameters);
    }

    /**
     * Instantiates a new Refl.
     *
     * @param objectClass the object class
     * @param parameters  the parameters
     */
    public Refl(final @NotNull Class<T> objectClass, Object @Nullable ... parameters) {
        this(objectClass, ReflectionUtils.objectsToClasses(parameters), parameters);
    }

    /**
     * Instantiates a new Refl.
     *
     * @param objectClass    the object class
     * @param parameterTypes the parameter types
     * @param parameters     the parameters
     */
    public Refl(final @NotNull Class<T> objectClass, final Class<?> @Nullable [] parameterTypes, Object @Nullable ... parameters) {
        try {
            Constructor<T> constructor = ReflectionUtils.getConstructor(objectClass, parameterTypes);
            this.object = constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            ExceptionUtils.throwException(e);
            throw new IllegalStateException("Unreachable code");
        }
    }

    /**
     * Instantiates a new Refl.
     *
     * @param object the object
     */
    public Refl(final T object) {
        this.object = object;
    }

    /**
     * Gets field from its type.
     * Uses {@link ReflectionUtils#getClass(String)}.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param fieldType the field type
     * @return the field nameless
     */
    public @NotNull Field getFieldNameless(final @NotNull String fieldType) {
        return getField(() -> ReflectionUtils.getFieldNameless(object, fieldType));
    }

    /**
     * Gets field from its type.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param fieldType the field type
     * @return the field
     */
    public @NotNull Field getField(final @NotNull Class<?> fieldType) {
        return getField(() -> ReflectionUtils.getField(object, fieldType));
    }

    /**
     * Gets field from its name.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param fieldName the field name
     * @return the field
     */
    public @NotNull Field getField(final @NotNull String fieldName) {
        return getField(() -> ReflectionUtils.getField(object, fieldName));
    }

    private @NotNull Field getField(final @NotNull Supplier<Field> fieldSupplier) {
        try {
            return ifObjectIsPresent(o -> fieldSupplier.get());
        } catch (NullPointerException e) {
            throw new IllegalStateException("Could not get field: wrapped object is null");
        }
    }

    /**
     * Gets the field content from its type.
     * Uses {@link ReflectionUtils#getClass(String)}.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>       the type parameter
     * @param fieldType the field type
     * @return the field object nameless
     */
    public <O> @Nullable O getFieldObjectNameless(final @NotNull String fieldType) {
        return getFieldObject(getFieldNameless(fieldType));
    }

    /**
     * Gets the field content from its type.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>       the type parameter
     * @param fieldType the field type
     * @return the field object
     */
    public <O> @Nullable O getFieldObject(final @NotNull Class<?> fieldType) {
        return getFieldObject(getField(fieldType));
    }

    /**
     * Gets the field content from its name.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>  the type parameter
     * @param name the name
     * @return the field object
     */
    public <O> @Nullable O getFieldObject(final @NotNull String name) {
        return getFieldObject(getField(name));
    }

    /**
     * Gets the field content from the given field.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>   the type parameter
     * @param field the field
     * @return the field object
     */
    public <O> @Nullable O getFieldObject(@NotNull Field field) {
        try {
            Field finalField = field;
            field = getField(() -> finalField);
            field.setAccessible(true);
            return (O) field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * If the object is not null, the given function is executed.
     * Otherwise, a {@link NullPointerException is thrown}.
     *
     * @param <O>      the object to return
     * @param function the function
     * @return the object to return
     */
    public <O> O ifObjectIsPresent(final @NotNull Function<@NotNull T, O> function) {
        if (this.object == null) throw new NullPointerException();
        return function.apply(this.object);
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (o instanceof Refl)
            return Objects.equals(this.object, ((Refl<?>) o).object);
        return super.equals(o);
    }

    @Override
    public @NotNull String toString() {
        return object == null ? "null" : object.toString();
    }
}

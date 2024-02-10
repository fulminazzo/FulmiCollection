package it.fulminazzo.fulmicollection.objects;


import it.fulminazzo.fulmicollection.utils.ExceptionUtils;
import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * A class that acts as a wrapper for an object.
 * It provides various utilities to work with reflections.
 *
 * @param <T> the object
 */
@Getter
public class Refl<T> {
    private final @Nullable T object;

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
    public Refl(final @Nullable T object) {
        this.object = object;
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

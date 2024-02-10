package it.fulminazzo.fulmicollection.objects;


import it.fulminazzo.fulmicollection.utils.ExceptionUtils;
import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
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
     * Gets the field from its type and sets its value to the specified one.
     * Uses {@link ReflectionUtils#getClass(String)}.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>       the type parameter
     * @param fieldType the field type
     * @param value     the value
     * @return the field object nameless
     */
    public <O> Refl<T> setFieldObjectNameless(final @NotNull String fieldType, final @Nullable O value) {
        return setFieldObject(getFieldNameless(fieldType), value);
    }

    /**
     * Gets the field from its type and sets its value to the specified one.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>       the type parameter
     * @param fieldType the field type
     * @param value     the value
     * @return the field object
     */
    public <O> Refl<T> setFieldObject(final @NotNull Class<?> fieldType, final @Nullable O value) {
        return setFieldObject(getField(fieldType), value);
    }

    /**
     * Gets the field from its name and sets its value to the specified one.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>   the type parameter
     * @param name  the name
     * @param value the value
     * @return the field object
     */
    public <O> Refl<T> setFieldObject(final @NotNull String name, final @Nullable O value) {
        return setFieldObject(getField(name), value);
    }

    /**
     * Gets the field from the predicate and sets its value to the specified one.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>       the type parameter
     * @param predicate the predicate
     * @param value     the value
     * @return the field object
     */
    public <O> Refl<T> setFieldObject(final @NotNull Predicate<Field> predicate, final @Nullable O value) {
        return setFieldObject(getField(predicate), value);
    }

    /**
     * Gets the field from the given field and sets its value to the specified one.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>   the type parameter
     * @param field the field
     * @param value the value
     * @return the field object
     */
    public <O> Refl<T> setFieldObject(@NotNull Field field, final @Nullable O value) {
        try {
            Field finalField = field;
            field = getField(() -> finalField);
            field.setAccessible(true);
            field.set(this.object, value);
            return this;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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

    /**
     * Gets field from the predicate.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param predicate the predicate
     * @return the field
     */
    public @NotNull Field getField(final @NotNull Predicate<Field> predicate) {
        return getField(() -> ReflectionUtils.getField(object, predicate));
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
     * Gets the field content from the predicate.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>       the type parameter
     * @param predicate the predicate
     * @return the field object
     */
    public <O> @Nullable O getFieldObject(final @NotNull Predicate<Field> predicate) {
        return getFieldObject(getField(predicate));
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
     * Gets a {@link Refl} wrapping the content of a field.
     * The contents may be null, but the {@link Refl} will never be.
     * Uses {@link ReflectionUtils#getClass(String)}.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>       the type parameter
     * @param fieldType the field type
     * @return the field refl nameless
     */
    public <O> @NotNull Refl<O> getFieldReflNameless(final @NotNull String fieldType) {
        return new Refl<>(getFieldObjectNameless(fieldType));
    }

    /**
     * Gets a {@link Refl} wrapping the content of a field.
     * The contents may be null, but the {@link Refl} will never be.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>       the type parameter
     * @param fieldType the field type
     * @return the field refl
     */
    public <O> @NotNull Refl<O> getFieldRefl(final @NotNull Class<?> fieldType) {
        return new Refl<>(getFieldObject(fieldType));
    }

    /**
     * Gets a {@link Refl} wrapping the content of a field.
     * The contents may be null, but the {@link Refl} will never be.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>  the type parameter
     * @param name the name
     * @return the field refl
     */
    public <O> @NotNull Refl<O> getFieldRefl(final @NotNull String name) {
        return new Refl<>(getFieldObject(name));
    }

    /**
     * Gets a {@link Refl} wrapping the content of a field.
     * The contents may be null, but the {@link Refl} will never be.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>       the type parameter
     * @param predicate the predicate
     * @return the field refl
     */
    public <O> @NotNull Refl<O> getFieldRefl(final @NotNull Predicate<Field> predicate) {
        return new Refl<>(getFieldObject(predicate));
    }

    /**
     * Gets a {@link Refl} wrapping the content of a field.
     * The contents may be null, but the {@link Refl} will never be.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>   the type parameter
     * @param field the field
     * @return the field refl
     */
    public <O> @NotNull Refl<O> getFieldRefl(final @NotNull Field field) {
        return new Refl<>(getFieldObject(field));
    }

    /**
     * Gets fields.
     *
     * @return the fields
     */
    public @NotNull List<Field> getFields() {
        return getFields(f -> true);
    }

    /**
     * Gets static fields.
     *
     * @return the static fields
     */
    public @NotNull List<Field> getStaticFields() {
        return getFields(f -> Modifier.isStatic(f.getModifiers()));
    }

    /**
     * Gets non-static fields.
     *
     * @return the non-static fields
     */
    public @NotNull List<Field> getNonStaticFields() {
        return getFields(f -> !Modifier.isStatic(f.getModifiers()));
    }

    /**
     * Gets fields.
     *
     * @param predicate the predicate
     * @return the fields
     */
    public @NotNull List<Field> getFields(final @NotNull Predicate<Field> predicate) {
        try {
            return ifObjectIsPresent(o -> ReflectionUtils.getFields(this.object, predicate));
        } catch (NullPointerException e) {
            throw new IllegalStateException("Could not get fields: wrapped object is null");
        }
    }

    /**
     * Gets method from its parameters.
     *
     * @param parameters the parameters
     * @return the method
     */
    public @Nullable Method getMethod(final Object @Nullable ... parameters) {
        return getMethod(null, null, ReflectionUtils.objectsToClasses(parameters));
    }

    /**
     * Gets method from its parameter types.
     *
     * @param paramTypes the param types
     * @return the method
     */
    public @Nullable Method getMethod(final Class<?> @Nullable ... paramTypes) {
        return getMethod(null, null, paramTypes);
    }

    /**
     * Gets method from its name and parameters.
     *
     * @param name       the name
     * @param parameters the parameters
     * @return the method
     */
    public @Nullable Method getMethod(final @Nullable String name, final Object @Nullable ... parameters) {
        return getMethod(name, ReflectionUtils.objectsToClasses(parameters));
    }

    /**
     * Gets method from its name and parameter types.
     *
     * @param name       the name
     * @param paramTypes the param types
     * @return the method
     */
    public @Nullable Method getMethod(final @Nullable String name, final Class<?> @Nullable ... paramTypes) {
        return getMethod(null, name, paramTypes);
    }

    /**
     * Gets method from its return type and parameters.
     *
     * @param returnType the return type
     * @param parameters the parameters
     * @return the method
     */
    public @Nullable Method getMethod(final @Nullable Class<?> returnType, final Object @Nullable ... parameters) {
        return getMethod(returnType, ReflectionUtils.objectsToClasses(parameters));
    }

    /**
     * Gets method from its return type and parameter types.
     *
     * @param returnType the return type
     * @param paramTypes the param types
     * @return the method
     */
    public @Nullable Method getMethod(final @Nullable Class<?> returnType, final Class<?> @Nullable ... paramTypes) {
        return getMethod(returnType, null, paramTypes);
    }

    /**
     * Gets method from its name, return type and parameters.
     *
     * @param returnType the return type
     * @param name       the name
     * @param parameters the parameters
     * @return the method
     */
    public @Nullable Method getMethod(final @Nullable Class<?> returnType, final @Nullable String name,
                                      final Object @Nullable ... parameters) {
        return getMethod(returnType, name, ReflectionUtils.objectsToClasses(parameters));
    }

    /**
     * Gets method from its name, return type and parameter types.
     *
     * @param returnType the return type
     * @param name       the name
     * @param paramTypes the param types
     * @return the method
     */
    public @Nullable Method getMethod(final @Nullable Class<?> returnType, final @Nullable String name,
                                      final Class<?> @Nullable ... paramTypes) {
        try {
            return ifObjectIsPresent(o -> ReflectionUtils.getMethod(this.object.getClass(), returnType, name, paramTypes));
        } catch (NullPointerException e) {
            throw new IllegalStateException(String.format("Could not get method %s %s(%s): wrapped object is null",
                    returnType, name, ReflectionUtils.classesToString(paramTypes)));
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

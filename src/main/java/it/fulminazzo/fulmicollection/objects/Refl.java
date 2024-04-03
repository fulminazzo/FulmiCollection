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
 * @param <T>  the object
 */
@SuppressWarnings("unchecked")
@Getter
public class Refl<T> {
    private final T object;

    /**
     * Instantiates a new Refl.
     *
     * @param className the class name
     * @param parameters the parameters
     */
    public Refl(final @NotNull String className, Object @Nullable ... parameters) {
        this(className, ReflectionUtils.objectsToClasses(parameters), parameters);
    }

    /**
     * Instantiates a new Refl.
     *
     * @param className the class name
     * @param parameterTypes the parameter types
     * @param parameters the parameters
     */
    public Refl(final @NotNull String className, final Class<?> @Nullable [] parameterTypes, Object @Nullable ... parameters) {
        this(ReflectionUtils.getClass(className), parameterTypes, parameters);
    }

    /**
     * Instantiates a new Refl.
     *
     * @param objectClass the object class
     * @param parameters the parameters
     */
    public Refl(final @NotNull Class<T> objectClass, Object @Nullable ... parameters) {
        this(objectClass, ReflectionUtils.objectsToClasses(parameters), parameters);
    }

    /**
     * Instantiates a new Refl.
     *
     * @param objectClass the object class
     * @param parameterTypes the parameter types
     * @param parameters the parameters
     */
    public Refl(final @NotNull Class<T> objectClass, final Class<?> @Nullable [] parameterTypes, Object @Nullable ... parameters) {
        try {
            Constructor<T> constructor = ReflectionUtils.getConstructor(objectClass, parameterTypes);
            this.object = ReflectionUtils.setAccessible(constructor).newInstance(parameters);
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
     * Creates an array with the class of the given object.
     * If the object is a class, it will be used to create the array.
     *
     * @param <V>       the type parameter
     * @param elements the elements
     * @return the array
     */
    public <V> @NotNull V[] toArray(final V @Nullable... elements) {
        V[] v = toArray(elements == null ? 0 : elements.length);
        if (elements != null)
            System.arraycopy(elements, 0, v, 0, elements.length);
        return v;
    }

    /**
     * Creates an array with the class of the given object.
     * If the object is a class, it will be used to create the array.
     *
     * @param <V>   the type parameter
     * @param size the size
     * @return the array
     */
    public <V> @NotNull V[] toArray(final int size) {
        return ifObjectIsPresent(o -> {
            final Class<V> arrayClass;
            if (o instanceof Class) arrayClass = (Class<V>) o;
            else arrayClass = (Class<V>) getObjectClass();
            return (V[]) Array.newInstance(arrayClass, size);
        });
    }

    /**
     * Gets the field from its type and sets its value to the specified one.
     * Uses {@link ReflectionUtils#getClass(String)}.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>        the type parameter
     * @param fieldType the field type
     * @param value the value
     * @return the field object nameless
     */
    public <O> Refl<T> setFieldObjectNameless(final @NotNull String fieldType, final @Nullable O value) {
        return setFieldObject(getFieldNameless(fieldType), value);
    }

    /**
     * Gets the field from its type and sets its value to the specified one.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>        the type parameter
     * @param fieldType the field type
     * @param value the value
     * @return the field object
     */
    public <O> Refl<T> setFieldObject(final @NotNull Class<?> fieldType, final @Nullable O value) {
        return setFieldObject(getField(fieldType), value);
    }

    /**
     * Gets the field from its name and sets its value to the specified one.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>    the type parameter
     * @param name the name
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
     * @param <O>        the type parameter
     * @param predicate the predicate
     * @param value the value
     * @return the field object
     */
    public <O> Refl<T> setFieldObject(final @NotNull Predicate<Field> predicate, final @Nullable O value) {
        return setFieldObject(getField(predicate), value);
    }

    /**
     * Gets the field from the given field and sets its value to the specified one.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>    the type parameter
     * @param field the field
     * @param value the value
     * @return the field object
     */
    public <O> @NotNull Refl<T> setFieldObject(@NotNull Field field, final @Nullable O value) {
        try {
            Field finalField = field;
            field = getField(() -> finalField);
            ReflectionUtils.setAccessible(field).set(this.object, value);
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
        return getField(() -> ReflectionUtils.getFieldNameless(getObjectClass(), fieldType));
    }

    /**
     * Gets field from its type.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param fieldType the field type
     * @return the field
     */
    public @NotNull Field getField(final @NotNull Class<?> fieldType) {
        return getField(() -> ReflectionUtils.getField(getObjectClass(), fieldType));
    }

    /**
     * Gets field from its name.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param fieldName the field name
     * @return the field
     */
    public @NotNull Field getField(final @NotNull String fieldName) {
        return getField(() -> ReflectionUtils.getField(getObjectClass(), fieldName));
    }

    /**
     * Gets field from the predicate.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param predicate the predicate
     * @return the field
     */
    public @NotNull Field getField(final @NotNull Predicate<Field> predicate) {
        return getField(() -> ReflectionUtils.getField(getObjectClass(), predicate));
    }

    private @NotNull Field getField(final @NotNull Supplier<Field> fieldSupplier) {
        try {
            return ifObjectIsPresent(o -> fieldSupplier.get());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Could not get field: wrapped object is null");
        }
    }

    /**
     * Gets the field content from its type.
     * Uses {@link ReflectionUtils#getClass(String)}.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>        the type parameter
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
     * @param <O>        the type parameter
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
     * @param <O>   the type parameter
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
     * @param <O>        the type parameter
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
     * @param <O>    the type parameter
     * @param field the field
     * @return the field object
     */
    public <O> @Nullable O getFieldObject(@NotNull Field field) {
        Field finalField = field;
        field = getField(() -> finalField);
        return ReflectionUtils.get(field, this.object);
    }

    /**
     * Gets a {@link Refl} wrapping the content of a field.
     * The contents may be null, but the {@link Refl} will never be.
     * Uses {@link ReflectionUtils#getClass(String)}.
     * Throws {@link IllegalStateException} if {@link #object} is null.
     *
     * @param <O>        the type parameter
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
     * @param <O>        the type parameter
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
     * @param <O>   the type parameter
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
     * @param <O>        the type parameter
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
     * @param <O>    the type parameter
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
            return ifObjectIsPresent(o -> ReflectionUtils.getFields(getObjectClass(), predicate));
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Could not get fields: wrapped object is null");
        }
    }

    /**
     * Gets method from its parameters.
     *
     * @param parameters the parameters
     * @return the method
     */
    public @NotNull Method getMethod(final Object @Nullable ... parameters) {
        return getMethod(null, null, ReflectionUtils.objectsToClasses(parameters));
    }

    /**
     * Gets method from its parameter types.
     *
     * @param paramTypes the parameter types
     * @return the method
     */
    public @NotNull Method getMethod(final Class<?> @Nullable ... paramTypes) {
        return getMethod(null, null, paramTypes);
    }

    /**
     * Gets method from its name and parameters.
     *
     * @param name the name
     * @param parameters the parameters
     * @return the method
     */
    public @NotNull Method getMethod(final @Nullable String name, final Object @Nullable ... parameters) {
        return getMethod(name, ReflectionUtils.objectsToClasses(parameters));
    }

    /**
     * Gets method from its name and parameter types.
     *
     * @param name the name
     * @param paramTypes the parameter types
     * @return the method
     */
    public @NotNull Method getMethod(final @Nullable String name, final Class<?> @Nullable ... paramTypes) {
        return getMethod(null, name, paramTypes);
    }

    /**
     * Gets method from its return type and parameters.
     *
     * @param returnType the return type
     * @param parameters the parameters
     * @return the method
     */
    public @NotNull Method getMethod(final @Nullable Class<?> returnType, final Object @Nullable ... parameters) {
        return getMethod(returnType, ReflectionUtils.objectsToClasses(parameters));
    }

    /**
     * Gets method from its return type and parameter types.
     *
     * @param returnType the return type
     * @param paramTypes the parameter types
     * @return the method
     */
    public @NotNull Method getMethod(final @Nullable Class<?> returnType, final Class<?> @Nullable ... paramTypes) {
        return getMethod(returnType, null, paramTypes);
    }

    /**
     * Gets method from its name, return type and parameters.
     *
     * @param returnType the return type
     * @param name the name
     * @param parameters the parameters
     * @return the method
     */
    public @NotNull Method getMethod(final @Nullable Class<?> returnType, final @Nullable String name,
                                      final Object @Nullable ... parameters) {
        return getMethod(returnType, name, ReflectionUtils.objectsToClasses(parameters));
    }

    /**
     * Gets method from its name, return type and parameter types.
     *
     * @param returnType the return type
     * @param name the name
     * @param paramTypes the parameter types
     * @return the method
     */
    public @NotNull Method getMethod(final @Nullable Class<?> returnType, final @Nullable String name,
                                      final Class<?> @Nullable ... paramTypes) {
        try {
            return ifObjectIsPresent(o -> ReflectionUtils.getMethod(getObjectClass(), returnType, name, paramTypes));
        } catch (IllegalStateException e) {
            throw new IllegalStateException(String.format("Could not get method %s %s(%s): wrapped object is null",
                    returnType, name, ReflectionUtils.classesToString(paramTypes)));
        }
    }

    /**
     * Invoke the best matching method and return its result.
     *
     * @param <O>         the type parameter
     * @param parameters the parameters
     * @return the result
     */
    public <O> @Nullable O invokeMethod(final Object @Nullable ... parameters) {
        return invokeMethod(null, null, ReflectionUtils.objectsToClasses(parameters), parameters);
    }

    /**
     * Invoke the best matching method and return its result.
     *
     * @param <O>         the type parameter
     * @param paramTypes the parameter types
     * @param parameters the parameters
     * @return the result
     */
    public <O> @Nullable O invokeMethod(final Class<?> @Nullable [] paramTypes, final Object @Nullable ... parameters) {
        return invokeMethod(null, null, paramTypes, parameters);
    }

    /**
     * Invoke the best matching method and return its result.
     *
     * @param <O>         the type parameter
     * @param name the name
     * @param parameters the parameters
     * @return the result
     */
    public <O> @Nullable O invokeMethod(final @Nullable String name, final Object @Nullable ... parameters) {
        return invokeMethod(name, ReflectionUtils.objectsToClasses(parameters), parameters);
    }

    /**
     * Invoke the best matching method and return its result.
     *
     * @param <O>         the type parameter
     * @param name the name
     * @param paramTypes the parameter types
     * @param parameters the parameters
     * @return the result
     */
    public <O> @Nullable O invokeMethod(final @Nullable String name, final Class<?> @Nullable [] paramTypes,
                                        final Object @Nullable ... parameters) {
        return invokeMethod(null, name, paramTypes, parameters);
    }

    /**
     * Invoke the best matching method and return its result.
     *
     * @param <O>         the type parameter
     * @param returnType the return type
     * @param parameters the parameters
     * @return the result
     */
    public <O> @Nullable O invokeMethod(final @Nullable Class<?> returnType, final Object @Nullable ... parameters) {
        return invokeMethod(returnType, ReflectionUtils.objectsToClasses(parameters), parameters);
    }

    /**
     * Invoke the best matching method and return its result.
     *
     * @param <O>         the type parameter
     * @param returnType the return type
     * @param paramTypes the parameter types
     * @param parameters the parameters
     * @return the result
     */
    public <O> @Nullable O invokeMethod(final @Nullable Class<?> returnType, final Class<?> @Nullable [] paramTypes,
                                        final Object @Nullable ... parameters) {
        return invokeMethod(returnType, null, paramTypes, parameters);
    }

    /**
     * Invoke the best matching method and return its result.
     *
     * @param <O>         the type parameter
     * @param returnType the return type
     * @param name the name
     * @param parameters the parameters
     * @return the result
     */
    public <O> @Nullable O invokeMethod(final @Nullable Class<?> returnType, final @Nullable String name,
                                        final Object @Nullable ... parameters) {
        return invokeMethod(returnType, name, ReflectionUtils.objectsToClasses(parameters), parameters);
    }

    /**
     * Invoke the best matching method and return its result.
     *
     * @param <O>         the type parameter
     * @param returnType the return type
     * @param name the name
     * @param paramTypes the parameter types
     * @param parameters the parameters
     * @return the result
     */
    public <O> @Nullable O invokeMethod(final @Nullable Class<?> returnType, final @Nullable String name,
                                        final Class<?> @Nullable [] paramTypes, final Object @Nullable ... parameters) {
        try {
            final Method method = getMethod(returnType, name, paramTypes);
            return (O) ReflectionUtils.setAccessible(method).invoke(this.object, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            ExceptionUtils.throwException(e);
            throw new IllegalStateException("Unreachable code");
        }
    }

    /**
     * Call the best matching method without returning its result.
     *
     * @param parameters the parameters
     * @return this refl
     */
    public Refl<T> callMethod(final Object @Nullable ... parameters) {
        return callMethod(null, null, ReflectionUtils.objectsToClasses(parameters), parameters);
    }

    /**
     * Call the best matching method without returning its result.
     *
     * @param paramTypes the parameter types
     * @param parameters the parameters
     * @return this refl
     */
    public Refl<T> callMethod(final Class<?> @Nullable [] paramTypes, final Object @Nullable ... parameters) {
        return callMethod(null, null, paramTypes, parameters);
    }

    /**
     * Call the best matching method without returning its result.
     *
     * @param name the name
     * @param parameters the parameters
     * @return this refl
     */
    public Refl<T> callMethod(final @Nullable String name, final Object @Nullable ... parameters) {
        return callMethod(name, ReflectionUtils.objectsToClasses(parameters), parameters);
    }

    /**
     * Call the best matching method without returning its result.
     *
     * @param name the name
     * @param paramTypes the parameter types
     * @param parameters the parameters
     * @return this refl
     */
    public Refl<T> callMethod(final @Nullable String name, final Class<?> @Nullable [] paramTypes,
                                        final Object @Nullable ... parameters) {
        return callMethod(null, name, paramTypes, parameters);
    }

    /**
     * Call the best matching method without returning its result.
     *
     * @param returnType the return type
     * @param parameters the parameters
     * @return this refl
     */
    public Refl<T> callMethod(final @Nullable Class<?> returnType, final Object @Nullable ... parameters) {
        return callMethod(returnType, ReflectionUtils.objectsToClasses(parameters), parameters);
    }

    /**
     * Call the best matching method without returning its result.
     *
     * @param returnType the return type
     * @param paramTypes the parameter types
     * @param parameters the parameters
     * @return this refl
     */
    public Refl<T> callMethod(final @Nullable Class<?> returnType, final Class<?> @Nullable [] paramTypes,
                                        final Object @Nullable ... parameters) {
        return callMethod(returnType, null, paramTypes, parameters);
    }

    /**
     * Call the best matching method without returning its result.
     *
     * @param returnType the return type
     * @param name the name
     * @param parameters the parameters
     * @return this refl
     */
    public Refl<T> callMethod(final @Nullable Class<?> returnType, final @Nullable String name,
                                        final Object @Nullable ... parameters) {
        return callMethod(returnType, name, ReflectionUtils.objectsToClasses(parameters), parameters);
    }

    /**
     * Call the best matching method without returning its result.
     *
     * @param returnType the return type
     * @param name the name
     * @param paramTypes the parameter types
     * @param parameters the parameters
     * @return this refl
     */
    public Refl<T> callMethod(final @Nullable Class<?> returnType, final @Nullable String name,
                                        final Class<?> @Nullable [] paramTypes, final Object @Nullable ... parameters) {
        invokeMethod(returnType, name, paramTypes, parameters);
        return this;
    }

    /**
     * Invoke the best matching method and return its result wrapped in a {@link Refl} object.
     *
     * @param <O>         the type parameter
     * @param parameters the parameters
     * @return the result
     */
    public <O> @Nullable Refl<O> invokeMethodRefl(final Object @Nullable ... parameters) {
        return invokeMethodRefl(null, null, ReflectionUtils.objectsToClasses(parameters), parameters);
    }

    /**
     * Invoke the best matching method and return its result wrapped in a {@link Refl} object.
     *
     * @param <O>         the type parameter
     * @param paramTypes the parameter types
     * @param parameters the parameters
     * @return the result
     */
    public <O> @Nullable Refl<O> invokeMethodRefl(final Class<?> @Nullable [] paramTypes, final Object @Nullable ... parameters) {
        return invokeMethodRefl(null, null, paramTypes, parameters);
    }

    /**
     * Invoke the best matching method and return its result wrapped in a {@link Refl} object.
     *
     * @param <O>         the type parameter
     * @param name the name
     * @param parameters the parameters
     * @return the result
     */
    public <O> @Nullable Refl<O> invokeMethodRefl(final @Nullable String name, final Object @Nullable ... parameters) {
        return invokeMethodRefl(name, ReflectionUtils.objectsToClasses(parameters), parameters);
    }

    /**
     * Invoke the best matching method and return its result wrapped in a {@link Refl} object.
     *
     * @param <O>         the type parameter
     * @param name the name
     * @param paramTypes the parameter types
     * @param parameters the parameters
     * @return the result
     */
    public <O> @Nullable Refl<O> invokeMethodRefl(final @Nullable String name, final Class<?> @Nullable [] paramTypes,
                                        final Object @Nullable ... parameters) {
        return invokeMethodRefl(null, name, paramTypes, parameters);
    }

    /**
     * Invoke the best matching method and return its result wrapped in a {@link Refl} object.
     *
     * @param <O>         the type parameter
     * @param returnType the return type
     * @param parameters the parameters
     * @return the result
     */
    public <O> @Nullable Refl<O> invokeMethodRefl(final @Nullable Class<?> returnType, final Object @Nullable ... parameters) {
        return invokeMethodRefl(returnType, ReflectionUtils.objectsToClasses(parameters), parameters);
    }

    /**
     * Invoke the best matching method and return its result wrapped in a {@link Refl} object.
     *
     * @param <O>         the type parameter
     * @param returnType the return type
     * @param paramTypes the parameter types
     * @param parameters the parameters
     * @return the result
     */
    public <O> @Nullable Refl<O> invokeMethodRefl(final @Nullable Class<?> returnType, final Class<?> @Nullable [] paramTypes,
                                        final Object @Nullable ... parameters) {
        return invokeMethodRefl(returnType, null, paramTypes, parameters);
    }

    /**
     * Invoke the best matching method and return its result wrapped in a {@link Refl} object.
     *
     * @param <O>         the type parameter
     * @param returnType the return type
     * @param name the name
     * @param parameters the parameters
     * @return the result
     */
    public <O> @Nullable Refl<O> invokeMethodRefl(final @Nullable Class<?> returnType, final @Nullable String name,
                                        final Object @Nullable ... parameters) {
        return invokeMethodRefl(returnType, name, ReflectionUtils.objectsToClasses(parameters), parameters);
    }

    /**
     * Invoke the best matching method and return its result wrapped in a {@link Refl} object.
     *
     * @param <O>         the type parameter
     * @param returnType the return type
     * @param name the name
     * @param paramTypes the parameter types
     * @param parameters the parameters
     * @return the result
     */
    public <O> @Nullable Refl<O> invokeMethodRefl(final @Nullable Class<?> returnType, final @Nullable String name,
                                        final Class<?> @Nullable [] paramTypes, final Object @Nullable ... parameters) {
        return new Refl<>(invokeMethod(returnType, name, paramTypes, parameters));
    }

    /**
     * Gets methods.
     *
     * @return the methods
     */
    public @NotNull List<Method> getMethods() {
        return getMethods(f -> true);
    }

    /**
     * Gets static methods.
     *
     * @return the static methods
     */
    public @NotNull List<Method> getStaticMethods() {
        return getMethods(f -> Modifier.isStatic(f.getModifiers()));
    }

    /**
     * Gets non-static methods.
     *
     * @return the non-static methods
     */
    public @NotNull List<Method> getNonStaticMethods() {
        return getMethods(f -> !Modifier.isStatic(f.getModifiers()));
    }

    /**
     * Gets methods.
     *
     * @param predicate the predicate
     * @return the methods
     */
    public @NotNull List<Method> getMethods(final @NotNull Predicate<Method> predicate) {
        try {
            return ifObjectIsPresent(o -> ReflectionUtils.getMethods(getObjectClass(), predicate));
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Could not get methods: wrapped object is null");
        }
    }

    /**
     * Gets object class.
     *
     * @return the object class
     */
    public Class<T> getObjectClass() {
        return (Class<T>) (this.object instanceof Class ? this.object : this.object.getClass());
    }

    /**
     * If the object is not null, the given function is executed.
     * Otherwise, a {@link IllegalStateException is thrown}.
     *
     * @param <O>       the object to return
     * @param function the function
     * @return the object to return
     */
    public <O> O ifObjectIsPresent(final @NotNull Function<@NotNull T, O> function) {
        if (this.object == null) throw new IllegalStateException();
        return function.apply(this.object);
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (o instanceof Refl)
            return Objects.equals(this.object, ((Refl<?>) o).object);
        return super.equals(o);
    }

    /**
     * Prints the current object fields.
     *
     * @return the fields
     */
    public @NotNull String print() {
        return ifObjectIsPresent(o -> Printable.printObject(o, ""));
    }

    /**
     * Recursively prints the current object fields and fields of non-primitive (or non-wrappers) fields.
     *
     * @return the string
     */
    public @NotNull String printFields() {
        return printFields(false);
    }

    /**
     * Recursively prints the current object fields and fields of non-primitive (or non-wrappers) fields.
     *
     * @param simpleNames if true, only the name of the classes will be displayed (not the path)
     * @return the string
     */
    public @NotNull String printFields(boolean simpleNames) {
        return printFields(simpleNames, false);
    }

    /**
     * Recursively prints the current object fields and fields of non-primitive (or non-wrappers) fields.
     *
     * @param simpleNames if true, only the name of the classes will be displayed (not the path)
     * @param printStatic if true, print the static fields
     * @return the string
     */
    public @NotNull String printFields(boolean simpleNames, boolean printStatic) {
        return printFields(simpleNames, printStatic, false);
    }

    /**
     * Recursively prints the current object fields and fields of non-primitive (or non-wrappers) fields.
     *
     * @param simpleNames if true, only the name of the classes will be displayed (not the path)
     * @param printStatic if true, print the static fields
     * @param recursive if true, converts the unknown objects to a {@link Refl} and invoke this function.
     * @return the string
     */
    public @NotNull String printFields(boolean simpleNames, boolean printStatic, boolean recursive) {
        return ifObjectIsPresent(o -> {
            final String SEPARATOR = "  ";
            final Function<Class<?>, String> className = c -> simpleNames ? c.getSimpleName() : c.getCanonicalName();
            final StringBuilder output = new StringBuilder(className.apply(getObjectClass())).append(" {");
            for (Class<?> c = getObjectClass(); c != null && !c.equals(Object.class); c = c.getSuperclass())
                for (final Field f : c.getDeclaredFields()) {
                    // Remove fields in inner classes.
                    if (f.getName().equalsIgnoreCase("this$1")) continue;
                    // Remove fields used by code coverage from Intellij IDEA.
                    if (f.getName().equals("__$hits$__")) continue;
                    if (!printStatic && Modifier.isStatic(f.getModifiers())) continue;
                    try {
                        final Object v = ReflectionUtils.get(f, o);
                        if (ReflectionUtils.equalsClass(o, v)) continue;
                        final String vToString;
                        output.append("\n").append(SEPARATOR);
                        output.append("(").append(className.apply(c)).append(") ");
                        if (Modifier.isStatic(f.getModifiers())) output.append("static ");
                        output.append(className.apply(f.getType()));
                        output.append(" ").append(f.getName());
                        output.append(" = ");
                        if (v == null) vToString = "null";
                        else if (ReflectionUtils.isPrimitiveOrWrapper(v.getClass())) vToString = v.toString();
                        else if (recursive)
                            vToString = new Refl<>(v).printFields(simpleNames, printStatic).replace("\n", "\n" + SEPARATOR);
                        else vToString = v.toString();
                        output.append(vToString);
                    } catch (Exception e) {
                        output.append("UNKNOWN");
                    } finally {
                        output.append(";");
                    }
                }
            return output + "\n}";
        });
    }

    @Override
    public @NotNull String toString() {
        return this.object == null ? "null" : this.object.toString();
    }
}

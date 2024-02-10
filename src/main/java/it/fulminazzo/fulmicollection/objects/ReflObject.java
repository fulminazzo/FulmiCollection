package it.fulminazzo.fulmicollection.objects;

import it.fulminazzo.fulmicollection.utils.ExceptionUtils;
import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import lombok.Getter;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

@Getter
@SuppressWarnings("unchecked")
public class ReflObject<T> {
    protected final Class<T> objectClass;
    protected final T object;

    public ReflObject(String classPath, Object... params) {
        this(ReflectionUtils.getClass(classPath), ReflectionUtils.objectsToClasses(params), params);
    }

    public ReflObject(String classPath, Class<?>[] paramTypes, Object... params) {
        this(ReflectionUtils.getClass(classPath), paramTypes, params);
    }

    public ReflObject(Class<T> objectClass, Object... params) {
        this(objectClass, ReflectionUtils.objectsToClasses(params), params);
    }

    /**
     * Sets field.
     *
     * @param <O>    the type parameter
     * @param name   the name
     * @param object the object
     * @return this refl object
     */
    public <O> @NotNull ReflObject<T> setField(@NotNull String name, @Nullable O object) {
        if (this.object == null) return this;
        Field field = getField(name);
        try {
            field.setAccessible(true);
            field.set(this.object, object);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            ExceptionUtils.throwException(e);
        }
        return this;
    }

    /**
     * Sets field.
     *
     * @param <O>    the type parameter
     * @param type   the type
     * @param object the object
     * @return this refl object
     */
    public <O> @NotNull ReflObject<T> setField(@NotNull Class<?> type, @Nullable O object) {
        if (this.object == null) return this;
        Field field = getFieldNameless(type);
        try {
            field.setAccessible(true);
            field.set(this.object, object);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            ExceptionUtils.throwException(e);
        }
        return this;
    }

    /**
     * Gets field.
     *
     * @param name the name
     * @return the field
     */
    public @NotNull Field getField(@NotNull String name) {
        try {
            Field field = ReflectionUtils.getField(objectClass, name);
            if (field == null) throw new NoSuchFieldException(String.format(FIELD_NOT_FOUND, name, objectClass));
            return field;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets field nameless.
     *
     * @param typeName the type name
     * @return the field
     */
    public @NotNull Field getFieldNameless(@NotNull String typeName) {
        try {
            Field field = ReflectionUtils.getFieldNameless(objectClass, typeName);
            if (field == null) throw new NoSuchFieldException(String.format(FIELD_TYPE_NOT_FOUND, typeName, objectClass));
            return field;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets field nameless.
     *
     * @param type the type
     * @return the field
     */
    public @NotNull Field getFieldNameless(@NotNull Class<?> type) {
        try {
            Field field = ReflectionUtils.getField(objectClass, type);
            if (field == null) throw new NoSuchFieldException(String.format(FIELD_TYPE_NOT_FOUND, type, objectClass));
            return field;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the corresponding object wrapped in a {@link ReflObject} from the specified field.
     *
     * @param <O>  the type parameter
     * @param name the name
     * @return the {@link ReflObject} containing the field object
     */
    public <O> @NotNull ReflObject<O> getFieldReflObject(@NotNull String name) {
        return getFieldReflObject(getField(name));
    }

    /**
     * Gets the corresponding object wrapped in a {@link ReflObject} from the specified field.
     *
     * @param <O>  the type parameter
     * @param type the type
     * @return the {@link ReflObject} containing the field object
     */
    public <O> @NotNull ReflObject<O> getFieldReflObjectNameless(@NotNull Class<?> type) {
        return getFieldReflObject(getFieldNameless(type));
    }

    /**
     * Gets the corresponding object wrapped in a {@link ReflObject} from the specified field.
     *
     * @param <O>      the type parameter
     * @param typeName the type name
     * @return the {@link ReflObject} containing the field object
     */
    public <O> @NotNull ReflObject<O> getFieldReflObjectNameless(@NotNull String typeName) {
        return getFieldReflObject(getFieldNameless(typeName));
    }

    private @NotNull <O> ReflObject<O> getFieldReflObject(@NotNull Field field) {
        Object obj = object == null ? objectClass : object;
        try {
            field.setAccessible(true);
            return new ReflObject<>((O) field.get(obj));
        } catch (IllegalAccessException e) {
            ExceptionUtils.throwException(e);
            throw new IllegalArgumentException("Unreachable code");
        }
    }

    /**
     * Gets the corresponding object from the specified field.
     *
     * @param <O>  the type parameter
     * @param name the name
     * @return the field object
     */
    public <O> @Nullable O getFieldObject(@NotNull String name) {
        return (O) getFieldReflObject(name).getObject();
    }

    /**
     * Gets the corresponding object from the specified field.
     *
     * @param <O>  the type parameter
     * @param type the type
     * @return the field object nameless
     */
    public <O> @Nullable O getFieldObjectNameless(@NotNull Class<?> type) {
        return (O) getFieldReflObjectNameless(type).getObject();
    }

    /**
     * Gets the corresponding object from the specified field.
     *
     * @param <O>      the type parameter
     * @param typeName the type name
     * @return the field object nameless
     */
    public <O> @Nullable O getFieldObjectNameless(@NotNull String typeName) {
        return (O) getFieldReflObjectNameless(typeName).getObject();
    }

    /**
     * Gets the corresponding object from the specified field.
     *
     * @param <O>   the type parameter
     * @param field the field
     * @return the field object
     */
    public <O> @Nullable O getFieldObject(@NotNull Field field) {
        return (O) getFieldReflObject(field).getObject();
    }

    /**
     * Gets fields.
     *
     * @return the fields
     */
    public @NotNull List<Field> getFields() {
        return ReflectionUtils.getFields(object == null ? objectClass : object);
    }

    public Method getMethod(String name, Object... params) {
        if (objectClass == null) return null;
        return getMethod(name, ReflectionUtils.objectsToClasses(params));
    }

    public Method getMethod(String name, Class<?>... paramTypes) {
        if (objectClass == null) return null;
        try {
            Method method = ReflectionUtils.getMethod(objectClass, null, name, paramTypes);
            if (method == null)
                throw new NoSuchMethodException(String.format("Method %s(%s) not found in class %s", name,
                        ReflectionUtils.classesToString(paramTypes), objectClass));
            return method;
        } catch (NoSuchMethodException e) {
            ExceptionUtils.throwException(e);
            return null;
        }
    }

    public <O> ReflObject<O> callMethod(String name, Object... params) {
        return callMethod(name, ReflectionUtils.objectsToClasses(params), params);
    }

    public <O> ReflObject<O> callMethod(String name, Class<?>[] paramTypes, Object... params) {
        Object obj = object == null ? objectClass : object;
        Method method = getMethod(name, paramTypes);
        method.setAccessible(true);
        try {
            return new ReflObject<>((O) method.invoke(obj, params));
        } catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
            ExceptionUtils.throwException(e);
            return null;
        }
    }

    public <O> O getMethodObject(String name, Object... params) {
        return getMethodObject(name, ReflectionUtils.objectsToClasses(params), params);
    }

    public <O> O getMethodObject(String name, Class<?>[] paramTypes, Object... params) {
        ReflObject<O> reflObject = callMethod(name, paramTypes, params);
        return reflObject == null ? null : reflObject.getObject();
    }

    public Method getMethodFromReturnType(Class<?> returnType, Object... params) {
        return getMethodFromReturnType(returnType, ReflectionUtils.objectsToClasses(params));
    }

    public Method getMethodFromReturnType(Class<?> returnType, Class<?>... paramTypes) {
        if (objectClass == null) return null;
        try {
            Method method = ReflectionUtils.getMethod(objectClass, returnType, null, paramTypes);
            if (method == null)
                throw new NoSuchMethodException(String.format("Method %s(%s)->%s not found in class %s", "<?>", "<?>",
                        returnType, objectClass));
            return method;
        } catch (NoSuchMethodException e) {
            ExceptionUtils.throwException(e);
            return null;
        }
    }

    public <O> ReflObject<O> callMethodFromReturnType(Class<?> returnType, Object... params) {
        Object obj = object == null ? objectClass : object;
        Method method = getMethodFromReturnType(returnType, params);
        return invokeMethod(obj, method, params);
    }

    public <O> O getMethodObjectFromReturnType(Class<?> returnType, Object... params) {
        ReflObject<O> reflObject = callMethodFromReturnType(returnType, params);
        return reflObject == null ? null : reflObject.getObject();
    }

    public Method getMethodNameless(Object... params) {
        if (objectClass == null) return null;
        return getMethodNameless(ReflectionUtils.objectsToClasses(params));
    }

    public Method getMethodNameless(Class<?>... paramTypes) {
        if (objectClass == null) return null;
        try {
            Method method = ReflectionUtils.getMethod(objectClass, null, null, paramTypes);
            if (method == null)
                throw new NoSuchMethodException(String.format("Method %s(%s) not found in class %s", "",
                        ReflectionUtils.classesToString(paramTypes), objectClass));
            return method;
        } catch (NoSuchMethodException e) {
            ExceptionUtils.throwException(e);
            return null;
        }
    }

    public <O> ReflObject<O> callMethodNameless(Object... params) {
        return callMethodNameless(ReflectionUtils.objectsToClasses(params), params);
    }

    public <O> ReflObject<O> callMethodNameless(Class<?>[] paramTypes, Object... params) {
        Object obj = object == null ? objectClass : object;
        Method method = getMethodNameless(paramTypes);
        return invokeMethod(obj, method, params);
    }

    private <O> ReflObject<O> invokeMethod(Object obj, Method method, Object[] params) {
        method.setAccessible(true);
        try {
            return new ReflObject<>((O) method.invoke(obj, params));
        } catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
            ExceptionUtils.throwException(e);
            return null;
        }
    }

    public <O> O getMethodNamelessObject(Object... params) {
        return getMethodNamelessObject(ReflectionUtils.objectsToClasses(params), params);
    }

    public <O> O getMethodNamelessObject(Class<?>[] paramTypes, Object... params) {
        ReflObject<O> reflObject = callMethodNameless(paramTypes, params);
        return reflObject == null ? null : reflObject.getObject();
    }

    public List<Method> getMethods() {
        return ReflectionUtils.getMethods(object == null ? objectClass : object);
    }

    public void printFields() {
        getFields().forEach(f -> System.out.printf("%s: %s%n", f, getFieldObject(f.getName())));
    }

    @Override
    public String toString() {
        return object == null ? (objectClass == null ? null : objectClass.toString()) : object.toString();
    }
}

package it.fulminazzo.fulmicollection.objects;

import it.fulminazzo.fulmicollection.utils.ExceptionUtils;
import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
@SuppressWarnings("unchecked")
public class ReflObject<T> {
    private static final String OBJECT_CLASS_NOT_NULL = "Object class cannot be null";
    private static final String CONSTRUCTOR_NOT_FOUND = "Constructor not found %s(%s)";
    private static final String FIELD_NOT_FOUND = "Field %s not found in class %s";
    private static final String FIELD_TYPE_NOT_FOUND = "Field of type %s not found in class %s";
    
    protected final @NotNull Class<T> objectClass;
    protected final @Nullable T object;

    /**
     * Instantiates a new Refl object.
     *
     * @param classPath the class path
     * @param params    the parameters
     */
    public ReflObject(@NotNull String classPath, Object @Nullable ... params) {
        this(ReflectionUtils.getClass(classPath), ReflectionUtils.objectsToClasses(params), params);
    }

    /**
     * Instantiates a new Refl object.
     *
     * @param classPath  the class path
     * @param paramTypes the parameter types
     * @param params     the parameters
     */
    public ReflObject(@NotNull String classPath, @Nullable Class<?>[] paramTypes, Object @Nullable ... params) {
        this(ReflectionUtils.getClass(classPath), paramTypes, params);
    }

    /**
     * Instantiates a new Refl object.
     *
     * @param objectClass the object class
     * @param params     the parameters
     */
    public ReflObject(Class<T> objectClass, Object @Nullable ... params) {
        this(objectClass, ReflectionUtils.objectsToClasses(params), params);
    }

    /**
     * Instantiates a new Refl object.
     *
     * @param objectClass the object class
     * @param paramTypes the parameter types
     * @param params     the parameters
     */
    public ReflObject(@Nullable Class<T> objectClass, @Nullable Class<?>[] paramTypes, Object @Nullable ... params) {
        T object = null;
        try {
            if (objectClass == null) throw new IllegalArgumentException(OBJECT_CLASS_NOT_NULL);
            Constructor<T> constructor = ReflectionUtils.getConstructor(objectClass, paramTypes);
            if (constructor == null)
                throw new NoSuchMethodException(String.format(CONSTRUCTOR_NOT_FOUND, objectClass.getName(),
                        ReflectionUtils.classesToString(paramTypes)));
            constructor.setAccessible(true);
            object = constructor.newInstance(params);
        } catch (NullPointerException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            ExceptionUtils.throwException(e);
        }
        this.objectClass = objectClass;
        this.object = object;
    }

    /**
     * Instantiates a new Refl object.
     *
     * @param classPath the class path
     * @param initiate  if true, the given class will be initiated with an empty constructor
     */
    public ReflObject(@NotNull String classPath, boolean initiate) {
        Class<T> objectClass;
        T object = null;
        try {
            objectClass = ReflectionUtils.getClass(classPath);
            if (objectClass == null) throw new IllegalArgumentException(OBJECT_CLASS_NOT_NULL);
            if (initiate) {
                Constructor<T> constructor = ReflectionUtils.getConstructor(objectClass);
                if (constructor == null)
                    throw new NoSuchMethodException(String.format(CONSTRUCTOR_NOT_FOUND, objectClass.getName(), ""));
                constructor.setAccessible(true);
                object = constructor.newInstance();
            }
        } catch (NullPointerException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            ExceptionUtils.throwException(e);
            throw new IllegalArgumentException("Unreachable code");
        }
        this.objectClass = objectClass;
        this.object = object;
    }

    /**
     * Instantiates a new Refl object.
     *
     * @param object      the object
     * @param objectClass the object class
     */
    public ReflObject(@Nullable T object, @Nullable Class<T> objectClass) {
        if (objectClass == null) throw new IllegalArgumentException(OBJECT_CLASS_NOT_NULL);
        this.objectClass = objectClass;
        this.object = object;
    }

    /**
     * Instantiates a new Refl object.
     *
     * @param object the object
     */
    public ReflObject(@NotNull T object) {
        this(object, (Class<T>) object.getClass());
    }

//    public Object[] getArray(ReflObject<?>... contents) {
//        Object[] objects = Arrays.stream(contents)
//                .map(o -> o.getObject() == null ? o.getObjectClass() : o.getObject())
//                .toArray();
//        return getArray(objects);
//    }
//
//    public Object[] getArray(Object... contents) {
//        if (objectClass == null) return null;
//        Object[] array = getArray(contents.length);
//        for (int i = 0; i < contents.length; i++) Array.set(array, i, contents[i]);
//        return array;
//    }
//
//    public Object[] getArray(int size) {
//        return (Object[]) Array.newInstance(objectClass, size);
//    }

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

    @Override
    public boolean equals(Object o) {
        if (o instanceof ReflObject) {
            ReflObject<?> reflObject = (ReflObject<?>) o;
            if (!this.objectClass.equals(reflObject.objectClass)) return false;
            return Objects.equals(this.object, reflObject.object);
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        return object == null ? objectClass.toString() : object.toString();
    }
}

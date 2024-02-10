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

    public ReflObject(Class<T> objectClass, Class<?>[] paramTypes, Object... params) {
        T object = null;
        try {
            Constructor<T> constructor = ReflectionUtils.getConstructor(objectClass, paramTypes);
            if (constructor == null)
                throw new NoSuchMethodException(String.format("Constructor not found %s(%s)", objectClass.getName(),
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

    public ReflObject(String classPath, boolean initiate) {
        Class<T> aClass = null;
        T object = null;
        try {
            aClass = ReflectionUtils.getClass(classPath);
            if (aClass == null) throw new ClassNotFoundException(classPath);
            if (initiate) {
                Constructor<T> constructor = ReflectionUtils.getConstructor(aClass);
                if (constructor == null) throw new NoSuchMethodException("Constructor not found");
                constructor.setAccessible(true);
                object = constructor.newInstance();
            }
        } catch (NullPointerException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            ExceptionUtils.throwException(e);
        }
        this.objectClass = aClass;
        this.object = object;
    }

    public ReflObject(T object, Class<T> objectClass) {
        this.objectClass = objectClass;
        this.object = object;
    }

    public ReflObject(T object) {
        this.objectClass = object == null ? null : (Class<T>) object.getClass();
        this.object = object;
    }

    public Object[] getArray(ReflObject<?>... contents) {
        Object[] objects = Arrays.stream(contents)
                .map(o -> o.getObject() == null ? o.getObjectClass() : o.getObject())
                .toArray();
        return getArray(objects);
    }

    public Object[] getArray(Object... contents) {
        if (objectClass == null) return null;
        Object[] array = getArray(contents.length);
        for (int i = 0; i < contents.length; i++) Array.set(array, i, contents[i]);
        return array;
    }

    public Object[] getArray(int size) {
        return (Object[]) Array.newInstance(objectClass, size);
    }

    public Field getField(String name) {
        if (objectClass == null) return null;
        try {
            Field field = ReflectionUtils.getField(objectClass, name);
            if (field == null) throw new NoSuchFieldException(String.format("Field %s not found in class %s", name, objectClass));
            return field;
        } catch (NoSuchFieldException e) {
            ExceptionUtils.throwException(e);
            return null;
        }
    }

    public Field getFieldNameless(Class<?> type) {
        if (objectClass == null) return null;
        try {
            Field field = ReflectionUtils.getField(objectClass, type);
            if (field == null) throw new NoSuchFieldException(String.format("Field of type %s not found in class %s", type, objectClass));
            return field;
        } catch (NoSuchFieldException e) {
            ExceptionUtils.throwException(e);
            return null;
        }
    }

    public Field getFieldNameless(String typeName) {
        if (objectClass == null) return null;
        try {
            Field field = ReflectionUtils.getFieldNameless(objectClass, typeName);
            if (field == null) throw new NoSuchFieldException(String.format("Field of type %s not found in class %s", typeName, objectClass));
            return field;
        } catch (NoSuchFieldException e) {
            ExceptionUtils.throwException(e);
            return null;
        }
    }

    public <O> void setField(String name, O object) {
        if (this.object == null) return;
        Field field = getField(name);
        if (field == null) return;
        try {
            field.setAccessible(true);
            field.set(this.object, object);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            ExceptionUtils.throwException(e);
        }
    }

    public <O> ReflObject<O> obtainField(String name) {
        return obtainField(getField(name));
    }

    public <O> ReflObject<O> obtainFieldNameless(Class<?> type) {
        return obtainField(getFieldNameless(type));
    }

    public <O> ReflObject<O> obtainFieldNameless(String typeName) {
        return obtainField(getFieldNameless(typeName));
    }

    private <O> ReflObject<O> obtainField(Field field) {
        Object obj = object == null ? objectClass : object;
        try {
            field.setAccessible(true);
            return new ReflObject<>((O) field.get(obj));
        } catch (IllegalAccessException e) {
            ExceptionUtils.throwException(e);
            return null;
        }
    }

    public <O> O getFieldObject(String name) {
        return (O) obtainField(name).getObject();
    }

    public <O> O getFieldObjectNameless(Class<?> type) {
        return (O) obtainFieldNameless(type).getObject();
    }

    public <O> O getFieldObjectNameless(String typeName) {
        return (O) obtainFieldNameless(typeName).getObject();
    }

    public List<Field> getFields() {
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

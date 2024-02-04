package it.fulminazzo.fulmicollection.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Reflection utils.
 */
public class ReflectionUtils {
    /**
     * The constant WRAPPER_CLASSES.
     */
    public static final Class<?>[] WRAPPER_CLASSES = new Class[]{Boolean.class, Byte.class, Short.class, Character.class,
            Integer.class, Long.class, Float.class, Double.class};

    /**
     * Gets class from its canonical name.
     * Checks if it is a class wrapped inside another.
     *
     * @param <T>       the type parameter
     * @param className the class name
     * @return the class
     */
    @SuppressWarnings("unchecked")
    public static <T> @NotNull Class<T> getClass(@NotNull String className) {
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            int index = className.lastIndexOf(".");
            if (index != -1)
                try {
                    return getClass(className.substring(0, index) + "$" + className.substring(index + 1));
                } catch (RuntimeException ignored) {}
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets field.
     *
     * @param object the object
     * @param name   the name
     * @return the field
     */
    public static @Nullable Field getField(@NotNull Object object, String name) {
        return getField(object.getClass(), name);
    }

    /**
     * Gets field.
     *
     * @param clazz the clazz
     * @param name  the name
     * @return the field
     */
    public static @Nullable Field getField(@NotNull Class<?> clazz, String name) {
        for (Class<?> c = clazz; c != null && !c.equals(Object.class); c = c.getSuperclass())
            for (Field field : c.getDeclaredFields())
                if (field.getName().equals(name)) {
                    field.setAccessible(true);
                    return field;
                }
        return null;
    }

    /**
     * Gets fields.
     *
     * @param object the object
     * @return the fields
     */
    public static List<Field> getFields(@NotNull Object object) {
        return getFields(object.getClass());
    }

    /**
     * Gets fields.
     *
     * @param clazz the clazz
     * @return the fields
     */
    public static @NotNull List<Field> getFields(@NotNull Class<?> clazz) {
        LinkedList<Field> fields = new LinkedList<>();
        for (Class<?> c = clazz; c != null && !c.equals(Object.class); c = c.getSuperclass())
            Arrays.stream(c.getDeclaredFields())
                    .sorted(Comparator.comparing(f -> Modifier.isStatic(f.getModifiers())))
                    .forEach(fields::addLast);
        return fields.stream()
                // Remove fields used by code coverage from Intellij IDEA.
                .filter(f -> !f.getName().equals("__$hits$__"))
                .peek(f -> f.setAccessible(true))
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Gets method.
     *
     * @param object     the object
     * @param name       the name
     * @param parameters the parameters
     * @return the method
     */
    public static @Nullable Method getMethod(@NotNull Object object, @Nullable Class<?> returnType, @NotNull String name,
                                             @Nullable Object... parameters) {
        if (parameters == null) parameters = new Object[0];
        final Class<?>[] paramTypes = new Class<?>[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            final Object obj = parameters[i];
            if (obj != null) paramTypes[i] = obj.getClass();
        }
        return getMethod(object.getClass(), returnType, name, paramTypes);
    }

    /**
     * Gets method.
     *
     * @param clazz   the clazz
     * @param name    the name
     * @param paramTypes the parameter types
     * @return the method
     */
    public static @Nullable Method getMethod(@NotNull Class<?> clazz, @Nullable Class<?> returnType, @NotNull String name,
                                             @Nullable Class<?>... paramTypes) {
        if (paramTypes == null) paramTypes = new Class<?>[0];
        for (Class<?> c = clazz; c != null && !c.equals(Object.class); c = c.getSuperclass()) {
            Method method = getMethodFromClass(c, returnType, name, paramTypes);
            if (method != null) return method;
            for (Class<?> i : c.getInterfaces()) {
                method = getMethod(i, returnType, name, paramTypes);
                if (method != null) return method;
            }
        }
        return null;
    }

    private @Nullable static Method getMethodFromClass(@NotNull Class<?> c, @Nullable Class<?> returnType, @NotNull String name, @Nullable Class<?> @NotNull [] paramTypes) {
        for (Method method : c.getDeclaredMethods()) {
            if (!method.getName().equalsIgnoreCase(name)) continue;
            if (returnType != null && !returnType.isAssignableFrom(method.getReturnType())) continue;
            if (method.getParameterCount() != paramTypes.length) continue;
            for (int i = 0; i < paramTypes.length; i++) {
                final Class<?> expected = paramTypes[i];
                if (expected == null) continue;
                final Class<?> actual = method.getParameterTypes()[i];
                if (!expected.isAssignableFrom(actual) && !actual.isAssignableFrom(expected))
                    return null;
            }
            return method;
        }
        return null;
    }

    /**
     * Gets methods.
     *
     * @param object the object
     * @return the methods
     */
    public static List<Method> getMethods(@NotNull Object object) {
        return getMethods(object.getClass());
    }

    /**
     * Gets methods.
     *
     * @param clazz the clazz
     * @return the methods
     */
    public static @NotNull List<Method> getMethods(@NotNull Class<?> clazz) {
        LinkedList<Method> methods = new LinkedList<>();
        for (Class<?> c = clazz; c != null && !c.equals(Object.class); c = c.getSuperclass()){
            addDeclaredMethods(c, methods);
            for (Class<?> i : c.getInterfaces())
                addDeclaredMethods(i, methods);
        }
        return methods.stream()
                .peek(f -> f.setAccessible(true))
                .distinct()
                .collect(Collectors.toList());
    }

    private static void addDeclaredMethods(@NotNull Class<?> clazz, @NotNull LinkedList<Method> methods) {
        Arrays.stream(clazz.getDeclaredMethods())
                .sorted(Comparator.comparing(f -> Modifier.isStatic(f.getModifiers())))
                .forEach(methods::addLast);
    }

    /**
     * Is primitive or wrapper boolean.
     *
     * @param clazz the class
     * @return the boolean
     */
    public static boolean isPrimitiveOrWrapper(@Nullable Class<?> clazz) {
        if (clazz == null) return false;
        for (Class<?> c : WRAPPER_CLASSES) if (clazz.equals(c)) return true;
        if (clazz.equals(String.class)) return true;
        return isPrimitive(clazz);
    }

    /**
     * Is primitive boolean.
     *
     * @param clazz the class
     * @return the boolean
     */
    public static boolean isPrimitive(@Nullable Class<?> clazz) {
        if (clazz == null) return false;
        else if (clazz.equals(boolean.class)) return true;
        else if (clazz.equals(byte.class)) return true;
        else if (clazz.equals(short.class)) return true;
        else if (clazz.equals(char.class)) return true;
        else if (clazz.equals(int.class)) return true;
        else if (clazz.equals(long.class)) return true;
        else if (clazz.equals(float.class)) return true;
        else return clazz.equals(double.class);
    }

    /**
     * Gets primitive class from wrapper.
     *
     * @param clazz the class
     * @return the primitive class
     */
    public static @Nullable Class<?> getPrimitiveClass(@Nullable Class<?> clazz) {
        if (clazz == null) return null;
        else if (clazz.equals(Boolean.class))
            return boolean.class;
        else if (clazz.equals(Byte.class))
            return byte.class;
        else if (clazz.equals(Short.class))
            return short.class;
        else if (clazz.equals(Character.class))
            return char.class;
        else if (clazz.equals(Integer.class))
            return int.class;
        else if (clazz.equals(Long.class))
            return long.class;
        else if (clazz.equals(Float.class))
            return float.class;
        else if (clazz.equals(Double.class))
            return double.class;
        return clazz;
    }

    /**
     * Gets wrapper class.
     *
     * @param clazz the clazz
     * @return the wrapper class
     */
    public static @Nullable Class<?> getWrapperClass(@Nullable Class<?> clazz) {
        if (clazz == null) return null;
        else if (clazz.equals(boolean.class))
            return Boolean.class;
        else if (clazz.equals(byte.class))
            return Byte.class;
        else if (clazz.equals(short.class))
            return Short.class;
        else if (clazz.equals(char.class))
            return Character.class;
        else if (clazz.equals(int.class))
            return Integer.class;
        else if (clazz.equals(long.class))
            return Long.class;
        else if (clazz.equals(float.class))
            return Float.class;
        else if (clazz.equals(double.class))
            return Double.class;
        return clazz;
    }
}

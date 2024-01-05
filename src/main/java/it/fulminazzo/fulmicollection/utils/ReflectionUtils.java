package it.fulminazzo.fulmicollection.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
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
    public static Field getField(Object object, String name) {
        return getField(object.getClass(), name);
    }

    /**
     * Gets field.
     *
     * @param clazz the clazz
     * @param name  the name
     * @return the field
     */
    public static Field getField(Class<?> clazz, String name) {
        do {
            for (Field field : clazz.getDeclaredFields())
                if (field.getName().equals(name)) {
                    field.setAccessible(true);
                    return field;
                }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        return null;
    }

    /**
     * Gets fields.
     *
     * @param object the object
     * @return the fields
     */
    public static List<Field> getFields(Object object) {
        return getFields(object.getClass());
    }

    /**
     * Gets fields.
     *
     * @param clazz the clazz
     * @return the fields
     */
    public static List<Field> getFields(Class<?> clazz) {
        LinkedList<Field> fields = new LinkedList<>();
        do {
            List<Field> tmp = new LinkedList<>();
            tmp.addAll(Arrays.asList(clazz.getFields()));
            tmp.addAll(Arrays.asList(clazz.getDeclaredFields()));
            tmp.stream().sorted(Comparator.comparing(f -> Modifier.isStatic(f.getModifiers()))).forEach(fields::addFirst);
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        return fields.stream()
                // Remove fields used by code coverage from Intellij IDEA.
                .filter(f -> !f.getName().equals("__$hits$__"))
                .peek(f -> f.setAccessible(true))
                .distinct()
                .collect(Collectors.toList());
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

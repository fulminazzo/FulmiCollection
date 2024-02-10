package it.fulminazzo.fulmicollection.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Reflection utils.
 */
@SuppressWarnings("unchecked")
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
     * @param <T>        the type parameter
     * @param className  the class name
     * @param paramTypes the param types
     * @return the class
     */
    public static <T> Class<T> getClass(String className, Class<?>... paramTypes) {
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            Class<T> aClazz = getInnerClass(className, paramTypes);
            if (aClazz == null) aClazz = getInnerInterface(className);
            return aClazz;
        }
    }

    private static <T> Class<T> getInnerClass(String classPath, Class<?>... paramTypes) {
        Class<T> aClass = null;
        String[] tmp = classPath.split("\\.");
        StringBuilder primClass = new StringBuilder();
        String clazz = tmp[tmp.length - 1];
        for (int i = 0; i < tmp.length - 1; i++) primClass.append(tmp[i]).append(".");
        if (primClass.toString().endsWith("."))
            primClass = new StringBuilder(primClass.substring(0, primClass.length() - 1));
        try {
            Class<?> primClazz = Class.forName(primClass.toString());
            aClass = (Class<T>) Stream.concat(Arrays.stream(primClazz.getClasses()), Arrays.stream(primClazz.getDeclaredClasses()))
                    .distinct()
                    .filter(c -> c.getSimpleName().equals(clazz))
                    .filter(c -> c.isInterface() || getConstructor(c, paramTypes) != null)
                    .findAny().orElse(null);
        } catch (ClassNotFoundException ignored) {}
        return aClass;
    }

    private static <T> Class<T> getInnerInterface(String classPath) {
        Class<T> aClass = null;
        String[] tmp = classPath.split("\\.");
        StringBuilder primClass = new StringBuilder();
        String clazz = tmp[tmp.length - 1];
        for (int i = 0; i < tmp.length - 1; i++) primClass.append(tmp[i]).append(".");
        if (primClass.toString().endsWith("."))
            primClass = new StringBuilder(primClass.substring(0, primClass.length() - 1));
        try {
            Class<?> primClazz = Class.forName(primClass.toString());
            aClass = (Class<T>) Arrays.stream(primClazz.getInterfaces())
                    .filter(c -> c.getSimpleName().equals(clazz))
                    .findAny().orElse(null);
        } catch (ClassNotFoundException ignored) {}
        return aClass;
    }

    /**
     * Converts the given objects to an array of classes.
     *
     * @param parameters the parameters
     * @return the classes
     */
    public static @NotNull Class<?>[] objectsToClasses(final Object @NotNull ... parameters) {
        final Class<?>[] paramTypes = new Class<?>[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            final Object obj = parameters[i];
            if (obj != null) paramTypes[i] = obj.getClass();
        }
        return paramTypes;
    }

    /**
     * Gets field nameless.
     *
     * @param object    the object
     * @param fieldType the field type
     * @return the field
     */
    public static @Nullable Field getFieldNameless(@NotNull Object object, String fieldType) {
        return getFieldNameless(object.getClass(), fieldType);
    }

    /**
     * Gets field nameless.
     *
     * @param clazz     the clazz
     * @param fieldType the field type
     * @return the field
     */
    public static @Nullable Field getFieldNameless(@NotNull Class<?> clazz, String fieldType) {
        return getField(clazz, getClass(fieldType));
    }

    /**
     * Gets field.
     *
     * @param object    the object
     * @param fieldType the field type
     * @return the field
     */
    public static @Nullable Field getField(@NotNull Object object, Class<?> fieldType) {
        return getField(object.getClass(), fieldType);
    }

    /**
     * Gets field.
     *
     * @param clazz     the clazz
     * @param fieldType the field type
     * @return the field
     */
    public static @Nullable Field getField(@NotNull Class<?> clazz, Class<?> fieldType) {
        for (Class<?> c = clazz; c != null && !c.equals(Object.class); c = c.getSuperclass())
            for (Field field : c.getDeclaredFields())
                if (fieldType.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    return field;
                }
        return null;
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
     * Gets constructor.
     *
     * @param <T>        the type parameter
     * @param object     the object
     * @param parameters the parameters
     * @return the constructor
     */
    public static @Nullable <T> Constructor<T> getConstructor(@NotNull Object object, @Nullable Object... parameters) {
        if (parameters == null) parameters = new Object[0];
        return getConstructor(object.getClass(), objectsToClasses(parameters));
    }

    /**
     * Gets constructor.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param paramTypes the param types
     * @return the constructor
     */
    public static @Nullable <T> Constructor<T> getConstructor(@NotNull Class<?> clazz, @Nullable Class<?>... paramTypes) {
        if (paramTypes == null) paramTypes = new Class<?>[0];
        for (Class<?> c = clazz; c != null && !c.equals(Object.class); c = c.getSuperclass()) {
            Constructor<T> constructor = getConstructorFromClass(c, paramTypes);
            if (constructor != null) return constructor;
        }
        return null;
    }

    private @Nullable static <T> Constructor<T> getConstructorFromClass(@NotNull Class<?> c, @Nullable Class<?> @NotNull [] paramTypes) {
        for (Constructor<?> constructor : c.getDeclaredConstructors()) {
            if (constructor.getParameterCount() != paramTypes.length) continue;
            for (int i = 0; i < paramTypes.length; i++) {
                final Class<?> expected = paramTypes[i];
                if (expected == null) continue;
                final Class<?> actual = constructor.getParameterTypes()[i];
                if (!expected.isAssignableFrom(actual) && !actual.isAssignableFrom(expected))
                    return null;
            }
            return (Constructor<T>) constructor;
        }
        return null;
    }

    /**
     * Gets method.
     *
     * @param object     the object
     * @param returnType the return type
     * @param name       the name
     * @param parameters the parameters
     * @return the method
     */
    public static @Nullable Method getMethod(@NotNull Object object, @Nullable Class<?> returnType, @NotNull String name,
                                             @Nullable Object... parameters) {
        if (parameters == null) parameters = new Object[0];
        return getMethod(object.getClass(), returnType, name, objectsToClasses(parameters));
    }

    /**
     * Gets method.
     *
     * @param clazz      the clazz
     * @param returnType the return type
     * @param name       the name
     * @param paramTypes the parameter types
     * @return the method
     */
    public static @Nullable Method getMethod(@NotNull Class<?> clazz, @Nullable Class<?> returnType, @Nullable String name,
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

    private @Nullable static Method getMethodFromClass(@NotNull Class<?> c, @Nullable Class<?> returnType, @Nullable String name, @Nullable Class<?> @NotNull [] paramTypes) {
        for (Method method : c.getDeclaredMethods()) {
            if (name != null && !method.getName().equalsIgnoreCase(name)) continue;
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

    /**
     * Converts the given classes to a string.
     *
     * @param classes the classes
     * @return the string
     */
    public static String classesToString(Class<?>... classes) {
        return classesToString(Arrays.asList(classes));
    }

    /**
     * Converts the given classes to a string.
     *
     * @param classes the classes
     * @return the string
     */
    public static String classesToString(List<Class<?>> classes) {
        return classes.stream().map(c -> c == null ? "null" : c.toString()).collect(Collectors.joining(", "));
    }
}

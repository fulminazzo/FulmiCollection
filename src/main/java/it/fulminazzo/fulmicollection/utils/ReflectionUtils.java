package it.fulminazzo.fulmicollection.utils;

import it.fulminazzo.fulmicollection.structures.tuples.NullableSinglet;
import it.fulminazzo.fulmicollection.structures.tuples.Singlet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * The type Reflection utils.
 */
@SuppressWarnings("unchecked")
public class ReflectionUtils {
    private static final String CLASS_NOT_FOUND = "Could not find class %class%";
    private static final String CONSTRUCTOR_NOT_FOUND = "Could not find constructor (%parameters%)";
    private static final String METHOD_NOT_FOUND = "Could not find method %type% %name%(%parameters%)";
    private static final String FIELD_NOT_FOUND = "Could not find field of name %name% in %class%";
    private static final String FIELD_TYPE_NOT_FOUND = "Could not find field of type %type% in %class%";

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
    public static <T> @NotNull Class<T> getClass(@NotNull String className) {
        try {
            if (!className.isEmpty())
                return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            Class<T> clazz = getInnerClass(className);
            if (clazz == null) clazz = getInnerInterface(className);
            if (clazz != null) return clazz;
        }
        throw new IllegalArgumentException(CLASS_NOT_FOUND.replace("%class%", className));
    }

    private static <T> @Nullable Class<T> getInnerClass(@NotNull String classPath) {
        Class<T> clazz = null;
        String[] tmp = classPath.split("\\.");
        StringBuilder mainClass = new StringBuilder();
        String realClass = tmp[tmp.length - 1];
        for (int i = 0; i < tmp.length - 1; i++) mainClass.append(tmp[i]).append(".");
        if (mainClass.toString().endsWith("."))
            mainClass = new StringBuilder(mainClass.substring(0, mainClass.length() - 1));
//        try {
            Class<?> primClazz = ReflectionUtils.getClass(mainClass.toString());
            clazz = (Class<T>) Arrays.stream(primClazz.getDeclaredClasses())
                    .distinct()
                    .filter(c -> c.getSimpleName().equals(realClass))
                    .findAny().orElse(null);
//        } catch (ClassNotFoundException ignored) {}
        return clazz;
    }

    private static <T> @Nullable Class<T> getInnerInterface(@NotNull String classPath) {
        Class<T> clazz = null;
        String[] tmp = classPath.split("\\.");
        StringBuilder mainClass = new StringBuilder();
        String realClass = tmp[tmp.length - 1];
        for (int i = 0; i < tmp.length - 1; i++) mainClass.append(tmp[i]).append(".");
        if (mainClass.toString().endsWith("."))
            mainClass = new StringBuilder(mainClass.substring(0, mainClass.length() - 1));
//        try {
            Class<?> primClazz = ReflectionUtils.getClass(mainClass.toString());
            clazz = (Class<T>) Arrays.stream(primClazz.getInterfaces())
                    .filter(c -> c.getSimpleName().equals(realClass))
                    .findAny().orElse(null);
//        } catch (ClassNotFoundException ignored) {}
        return clazz;
    }

    /**
     * Converts the given objects to an array of classes.
     *
     * @param parameters the parameters
     * @return the classes
     */
    public static @NotNull Class<?> @NotNull [] objectsToClasses(final Object @Nullable ... parameters) {
        if (parameters == null) return new Class[0];
        final Class<?>[] paramTypes = new Class<?>[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            final Object obj = parameters[i];
            if (obj != null) paramTypes[i] = obj.getClass();
        }
        return paramTypes;
    }

    /**
     * Gets the corresponding object from the given field.
     * If it fails, {@link #inaccessibleObject(Object)} is thrown.
     *
     * @param <T>    the type of the object
     * @param field  the field
     * @param object the object
     * @return a {@link NullableSinglet} containing the object, if the field could be set accessible
     */
    public static <T> T getOrThrow(final @NotNull Field field, final Object object) {
        return setAccessible(field).toNullable().map(f -> f.get(object)).map(o -> (T) o).orElseThrow(inaccessibleObject(field));
    }

    /**
     * Gets the corresponding object from the given field.
     *
     * @param <T>    the type of the object
     * @param field  the field
     * @param object the object
     * @return a {@link NullableSinglet} containing the object, if the field could be set accessible
     */
    public static <T> NullableSinglet<T> get(final @NotNull Field field, final Object object) {
        return setAccessible(field).toNullable().map(f -> f.get(object)).map(o -> (T) o);
    }

    /**
     * Sets the given object accessible using a {@link PrivilegedAction}.
     * If it fails, {@link #inaccessibleObject(Object)} is thrown.
     *
     * @param <T>    the type of the object
     * @param object the object
     * @return the object
     */
    public static <T extends AccessibleObject> @NotNull T setAccessibleOrThrow(final @NotNull T object) {
        return setAccessible(object).orElseThrow(inaccessibleObject(object));
    }

    /**
     * Sets the given object accessible using a {@link PrivilegedAction}.
     *
     * @param <T>    the type of the object
     * @param object the object
     * @return a {@link Singlet} containing the object, if it could be set accessible
     */
    public static <T extends AccessibleObject> @NotNull Singlet<T> setAccessible(final @NotNull T object) {
        Singlet<T> singlet = new Singlet<>();
        try {
            singlet.setValue(AccessController.doPrivileged((PrivilegedAction<T>) () -> {
                object.setAccessible(true);
                return object;
            }));
        } catch (RuntimeException e) {
            if (!e.getClass().getCanonicalName().equals("java.lang.reflect.InaccessibleObjectException"))
                throw e;
        }
        return singlet;
    }

    /**
     * Returns the exception
     * used to signal
     * that the given object could not be set to accessible from {@link #setAccessible(AccessibleObject)}.
     *
     * @param object the object
     * @return the supplier
     */
    public static @NotNull Supplier<IllegalArgumentException> inaccessibleObject(final @NotNull Object object) {
        return () -> new IllegalArgumentException(String.format("Could not set '%s' accessible", object));
    }

    /**
     * Gets field nameless.
     *
     * @param object    the object
     * @param fieldType the field type
     * @return the field
     */
    public static @NotNull Field getFieldNameless(@NotNull Object object, @NotNull String fieldType) {
        return getFieldNameless(object.getClass(), fieldType);
    }

    /**
     * Gets field nameless.
     *
     * @param clazz     the clazz
     * @param fieldType the field type
     * @return the field
     */
    public static @NotNull Field getFieldNameless(@NotNull Class<?> clazz, @NotNull String fieldType) {
        return getField(clazz, getClass(fieldType));
    }

    /**
     * Gets field.
     *
     * @param object    the object
     * @param fieldType the field type
     * @return the field
     */
    public static @NotNull Field getField(@NotNull Object object, @NotNull Class<?> fieldType) {
        return getField(object.getClass(), fieldType);
    }

    /**
     * Gets field.
     *
     * @param clazz     the clazz
     * @param fieldType the field type
     * @return the field
     */
    public static @NotNull Field getField(@NotNull Class<?> clazz, @NotNull Class<?> fieldType) {
        try {
            return getField(clazz, f -> fieldType.isAssignableFrom(f.getType()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(FIELD_TYPE_NOT_FOUND
                    .replace("%type%", fieldType.getName())
                    .replace("%class%", clazz.getSimpleName()));
        }
    }

    /**
     * Gets field.
     *
     * @param object the object
     * @param name   the name
     * @return the field
     */
    public static @NotNull Field getField(@NotNull Object object, @NotNull String name) {
        return getField(object.getClass(), name);
    }

    /**
     * Gets field.
     *
     * @param clazz the clazz
     * @param name  the name
     * @return the field
     */
    public static @NotNull Field getField(@NotNull Class<?> clazz, @NotNull String name) {
        try {
            return getField(clazz, f -> f.getName().equalsIgnoreCase(name));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(FIELD_NOT_FOUND
                    .replace("%name%", name)
                    .replace("%class%", clazz.getSimpleName()));
        }
    }

    /**
     * Gets field.
     *
     * @param object    the object
     * @param predicate the predicate
     * @return the field
     */
    public static @NotNull Field getField(@NotNull Object object, @NotNull Predicate<Field> predicate) {
        return getField(object.getClass(), predicate);
    }

    /**
     * Gets field from the given predicate.
     * If nothing is found, throws a {@link IllegalArgumentException}.
     *
     * @param clazz     the clazz
     * @param predicate the predicate
     * @return the field
     */
    public static @NotNull Field getField(@NotNull Class<?> clazz, @NotNull Predicate<Field> predicate) {
        for (Class<?> c = clazz; c != null && !c.equals(Object.class); c = c.getSuperclass())
            for (Field field : c.getDeclaredFields())
                if (predicate.test(field)) return field;
        throw new IllegalArgumentException(String.format("Could not find field from class '%s' and predicate", clazz.getCanonicalName()));
    }

    /**
     * Gets fields.
     *
     * @param object    the object
     * @param predicate the predicate
     * @return the fields
     */
    public static @NotNull List<Field> getFields(@NotNull Object object, @NotNull Predicate<Field> predicate) {
        return getFields(object.getClass(), predicate);
    }

    /**
     * Gets fields.
     *
     * @param clazz     the clazz
     * @param predicate the predicate
     * @return the fields
     */
    public static @NotNull List<Field> getFields(@NotNull Class<?> clazz, @NotNull Predicate<Field> predicate) {
        return getFields(clazz).stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * Gets fields.
     *
     * @param object the object
     * @return the fields
     */
    public static @NotNull List<Field> getFields(@NotNull Object object) {
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
                .distinct().collect(Collectors.toList());
    }

    /**
     * Gets constructor.
     *
     * @param <T>        the type parameter
     * @param object     the object
     * @param parameters the parameters
     * @return the constructor
     */
    public static <T> @NotNull Constructor<T> getConstructor(@NotNull Object object, Object @Nullable ... parameters) {
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
    public static <T> @NotNull Constructor<T> getConstructor(@NotNull Class<?> clazz, Class<?> @Nullable ... paramTypes) {
        if (paramTypes == null) paramTypes = new Class<?>[0];
        for (Class<?> c = clazz; c != null && !c.equals(Object.class); c = c.getSuperclass()) {
            Constructor<T> constructor = getConstructorFromClass(c, paramTypes);
            if (constructor != null) return constructor;
        }
        throw new IllegalArgumentException(CONSTRUCTOR_NOT_FOUND.replace("%parameters%", classesToString(paramTypes)));
    }

    private @Nullable static <T> Constructor<T> getConstructorFromClass(@NotNull Class<?> c, Class<?> @Nullable [] paramTypes) {
        for (Constructor<?> constructor : c.getDeclaredConstructors()) {
            if (paramTypes == null)
                if (constructor.getParameterCount() == 0) return (Constructor<T>) constructor;
                else continue;
            if (constructor.getParameterCount() != paramTypes.length) continue;
            if (validateParameters(paramTypes, constructor)) return (Constructor<T>) constructor;
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
    public static @NotNull Method getMethod(@NotNull Object object, @Nullable Class<?> returnType, @Nullable String name,
                                            Object @Nullable ... parameters) {
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
    public static @NotNull Method getMethod(@NotNull Class<?> clazz, @Nullable Class<?> returnType, @Nullable String name,
                                             Class<?> @Nullable ... paramTypes) {
        try {
            return getMethod(clazz, m -> {
                if (name != null && !m.getName().equalsIgnoreCase(name)) return false;
                if (returnType != null && !returnType.isAssignableFrom(m.getReturnType())) return false;
                if (paramTypes == null) return m.getParameterCount() == 0;
                if (m.getParameterCount() != paramTypes.length) return false;
                return validateParameters(paramTypes, m);
            });
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(METHOD_NOT_FOUND
                    .replace("%type%", returnType == null ? "?" : returnType.getSimpleName())
                    .replace("%name%", name == null ? "?" : name)
                    .replace("%parameters%", classesToString(paramTypes))
            );
        }
    }

    private static boolean validateParameters(@Nullable Class<?> @NotNull [] paramTypes, @NotNull Executable executable) {
        for (int i = 0; i < paramTypes.length; i++) {
            final Class<?> expected = getWrapperClass(paramTypes[i]);
            if (expected == null) continue;
            final Class<?> actual = getWrapperClass(executable.getParameterTypes()[i]);
            if (actual == null) throw new IllegalStateException("Unreachable code");
            if (expected.isArray() && (!actual.isArray() || !expected.getComponentType().isAssignableFrom(actual.getComponentType())))
                return false;
            if (!actual.isAssignableFrom(expected))
                return false;
        }
        return true;
    }

    /**
     * Gets method.
     *
     * @param clazz     the clazz
     * @param predicate the predicate
     * @return the method
     */
    public static @NotNull Method getMethod(@NotNull Class<?> clazz, @NotNull Predicate<Method> predicate) {
        for (Class<?> c = clazz; c != null && !c.equals(Object.class); c = c.getSuperclass()) {
            Method method = getMethodFromClass(c, predicate);
            if (method != null) return method;
            for (Class<?> i : c.getInterfaces())
                try {
                    return getMethod(i, predicate);
                } catch (IllegalArgumentException ignored) {}
        }
        throw new IllegalArgumentException(String.format("Could not find method from class '%s' and predicate", clazz.getCanonicalName()));
    }

    private @Nullable static Method getMethodFromClass(@NotNull Class<?> c, @NotNull Predicate<Method> predicate) {
        for (Method method : c.getDeclaredMethods())
            if (predicate.test(method)) return method;
        return null;
    }

    /**
     * Gets methods.
     *
     * @param object    the object
     * @param predicate the predicate
     * @return the methods
     */
    public static @NotNull List<Method> getMethods(@NotNull Object object, @NotNull Predicate<Method> predicate) {
        return getMethods(object.getClass(), predicate);
    }

    /**
     * Gets methods.
     *
     * @param clazz     the clazz
     * @param predicate the predicate
     * @return the methods
     */
    public static @NotNull List<Method> getMethods(@NotNull Class<?> clazz, @NotNull Predicate<Method> predicate) {
        return getMethods(clazz).stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * Gets methods.
     *
     * @param object the object
     * @return the methods
     */
    public static @NotNull List<Method> getMethods(@NotNull Object object) {
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
        return methods.stream().distinct().collect(Collectors.toList());
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
     * Compares the given field by getting the actual values from the two objects.
     *
     * @param field   the field
     * @param object1 the first object
     * @param object2 the second object
     * @return true if they are equal
     */
    public static boolean compareFields(final @NotNull Field field, final @NotNull Object object1, final @NotNull Object object2) {
        return get(field, object1).map(o -> Objects.equals(o, get(field, object2).getValue())).getValue();
    }

    /**
     * Compares object1 fields with object2 fields.
     *
     * @param object1 the first object
     * @param object2 the second object
     * @return true if all the fields are equal using {@link Object#equals(Object)}.
     */
    public static boolean equalsFields(@NotNull Object object1, @Nullable Object object2) {
        if (object2 == null) return false;
        if (!object1.getClass().isAssignableFrom(object2.getClass())) return false;
        for (Field field : getFields(object1)) {
            // Do not compare fields in inner classes
            if (field.getName().matches("this\\$[0-9]+")) continue;
            if (Modifier.isStatic(field.getModifiers())) continue;
            if (!compareFields(field, object1, object2)) return false;
        }
        return true;
    }

    /**
     * A method that checks if two objects are equal.
     * If a {@link ClassCastException} occurs while comparing,
     * it is ignored and the result is false.
     *
     * @param object1 the first object
     * @param object2 the second object
     * @return true if they match
     */
    public static boolean equalsClass(final Object object1, final Object object2) {
        try {
            return Objects.equals(object1, object2);
        } catch (ClassCastException e) {
            return false;
        }
    }

    /**
     * Converts the given classes to a string.
     *
     * @param classes the classes
     * @return the string
     */
    public static @NotNull String classesToString(Class<?> @Nullable... classes) {
        return classesToString(classes == null ? new ArrayList<>() : Arrays.asList(classes));
    }

    /**
     * Converts the given classes to a string.
     *
     * @param classes the classes
     * @return the string
     */
    public static @NotNull String classesToString(@NotNull List<Class<?>> classes) {
        return classes.stream().map(c -> c == null ? "null" : c.getCanonicalName()).collect(Collectors.joining(", "));
    }
}

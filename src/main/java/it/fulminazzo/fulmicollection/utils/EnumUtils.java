package it.fulminazzo.fulmicollection.utils;


import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumUtils {

    /**
     * Returns the value of an enum from the
     * given enum name. If an enum is not found,
     * null is returned.
     *
     * @param <E>    the type of the enum
     * @param eClass the enum class
     * @param name   the name of the enum
     * @return the enum
     */
    public static <E extends Enum<E>> E valueOf(@NotNull Class<? extends E> eClass, @NotNull String name) {
        for (E e : eClass.getEnumConstants())
            if (e.name().equalsIgnoreCase(name)) return e;
        return null;
    }

    /**
     * Returns the names of all the values in
     * an enum.
     *
     * @param <E>    the type of the enum
     * @param eClass the enum class
     * @return the names
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public static <E extends Enum<E>> List<String> returnValuesAsNames(@NotNull Class<E> eClass) {
        try {
            E[] values = (E[]) eClass.getMethod("values").invoke(eClass);
            return Arrays.stream(values).map(Enum::name).map(StringUtils::capitalize).collect(Collectors.toList());
        } catch (IllegalAccessException | ClassCastException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
package it.fulminazzo.fulmicollection.objects;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Represents an abstract object that should behave as an {@link Enum}.
 * This means that it will have all the enum main methods:
 * <code>name</code>, <code>ordinal</code> and some utility functions
 * for <code>valueOf</code> and <code>values</code>.
 */
public abstract class EnumObject {
    private static final Map<Class<? extends EnumObject>, Integer> ORDINALS = new HashMap<>();
    private final int ordinal;

    protected EnumObject() {
        int previous = ORDINALS.getOrDefault(getClass(), -1);
        this.ordinal = ++previous;
        ORDINALS.put(getClass(), previous);
    }

    /**
     * Gets the number associated with the current enum object.
     *
     * @return the number
     */
    public int ordinal() {
        return this.ordinal;
    }

    /**
     * Gets the name of the current enum object.
     *
     * @return the name
     */
    public @NotNull String name() {
        Refl<?> refl = new Refl<>(getClass());
        return getFieldsStream(getClass())
                .filter(f -> equals(refl.getFieldObject(f)))
                .map(Field::getName)
                .findFirst().orElseThrow(() -> new IllegalStateException("Could not find any field matching: " + this));
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + ordinal();
    }

    @Override
    public boolean equals(Object o) {
        return o != null && getClass().equals(o.getClass()) && ordinal() == ((EnumObject) o).ordinal();
    }

    @Override
    public String toString() {
        return name();
    }

    /**
     * Gets the most appropriate {@link EnumObject} value from the given name.
     * If none was found, a {@link IllegalArgumentException} will be thrown.
     *
     * @param <E>       the type of the class
     * @param enumClass the enum class
     * @param name      the name
     * @return the value
     */
    protected static <E extends EnumObject> @NotNull E valueOf(final @NotNull Class<E> enumClass,
                                                               final @NotNull String name) {
        for (E e : values(enumClass))
            if (e.name().equals(name))
                return e;
        throw new IllegalArgumentException(String.format("No enum constant %s.%s",
                enumClass.getCanonicalName(), name));
    }

    /**
     * Gets all the {@link EnumObject} values of the specified class.
     *
     * @param <E>       the type of the class
     * @param enumClass the enum class
     * @return the values
     */
    @SuppressWarnings("unchecked")
    protected static <E extends EnumObject> E @NotNull [] values(final @NotNull Class<E> enumClass) {
        Refl<?> refl = new Refl<>(enumClass);
        return getFieldsStream(enumClass)
                .map(refl::getFieldObject)
                .map(enumClass::cast)
                .toArray(a -> (E[]) Array.newInstance(enumClass, a));
    }

    private static <E extends EnumObject> @NotNull Stream<Field> getFieldsStream(@NotNull Class<E> enumClass) {
        return new Refl<>(enumClass).getStaticFields().stream()
                .filter(f -> enumClass.isAssignableFrom(f.getType()) || f.getType().isAssignableFrom(enumClass));
    }

}

package it.fulminazzo.fulmicollection.objects;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an abstract object that should behave as an {@link Enum}.
 * This means that it will have all the enum main methods:
 * <code>name</code>, <code>ordinal</code> and some utility functions
 * for <code>valueOf</code> and <code>values</code>.
 */
public abstract class EnumObject {
    private static final Map<Class<? extends EnumObject>, Integer> ORDINALS = new HashMap<>();
    private final int ordinal;

    /**
     * Instantiates a new Enum object.
     */
    public EnumObject() {
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
        return refl.getStaticFields().stream()
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
     * Gets all the {@link EnumObject} values of the specified class.
     *
     * @param <E>       the type of the class
     * @param enumClass the enum class
     * @return the values
     */
    @SuppressWarnings("unchecked")
    public static <E extends EnumObject> E @NotNull [] values(final @NotNull Class<E> enumClass) {
        Refl<?> refl = new Refl<>(enumClass);
        return (E[]) refl.getStaticFields().stream()
                .map(f -> refl.getFieldObject(f))
                .map(o -> enumClass.cast(o))
                .toArray();
    }

}

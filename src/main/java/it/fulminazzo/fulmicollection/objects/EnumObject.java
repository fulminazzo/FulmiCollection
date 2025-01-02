package it.fulminazzo.fulmicollection.objects;

import org.jetbrains.annotations.NotNull;

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
    public abstract @NotNull String name();

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

}

package it.fulminazzo.fulmicollection.objects;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an abstract object that should behave as an {@link Enum}.
 * This means that it will have all the enum main methods:
 * <code>name</code>, <code>ordinal</code> and some utility functions
 * for <code>valueOf</code> and <code>values</code>.
 */
public abstract class EnumObject {

    public abstract int ordinal();

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

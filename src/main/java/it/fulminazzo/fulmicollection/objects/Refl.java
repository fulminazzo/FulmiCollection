package it.fulminazzo.fulmicollection.objects;


import lombok.Getter;

import java.sql.Ref;
import java.util.Objects;

/**
 * A class that acts as a wrapper for an object.
 * It provides various utilities to work with reflections.
 *
 * @param <T> the object
 */
@Getter
public class Refl<T> {
    private final T object;

    public Refl(T object) {
        this.object = object;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Refl)
            return Objects.equals(this.object, ((Refl<?>) o).object);
        return super.equals(o);
    }

    @Override
    public String toString() {
        return object == null ? null : object.toString();
    }
}

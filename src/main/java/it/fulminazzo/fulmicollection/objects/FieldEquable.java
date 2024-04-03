package it.fulminazzo.fulmicollection.objects;

import it.fulminazzo.fulmicollection.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * A class that overrides {@link #hashCode()} to return a coherent value based on its fields.
 */
public abstract class FieldEquable extends Printable {
    private static final int OFFSET = 31;
    private static final int DEFAULT_HASH_CODE = 191;

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o instanceof FieldEquable) return hashCode() == o.hashCode();
        return false;
    }

    @Override
    public int hashCode() {
        Class<?> clazz = clazz();
        int hash = clazz.hashCode();
        for (Class<?> c = clazz; c != null && !c.equals(Object.class); c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields())
                if (!Modifier.isStatic(field.getModifiers())) {
                    Object object = ReflectionUtils.get(field, this);
                    final int v;
                    if (object != null) v = object.hashCode();
                    else v = DEFAULT_HASH_CODE;
                    hash = OFFSET * hash + v;
                }
        }
        return hash;
    }

    protected Class<? extends FieldEquable> clazz() {
        return getClass();
    }
}

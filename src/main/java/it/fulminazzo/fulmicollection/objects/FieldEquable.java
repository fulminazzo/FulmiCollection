package it.fulminazzo.fulmicollection.objects;

import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import org.jetbrains.annotations.Nullable;

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
        if (o instanceof FieldEquable) {
            FieldEquable fe = (FieldEquable) o;
            Class<?> c1 = clazz();
            Class<?> c2 = fe.clazz();
            if (!c1.equals(c2)) return false;
            for (Class<?> c = c1; c != null && !c.equals(Object.class); c = c.getSuperclass()) {
                for (Field field : c.getDeclaredFields())
                    if (!Modifier.isStatic(field.getModifiers())) {
                        Object o1 = ReflectionUtils.getOrThrow(field, this);
                        Object o2 = ReflectionUtils.getOrThrow(field, fe);
                        if (calculateHash(o1) != calculateHash(o2)) return false;
                    }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        Class<?> clazz = clazz();
        int hash = clazz.hashCode();
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields())
                if (!Modifier.isStatic(field.getModifiers())) {
                    Object object = ReflectionUtils.getOrThrow(field, this);
                    hash = OFFSET * hash + calculateHash(object);
                }
        }
        return hash;
    }

    private int calculateHash(final @Nullable Object object) {
        return object == null ? DEFAULT_HASH_CODE : object.hashCode();
    }

    protected Class<? extends FieldEquable> clazz() {
        return getClass();
    }
}

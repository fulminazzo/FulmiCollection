package it.fulminazzo.fulmicollection.objects;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * Represents an object that on toString() calls
 * prints itself and the fields it contains.
 */
public abstract class Printable {

    /**
     * Prints the object class and fields in a nice format.
     *
     * @param object    the object
     * @param headStart the start string
     * @return the string containing the information
     */
    public static @Nullable String printObject(@Nullable Object object, @Nullable String headStart) {
        if (object == null) return null;
        if (headStart == null) headStart = "";
        StringBuilder result = new StringBuilder(String.format("%s {\n", object.getClass().getSimpleName()));
        Class<?> oClass = object.getClass();
        while (oClass != null) {
            Field[] fields = oClass.getDeclaredFields();
            for (Field field : fields) {
                // Remove fields used by code coverage from Intellij IDEA.
                if (field.getName().equals("__$hits$__")) continue;
                field.setAccessible(true);
                try {
                    Object o = field.get(object);
                    String str = o instanceof Printable ? printObject(o, headStart + "  ") : o == null ? "null" : o.toString();
                    result.append(String.format("%s%s: %s\n", headStart + "  ", field.getName(), str));
                } catch (IllegalAccessException ignored) {}
            }
            oClass = oClass.getSuperclass();
        }
        return result + String.format("%s}", headStart);
    }

    @Override
    public @Nullable String toString() {
        return printObject(this, "");
    }
}

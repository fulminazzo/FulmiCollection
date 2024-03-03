package it.fulminazzo.fulmicollection.objects;

import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Represents an object that on toString() calls
 * prints itself and the fields it contains.
 */
public abstract class Printable {

    public static @NotNull String convertToJson(@Nullable Object object) {
        if (object == null) return "{}";
        StringBuilder result = new StringBuilder("{");
        Class<?> oClass = object.getClass();
        while (oClass != null) {
            Field[] fields = oClass.getDeclaredFields();
            for (Field field : fields) {
                // Remove fields in inner classes.
                if (field.getName().equalsIgnoreCase("this$1")) continue;
                // Remove fields used by code coverage from Intellij IDEA.
                if (field.getName().equals("__$hits$__")) continue;
                // Prevent static non-relevant fields to be shown.
                if (Modifier.isStatic(field.getModifiers())) continue;
                try {
                    field.setAccessible(true);
                    Object o = field.get(object);
                    if (o != null && o.hashCode() == object.hashCode()) continue;
                    String objectString;
                    if (o == null) objectString = "null";
                    else if (ReflectionUtils.isPrimitive(o.getClass())) objectString = o.toString();
                    else if (o instanceof String) objectString = String.format("\"%s\"", o);
                    else objectString = convertToJson(o);
                    result.append(String.format("\"%s\"", field.getName()))
                            .append(": ")
                            .append(objectString)
                            .append(", ");
                } catch (IllegalAccessException ignored) {}
            }
            oClass = oClass.getSuperclass();
        }
        String output = result.toString();
        if (output.length() > 2) output = output.substring(0, output.length() - 2);
        return output + "}";
    }

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
                // Remove fields in inner classes.
                if (field.getName().equalsIgnoreCase("this$1")) continue;
                // Remove fields used by code coverage from Intellij IDEA.
                if (field.getName().equals("__$hits$__")) continue;
                // Prevent static non-relevant fields to be shown.
                if (Modifier.isStatic(field.getModifiers())) continue;
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

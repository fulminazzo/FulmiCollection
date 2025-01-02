package it.fulminazzo.fulmicollection.objects;

import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Represents an object that on toString() calls
 * prints itself and the fields it contains.
 */
public abstract class Printable {

    /**
     * Convert the given object to a json compatible string.
     *
     * @param object the object
     * @return the string
     */
    public static @NotNull String convertToJson(@Nullable Object object) {
        if (object == null) return "null";
        else if (object instanceof Enum<?>) return ((Enum<?>) object).name();
        else if (object instanceof String) return String.format("\"%s\"", object);
        else if (ReflectionUtils.isPrimitiveOrWrapper(object.getClass())) return object.toString();
        else if (object instanceof Iterable) {
            StringBuilder tmp = new StringBuilder("[");
            Iterable<?> i = (Iterable<?>) object;
            for (Object o : i) tmp.append(convertToJson(o)).append(", ");
            String output = tmp.toString();
            if (output.length() > 1) output = output.substring(0, output.length() - 2);
            return output + "]";
        } else if (object instanceof Map) {
            StringBuilder tmp = new StringBuilder("{");
            Map<?, ?> i = (Map<?, ?>) object;
            for (Object k : i.keySet()) {
                Object v = i.get(k);
                tmp.append(convertToJson(k)).append(": ").append(convertToJson(v)).append(", ");
            }
            String output = tmp.toString();
            if (output.length() > 1) output = output.substring(0, output.length() - 2);
            return output + "}";
        }
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
                ReflectionUtils.get(field, object).ifPresent(o -> {
                    if (o != null && o.hashCode() == object.hashCode()) return;
                    String objectString = convertToJson(o);
                    result.append(String.format("\"%s\"", field.getName()))
                            .append(": ")
                            .append(objectString)
                            .append(", ");
                });
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
    public static String printObject(@Nullable Object object, @Nullable String headStart) {
        if (object == null) return null;
        final String finalHeadStart = headStart == null ? "" : headStart;
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
                ReflectionUtils.get(field, object).ifPresent(o -> {
                    String str = o instanceof Printable ? printObject(o, finalHeadStart + "  ") :
                            o == null ? "null" : o.toString();
                    result.append(String.format("%s%s: %s\n", finalHeadStart + "  ", field.getName(), str));
                });
            }
            oClass = oClass.getSuperclass();
        }
        return result + String.format("%s}", finalHeadStart);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString() {
        return printObject(this, "");
    }
}

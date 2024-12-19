package it.fulminazzo.fulmicollection.utils;

import org.jetbrains.annotations.Nullable;

import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type String utils.
 */
public class StringUtils {

    /**
     * Gets file name from its path.
     *
     * @param filePath the file path
     * @return the file name
     */
    public static String getFileName(@Nullable String filePath) {
        if (filePath == null) return null;
        return Paths.get(filePath).getFileName().toString();
    }

    /**
     * Repeats a string for a specified number of times.
     *
     * @param c     the string to repeat
     * @param times the number of times
     * @return the resulting string
     */
    @Nullable
    public static String repeat(@Nullable String c, long times) {
        if (c == null) return null;
        if (times < 0) return "";
        StringBuilder builder = new StringBuilder((int) (c.length() * times));
        for (long i = 0; i < times; i++) builder.append(c);
        return builder.toString();
    }

    /**
     * Inverse function of {@link #capitalize(String)}.
     *
     * @param string the string to convert
     * @return the converted string
     */
    @Nullable
    public static String decapitalize(@Nullable String string) {
        if (string == null) return null;
        final String SEPARATOR = "_";
        string = string.replaceAll("[\r\n\t ]", SEPARATOR);
        String result = "";
        for (char c : string.toCharArray()) {
            if (Character.isUpperCase(c) && !result.endsWith(SEPARATOR)) result += SEPARATOR;
            result += c;
        }
        if (result.startsWith(SEPARATOR)) result = result.substring(1);
        return result.toUpperCase();
    }

    /**
     * Replacement for WordUtils.capitalizeFully(string.replace("_", " ")):
     * converts a string by capitalizing every first char of any word.
     * For example, "this string" will be formatted as "This String".
     *
     * @param string the string to convert
     * @return the converted string
     */
    @Nullable
    public static String capitalize(@Nullable String string) {
        if (string == null) return null;
        string = string.toLowerCase();
        StringBuilder resultString = new StringBuilder();
        Matcher matcher = Pattern.compile("([^ \t\n\r_-]*)(.?)", Pattern.DOTALL).matcher(string);
        while (matcher.find()) {
            String s = matcher.group(1);
            if (!s.isEmpty()) resultString.append(s.substring(0, 1).toUpperCase()).append(s.length() > 1 ? s.substring(1) : "");
            resultString.append(matcher.group(2));
        }
        return resultString.toString();
    }
}

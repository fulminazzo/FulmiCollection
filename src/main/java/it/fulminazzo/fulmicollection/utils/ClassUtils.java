package it.fulminazzo.fulmicollection.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * The type Class utils.
 */
public class ClassUtils {

    public static @NotNull Set<Class<?>> findClassesInPackage(@Nullable String packageName)  {
        return findClassesInPackage(packageName, ClassUtils.class);
    }

    /**
     * Obtain the calling class path, whether the current
     * code is running from a JAR file or from an IDE.
     * Then search the directory tree for every class
     * contained in the given package name.
     * Any subpackage will recursively invoke this function,
     * and the result will be appended to the final one.
     *
     * @param packageName  the package name
     * @param callingClass the calling class
     * @return the set of classes
     */
    @NotNull
    public static Set<Class<?>> findClassesInPackage(@Nullable String packageName, @NotNull Class<?> callingClass)  {
        TreeSet<Class<?>> classes = new TreeSet<>(Comparator.comparing(Class::getCanonicalName));
        if (packageName == null || packageName.trim().isEmpty()) return classes;
        if (packageName.endsWith(File.separator)) packageName = packageName.substring(0, packageName.length() - 1);
        if (packageName.endsWith(".")) packageName = packageName.substring(0, packageName.length() - 1);
        String path = packageName.replaceAll("\\.", File.separator);
        String currentJar = JarUtils.getJarName(callingClass);
        try {
            if (currentJar.endsWith(".jar")) {
                // JAR File
                FileInputStream fileInputStream = new FileInputStream(currentJar);
                JarInputStream inputStream = new JarInputStream(fileInputStream);
                JarEntry entry;
                while ((entry = inputStream.getNextJarEntry()) != null) {
                    String className = entry.getName();
                    if (!className.startsWith(path)) continue;
                    if (className.equalsIgnoreCase(path + File.separator)) continue;
                    className = className.replace("/", ".");
                    if (!className.endsWith(".class")) classes.addAll(findClassesInPackage(className));
                    else {
                        className = className.substring(0, className.length() - ".class".length());
                        Class<?> clazz = Class.forName(className);
                        if (clazz.getCanonicalName() != null) classes.add(clazz);
                    }
                }
            } else {
                // File System
                File directory = new File(currentJar, path);
                if (!directory.isDirectory()) return classes;
                File[] files = directory.listFiles();
                if (files == null) return classes;
                for (File file : files) {
                    String className = file.getName();
                    if (!className.endsWith(".class"))
                        classes.addAll(findClassesInPackage(packageName + "." + className));
                    else {
                        className = className.substring(0, className.length() - ".class".length());
                        classes.add(Class.forName(packageName + "." + className));
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new LinkedHashSet<>(classes);
    }
}

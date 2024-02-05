package it.fulminazzo.fulmicollection.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

/**
 * The type Class utils.
 */
public class ClassUtils {

    /**
     * This code works whether it is run from a JAR file or from an IDE.
     * It searches the directory tree for every class contained in the given package name.
     * Any subpackage will recursively invoke this function, and the result will be appended to the final one.
     *
     * @param packageName the package name
     * @return the set of classes
     */
    public static @NotNull Set<Class<?>> findClassesInPackage(@Nullable String packageName)  {
        return findClassesInPackage(packageName, null);
    }

    /**
     * This code works whether it is run from a JAR file or from an IDE.
     * It searches the directory tree for every class contained in the given package name.
     * Any subpackage will recursively invoke this function, and the result will be appended to the final one.
     *
     * @param packageName the package name
     * @param clazz       this class will be used to look up the corresponding JAR file that might contain the package
     * @return the set of classes
     */
    public static @NotNull Set<Class<?>> findClassesInPackage(@Nullable String packageName, @Nullable Class<?> clazz)  {
        if (packageName == null || packageName.trim().isEmpty()) return new HashSet<>();
        if (packageName.endsWith(File.separator)) packageName = packageName.substring(0, packageName.length() - 1);
        if (packageName.endsWith(".")) packageName = packageName.substring(0, packageName.length() - 1);

        final TreeSet<Class<?>> classes = new TreeSet<>(Comparator.comparing(Class::getCanonicalName));
        final List<String> classPathEntries = new ArrayList<>(Arrays.asList(System.getProperty("java.class.path").split(File.pathSeparator)));
        // If this code is run in a JAR container as an extension (plugin) to another program,
        // it will not be present in the classPathEntries list.
        try {
            classPathEntries.add(JarUtils.getCurrentJarName());
        } catch (RuntimeException ignored) {}
        if (clazz != null) {
            JarFile jarFile = JarUtils.getJarFile(clazz);
            if (jarFile != null) classPathEntries.add(jarFile.getName());
        }

        for (String currentJar : classPathEntries) classes.addAll(findClassesInPackageSingle(packageName, currentJar));

        return classes;
    }


    /**
     * This code works whether it is run from a JAR file or from an IDE.
     * It searches the given path (JAR or directory) for every class contained in the given package name.
     * Any subpackage will recursively invoke this function, and the result will be appended to the final one.
     *
     * @param packageName the package name
     * @param classPath   the class path
     * @return the set of classes
     */
    public static @NotNull Set<Class<?>> findClassesInPackageSingle(@Nullable String packageName, String classPath)  {
        if (packageName == null || packageName.trim().isEmpty()) return new HashSet<>();
        if (packageName.endsWith(File.separator)) packageName = packageName.substring(0, packageName.length() - 1);
        if (packageName.endsWith(".")) packageName = packageName.substring(0, packageName.length() - 1);

        final String path = packageName.replace(".", File.separator);
        final TreeSet<Class<?>> classes = new TreeSet<>(Comparator.comparing(Class::getCanonicalName));

        try {
            if (classPath.endsWith(".jar")) {
                // JAR File
                FileInputStream fileInputStream = new FileInputStream(classPath);
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
                File directory = new File(classPath, path);
                if (!directory.isDirectory()) return classes;
                File[] files = directory.listFiles();
                if (files == null) return classes;
                for (File file : files) {
                    String className = file.getName();
                    if (!className.endsWith(".class"))
                        classes.addAll(findClassesInPackage(packageName + "." + className));
                    else {
                        className = className.substring(0, className.length() - ".class".length());
                        Class<?> clazz = Class.forName(packageName + "." + className);
                        if (clazz.getCanonicalName() != null) classes.add(clazz);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return classes;
    }
}

package it.fulminazzo.fulmicollection.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * The type Jar utils.
 */
public class JarUtils {

    /**
     * Gets a resource from the current jar file.
     *
     * @param resourceName the resource name
     * @return the resource
     */
    @Nullable
    public static InputStream getResource(@NotNull String resourceName) {
        if (resourceName.startsWith("/")) resourceName = resourceName.substring(1);
        InputStream resource = JarUtils.class.getResourceAsStream(resourceName);
        if (resource == null) resource = JarUtils.class.getResourceAsStream("/" + resourceName);
        return resource;
    }

    /**
     * Returns the file at the specified path from the given File.
     *
     * @param file the File (should be a .jar)
     * @param path the path
     * @return an InputStream with the file contents (if found)
     */
    @Nullable
    public static InputStream getJarFile(@NotNull File file, @NotNull String path) {
        JarFile jarFile = getJar(file);
        if (jarFile == null) return null;
        else return getJarFile(jarFile, path);
    }

    /**
     * Returns the file at the specified path from the given JarFile.
     *
     * @param jar  the JarFile
     * @param path the path
     * @return an InputStream with the file contents (if found)
     */
    @Nullable
    public static InputStream getJarFile(@NotNull JarFile jar, @NotNull String path) {
        try {
            ZipEntry jarEntry = jar.getEntry(path);
            return jar.getInputStream(jarEntry);
        } catch (IOException | NullPointerException e) {
            return null;
        }
    }

    /**
     * Returns an instance of JarFile from the given Class.
     *
     * @param jarClass the Class
     * @return the corresponding JarFile, if present
     */
    @Nullable
    public static JarFile getJarFile(@NotNull Class<?> jarClass) {
        try {
            return getJar(jarClass.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns an instance of JarFile from the given path.
     *
     * @param jarPath the String path
     * @return the corresponding JarFile, if present
     */
    @Nullable
    public static JarFile getJar(@NotNull String jarPath) {
        return getJar(new File(jarPath));
    }

    /**
     * Returns an instance of JarFile from the given File.
     *
     * @param jarFile the File
     * @return the corresponding JarFile, if present
     */
    @Nullable
    public static JarFile getJar(@NotNull File jarFile) {
        try {
            if (jarFile.isFile()) return new JarFile(jarFile);
            else return null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Gets jar name.
     *
     * @param jarClass the jar class
     * @return the jar name
     */
    @NotNull
    public static String getJarName(@NotNull Class<?> jarClass) {
        try {
            return jarClass.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets current jar.
     *
     * @return the current jar
     */
    @NotNull
    public static File getCurrentJar() {
        return new File(getCurrentJarName());
    }

    /**
     * Gets current jar name.
     *
     * @return the current jar name
     */
    @NotNull
    public static String getCurrentJarName() {
        try {
            return JarUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
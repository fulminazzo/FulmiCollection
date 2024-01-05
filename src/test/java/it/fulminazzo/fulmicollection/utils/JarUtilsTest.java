package it.fulminazzo.fulmicollection.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class JarUtilsTest {
    private final String jarDirectory = "build/libs/";
    private final String jarName = "FulmiCollection-[0-9].[0-9].jar";

    private File getJar() {
        return new File(JarUtils.getJarName(Assertions.class));
    }

    private String getJarName() {
        return getJar().getAbsolutePath();
    }

    @Test
    void testGetExistingResource() {
        assertNotNull(JarUtils.getResource("jarutils-test1.txt"));
    }

    @Test
    void testGetNotExistingResource() {
        assertNull(JarUtils.getResource("jarutils-test0.txt"));
    }

    @Test
    void testGetMainClassFromJar() {
        assertNotNull(JarUtils.getJarFile(getJar(), Assertions.class.getCanonicalName().replace(".", "/") + ".class"));
    }

    @Test
    void testGetNotExistingClassFromJar() {
        assertNull(JarUtils.getJarFile(getJar(), "it/fulminazzo/fulmicollection/SuperFulmiCollection.class"));
    }

    @Test
    void testGetJarFileOfJUnit() {
        assertNotNull(JarUtils.getJarFile(Assertions.class));
    }

    @Test
    void testGetJarFileNameOfJUnit() {
        assertNotNull(JarUtils.getJarName(Assertions.class));
    }

    @Test
    void getJarFromExistingFile() {
        getJar();
        assertNotNull(JarUtils.getJar(getJarName()));
    }

    @Test
    void getJarFromNotExistingFile() {
        getJar();
        assertNull(JarUtils.getJar(getJarName() + "x"));
    }
}
package it.fulminazzo.fulmicollection.objects;

import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractReflTest {
    protected TestClass testClass;
    protected Refl<?> refl;

    @BeforeEach
    void setUp() {
        this.testClass = new TestClass("James");
        this.refl = new Refl<>(this.testClass);
    }

    protected static class TestClass {
        final String name;

        TestClass(String name) {
            this.name = name;
        }

        public String printField(String greeting) {
            return greeting + name;
        }
    }
}

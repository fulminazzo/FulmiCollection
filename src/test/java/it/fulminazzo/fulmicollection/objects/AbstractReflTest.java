package it.fulminazzo.fulmicollection.objects;

import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractReflTest {
    protected TestClass testClass;
    protected Refl<TestClass> refl;

    @BeforeEach
    void setUp() {
        this.testClass = new TestClass("James");
        this.refl = new Refl<>(this.testClass);
    }

    protected static class TestClass {
        int age = 10;
        final String name;
        static final String CONSTANT = "";

        TestClass(String name) {
            this.name = name;
        }

        public String printField(String greeting) {
            return greeting + name;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof TestClass) return this.name.equals(((TestClass) o).name);
            return super.equals(o);
        }

        public static String print() {
            return TestClass.class.getCanonicalName();
        }
    }
}

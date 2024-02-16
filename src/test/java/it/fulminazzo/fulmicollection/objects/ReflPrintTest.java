package it.fulminazzo.fulmicollection.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReflPrintTest {
    private Refl<DemoClass> refl;

    @BeforeEach
    void setUp() {
        this.refl = new Refl<>(new DemoClass());
    }

    @Test
    void testPrint() {
        assertEquals("DemoClass {\n" +
                "  type: Demo\n" +
                "  by: null\n" +
                "  reflObject: 10\n" +
                "  testClass: null\n" +
                "  refl: null\n" +
                "}", this.refl.print());
    }

    @Test
    void testPrintFields() {
        assertEquals("it.fulminazzo.fulmicollection.objects.ReflPrintTest.DemoClass {\n" +
                "  (it.fulminazzo.fulmicollection.objects.ReflPrintTest.DemoClass) java.lang.String type = Demo;\n" +
                "  (it.fulminazzo.fulmicollection.objects.ReflPrintTest.DemoClass) java.lang.String by = null;\n" +
                "  (it.fulminazzo.fulmicollection.objects.ReflPrintTest.DemoClass) static long date = 53895;\n" +
                "  (it.fulminazzo.fulmicollection.objects.ReflPrintTest.DemoClass) static it.fulminazzo.fulmicollection.objects.AbstractReflTest.TestClass testClass = it.fulminazzo.fulmicollection.objects.AbstractReflTest.TestClass {\n" +
                "    (it.fulminazzo.fulmicollection.objects.AbstractReflTest.TestClass) int age = 10;\n" +
                "    (it.fulminazzo.fulmicollection.objects.AbstractReflTest.TestClass) java.lang.String name = James;\n" +
                "    (it.fulminazzo.fulmicollection.objects.AbstractReflTest.TestClass) static java.lang.String CONSTANT = ;\n" +
                "  };\n" +
                "  (it.fulminazzo.fulmicollection.objects.ReflPrintTest.DemoClass) it.fulminazzo.fulmicollection.objects.Refl reflObject = it.fulminazzo.fulmicollection.objects.Refl {\n" +
                "    (it.fulminazzo.fulmicollection.objects.Refl) java.lang.Object object = 10;\n" +
                "  };\n" +
                "  (it.fulminazzo.fulmicollection.objects.AbstractReflTest) it.fulminazzo.fulmicollection.objects.AbstractReflTest.TestClass testClass = null;\n" +
                "  (it.fulminazzo.fulmicollection.objects.AbstractReflTest) it.fulminazzo.fulmicollection.objects.Refl refl = null;\n" +
                "}", this.refl.printFields(false, true, true));
    }

    @Test
    void testPrintFieldsSimple() {
        assertEquals("DemoClass {\n" +
                "  (DemoClass) String type = Demo;\n" +
                "  (DemoClass) String by = null;\n" +
                "  (DemoClass) static long date = 53895;\n" +
                "  (DemoClass) static TestClass testClass = TestClass {\n" +
                "    (TestClass) int age = 10;\n" +
                "    (TestClass) String name = James;\n" +
                "    (TestClass) static String CONSTANT = ;\n" +
                "  };\n" +
                "  (DemoClass) Refl reflObject = Refl {\n" +
                "    (Refl) Object object = 10;\n" +
                "  };\n" +
                "  (AbstractReflTest) TestClass testClass = null;\n" +
                "  (AbstractReflTest) Refl refl = null;\n" +
                "}", this.refl.printFields(true, true, true));
    }

    static class DemoClass extends AbstractReflTest {
        String type;
        String by;
        static long date;
        static AbstractReflTest.TestClass testClass;
        Refl<Integer> reflObject;

        public DemoClass() {
            type = "Demo";
            by = null;
            date = 53895;
            testClass = new AbstractReflTest.TestClass("James");
            reflObject = new Refl<>(10);
        }
    }
}

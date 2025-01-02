package it.fulminazzo.fulmicollection.objects;

import static org.junit.jupiter.api.Assertions.*;

class EnumObjectTest {

    static class MockEnum extends EnumObject {
        public static final MockEnum FIRST = new MockEnum();
        public static final MockEnum SECOND = new MockEnum();
        public static final MockEnum THIRD = new MockEnum();

        public static MockEnum valueOf(String name) {
            return valueOf(MockEnum.class, name);
        }

        public static MockEnum[] values() {
            return values(MockEnum.class);
        }

    }

}
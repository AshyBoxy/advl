package xyz.ashyboxy.advl;

import xyz.ashyboxy.advl.loader.Logger;

public class TestClass implements TestInterface, SecondTestInterface {
    private final String messageSuffix;

    public TestClass(String messageSuffix) {
        this.messageSuffix = messageSuffix;
    }

    public void test() {
        Logger.logO("test class over here:", messageSuffix);
    }

    public InnerTestClass classTest(int i) {
        return new InnerTestClass(i * 2, i / 1.3f, i % 2 != 0);
    }

    @Override
    public boolean isBaz() {
        return false;
    }

    @Override
    public boolean isFunny() {
        return true;
    }

    public class InnerTestClass implements InnerTestInterface {
        private final int foo;
        private final float bar;
        private final boolean baz;

        private InnerTestClass(int foo, float bar, boolean baz) {
            this.foo = foo;
            this.bar = bar;
            this.baz = baz;
        }

        public int getFoo() {
            test();
            return foo;
        }

        public float getBar() {
            return bar;
        }

        public boolean isBaz() {
            return baz;
        }
    }

    public interface InnerTestInterface extends TestInterface {
        int getFoo();
    }
}

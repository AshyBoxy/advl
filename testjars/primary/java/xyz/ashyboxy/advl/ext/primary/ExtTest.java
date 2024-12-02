package xyz.ashyboxy.advl.ext.primary;

import xyz.ashyboxy.advl.loader.Logger;

public class ExtTest {
    public static void test() {
        Logger.log("Hello from ext.primary! This should've been loaded from a jar (and it's inaccessible at compile time to the root project).");
        SecondaryTest.run();
    }
}

package xyz.ashyboxy.advl.ext.primary;

import xyz.ashyboxy.advl.asm.parent.Logger;
import xyz.ashyboxy.advl.ext.secondary.SecondaryThing;

public class SecondaryTest {
    public static void run() {
        Logger.log("Now trying referencing secondary.jar from primary.jar");
        new SecondaryThing().logThing();
    }
}

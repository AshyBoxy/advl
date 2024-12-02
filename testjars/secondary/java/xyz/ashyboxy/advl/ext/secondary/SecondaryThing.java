package xyz.ashyboxy.advl.ext.secondary;

import xyz.ashyboxy.advl.loader.Logger;

public class SecondaryThing {
    public void logThing() {
        Logger.log("And hello from ext.secondary (secondary.jar)");
        second();
    }

    public void second() {
        Logger.log("Hi from ext.secondary second");
    }
}

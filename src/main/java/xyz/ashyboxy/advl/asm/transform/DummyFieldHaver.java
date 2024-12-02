package xyz.ashyboxy.advl.asm.transform;

import xyz.ashyboxy.advl.loader.Logger;
import xyz.ashyboxy.advl.asm.CopiedMethods;
import xyz.ashyboxy.advl.asm.FieldHaver;

public class DummyFieldHaver implements FieldHaver, CopiedMethods {
    private static int field = 3;

    @Override
    public int getField() {
        return field;
    }

    @Override
    public void copyMe() {
        Logger.log("This was copyMe in DummyFieldHaver");
    }

    @Override
    public void copyMe2() {
        Logger.log("This was copyMe2 in DummyFieldHaver");
    }

    public boolean removeMe() {
        throw new AssertionError();
    }

    public void copyRemoveMe() {
        Logger.log("This was copyRemoveMe in DummyFieldHaver. It should've been removed, but first copied to another name (not renamed).");
    }

    public String stringReturner() {
        return "hello there";
    }
}

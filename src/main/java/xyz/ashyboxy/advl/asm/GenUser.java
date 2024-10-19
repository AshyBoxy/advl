package xyz.ashyboxy.advl.asm;

import xyz.ashyboxy.advl.asm.parent.Logger;
import xyz.ashyboxy.advl.asm.gen.NonExistent;
import xyz.ashyboxy.advl.asm.gen.Test;

import java.lang.reflect.Field;

public class GenUser {
    public static void test() throws Exception {
        Logger.log("xyz.ashyboxy.advl.asm.gen.Test has field:", Integer.toString(Test.field));
        Logger.log("NonExistent.hexString(69):", NonExistent.hexString(69));

        Class<?> c = Class.forName("xyz.ashyboxy.advl.asm.gen.ForNamed");
        Field f = c.getField("field");
        Logger.log("xyz.ashyboxy.advl.asm.gen.ForNamed has field:", Integer.toString(f.getInt(null)));
    }
}

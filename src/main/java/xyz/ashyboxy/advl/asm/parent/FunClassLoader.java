package xyz.ashyboxy.advl.asm.parent;

import java.net.URL;

public interface FunClassLoader extends AccessibleClassLoader {
    byte[] tryFindClassFile(String name) throws NoClassDefFoundError, ClassNotFoundException;
    void addURL(URL url);
}

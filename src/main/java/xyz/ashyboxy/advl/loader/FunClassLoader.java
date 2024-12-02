package xyz.ashyboxy.advl.loader;

import java.net.URL;

public interface FunClassLoader extends AccessibleClassLoader {
    byte[] tryFindClassFile(String name) throws NoClassDefFoundError, ClassNotFoundException;
    void addURL(URL url);

    /**
     * implemented by IsolatedClassLoader, to allow loading from the parent classloader as the final step
     */
    default Class<?> loadClassFromParent(String name, boolean resolve) throws ClassNotFoundException {
        return null;
    }
}

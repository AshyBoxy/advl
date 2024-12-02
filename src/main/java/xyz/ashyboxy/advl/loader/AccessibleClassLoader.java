package xyz.ashyboxy.advl.loader;

public interface AccessibleClassLoader {
    Class<?> findClass(String name) throws ClassNotFoundException;

    Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException;
}

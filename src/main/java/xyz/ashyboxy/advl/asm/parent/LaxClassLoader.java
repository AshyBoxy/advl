package xyz.ashyboxy.advl.asm.parent;

public class LaxClassLoader extends ClassLoader implements AccessibleClassLoader {
    public LaxClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }
}

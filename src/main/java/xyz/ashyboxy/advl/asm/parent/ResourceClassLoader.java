package xyz.ashyboxy.advl.asm.parent;

import java.net.URL;
import java.net.URLClassLoader;

public class ResourceClassLoader extends URLClassLoader implements FunClassLoader {
    LaxClassLoader parent;

    public ResourceClassLoader(URL[] urls, LaxClassLoader parent) {
        super(urls, parent);
        this.parent = (LaxClassLoader) getParent();
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return parent.findClass(name);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return parent.loadClass(name, resolve);
    }

    @Override
    public byte[] tryFindClassFile(String name) throws NoClassDefFoundError, ClassNotFoundException {
        return null;
    }

    @Override
    public void addURL(URL url) {
        if (Consts.LOG_RESOURCE_ADD) Logger.log("Adding to ResourceClassLoader:", url.toString());
        super.addURL(url);
    }
}

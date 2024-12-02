package xyz.ashyboxy.advl.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

public class IsolatedClassLoader extends ClassLoader implements FunClassLoader {
    private final ResourceClassLoader parent;
    private final List<String> allowedParentPackages;

    public IsolatedClassLoader(ResourceClassLoader parent, List<String> allowedParentPackages) {
        super(null);
        this.parent = parent;
        this.allowedParentPackages = allowedParentPackages;
    }

    public byte[] tryFindClassFile(String name) throws NoClassDefFoundError, ClassLoadingException {
        URL resource = getResource(name.replace('.', '/') + ".class");
        if (resource == null) throw new NoClassDefFoundError(name);
        if (!resource.getProtocol().equals("file") && !resource.getProtocol().equals("jar")) {
            throw new NoClassDefFoundError(name);
        }

        try (InputStream is = resource.openStream()) {
            int b = is.available();
            // TODO: what sizes should be used here?
            ByteArrayOutputStream o = new ByteArrayOutputStream(Math.max(b, 32768));
            byte[] buffer = new byte[8192];
            int l;
            while ((l = is.read(buffer)) > 0) o.write(buffer, 0, l);
            return o.toByteArray();
        } catch (Exception e) {
            throw new ClassLoadingException(name, e);
        }
    }

    // TransformingClassLoader should handle loading lower classes by asking us for the class' byte[]
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.startsWith("java.")) return null;

        if (allowedParentPackages.stream().anyMatch(name::startsWith)) return parent.findClass(name);

        if (Consts.DISABLE_ISOLATION) {
            Logger.logO("[WARNING] Finding", name, "with parent classloader");
            return parent.findClass(name);
        }
        throw new ClassNotFoundException(name);
    }

    // TODO: resolve
    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (name.startsWith("java.")) return getPlatformClassLoader().loadClass(name);

        if (allowedParentPackages.stream().anyMatch(name::startsWith)) return parent.loadClass(name);


        if (Consts.DISABLE_ISOLATION) {
            Logger.logO("[WARNING] Loading", name, "with parent classloader");
            return parent.loadClass(name);
        }
        throw new ClassNotFoundException(name);
    }

    @Override
    public Class<?> loadClassFromParent(String name, boolean resolve) throws ClassNotFoundException {
        return parent.loadClass(name, resolve);
    }

    // parent passthroughs

    @Override
    public URL getResource(String name) {
        return parent.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return parent.getResources(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return parent.getResourceAsStream(name);
    }

    @Override
    public void addURL(URL url) {
        parent.addURL(url);
    }
}

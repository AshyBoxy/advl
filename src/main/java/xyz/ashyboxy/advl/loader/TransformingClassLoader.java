package xyz.ashyboxy.advl.loader;

import xyz.ashyboxy.advl.asm.ClassGen;
import xyz.ashyboxy.advl.loader.transformers.ClassTransformer;
import xyz.ashyboxy.advl.asm.Utils;
import xyz.ashyboxy.advl.loader.transformers.TransformerCheckerPredicate;

import java.net.URL;

public class TransformingClassLoader extends ClassLoader implements FunClassLoader {
    public static final String DUMMY_INSIDE_CLASS = "xyz.ashyboxy.advl.asm.Dummy";

    private static TransformingClassLoader instance;
    public static TransformingClassLoader getInstance() {
        return instance;
    }

    public TransformingClassLoader(String name, ClassLoader parent) {
        super(name, parent);
        if (instance != null) throw new IllegalStateException();
        if (!(parent instanceof FunClassLoader)) throw new AssertionError();
        instance = this;
    }

    public Class<?> getDummyInsideClass() {
        try {
            return this.loadClass(DUMMY_INSIDE_CLASS, false);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        if (Consts.LOG_EXTRA_CLASS_LOADING) Logger.debug("Trying to find class ", name);
        return super.findClass(name);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (Consts.LOG_EXTRA_CLASS_LOADING) Logger.debug("Trying to load class ", name);
//        if (name.startsWith("xyz.ashyboxy.advl.") && !name.startsWith("xyz.ashyboxy.advl.asm.parent"))
//            return tryLoadClass(name, resolve);
//        return super.loadClass(name, resolve);
        Class<?> c = null;

        if (name.startsWith("sun.")) {
            Logger.info("Trying to load sun class {} with TransformingClassLoader?", name);
        }

        if (name.startsWith("jdk.")) {
            Logger.info("Trying to load jdk class {} with TransformingClassLoader?", name);
        }

        try {
            c = super.loadClass(name, resolve);
        } catch (ClassNotFoundException ignored) {}

        try {
            if (c == null) c = tryLoadClass(name, resolve);
        } catch (ClassNotFoundException ignored) {}

        if (c == null) {
            Logger.info("Can't find {}, trying the parent classloader", name);
            c = parent().loadClassFromParent(name, resolve);
        }

        // realistically, this throw should never happen, but who knows?
        if (c == null) throw new ClassNotFoundException(name);
        return c;
    }

    private Class<?> tryLoadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> c = this.findLoadedClass(name);
            if (c == null) {
                if (Consts.LOG_CLASS_LOADING) Logger.debug("Loading class: ", name);
                byte[] b = tryGetClassBytes(name);
                c = simpleDefineClass(name, b);
            } else if (Consts.LOG_CLASS_LOADING) Logger.debug("Class already loaded: ", name);
            if (resolve) this.resolveClass(c);
            return c;
        }
    }

    public byte[] tryGetClassBytes(String name) throws ClassNotFoundException {
        return tryGetClassBytes(name, (n, t) -> true);
    }

    public byte[] tryGetClassBytes(String name, TransformerCheckerPredicate tp) throws ClassNotFoundException {
        if (name.startsWith("xyz.ashyboxy.advl.asm.gen.")) return findCustomClass(name);
//        if (name.startsWith("xyz.ashyboxy.advl.asm.transform.")) return findTransformedClass(name);
//        if (name.startsWith("xyz.ashyboxy.advl.")) return findRealClass(name);
        return findTransformedClass(name, tp);
    }

    private byte[] findCustomClass(String name) throws ClassNotFoundException {
        if (name.equals("xyz.ashyboxy.advl.asm.gen.NonExistent"))
            return ClassGen.generateExtenderDef("NonExistent", "xyz.ashyboxy.advl.asm.Utils");
        int fieldValue;
        if (name.equals("xyz.ashyboxy.advl.asm.gen.Test")) fieldValue = 69;
        else fieldValue = name.hashCode();
        byte[] b = ClassGen.generate(fieldValue, name);
        if (Consts.DO_CLASS_DUMP) Utils.dumpClass(b, Consts.outputDir, name);
        return b;
    }

    private byte[] findTransformedClass(String name, TransformerCheckerPredicate tp) throws ClassNotFoundException {
        return ClassTransformer.transformClass(name, findRealClass(name), tp);
    }

    private byte[] findRealClass(String name) throws ClassNotFoundException {
        try {
            return parent().tryFindClassFile(name);
        } catch (NoClassDefFoundError e) {
            throw new ClassNotFoundException(name, e);
        }
    }

    private Class<?> simpleDefineClass(String name, byte[] b) {
        return defineClass(name, b, 0, b.length);
    }

    private FunClassLoader parent() {
        return (FunClassLoader) super.getParent();
    }

    @Override
    public byte[] tryFindClassFile(String name) throws NoClassDefFoundError, ClassNotFoundException {
        return parent().tryFindClassFile(name);
    }

    @Override
    public void addURL(URL url) {
        parent().addURL(url);
    }
}

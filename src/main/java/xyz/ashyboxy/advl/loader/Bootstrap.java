package xyz.ashyboxy.advl.loader;

import xyz.ashyboxy.advl.asm.Utils;
import xyz.ashyboxy.advl.loader.transformers.ClassTransformer;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class Bootstrap {
    public static void main(String[] args) throws Exception {
        // TODO: get rid of the evil things please i beg
        ClassLoader cl = new TransformingClassLoader("AdvlClassLoader",
                new IsolatedClassLoader(
                        new ResourceClassLoader(new URL[0],
                                new LaxClassLoader(Bootstrap.class.getClassLoader())),
                        Consts.PARENT_CLASSES));
        Thread.currentThread().setContextClassLoader(cl);

        if (Consts.DO_CLASS_DUMP) try { Utils.deleteRecursively(Consts.outputDir); } catch (IOException e) {
            if (Files.exists(Consts.outputDir)) throw new RuntimeException("Failed to delete class dump dir");
        }

        Logger.info("Advl bootstrap finishing");

        ClassTransformer.lockTransformerProviders();

        Class<?> c = cl.loadClass("xyz.ashyboxy.advl.asm.Main");
        c.getMethod("main", String[].class).invoke(null, (Object) args);
    }
}

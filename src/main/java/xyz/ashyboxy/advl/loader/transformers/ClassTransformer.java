package xyz.ashyboxy.advl.loader.transformers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import xyz.ashyboxy.advl.loader.Logger;
import xyz.ashyboxy.advl.asm.Utils;
import xyz.ashyboxy.advl.loader.Consts;
import xyz.ashyboxy.advl.loader.mixin.MixinTransformerProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ClassTransformer {
    private static final List<TransformerProvider> TRANSFORMER_PROVIDERS = new ArrayList<>(List.of(
            new MixinTransformerProvider(),
            new TransformTransformerProvider(),
            new StringReplacerTransformerProvider()
    ));

    private static boolean locked = false;
    public static void addTransformerProvider(TransformerProvider provider) {
        TRANSFORMER_PROVIDERS.add(provider);
    }
    public static void lockTransformerProviders() {
        if (locked) throw new AssertionError();
        locked = true;
    }

    public static byte[] transformClass(String name, byte[] originalClass) {
        return transformClass(name, originalClass, (n, t) -> true);
    }

    public static byte[] transformClass(String name, byte[] originalClass, TransformerCheckerPredicate tp) {
        List<TransformerProvider> transformerProviders = getTransformerProvidersFor(name, tp);
        if (transformerProviders.isEmpty()) return originalClass;

        ClassReader cr = new ClassReader(originalClass);
        ClassWriter cw = new ClassWriter(cr, 0);
        AtomicReference<ClassVisitor> cv = new AtomicReference<>(cw);
        AtomicBoolean transformed = new AtomicBoolean(false);
        transformerProviders.forEach(t -> cv.set(t.get(name, cv.get(), () -> transformed.set(true))));

        cr.accept(cv.get(), 0);

        byte[] b = cw.toByteArray();
        if (transformed.get() && Consts.LOG_CLASS_LOADING) Logger.logO("Transformed class:", name);
        if (transformed.get() && Consts.DO_CLASS_DUMP) Utils.dumpClass(b, Consts.outputDir, name);
        return b;
    }

    private static List<TransformerProvider> getTransformerProvidersFor(String name, TransformerCheckerPredicate tp) {
        return TRANSFORMER_PROVIDERS.stream().filter(t -> t.has(name)).filter(t -> tp.test(name, t)).toList();
    }

}

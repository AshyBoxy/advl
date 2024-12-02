package xyz.ashyboxy.advl.loader.mixin;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import xyz.ashyboxy.advl.loader.Consts;
import xyz.ashyboxy.advl.loader.Logger;
import xyz.ashyboxy.advl.loader.transformers.TransformerProvider;

import java.lang.reflect.Method;
import java.util.List;

public class MixinTransformerProvider implements TransformerProvider {
    public static boolean MIXIN_READY = false;
    private static boolean INITED = false;
    private static boolean STARTED = false;

    private static final List<String> mixinDebugProperties = List.of(
            "mixin.debug.export", "mixin.debug.countInjections", "mixin.debug.strict",
            "mixin.dumpTargetOnFailure", "mixin.checks"
    );

    public static void init () {
        if (INITED) throw new IllegalStateException("Mixin is already initialized");

        System.setProperty("mixin.bootstrapService", AdvlMixinBootstrapService.class.getName());
        System.setProperty("mixin.service", AdvlMixinService.class.getName());
        System.setProperty("mixin.env.obf", "false");
        System.setProperty("mixin.env.disableRefMap", "false");
        if (Consts.MIXIN_DEBUG) mixinDebugProperties.forEach(p -> System.setProperty(p, "true"));
        MixinBootstrap.init();

        Mixins.addConfiguration("advl.mixins.json");

        // "temporary"? yeah sure
        // changing the phase should actually be done after exts are loaded
        // we need to load the exts to get their configs, soooo...
        // ^ that also means we should probably forbid loading most
        // classes until after we have all the mixin configs
        // it *should* just be able to find them, as our mixin service sends
        // getResourceAsStream to TransformerClassLoader (>IsolatedClassLoader>ResourceClassLoader)
        try {
            Method gotoPhase = MixinEnvironment.class.getDeclaredMethod("gotoPhase", MixinEnvironment.Phase.class);
            gotoPhase.setAccessible(true);
            gotoPhase.invoke(null, MixinEnvironment.Phase.INIT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        MIXIN_READY = true;
    }

    public static void start() {
        if (STARTED) throw new IllegalStateException("Mixin is already started");

        try {
            Method gotoPhase = MixinEnvironment.class.getDeclaredMethod("gotoPhase", MixinEnvironment.Phase.class);
            gotoPhase.setAccessible(true);
            gotoPhase.invoke(null, MixinEnvironment.Phase.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ClassVisitor get(String name, ClassVisitor cv, InformTransformed t) {
        return new MixinAdapter(cv, t);
    }

    @Override
    public boolean has(String name) {
        if (!MIXIN_READY) init();
        return AdvlMixinService.mixin != null;
    }

    private static class MixinAdapter extends ClassVisitor {
        private final ClassVisitor next;
        private final InformTransformed t;

        public MixinAdapter(ClassVisitor cv, InformTransformed t) {
            super(Opcodes.ASM9, new ClassNode(Opcodes.ASM9));
            next = cv;
            this.t = t;
        }

        @Override
        public void visitEnd() {
            ClassNode cn = (ClassNode) cv;

            // give to mixin
            String name = cn.name.replace("/", ".");
            boolean transformed = AdvlMixinService.mixin.transformClass(MixinEnvironment.getCurrentEnvironment(),
                    name, cn);
            if (transformed) {
                t.inform();
                if(Consts.LOG_CLASS_LOADING) Logger.logO("Mixin changed", name);
            }

            // and let the next thing in the chain go
            cn.accept(next);
        }
    }
}

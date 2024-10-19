package xyz.ashyboxy.advl.asm.parent.transformers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import xyz.ashyboxy.advl.asm.parent.Logger;
import xyz.ashyboxy.advl.asm.Utils;
import xyz.ashyboxy.advl.asm.adapters.ImplementerAdapter;
import xyz.ashyboxy.advl.asm.adapters.MethodCopierAdapter;
import xyz.ashyboxy.advl.asm.adapters.MethodRemoverAdapter;
import xyz.ashyboxy.advl.asm.adapters.StringMethodReplacerAdapter;
import xyz.ashyboxy.advl.asm.desc.MethodDesc;
import xyz.ashyboxy.advl.asm.parent.Consts;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.objectweb.asm.Opcodes.*;

public class ClassTransformer {
    private static final List<TransformerProvider> TRANSFORMER_PROVIDERS = List.of(
            new MixinTransformerProvider(),
            new TransformTransformerProvider()
//            new StringReplacerTransformerProvider()
    );

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
        if (transformed.get() && Consts.LOG_CLASS_LOADING) Logger.log("Transformed class:", name);
        if (transformed.get() && Consts.DO_CLASS_DUMP) Utils.dumpClass(b, Consts.outputDir, name);
        return b;
    }

    private static List<TransformerProvider> getTransformerProvidersFor(String name, TransformerCheckerPredicate tp) {
        return TRANSFORMER_PROVIDERS.stream().filter(t -> t.has(name)).filter(t -> tp.test(name, t)).toList();
    }

    private static class StringReplacerTransformerProvider implements TransformerProvider {
        private static final String[] PKGS = new String[] {
                "xyz.ashyboxy.advl.asm.transform.",
                "xyz.ashyboxy.Uwuifier"
        };

        @Override
        public ClassVisitor get(String name, ClassVisitor cv, InformTransformed t) {
            if (!has(name)) return cv;
            t.inform();
            return new StringMethodReplacerAdapter(cv, "*", "uwu");
        }

        @Override
        public boolean has(String name) {
            return Arrays.stream(PKGS).anyMatch(name::startsWith);
        }
    }

    private static class TransformTransformerProvider implements TransformerProvider {
        private static final String PKG = "xyz.ashyboxy.advl.asm.transform.";

        @Override
        public ClassVisitor get(String name, ClassVisitor cv, InformTransformed t) {
            t.inform();
            return new ClassTransformerVisitor(makeAdapters(cv));
        }

        @Override
        public boolean has(String name) {
            return name.startsWith(PKG);
        }

        private static ClassVisitor makeAdapters(ClassVisitor cv) {
            ClassVisitor removers = makeRemovers(cv);
            ClassVisitor copiers = makeCopiers(removers);
            ClassVisitor implementers = makeImplementers(copiers);
            return implementers;
        }
        private static ClassVisitor makeRemovers(ClassVisitor cv) {
            ClassVisitor r1 = new MethodRemoverAdapter(cv, List.of(
                    new MethodDesc("removeMe", "()Z"),
                    new MethodDesc("copyRemoveMe", "()V")
            ));
            return r1;
        }
        private static ClassVisitor makeCopiers(ClassVisitor cv) {
            ClassVisitor c1 = new MethodCopierAdapter(cv, "copyMe", "copied", "()V");
            ClassVisitor c2 = new MethodCopierAdapter(c1, "copyMe2", "copied2", "()V");
            ClassVisitor c3 = new MethodCopierAdapter(c2, "copyRemoveMe", "copiedRemoved", "()V");
            return c3;
        }
        private static ClassVisitor makeImplementers(ClassVisitor cv) {
            ClassVisitor i1 = new ImplementerAdapter(cv, "xyz/ashyboxy/advl/asm/CopiedMethods$CopiedToMethods");
            return i1;
        }

        private static class ClassTransformerVisitor extends ClassVisitor {
            private String name;
            private String[] implementing;

            protected ClassTransformerVisitor(ClassVisitor classVisitor) {
                super(ASM9, classVisitor);
            }

            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                implementing = interfaces.clone();
                this.name = name;
                super.visit(version, access, name, signature, superName, interfaces);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                if (name.equals("<clinit>") && Arrays.asList(implementing).contains("xyz/ashyboxy/advl/asm/FieldHaver"))
                    mv = new FieldHaverVisitor(mv);
                return mv;
            }

            private class FieldHaverVisitor extends MethodVisitor {
                protected FieldHaverVisitor(MethodVisitor mv) {
                    super(ASM9, mv);
                }

                @Override
                public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                    if (opcode == PUTSTATIC && owner.equals(ClassTransformerVisitor.this.name) && name.equals("field") && descriptor.equals("I")) {
                        super.visitInsn(POP);
                        super.visitIntInsn(BIPUSH, 42);
                    }
                    super.visitFieldInsn(opcode, owner, name, descriptor);
                }
            }
        }
    }
}

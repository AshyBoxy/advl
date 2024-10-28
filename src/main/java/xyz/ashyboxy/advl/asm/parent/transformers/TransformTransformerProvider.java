package xyz.ashyboxy.advl.asm.parent.transformers;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import xyz.ashyboxy.advl.asm.adapters.ImplementerAdapter;
import xyz.ashyboxy.advl.asm.adapters.MethodCopierAdapter;
import xyz.ashyboxy.advl.asm.adapters.MethodRemoverAdapter;
import xyz.ashyboxy.advl.asm.desc.MethodDesc;

import java.util.Arrays;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

class TransformTransformerProvider implements TransformerProvider {
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
                mv = new ClassTransformerVisitor.FieldHaverVisitor(mv);
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

package xyz.ashyboxy.advl.asm.adapters;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;

public class StringMethodReplacerAdapter extends ClassVisitor {
    private final String name;
    private final String replacement;
    private final STATIC_REPLACE staticReplace;

    public StringMethodReplacerAdapter(ClassVisitor cv, String name, String replacement) {
        this(cv, name, replacement, STATIC_REPLACE.ANY);
    }

    public StringMethodReplacerAdapter(ClassVisitor cv, String name, String replacement, STATIC_REPLACE staticReplace) {
        super(Opcodes.ASM9, cv);
        this.name = name;
        this.replacement = replacement;
        this.staticReplace = staticReplace;
    }

    public enum STATIC_REPLACE {
        INSTANCE_ONLY,
        STATIC_ONLY,
        ANY
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if ((name.equals(this.name) || this.name.equals("*")) && descriptor.endsWith(")Ljava/lang/String;") && shouldReplace(access,
                staticReplace)) return
                new StringMethodReplacerVisitor(mv,
                        Arrays.stream(Type.getArgumentTypes(descriptor)).map(Type::getSize).reduce(Integer::sum).orElse(0),
                        (access & Opcodes.ACC_STATIC) > 0
                );
        return mv;
    }

    private static boolean shouldReplace(int access, STATIC_REPLACE staticReplace) {
        return switch (staticReplace) {
            case INSTANCE_ONLY -> (access & Opcodes.ACC_STATIC) == 0;
            case STATIC_ONLY -> (access & Opcodes.ACC_STATIC) > 0;
            default -> true;
        };
    }

    private class StringMethodReplacerVisitor extends MethodVisitor {
        private final MethodVisitor delegate;
        private final int paramCount;
        private final boolean _static;

        protected StringMethodReplacerVisitor(MethodVisitor delegate, int paramCount, boolean _static) {
            super(Opcodes.ASM9);
            this.delegate = delegate;
            this.paramCount = paramCount;
            this._static = _static;
        }

        @Override
        public void visitCode() {
            delegate.visitCode();
            delegate.visitLdcInsn(replacement);
            delegate.visitInsn(Opcodes.ARETURN);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            delegate.visitMaxs(1, paramCount + (_static ? 0 : 1));
        }

        @Override
        public void visitEnd() {
            delegate.visitEnd();
        }
    }
}

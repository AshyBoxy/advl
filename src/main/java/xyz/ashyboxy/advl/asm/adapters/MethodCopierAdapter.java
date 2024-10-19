package xyz.ashyboxy.advl.asm.adapters;

import org.objectweb.asm.*;

public class MethodCopierAdapter extends ClassVisitor {
    private final String from;
    private final String to;
    private final String desc;

    public MethodCopierAdapter(ClassVisitor cv, String from, String to, String desc) {
        super(Opcodes.ASM9, cv);
        this.from = from;
        this.to = to;
        this.desc = desc;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (name.equals(from) && descriptor.equals(desc))
            return new MethodCopierVisitor(super.visitMethod(access, name, descriptor, signature, exceptions), super.visitMethod(access, to, descriptor, signature, exceptions));
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    /**
     * terrible
     */
    private class MethodCopierVisitor extends MethodVisitor {
        private final MethodVisitor copy;

        private MethodCopierVisitor(MethodVisitor orig, MethodVisitor copy) {
            super(Opcodes.ASM9, orig);
            this.copy = copy;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            copy.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
            copy.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
            super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            copy.visitJumpInsn(opcode, label);
            super.visitJumpInsn(opcode, label);
        }

        @Override
        public void visitLdcInsn(Object value) {
            copy.visitLdcInsn(value);
            super.visitLdcInsn(value);
        }

        @Override
        public void visitIincInsn(int varIndex, int increment) {
            copy.visitIincInsn(varIndex, increment);
            super.visitIincInsn(varIndex, increment);
        }

        @Override
        public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
            copy.visitTableSwitchInsn(min, max, dflt, labels);
            super.visitTableSwitchInsn(min, max, dflt, labels);
        }

        @Override
        public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
            copy.visitLookupSwitchInsn(dflt, keys, labels);
            super.visitLookupSwitchInsn(dflt, keys, labels);
        }

        @Override
        public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
            copy.visitMultiANewArrayInsn(descriptor, numDimensions);
            super.visitMultiANewArrayInsn(descriptor, numDimensions);
        }

        @Override
        public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
            copy.visitTryCatchBlock(start, end, handler, type);
            super.visitTryCatchBlock(start, end, handler, type);
        }

        @Override
        public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
            copy.visitLocalVariable(name, descriptor, signature, start, end, index);
            super.visitLocalVariable(name, descriptor, signature, start, end, index);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            copy.visitMaxs(maxStack, maxLocals);
            super.visitMaxs(maxStack, maxLocals);
        }

        @Override
        public void visitEnd() {
            copy.visitEnd();
            super.visitEnd();
        }

        @Override
        public void visitParameter(String name, int access) {
            copy.visitParameter(name, access);
            super.visitParameter(name, access);
        }

        @Override
        public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
            copy.visitAnnotableParameterCount(parameterCount, visible);
            super.visitAnnotableParameterCount(parameterCount, visible);
        }

        @Override
        public void visitAttribute(Attribute attribute) {
            copy.visitAttribute(attribute);
            super.visitAttribute(attribute);
        }

        @Override
        public void visitCode() {
            copy.visitCode();
            super.visitCode();
        }

        @Override
        public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
            copy.visitFrame(type, numLocal, local, numStack, stack);
            super.visitFrame(type, numLocal, local, numStack, stack);
        }

        @Override
        public void visitInsn(int opcode) {
            copy.visitInsn(opcode);
            super.visitInsn(opcode);
        }

        @Override
        public void visitIntInsn(int opcode, int operand) {
            copy.visitIntInsn(opcode, operand);
            super.visitIntInsn(opcode, operand);
        }

        @Override
        public void visitVarInsn(int opcode, int varIndex) {
            copy.visitVarInsn(opcode, varIndex);
            super.visitVarInsn(opcode, varIndex);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            copy.visitTypeInsn(opcode, type);
            super.visitTypeInsn(opcode, type);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            copy.visitFieldInsn(opcode, owner, name, descriptor);
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }

        // TODO: annotations
        @Override
        public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            return super.visitInsnAnnotation(typeRef, typePath, descriptor, visible);
        }

        @Override
        public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            return super.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible);
        }

        @Override
        public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
            return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible);
        }

        @Override
        public AnnotationVisitor visitAnnotationDefault() {
            return super.visitAnnotationDefault();
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            return super.visitAnnotation(descriptor, visible);
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
            return super.visitParameterAnnotation(parameter, descriptor, visible);
        }

        // just debug stuff
        @Override
        public void visitLabel(Label label) {}

        @Override
        public void visitLineNumber(int line, Label start) {}
    }
}

package xyz.ashyboxy.advl.asm;

import org.objectweb.asm.*;
import xyz.ashyboxy.advl.asm.parent.Consts;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.objectweb.asm.Opcodes.*;

public class TestMain {
    public static final Path baseClass = Consts.baseDir.resolve("thing.class");
    private static final boolean doTwo = false;

    public static void main(String[] args) throws Exception {
        byte[] b = Files.readAllBytes(baseClass);
        ClassReader cr = new ClassReader(b);
        ClassWriter cw = new ClassWriter(0);
        Visitor cv = new Visitor(cw);
        cr.accept(cv, 0);
        Files.write(Consts.baseDir.resolve(!doTwo ? "thing1.class" : "thing2.class"), cw.toByteArray());
    }

    private static class Visitor extends ClassVisitor {
        private boolean did = false;
        public Visitor(ClassVisitor cv) {
            super(ASM9, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            if (did) return super.visitMethod(access, name, descriptor, signature, exceptions);
            MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "doThing", "()V", null, null);
            did = true;


            mv.visitCode();

            Label after = new Label();
            Label two = new Label();

            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "thing", "getValue", "()Ljava/lang/Object;", false);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitVarInsn(ALOAD, 1);

            // diff start
            if (!doTwo) {
                mv.visitJumpInsn(IFNONNULL, after);
            } else {
                mv.visitTypeInsn(INSTANCEOF, "java/lang/Object");
                mv.visitJumpInsn(IFEQ, two);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitJumpInsn(GOTO, after);
                mv.visitLabel(two);
            }

            // diff end
            mv.visitInsn(RETURN);
            mv.visitLabel(after);
            mv.visitMethodInsn(INVOKESTATIC, "thing", "nop", "()V", false);
            mv.visitInsn(RETURN);

            mv.visitMaxs(2, 2);
            mv.visitEnd();

            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }
}

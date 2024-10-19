package xyz.ashyboxy.advl.asm;

import org.objectweb.asm.ClassWriter;

import static org.objectweb.asm.Opcodes.*;

public class ClassGen {
    public static final String BASE_PKG_NAME = "xyz/ashyboxy/advl/asm/gen/";

    public static byte[] generateDef(int fieldValue, String name) {
        return generate(fieldValue, BASE_PKG_NAME + name);
    }

    public static byte[] generate(int fieldValue, String name) {
        ClassWriter cw = new ClassWriter(0);
        cw.visit(V21, ACC_PUBLIC | ACC_FINAL, toCanonical(name), null, "java/lang/Object", null);
        cw.visitField(ACC_PUBLIC | ACC_STATIC | ACC_FINAL, "field", "I", null, fieldValue);

        addSig(cw);

        cw.visitEnd();
        return cw.toByteArray();
    }

    public static byte[] generateExtenderDef(String name, String extending) {return generateExtender(BASE_PKG_NAME + name, extending);}

    public static byte[] generateExtender(String name, String extending) {
        ClassWriter cw = new ClassWriter(0);
        cw.visit(V21, ACC_PUBLIC, toCanonical(name), null, toCanonical(extending), null);
        addSig(cw);
        cw.visitEnd();
        return cw.toByteArray();
    }

    private static String toCanonical(String name) {
        return name.replace(".", "/");
    }

    private static void addSig(ClassWriter cw) {
        cw.visitField(ACC_PRIVATE | ACC_STATIC | ACC_FINAL, "sig", "Ljava/lang/String;", null, "uwu :3");
    }
}

package xyz.ashyboxy.advl.asm;

import org.objectweb.asm.*;
import xyz.ashyboxy.advl.asm.parent.Logger;

import java.lang.reflect.ClassFileFormatVersion;

public class ClassPrinter extends ClassVisitor {
    protected ClassPrinter() {
        super(Opcodes.ASM9);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        String _implements = String.join(", ", interfaces);
        if (!_implements.isEmpty()) _implements = "implements " + _implements;
        Logger.log(Utils.accessString(access), "class", name, "extends", superName,
                !_implements.isEmpty() ? _implements : "" +
                "(" + ClassFileFormatVersion.fromMajor(version & 0x00FF).name() + ")",
                "{");
    }

    @Override
    public void visitSource(String source, String debug) {
    }

    @Override
    public ModuleVisitor visitModule(String name, int access, String version) {
        return null;
    }

    @Override
    public void visitNestHost(String nestHost) {
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return null;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return null;
    }

    @Override
    public void visitAttribute(Attribute attribute) {
    }

    @Override
    public void visitNestMember(String nestMember) {
    }

    @Override
    public void visitPermittedSubclass(String permittedSubclass) {
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        Logger.log("    " + Utils.accessString(access), "class", name);
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
        return null;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        Logger.log("    " + Utils.accessString(access), descriptor, name);
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        Logger.log("    " + Utils.accessString(access), name, descriptor);
        return null;
    }

    @Override
    public void visitEnd() {
        Logger.log("}");
    }
}

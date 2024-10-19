package xyz.ashyboxy.advl.asm.adapters;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import xyz.ashyboxy.advl.asm.desc.MethodDesc;

import java.util.List;

public class MethodRemoverAdapter extends ClassVisitor {
    private final List<MethodDesc> methods;

    public MethodRemoverAdapter(ClassVisitor cv, List<MethodDesc> methods) {
        super(Opcodes.ASM9, cv);
        this.methods = methods;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (methods.stream().anyMatch(m -> m.name().equals(name) && m.descriptor().equals(descriptor))) return null;
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }
}

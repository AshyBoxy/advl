package xyz.ashyboxy.advl.asm.adapters;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.HashSet;

public class ImplementerAdapter extends ClassVisitor {
    private final String[] addInterfaces;

    public ImplementerAdapter(ClassVisitor cv, String... addInterfaces) {
        super(Opcodes.ASM9, cv);
        this.addInterfaces = addInterfaces;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        HashSet<String> i = new HashSet<>(Arrays.asList(interfaces));
        i.addAll(Arrays.asList(addInterfaces));
        super.visit(version, access, name, signature, superName, i.toArray(new String[0]));
    }
}

package xyz.ashyboxy.advl.asm.adapters;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public class ClassNodeAdapter extends ClassNode {
    public ClassNodeAdapter(ClassVisitor cv) {
        super(Opcodes.ASM9);
        this.cv = cv;
    }

    @Override
    public void visitEnd() {
        accept(cv);
    }
}

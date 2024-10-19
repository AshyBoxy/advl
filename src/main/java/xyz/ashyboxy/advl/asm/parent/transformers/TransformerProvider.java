package xyz.ashyboxy.advl.asm.parent.transformers;

import org.objectweb.asm.ClassVisitor;

import java.util.function.Supplier;

public interface TransformerProvider {
    ClassVisitor get(String name, ClassVisitor cv, InformTransformed informTransformed);

    boolean has(String name);

    @FunctionalInterface
    interface InformTransformed {
        /**
         * Called to inform ClassTransformer that the TransformerProvider actually did something
         */
        void inform();
    }
}

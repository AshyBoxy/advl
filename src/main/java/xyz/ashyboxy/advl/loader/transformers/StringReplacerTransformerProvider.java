package xyz.ashyboxy.advl.loader.transformers;

import org.objectweb.asm.ClassVisitor;
import xyz.ashyboxy.advl.loader.adapters.StringMethodReplacerAdapter;

import java.util.Arrays;

class StringReplacerTransformerProvider implements TransformerProvider {
    private static final String[] PKGS = new String[]{
            "xyz.ashyboxy.advl.asm.transform.",
            "xyz.ashyboxy.Uwuifier"
    };

    @Override
    public ClassVisitor get(String name, ClassVisitor cv, InformTransformed t) {
        if (!has(name)) return cv;
        t.inform();
        return new StringMethodReplacerAdapter(cv, "*", "uwu");
    }

    @Override
    public boolean has(String name) {
        return Arrays.stream(PKGS).anyMatch(name::startsWith);
    }
}

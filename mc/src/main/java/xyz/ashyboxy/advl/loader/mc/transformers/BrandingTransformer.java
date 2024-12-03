package xyz.ashyboxy.advl.loader.mc.transformers;

import org.objectweb.asm.ClassVisitor;
import xyz.ashyboxy.advl.loader.adapters.StringMethodReplacerAdapter;
import xyz.ashyboxy.advl.loader.transformers.TransformerProvider;

public class BrandingTransformer implements TransformerProvider {
    @Override
    public ClassVisitor get(String name, ClassVisitor cv, InformTransformed informTransformed) {
        if (!has(name)) return cv;
        informTransformed.inform();
        return new StringMethodReplacerAdapter(cv, "getClientModName", "advl");
    }

    @Override
    public boolean has(String name) {
        return name.equals("net.minecraft.client.ClientBrandRetriever");
    }
}

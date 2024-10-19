package xyz.ashyboxy.advl.asm.parent.transformers;

@FunctionalInterface
public interface TransformerCheckerPredicate {
    boolean test(String name, TransformerProvider tp);
}

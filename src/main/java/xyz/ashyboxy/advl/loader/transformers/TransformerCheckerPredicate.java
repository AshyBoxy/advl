package xyz.ashyboxy.advl.loader.transformers;

@FunctionalInterface
public interface TransformerCheckerPredicate {
    boolean test(String name, TransformerProvider tp);
}

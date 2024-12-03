package xyz.ashyboxy.advl.loader;

// sorry
public class Potato {
    public static boolean run = false;
    public static String nextClass;
    public static String[] nextArgs;
    public static PotatoCallback callback;

    @FunctionalInterface
    public interface PotatoCallback {
        void callback();
    }
}

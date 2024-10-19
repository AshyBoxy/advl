package xyz.ashyboxy.advl.asm.parent;

public class Logger {
    public static void log(String message) {
        System.out.println(message);
    }

    public static void log(String... message) {
        log(String.join(" ", message));
    }
}

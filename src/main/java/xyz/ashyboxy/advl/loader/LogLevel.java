package xyz.ashyboxy.advl.loader;

public record LogLevel(String name, int value) {
    public static LogLevel INFO = new LogLevel("INFO");
    public static LogLevel ERROR = new LogLevel("ERROR");
    public static LogLevel WARN = new LogLevel("WARN");
    public static LogLevel DEBUG = new LogLevel("DEBUG");

    public LogLevel(String name) {
        this(name, 0);
    }
}

package xyz.ashyboxy.advl.loader;

public class Logger {
    private static String join(String... message) {
        if (message.length == 0) return "";
        if (message.length == 1) return message[0];

//        StringBuilder sb = new StringBuilder();
//        String[] parts = message[0].split("\\{}");
//
//        int i = 0;
//        while (i < parts.length) {
//            sb.append(parts[i]);
////            if (message.length > i + 1) sb.append(message[i + 1]);
////            else
//                sb.append("{}");
//            i++;
//        }

//        while (i < message.length - 1) {
//            sb.append(message[i + 1]);
//            i++;
//        }

//        return sb.toString();
        return String.join("", message);
    }

    public static void log(String message) {
        System.out.println(message);
    }

    @Deprecated
    public static void logO(String... message) {
        if (message.length == 0) return;
        log(String.join(" ", message));
    }

    public static void log(String... message) {
        if (message.length == 0) return;
        log(join(message));
    }

    public static void log(LogLevel level, String message) {
        log(String.format("[%s] %s", level.name(), message));
    }

    public static void log(LogLevel level, String... message) {
        if (message.length == 0) return;
        log(level, join(message));
    }

    public static void info(String... message) {
        log(LogLevel.INFO, message);
    }

    public static void warn(String message) {
        log(LogLevel.WARN, message);
    }

    public static void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public static void debug(String... message) {
        log(LogLevel.DEBUG, message);
    }
}

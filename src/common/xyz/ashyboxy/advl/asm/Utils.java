package xyz.ashyboxy.advl.asm;

import org.objectweb.asm.Opcodes;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {
    public static String accessString(int access) {
        ArrayList<String> accessString = new ArrayList<>();
        AtomicInteger a = new AtomicInteger(access);

        if (mutateCheckOpcode(Opcodes.ACC_PUBLIC, a)) accessString.add("public");
        if (mutateCheckOpcode(Opcodes.ACC_PRIVATE, a)) accessString.add("private");
        if (mutateCheckOpcode(Opcodes.ACC_STATIC, a)) accessString.add("static");
        if (mutateCheckOpcode(Opcodes.ACC_FINAL, a)) accessString.add("final");

        if (accessString.isEmpty()) return hexString(access);
        if (a.get() > 0) return hexString(a.get()) + " " + String.join(" ", accessString);
        return String.join(" ", accessString);
    }

    public static boolean mutateCheckOpcode(int checkOp, AtomicInteger op) {
        int o = op.get();
        op.set(o & ~checkOp);
        return (o & checkOp) > 0;
    }

    public static String hexString(int num) {
        return (num < 0 ? "-" : "") + "0x" + Integer.toString(Math.abs(num), 16);
    }

    public static void deleteRecursively(Path dir) throws IOException {
        if (!dir.toFile().exists()) throw new FileNotFoundException();
        if (Files.isSymbolicLink(dir)) {
            dir.toFile().delete();
            return;
        }
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.map(Path::toFile).sorted(Comparator.reverseOrder()).forEach(File::delete);
        }
    }

    public static String resourceURLReadTextFile(URL url) throws IOException {
        if (url == null) return null;
        try (InputStream is = url.openStream()) {
            if (is == null) return null;
            try (InputStreamReader isr = new InputStreamReader(is); BufferedReader br = new BufferedReader(isr)) {
                return br.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }

    public static void dumpClass(byte[] b, Path dir, String name) {
        Path p = dir.resolve(name.replace(".", "/") + ".class");
        p.resolve("..").toFile().mkdirs();
        try {
            Files.write(p, b);
        } catch (IOException ignored) {}
    }
}

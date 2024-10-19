package xyz.ashyboxy.advl.jar;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class JarHelper {
    // TODO: recursion
    public static List<File> findJars(File d) throws IOException {
        if (!d.isDirectory()) throw new IOException();

        File[] files = d.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null) throw new IOException();

        Stream<File> f = Arrays.stream(files);
        f = f.filter(File::isFile);

        return f.toList();
    }
}

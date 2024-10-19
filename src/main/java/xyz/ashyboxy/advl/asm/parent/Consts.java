package xyz.ashyboxy.advl.asm.parent;

import java.nio.file.Path;
import java.util.List;

public class Consts {
    public static final Path baseDir = Path.of(System.getProperty("user.dir")).toAbsolutePath();
    public static final Path outputDir = baseDir.resolve("gen");
    public static final Path jarDir = baseDir.resolve("jars");

    public static final boolean MIXIN_DEBUG = true;

    // Log flags
    public static final boolean LOG_CLASS_LOADING = true;
    public static final boolean LOG_RESOURCE_ADD = true;

    // ClassLoader things
    protected static final List<String> PARENT_CLASSES = List.of(
            "xyz.ashyboxy.advl.asm.parent.",
            "org.objectweb.asm.",
            "org.spongepowered.asm.",
            // mixin dependencies
            "com.google.gson.",
            "com.google.common.",
            "com.google.thirdparty.publicsuffix."
    );
    public static final boolean DISABLE_ISOLATION = false;
    // CustomClassLoader
    public static final boolean DO_CLASS_DUMP = true;

    // Main
    public static final boolean DO_CLASS_READER = true;
    public static final boolean DO_CLASS_WRITER_SAVE = false;
    public static final boolean DO_GEN_USER_TEST = true;
    public static final boolean DO_TRANSFORM_TEST = true;
    public static final boolean DO_ISOLATION_TEST = true;
    public static final boolean DO_JARS = true;
}

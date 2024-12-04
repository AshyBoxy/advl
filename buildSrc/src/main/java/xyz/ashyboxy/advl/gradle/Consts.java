package xyz.ashyboxy.advl.gradle;

import java.nio.file.Path;

public class Consts {
    // various urls
    public static final String MOJANG_LIBRARIES = "https://libraries.minecraft.net/";
    public static final String ASSETS_URL = "https://resources.download.minecraft.net/";
    public static final String TMP_VERSION_JSON_URL = "https://piston-meta.mojang.com/v1/packages/4298ca546ceb8a7ba52b7e154bc0df4d952b8dbf/1.21.1.json";

    // file paths
    public static final Path BASE_PATH = Path.of(".gradle/advl");
    public static final Path VERSION_JSON_PATH = Path.of("version.json");
    public static final Path CLIENT_JAR_PATH = Path.of("client.jar");
    public static final Path CLIENT_REMAPPED_JAR_PATH = Path.of("client-remapped.jar");
    public static final Path CLIENT_SOURCE_JAR_PATH = Path.of("client-remapped-sources.jar");
    public static final Path CLIENT_MAPPINGS_PATH = Path.of("client-mappings.txt");
    public static final Path ASSETS_DIR = Path.of("assets");

    // other stuff
    public static final String TASK_GROUP = "advl";
}

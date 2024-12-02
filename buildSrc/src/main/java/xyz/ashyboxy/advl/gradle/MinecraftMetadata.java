package xyz.ashyboxy.advl.gradle;

import org.gradle.api.Project;
import org.gradle.api.artifacts.ModuleDependency;
import xyz.ashyboxy.advl.gradle.mc.Version;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;

public class MinecraftMetadata {
    public static final String TMP_URL = "https://piston-meta.mojang.com/v1/packages/4298ca546ceb8a7ba52b7e154bc0df4d952b8dbf/1.21.1.json";
    public static File jsonPath;
    public static File jarPath;

    public static void init(Project project) {
        jsonPath = project.file(".gradle/advl/version.json");
        jarPath = project.file(".gradle/advl/client.jar");
    }

    public static void download() {
        try {
            Version version = AdvlGradlePlugin.gson.fromJson(Downloader.tryDownloadText(jsonPath,
                    new URI(TMP_URL).toURL()), Version.class);
            Version.Download download = version.downloads().get("client");
            if (download == null) throw new AssertionError();
            Downloader.tryDownload(jarPath, download.url());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void apply(Project project) {
        project.getDependencies().add("implementation", project.files(jarPath));

        try {
            Version version = AdvlGradlePlugin.gson.fromJson(Files.readString(jsonPath.toPath()), Version.class);

            // not actually tested on anything other than linux
//            String _currentOS = System.getProperty("os.path").toLowerCase();
            String _currentOS = "linux";
            if (_currentOS.contains("windows")) _currentOS = "windows";
            else if (_currentOS.contains("linux")) _currentOS = "linux";
            else if (_currentOS.contains("mac")) _currentOS = "osx";
            else throw new AssertionError();

            final String currentOS = _currentOS;

            version.libraries().forEach((library -> {
                // ignoring natives for now, since we're not using < 1.19.3
                if (!library.allowedForOS(currentOS)) return;

                var dependency = project.getDependencies().add("implementation", library.name());
                // thanks loom
                if (dependency instanceof ModuleDependency md) md.setTransitive(false);

                // and then natives...
            }));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

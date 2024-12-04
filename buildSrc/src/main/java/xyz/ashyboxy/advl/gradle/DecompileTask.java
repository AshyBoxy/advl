package xyz.ashyboxy.advl.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.decompiler.SingleFileSaver;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;

import java.util.Map;

public class DecompileTask extends DefaultTask {
    public DecompileTask() {
        super();
        setGroup(Consts.TASK_GROUP);
    }

    @TaskAction
    public void run() {
        getLogger().lifecycle("Decompiling {} to {}", MinecraftFiles.remappedJarPath, MinecraftFiles.remappedJarPath);

        Map<String, Object> prefs = Map.of(
                IFernflowerPreferences.DECOMPILE_GENERIC_SIGNATURES, "1",
                IFernflowerPreferences.BYTECODE_SOURCE_MAPPING, "1",
                IFernflowerPreferences.REMOVE_SYNTHETIC, "1",
                IFernflowerPreferences.LOG_LEVEL, "info",
                IFernflowerPreferences.THREADS, String.valueOf(Runtime.getRuntime().availableProcessors()),
                IFernflowerPreferences.INDENT_STRING, "    "
        );

        Fernflower vf = new Fernflower(new SingleFileSaver(MinecraftFiles.sourceJarPath), prefs, new VineflowerLogger());

        vf.addSource(MinecraftFiles.remappedJarPath);
        vf.decompileContext();
        vf.clearContext();
    }

//    private static class VineflowerSaver implements IResultSaver {
//        @Override
//        public void createArchive(String path, String archiveName, Manifest manifest) {}
//
//        @Override
//        public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {}
//
//        @Override
//        public void closeArchive(String path, String archiveName) {}
//
//
//        @Override
//        public void saveFolder(String path) {}
//
//        @Override
//        public void copyFile(String source, String path, String entryName) {}
//
//        @Override
//        public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {}
//
//        @Override
//        public void saveDirEntry(String path, String archiveName, String entryName) {}
//
//        @Override
//        public void copyEntry(String source, String path, String archiveName, String entry) {}
//    }

    private class VineflowerLogger extends IFernflowerLogger {
        @Override
        public void writeMessage(String message, Severity severity) {
            if (severity.ordinal() < Severity.ERROR.ordinal()) return;

            System.err.println(message);
        }

        @Override
        public void writeMessage(String message, Severity severity, Throwable t) {
            if (severity.ordinal() < Severity.ERROR.ordinal()) return;

            writeMessage(message, severity);
            t.printStackTrace(System.err);
        }

        @Override
        public void startClass(String className) {
            getLogger().lifecycle("Decompiling {}", className);
        }
    }
}

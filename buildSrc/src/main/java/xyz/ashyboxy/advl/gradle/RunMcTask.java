package xyz.ashyboxy.advl.gradle;

import org.gradle.api.tasks.JavaExec;

public class RunMcTask extends JavaExec {
    public RunMcTask() {
        super();
        setGroup(Consts.TASK_GROUP);

        getMainClass().set("xyz.ashyboxy.advl.loader.mc.Bootstrap");
        args(
                "--assetIndex", MinecraftAssets.indexName,
                "--assetsDir", MinecraftFiles.assetsDir.getAbsolutePath()
        );
    }
}

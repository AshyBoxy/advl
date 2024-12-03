package xyz.ashyboxy.advl.loader.mc;

import xyz.ashyboxy.advl.loader.Consts;
import xyz.ashyboxy.advl.loader.Logger;
import xyz.ashyboxy.advl.loader.Potato;
import xyz.ashyboxy.advl.loader.mc.transformers.BrandingTransformer;
import xyz.ashyboxy.advl.loader.transformers.ClassTransformer;

public class Bootstrap {
    public static void main(String[] args) throws Exception {
        Logger.info("Hello from mc bootstrap");
        System.setProperty("minecraft.launcher.brand", "advl");
        System.setProperty("minecraft.launcher.version", "0.0.0");
        Potato.nextClass = "net.minecraft.client.main.Main";
        Potato.nextArgs = new String[]{
            "--accessToken", "",
            "--version", "1.21.1",
            "--versionType", "release",
            "--gameDir", Consts.baseDir.resolve("mcRun").toAbsolutePath().toString()
        };

        ClassTransformer.addTransformerProvider(new BrandingTransformer());

        xyz.ashyboxy.advl.loader.Bootstrap.main(args);
    }
}

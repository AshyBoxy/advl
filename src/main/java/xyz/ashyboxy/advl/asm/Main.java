package xyz.ashyboxy.advl.asm;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.spongepowered.asm.mixin.Mixins;
import xyz.ashyboxy.advl.asm.transform.DummyFieldHaver;
import xyz.ashyboxy.advl.jar.JarHelper;
import xyz.ashyboxy.advl.loader.Logger;
import xyz.ashyboxy.advl.loader.Potato;
import xyz.ashyboxy.advl.loader.TransformingClassLoader;
import xyz.ashyboxy.advl.loader.mixin.MixinTransformerProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static xyz.ashyboxy.advl.loader.Consts.*;

public class Main {
    private static final String LOG_BREAK = "\n----------";

    public static void main(@Nullable String[] args) throws Exception {
        Logger.logO("Running with classloader", Main.class.getClassLoader().getClass().getName());

        TransformingClassLoader cl;
        if (!DISABLE_ISOLATION) {
            cl = (TransformingClassLoader) Main.class.getClassLoader();
        }
        outputDir.toFile().mkdirs();

        if (DO_CLASS_READER) {
            Logger.log(LOG_BREAK);
            Logger.log("Trying ClassReader");
            ClassPrinter cp = new ClassPrinter();
//            ClassReader cr = new ClassReader("xyz/ashyboxy/advl/TestClass");
            ClassReader cr = new ClassReader(cl.tryGetClassBytes("xyz.ashyboxy.advl.asm.gen.NonExistent"));
            cr.accept(cp, 0);
        }

        if (DO_CLASS_WRITER_SAVE) {
            Logger.log(LOG_BREAK);
            Logger.log("Trying ClassGen");
            byte[] b = ClassGen.generateDef(69, "Test");
            Files.write(outputDir.resolve("Test.class"), b);
            Files.write(outputDir.resolve("NonExistent.class"), ClassGen.generateExtenderDef("NonExistent", Utils.class.getName()));
        }

        if (DO_GEN_USER_TEST) {
            Logger.log(LOG_BREAK);
            Logger.log("Trying GenUser");
            GenUser.test();
        }

        if (DO_TRANSFORM_TEST) {
            Logger.log(LOG_BREAK);
            Logger.log("Checking for transformations");
            Logger.logO("DummyFieldHaver#getField():", Integer.toString(new DummyFieldHaver().getField()));
            CopiedMethods.CopiedToMethods dummy = (CopiedMethods.CopiedToMethods) new DummyFieldHaver();
            Logger.log("Calling DummyFieldHaver#copyMe()");
            dummy.copyMe();
            Logger.log("Calling DummyFieldHaver#copied()");
            dummy.copied();
        }

        if (DO_JARS) {
            Logger.log(LOG_BREAK);

            File jf = jarDir.toFile();
            jf.mkdirs();
            Logger.info("Looking for jars in ", jf.toString());

            List<File> jars = JarHelper.findJars(jf);
            Logger.log("Found jars:");
            for (File jar : jars) {
                Logger.logO("   ", jar.getName());
                cl.addURL(jar.toURI().toURL());

                try (ZipFile file = new ZipFile(jar)) {
                    ZipEntry entry = file.getEntry("advl.mixin.conf");
                    if (entry == null) continue;
                    String mixinPath = new BufferedReader(new InputStreamReader(file.getInputStream(entry))).readLine();
                    if (mixinPath.isEmpty()) continue;
                    Logger.logO("Adding mixin configuration", mixinPath);
                    Mixins.addConfiguration(mixinPath);
                }
            }

            MixinTransformerProvider.start();

            Logger.log("Trying to load woah.txt (from primary.jar)");
            URL woahURL = cl.getResource("woah.txt");
            if (woahURL != null) {
                Logger.logO("woah.txt is at", woahURL.toString(), ":");
                Stream<String> woah = Utils.resourceURLReadTextFile(woahURL).lines();
                woah.forEach(s -> Logger.logO("    -", s));
            } else Logger.log("Could not find woah.txt");

            Logger.log("Trying to run ExtTest.test() reflectively");
            Class.forName("xyz.ashyboxy.advl.ext.primary.ExtTest")
                    .getMethod("test").invoke(null);

        }

        if (DO_ISOLATION_TEST) {
            Logger.log(LOG_BREAK);
            Logger.log("Checking ClassLoader load order and transformations (and mixins)");
            Logger.log("Uwuifier.uwuify should've been replaced with a method that just returns \"uwu\"");
            Logger.log("And then a mixin should've been injected at its head:");
            IsolatedTest.run();
        }

        if (Potato.run) {
            Logger.log(LOG_BREAK);
            Logger.info("Calling next class: ", Potato.nextClass);
            Logger.info("With arguments: ", Arrays.toString(Potato.nextArgs));

            // this causes a ton of lag with minecraft (duh)
            LOG_CLASS_LOADING = false;
//            LOG_EXTRA_CLASS_LOADING = true;

            Class<?> c = Main.class.getClassLoader().loadClass(Potato.nextClass);
            c.getMethod("main", String[].class).invoke(null, (Object) Potato.nextArgs);
        }
    }
}

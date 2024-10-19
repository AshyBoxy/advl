package xyz.ashyboxy.advl.asm;

import org.objectweb.asm.ClassReader;
import xyz.ashyboxy.advl.asm.parent.Logger;
import xyz.ashyboxy.advl.asm.parent.TransformingClassLoader;
import xyz.ashyboxy.advl.asm.transform.DummyFieldHaver;
import xyz.ashyboxy.advl.jar.JarHelper;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;

import static xyz.ashyboxy.advl.asm.parent.Consts.*;

public class Main {
    private static final String LOG_BREAK = "\n----------";

    public static void main(String[] args) throws Exception {
        Logger.log("Running with classloader", Main.class.getClassLoader().getClass().getName());

        TransformingClassLoader cl = (TransformingClassLoader) Main.class.getClassLoader();
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
            Logger.log("DummyFieldHaver#getField():", Integer.toString(new DummyFieldHaver().getField()));
            CopiedMethods.CopiedToMethods dummy = (CopiedMethods.CopiedToMethods) new DummyFieldHaver();
            Logger.log("Calling DummyFieldHaver#copyMe()");
            dummy.copyMe();
            Logger.log("Calling DummyFieldHaver#copied()");
            dummy.copied();
        }

        if (DO_ISOLATION_TEST) {
            Logger.log(LOG_BREAK);
            Logger.log("Checking ClassLoader load order and transformations (and mixins)");
            Logger.log("Uwuifier.uwuify should've been replaced with a method that just returns \"uwu\"");
            Logger.log("And then a mixin should've been injected at its head:");
            IsolatedTest.run();
        }

        if (DO_JARS) {
            Logger.log(LOG_BREAK);
            Logger.log("Looking for jars");

            File jf = jarDir.toFile();
            jf.mkdirs();
            List<File> jars = JarHelper.findJars(jf);
            Logger.log("Found jars:");
            for (File jar : jars) Logger.log("   ", jar.getName());
            for (File jar : jars) cl.addURL(jar.toURI().toURL());

            Logger.log("Trying to load woah.txt (from primary.jar)");
            URL woahURL = cl.getResource("woah.txt");
            if (woahURL != null) {
                Logger.log("woah.txt is at", woahURL.toString(), ":");
                Stream<String> woah = Utils.resourceURLReadTextFile(woahURL).lines();
                woah.forEach(s -> Logger.log("    -", s));
            } else Logger.log("Could not find woah.txt");

            Logger.log("Trying to run ExtTest.test() reflectively");
            Class.forName("xyz.ashyboxy.advl.ext.primary.ExtTest")
                    .getMethod("test").invoke(null);

        }
    }
}
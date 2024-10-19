package xyz.ashyboxy.advl.asm.parent.transformers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.platform.container.ContainerHandleURI;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.IMixinTransformerFactory;
import org.spongepowered.asm.service.*;
import org.spongepowered.asm.util.ReEntranceLock;
import xyz.ashyboxy.advl.asm.parent.TransformingClassLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

public class AdvlMixinService implements IMixinService, IClassProvider, IClassBytecodeProvider {
    protected static IMixinTransformer mixin = null;

    private final ReEntranceLock lock = new ReEntranceLock(1);

    // IMixinService
    @Override
    public String getName() {
        return "Advl";
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void prepare() {}

    @Override
    public MixinEnvironment.Phase getInitialPhase() {
        return MixinEnvironment.Phase.PREINIT;
    }

    @Override
    public void offer(IMixinInternal i) {
        if(i instanceof IMixinTransformerFactory m) mixin = m.createTransformer();
    }

    @Override
    public void init() {

    }

    @Override
    public void beginPhase() {

    }

    @Override
    public void checkEnv(Object o) {

    }

    @Override
    public ReEntranceLock getReEntranceLock() {
        return lock;
    }

    @Override
    public IClassProvider getClassProvider() {
        return this;
    }

    @Override
    public IClassBytecodeProvider getBytecodeProvider() {
        return this;
    }

    @Override
    public ITransformerProvider getTransformerProvider() {
        return null;
    }

    @Override
    public IClassTracker getClassTracker() {
        return null;
    }

    @Override
    public IMixinAuditTrail getAuditTrail() {
        return null;
    }

    @Override
    public Collection<String> getPlatformAgents() {
        return List.of("org.spongepowered.asm.launch.platform.MixinPlatformAgentDefault");
    }

    @Override
    public IContainerHandle getPrimaryContainer() {
        try {
//            Class<?> c = TransformingClassLoader.getInstance().getDummyInsideClass();
            Class<?> c = TransformingClassLoader.class;
            return new ContainerHandleURI(c.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<IContainerHandle> getMixinContainers() {
        return List.of();
    }

    @Override
    public InputStream getResourceAsStream(String s) {
        return TransformingClassLoader.getInstance().getResourceAsStream(s);
    }

    @Override
    public String getSideName() {
        return MixinEnvironment.Side.CLIENT.name();
    }

    @Override
    public MixinEnvironment.CompatibilityLevel getMinCompatibilityLevel() {
        return MixinEnvironment.CompatibilityLevel.JAVA_21;
    }

    @Override
    public MixinEnvironment.CompatibilityLevel getMaxCompatibilityLevel() {
        return MixinEnvironment.CompatibilityLevel.JAVA_21;
    }

    @Override
    public ILogger getLogger(String s) {
        return MixinLogger.get(s);
    }

    // IClassProvider
    @Override
    public URL[] getClassPath() {
        // according to fabric "Mixin 0.7.x only uses getClassPath() to find itself"
        return new URL[0];
    }

    // i guess this should use findClass, but that'll just bubble up in TCL
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return TransformingClassLoader.getInstance().loadClass(name);
    }

    @Override
    public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, TransformingClassLoader.getInstance());
    }

    @Override
    public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, TransformingClassLoader.class.getClassLoader());
    }

    // IClassBytecodeProvider
    @Override
    public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException {
        return getClassNode(name, true);
    }

    @Override
    public ClassNode getClassNode(String name, boolean runTransformers) throws ClassNotFoundException, IOException {
        return getClassNode(name, runTransformers, 0);
    }

    @Override
    public ClassNode getClassNode(String name, boolean runTransformers, int readerFlags) throws ClassNotFoundException, IOException {
        // TODO: runTransformers is effectively always true
        byte[] b = TransformingClassLoader.getInstance().tryGetClassBytes(name, (n, t) -> !(t instanceof MixinTransformerProvider));
        ClassReader cr = new ClassReader(b);
        ClassNode cn = new ClassNode();
        cr.accept(cn, readerFlags);
        return cn;
    }
}

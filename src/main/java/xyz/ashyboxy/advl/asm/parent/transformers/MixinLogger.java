package xyz.ashyboxy.advl.asm.parent.transformers;

import org.spongepowered.asm.logging.Level;
import org.spongepowered.asm.logging.LoggerAdapterAbstract;
import xyz.ashyboxy.advl.asm.parent.Consts;
import xyz.ashyboxy.advl.asm.parent.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MixinLogger extends LoggerAdapterAbstract {
    private static HashMap<String, MixinLogger> loggers = new HashMap<>();

    private static Level logLevel = Consts.MIXIN_DEBUG ? Level.DEBUG : Level.INFO;

    public static MixinLogger get(String id) {
        return loggers.computeIfAbsent(id, MixinLogger::new);
    }

    private MixinLogger(String id) {
        super(id);
    }

    @Override
    public String getType() {
        return "Advl Mixin Logger";
    }

    @Override
    public void catching(Level level, Throwable t) {
        log(level, "Catching " + t.toString(), t);
    }

    @Override
    public void log(Level level, String message, Object... params) {
        if (level.compareTo(logLevel) > 0) return;
        // this WILL look broken, no Log4j in sight over here
        List<String> p = Arrays.stream(params).map(Object::toString).toList();
        Logger.log("[MIXIN] (" + level.name() + ")", message, String.join(" ", p));
    }

    @Override
    public void log(Level level, String message, Throwable t) {
        log(level, message, t.getLocalizedMessage());
    }

    @Override
    public <T extends Throwable> T throwing(T t) {
        return null;
    }
}

package xyz.ashyboxy.advl.loader.mixin;

import org.spongepowered.asm.logging.Level;
import org.spongepowered.asm.logging.LoggerAdapterAbstract;
import xyz.ashyboxy.advl.loader.Consts;
import xyz.ashyboxy.advl.loader.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
        ArrayList<String> p = new ArrayList<>();
        p.addFirst("(MIXIN) [" + level.name() + "] " + message);
        p.addAll(Arrays.stream(params).map(Object::toString).toList());
        Logger.log(p.toArray(new String[0]));
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

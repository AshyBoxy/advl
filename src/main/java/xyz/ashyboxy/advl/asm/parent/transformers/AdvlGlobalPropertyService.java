package xyz.ashyboxy.advl.asm.parent.transformers;

import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

import java.util.HashMap;

public class AdvlGlobalPropertyService implements IGlobalPropertyService {
    // this is kind of silly
    private static final HashMap<String, Object> properties = new HashMap<>();

    private static String getKey(IPropertyKey key) {
        return ((AdvlKey) key).key;
    }

    @Override
    public IPropertyKey resolveKey(String name) {
        return new AdvlKey(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(IPropertyKey key) {
        return (T) properties.get(getKey(key));
    }

    @Override
    public void setProperty(IPropertyKey key, Object value) {
        properties.put(getKey(key), value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(IPropertyKey key, T defaultValue) {
        return (T) properties.getOrDefault(getKey(key), defaultValue);
    }

    @Override
    public String getPropertyString(IPropertyKey key, String defaultValue) {
        if (properties.get(getKey(key)) instanceof Object o) return o.toString();
        return defaultValue;
    }

    public record AdvlKey(String key) implements IPropertyKey {
        @Override
        public String toString() {
            return "AdvlKey[" + this.key + "]";
        }
    }
}

package xyz.ashyboxy.advl.asm.parent.transformers;

import org.spongepowered.asm.service.IMixinServiceBootstrap;

public class AdvlMixinBootstrapService implements IMixinServiceBootstrap {
    @Override
    public String getName() {
        return "Advl";
    }

    @Override
    public String getServiceClassName() {
        return "";
    }

    @Override
    public void bootstrap() {

    }
}

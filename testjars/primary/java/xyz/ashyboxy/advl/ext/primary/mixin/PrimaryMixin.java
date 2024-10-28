package xyz.ashyboxy.advl.ext.primary.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.ashyboxy.advl.asm.parent.Logger;
import xyz.ashyboxy.advl.ext.secondary.SecondaryThing;

@Mixin(SecondaryThing.class)
public class PrimaryMixin {
    @Inject(method = "second", at = @At("HEAD"))
    public void second(CallbackInfo ci) {
        Logger.log("Hi from PrimaryMixin in SecondarThing#second");
    }
}

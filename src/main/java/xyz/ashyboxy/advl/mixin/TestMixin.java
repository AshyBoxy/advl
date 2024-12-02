package xyz.ashyboxy.advl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.ashyboxy.Uwuifier;
import xyz.ashyboxy.advl.loader.Logger;

@Mixin(Uwuifier.class)
public class TestMixin {
    @Inject(method = "uwuify", at = @At("HEAD"))
    private static void uwuifyMixin(String input, CallbackInfoReturnable<String> cir) {
        Logger.logO("Hello from the test mixin!", "(Uwuifier.uwuify was called with the input: `" + input + "`)");
    }
}

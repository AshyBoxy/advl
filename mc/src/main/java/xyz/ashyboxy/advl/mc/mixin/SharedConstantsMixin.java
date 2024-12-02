package xyz.ashyboxy.advl.mc.mixin;

import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SharedConstants.class)
public class SharedConstantsMixin {
    @Shadow
    private static WorldVersion CURRENT_VERSION;

    @Inject(method = "getCurrentVersion", at = @At("HEAD"), cancellable = true)
    private static void getCurrentVersion(CallbackInfoReturnable<WorldVersion> cir) {
        if (CURRENT_VERSION == null) cir.setReturnValue(DetectedVersion.tryDetectVersion());
    }
}

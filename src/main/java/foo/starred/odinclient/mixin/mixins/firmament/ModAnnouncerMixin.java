package foo.starred.odinclient.mixin.mixins.firmament;

import moe.nea.firmament.features.misc.ModAnnouncer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModAnnouncer.class, remap = false)
public class ModAnnouncerMixin {
    @Inject(method = "onServerJoin", at = @At("HEAD"), cancellable = true, remap = false)
    private void onServerJoin(CallbackInfo ci) {
        ci.cancel();
        System.out.println("Firmament server mod list packet cancelled.");
    }
}
package foo.starred.odinclient.mixin.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import foo.starred.odinclient.events.InteractEvent;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    public HitResult hitResult;

    @Shadow
    public LocalPlayer player;

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void odinClient$startAttack(CallbackInfoReturnable<Boolean> cir) {
        if (hitResult == null) return;
        if (hitResult.getType() != HitResult.Type.BLOCK) return;

        BlockHitResult bhr = (BlockHitResult) hitResult;
        if (new InteractEvent.HitBlock(player.getMainHandItem(), bhr.getBlockPos()).postAndCatch()) cir.cancel();
    }

    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void odinClient$continueAttack(boolean leftClick, CallbackInfo ci) {
        if (hitResult == null) return;
        if (hitResult.getType() != HitResult.Type.BLOCK) return;

        BlockHitResult bhr = (BlockHitResult) hitResult;
        if (new InteractEvent.HitBlock(player.getMainHandItem(), bhr.getBlockPos()).postAndCatch()) ci.cancel();
    }
}
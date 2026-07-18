package foo.starred.odinclient.mixin.mixins;

import foo.starred.odinclient.features.impl.dungeons.Highlight;
import foo.starred.odinclient.features.impl.render.NoGlow;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    public void odinClient$getTeamColor(CallbackInfoReturnable<Integer> cir) {
        Entity self = (Entity)(Object)this;

        Integer color = Highlight.getTeammateColor(self);
        if (color != null) cir.setReturnValue(color);
    }

    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    public void odinClient$isCurrentlyGlowing(CallbackInfoReturnable<Boolean> cir) {
        if (NoGlow.INSTANCE.getEnabled()) cir.setReturnValue(false);
    }
}
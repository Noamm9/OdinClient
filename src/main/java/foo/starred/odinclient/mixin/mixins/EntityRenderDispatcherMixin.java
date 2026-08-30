package foo.starred.odinclient.mixin.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import foo.starred.odinclient.accessors.EntityRenderStateAccessor;
import foo.starred.odinclient.events.WorldRenderEvent;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "extractEntity", at = @At("RETURN"))
    private void odinClient$extractEntity(Entity entity, float partialTick, CallbackInfoReturnable<EntityRenderState> cir) {
        EntityRenderState renderState = cir.getReturnValue();
        ((EntityRenderStateAccessor) renderState).odc$setEntity(entity);
    }

    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    private void odinClient$submit(EntityRenderState renderState, CameraRenderState cameraRenderState, double camX, double camY, double camZ, PoseStack poseStack, SubmitNodeCollector nodeCollector, CallbackInfo ci) {
        Entity entity = ((EntityRenderStateAccessor) renderState).odc$getEntity();
        if (new WorldRenderEvent.Entity.Pre(renderState, poseStack, cameraRenderState, entity).postAndCatch()) ci.cancel();
    }
}

package foo.starred.odinclient.mixin.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import foo.starred.odinclient.features.impl.general.CameraHelper;
import net.minecraft.client.Camera;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {
    //? if >= 26.1 {
    @WrapOperation(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
    ///? } else {
    /*@WrapOperation(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
    *///? }
    private double odinClient$setup(LivingEntity instance, Holder<Attribute> attribute, Operation<Double> original) {
        return CameraHelper.INSTANCE.getEnabled() && CameraHelper.INSTANCE.getEnableDist() ? CameraHelper.INSTANCE.getCameraDist() : original.call(instance, attribute);
    }

    @Inject(method = "getMaxZoom", at = @At("HEAD"), cancellable = true)
    private void odinClient$getMaxZoom(float maxZoom, CallbackInfoReturnable<Float> cir) {
        if (CameraHelper.INSTANCE.getEnabled() && CameraHelper.INSTANCE.getCameraClip()) cir.setReturnValue(maxZoom);
    }
}
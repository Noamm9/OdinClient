package foo.starred.odinclient.mixin.mixins.od;

import com.odtheking.odin.clickgui.Panel;
import com.odtheking.odin.features.Category;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static foo.starred.odinclient.commands.StreamCommandKt.streamMode;

@Mixin(value = Panel.class, remap = false)
public class PanelMixin {
    @Final
    @Shadow
    private Category category;

    @Inject(method = "draw", at = @At("HEAD"), cancellable = true)
    private void odinClient$draw(float mouseX, float mouseY, CallbackInfo ci) {
        if (category == foo.starred.odinclient.utils.Category.CHEATS && streamMode) ci.cancel();
    }

    @Inject(method = "handleScroll", at = @At("HEAD"), cancellable = true)
    private void odinClient$handleScroll(int amount, CallbackInfoReturnable<Boolean> cir) {
        if (category == foo.starred.odinclient.utils.Category.CHEATS && streamMode) cir.setReturnValue(false);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void odinClient$mouseClicked(float mouseX, float mouseY, MouseButtonEvent click, CallbackInfoReturnable<Boolean> cir) {
        if (category == foo.starred.odinclient.utils.Category.CHEATS && streamMode) cir.setReturnValue(false);
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void odinClient$mouseReleased(MouseButtonEvent click, CallbackInfo ci) {
        if (category == foo.starred.odinclient.utils.Category.CHEATS && streamMode) ci.cancel();
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void odinClient$keyPressed(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        if (category == foo.starred.odinclient.utils.Category.CHEATS && streamMode) cir.setReturnValue(false);
    }

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    private void odinClient$keyTyped(CharacterEvent input, CallbackInfoReturnable<Boolean> cir) {
        if (category == foo.starred.odinclient.utils.Category.CHEATS && streamMode) cir.setReturnValue(false);
    }
}
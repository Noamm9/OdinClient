//? if >= 26.1 {
package foo.starred.odinclient.mixin.mixins.od;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.odtheking.odin.features.impl.dungeon.map.DungeonScan;
import com.odtheking.odin.utils.Color;
import foo.starred.odinclient.features.impl.dungeons.CheaterMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DungeonScan.class)
public class DungeonScanMixin {
    @Unique
    private static boolean odinClient$bool$0;

    @Unique
    private static boolean odinClient$bool$1;

    @ModifyExpressionValue(method = "updateViewableDoors", at = @At(value = "INVOKE", target = "Lcom/odtheking/odin/features/impl/dungeon/map/tile/DungeonRoom;isViewable()Z", ordinal = 0))
    private static boolean odinClient$updateViewableDoors$1(boolean original) {
        if (CheaterMap.getDarken()) odinClient$bool$0 = !original;
        return CheaterMap.getShowRooms() || original;
    }

    @ModifyExpressionValue(method = "updateViewableDoors", at = @At(value = "INVOKE", target = "Lcom/odtheking/odin/features/impl/dungeon/map/tile/DungeonRoom;isViewable()Z", ordinal = 1))
    private static boolean odinClient$updateViewableDoors$2(boolean original) {
        if (CheaterMap.getDarken()) odinClient$bool$1 = !original;
        return CheaterMap.getShowRooms() || original;
    }

    @ModifyArg(method = "updateViewableDoors", at = @At(value = "INVOKE", target = "Lcom/odtheking/odin/features/impl/dungeon/map/tile/DungeonDoor;setColor(Lcom/odtheking/odin/utils/Color;)V"), index = 0)
    private static Color odinClient$updateViewableDoors$3(Color color) {
        if (!CheaterMap.getShowRooms()) return color;
        if (!CheaterMap.getDarken()) return color;
        if (!odinClient$bool$0 && !odinClient$bool$1) return color;

        return Color.Companion.darker(color, (float) CheaterMap.INSTANCE.getDarkenFactor());
    }
}
//? }
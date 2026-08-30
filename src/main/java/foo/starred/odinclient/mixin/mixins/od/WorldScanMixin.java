package foo.starred.odinclient.mixin.mixins.od;

import com.odtheking.odin.features.impl.dungeon.map.WorldScan;
import foo.starred.odinclient.features.impl.dungeons.CheaterMap;
import foo.starred.odinclient.features.impl.dungeons.DoorHighlight;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldScan.class)
public class WorldScanMixin {
    @Inject(method = "scanChunk", at = @At("TAIL"))
    private void odinClient$scanChunk(LevelChunk chunk, CallbackInfo ci) {
        if (!CheaterMap.getShowRooms() && !DoorHighlight.INSTANCE.getDepth()) return;

        CheaterMap.scanDoor(chunk);
    }
}

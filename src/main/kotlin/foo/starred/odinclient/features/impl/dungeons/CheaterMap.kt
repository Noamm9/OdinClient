package foo.starred.odinclient.features.impl.dungeons

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.features.Module
import com.odtheking.odin.features.impl.dungeon.map.DungeonScan
import com.odtheking.odin.features.impl.dungeon.map.tile.DoorRotation
import com.odtheking.odin.features.impl.dungeon.map.tile.DoorType
import com.odtheking.odin.features.impl.dungeon.map.tile.DungeonDoor
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.IVec2
import com.odtheking.odin.utils.toIVec2
import foo.starred.odinclient.utils.Category
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.chunk.LevelChunk

object CheaterMap : Module(
    name = "Cheater map",
    description = "Modifications for the Odin map.",
    category = Category.CHEATS
) {
    private val _showRooms by BooleanSetting("Show hidden rooms", true, desc = "Shows hidden rooms.")
    private val _darken by BooleanSetting("Darken tiles", true, desc = "Darken hidden rooms and doors.")
    private val _names by BooleanSetting("Show names", true, desc = "Show names for hidden rooms and rooms that haven't been walked into.")
    val darkenFactor by NumberSetting("Darken factor", 0.6, 0.0, 1.0, 0.1, desc = "Darken factor.")

    @JvmStatic
    val showRooms: Boolean
        get() = enabled && _showRooms

    @JvmStatic
    val darken: Boolean
        get() = enabled && _darken

    @JvmStatic
    val names: Boolean
        get() = enabled && _names

    @JvmStatic
    fun scanDoor(chunk: LevelChunk) {
        val (x, z) = chunk.pos.toIVec2()
        if (x !in -12..-2) return
        if (z !in -12..-2) return
        if ((x % 2 == 0) == (z % 2 == 0)) return

        val pos = IVec2(x, z)
        if (DungeonScan.doors.containsKey(pos)) return

        val x1 = x * 16 + 7
        val y1 = z * 16 + 7

        if ((86..160).any { !chunk.getBlockState(BlockPos(x1, it, y1)).isAir }) return
        if (chunk.getBlockState(BlockPos(x1, 68, y1)).isAir) return

        val type = when (chunk.getBlockState(BlockPos(x1, 69, y1)).block) {
            Blocks.COAL_BLOCK -> DoorType.Wither
            Blocks.RED_TERRACOTTA -> DoorType.Blood
            else -> DoorType.Normal
        }

        DungeonScan.doors[pos] = DungeonDoor(((pos - 1) / 2) + 6, if (z % 2 == 0) DoorRotation.Horizontal else DoorRotation.Vertical, type, Colors.WHITE)
        DungeonScan.updateViewableDoors()
    }
}

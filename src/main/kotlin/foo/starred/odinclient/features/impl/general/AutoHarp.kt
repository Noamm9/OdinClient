package foo.starred.odinclient.features.impl.general

import com.odtheking.odin.events.TickEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.events.core.onSend
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.skyblock.LocationUtils
import foo.starred.odinclient.utils.Category
import foo.starred.odinclient.utils.guiClick
import foo.starred.snowbird.api.client
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Items

object AutoHarp : Module(
    name = "Auto Harp",
    description = "Tries to do the Harp for you!",
    category = Category.CHEATS
) {
    private var bool: Boolean = false
    private var hash: Int = 0

    init {
        onReceive<ClientboundOpenScreenPacket> {
            if (!LocationUtils.isInSkyblock) return@onReceive
            if (!title.string.startsWith("Harp - ")) return@onReceive
            if (type != MenuType.GENERIC_9x6) return@onReceive

            bool = true
        }

        onReceive<ClientboundContainerClosePacket> {
            bool = false
        }

        onSend<ServerboundContainerClosePacket> {
            bool = false
        }

        on<TickEvent.Start> {
            if (!bool) return@on

            val screen = client.screen as? AbstractContainerScreen<*> ?: return@on
            val slots = screen.menu.slots

            var hash0 = 0
            var slot = -1

            for (i in 37..43) {
                val b0 = slots.getOrNull(i)?.item?.item == Items.QUARTZ_BLOCK
                hash0 = (hash0 shl 1) or if (b0) 1 else 0
                if (slot == -1 && b0) slot = i
            }

            if (hash == hash0) return@on
            hash = hash0

            if (slot == -1) return@on
            guiClick(screen.menu.containerId, slot, clickType = ContainerInput.CLONE)
        }
    }
}
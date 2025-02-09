package me.odinmain.utils.skyblock

import me.odinmain.OdinMain.logger
import me.odinmain.OdinMain.mc
import me.odinmain.features.impl.floor7.p3.termsim.TermSimGui
import me.odinmain.utils.clock.Executor
import me.odinmain.utils.clock.Executor.Companion.register
import me.odinmain.utils.render.Color
import me.odinmain.utils.render.Renderer
import net.minecraft.inventory.ContainerChest
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraft.util.Vec3

object PlayerUtils {
    var shouldBypassVolume = false

    /**
     * Plays a sound at a specified volume and pitch, bypassing the default volume setting.
     *
     * @param sound The identifier of the sound to be played.
     * @param volume The volume at which the sound should be played.
     * @param pitch The pitch at which the sound should be played.
     *
     * @author Aton
     */
    fun playLoudSound(sound: String?, volume: Float, pitch: Float, pos: Vec3? = null) {
        mc.addScheduledTask {
            shouldBypassVolume = true
            mc.theWorld?.playSound(pos?.xCoord ?: mc.thePlayer.posX, pos?.yCoord ?: mc.thePlayer.posY, pos?.zCoord  ?: mc.thePlayer.posZ, sound, volume, pitch, false)
            shouldBypassVolume = false
        }
    }

    /**
     * Displays an alert on screen and plays a sound
     *
     * @param title String to be displayed.
     * @param playSound Toggle for sound.
     *
     * @author Odtheking, Bonsai
     */
    fun alert(title: String, time: Int = 20, color: Color = Color.WHITE, playSound: Boolean = true, displayText: Boolean = true) {
        if (playSound) playLoudSound("note.pling", 100f, 1f)
        if (displayText) Renderer.displayTitle(title , time, color = color)
    }

    inline val posX get() = mc.thePlayer?.posX ?: 0.0
    inline val posY get() = mc.thePlayer?.posY ?: 0.0
    inline val posZ get() = mc.thePlayer?.posZ ?: 0.0

    fun getPositionString() = "x: ${posX.toInt()}, y: ${posY.toInt()}, z: ${posZ.toInt()}"

    private data class WindowClick(val slotId: Int, val button: Int, val mode: Int)

    private val windowClickQueue = mutableListOf<WindowClick>()

    init {
        // Used to clear the click queue every 500ms, to make sure it isn't getting filled up.
        Executor(delay = 500, "Click Dispatcher") { windowClickQueue.clear() }.register()
    }

    /*
     * Wrapper for windowClick which handles click spamming. Use instant for player action click redirect.
     */
    fun windowClick(slotId: Int, button: Int, mode: Int, instant: Boolean = false) {
        if (mc.currentScreen is TermSimGui) {
            val gui = mc.currentScreen as TermSimGui
            gui.delaySlotClick(gui.inventorySlots.getSlot(slotId), button)
        } else if (instant) sendWindowClickPacket(slotId, button, mode)
        else windowClickQueue.add(WindowClick(slotId, button, mode))
    }

    @JvmStatic
    fun handleWindowClickQueue() {
        if (mc.thePlayer?.openContainer == null) return windowClickQueue.clear()
        if (windowClickQueue.isEmpty()) return
        windowClickQueue.first().apply {
            try {
                sendWindowClick(slotId, button, mode)
            } catch (e: Exception) {
                println("Error sending window click: $this")
                logger.error(e)
                windowClickQueue.clear()
            }
        }
        windowClickQueue.removeFirstOrNull()
    }

    private fun sendWindowClick(slotId: Int, button: Int, mode: Int) {
        mc.thePlayer?.openContainer?.let {
            if (it !is ContainerChest) return
            if (slotId !in 0 until it.inventorySlots.size) return

            mc.playerController?.windowClick(it.windowId, slotId, button, mode, mc.thePlayer)
        }
    }

    private fun sendWindowClickPacket(slotId: Int, button: Int, mode: Int) {
        mc.thePlayer?.openContainer?.let {
            if (it !is ContainerChest) return
            if (slotId !in 0 until it.inventorySlots.size) return
            mc.netHandler?.networkManager?.sendPacket(C0EPacketClickWindow(it.windowId, slotId, button, mode, it.inventory[slotId], it.getNextTransactionID(mc.thePlayer?.inventory)))
        }
    }

    fun windowClick(slotId: Int, clickType: ClickType, instant: Boolean = false) {
        when (clickType) {
            is ClickType.Left -> windowClick(slotId, 0, 0, instant)
            is ClickType.Right -> windowClick(slotId, 1, 0, instant)
            is ClickType.Middle -> windowClick(slotId, 2, 3, instant)
            is ClickType.Shift -> windowClick(slotId, 0, 1, instant)
        }
    }
}

sealed class ClickType {
    data object Left   : ClickType()
    data object Right  : ClickType()
    data object Middle : ClickType()
    data object Shift  : ClickType()
}
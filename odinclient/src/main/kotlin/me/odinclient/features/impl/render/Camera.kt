package me.odinclient.features.impl.render

import me.odinmain.features.Category
import me.odinmain.features.Module
import me.odinmain.features.settings.Setting.Companion.withDependency
import me.odinmain.features.settings.impl.BooleanSetting
import me.odinmain.features.settings.impl.DropdownSetting
import me.odinmain.features.settings.impl.KeybindSetting
import me.odinmain.features.settings.impl.NumberSetting
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import net.minecraftforge.client.event.EntityViewRenderEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import kotlin.math.cos
import kotlin.math.sin

object Camera : Module(
    name = "Camera",
    category = Category.RENDER,
    description = "Various camera improvements and settings."
) {
    private val frontCamera by BooleanSetting("No Front Camera", false, description = "Disables front camera.")
    private val cameraClip by BooleanSetting("Camera Clip", false, description = "Allows the camera to clip through blocks.")
    private val cameraDist by NumberSetting("Distance", 4f, 3.0, 12.0, 0.1, description = "The distance of the camera from the player.")
    private val fov by NumberSetting("FOV", mc.gameSettings.fovSetting, 1f, 180f, 1f, description = "The field of view of the camera.")
    private val freelookDropdown by DropdownSetting("Freelook")
    private val toggle by BooleanSetting("Type", false, description = "The type of freelook (Hold/Toggle).").withDependency { freelookDropdown }
    private val freelookKeybind by KeybindSetting("Freelook Key", Keyboard.KEY_NONE, description = "Keybind to toggle/ hold for freelook.")
        .withDependency { freelookDropdown }
        .onPress {
            if (!freelookToggled && enabled) enable()
            else if ((toggle || !enabled) && freelookToggled) disable()
    }
    var freelookToggled = false
    private var cameraYaw = 0f
    private var cameraPitch = 0f
    private var lastPerspective = -1

    private var previousFov = mc.gameSettings.fovSetting

    override fun onEnable() {
        previousFov = mc.gameSettings.fovSetting
        super.onEnable()
    }

    override fun onDisable() {
        mc.gameSettings.fovSetting = previousFov
        super.onDisable()
    }

    fun getCameraDistance(): Float {
        return if (enabled) cameraDist else 4f
    }

    fun getCameraClipEnabled(): Boolean {
        return if (enabled) cameraClip else false
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (mc.gameSettings.fovSetting != fov)
            mc.gameSettings.fovSetting = fov

        if (frontCamera && mc.gameSettings.thirdPersonView == 2)
            mc.gameSettings.thirdPersonView = 0

        if (!freelookKeybind.isDown() && freelookToggled && !toggle) disable()
    }

    private fun enable() {
        cameraYaw = mc.thePlayer.rotationYaw + 180
        cameraPitch = mc.thePlayer.rotationPitch
        freelookToggled = true
        lastPerspective = mc.gameSettings.thirdPersonView
        mc.gameSettings.thirdPersonView = 1
    }

    private fun disable() {
        freelookToggled = false
        mc.gameSettings.thirdPersonView = if (lastPerspective != -1) lastPerspective else 0
        lastPerspective = -1
    }

    @SubscribeEvent
    fun cameraSetup(e: EntityViewRenderEvent.CameraSetup) {
        if (!freelookToggled) return
        e.yaw = cameraYaw
        e.pitch = cameraPitch
    }

    fun updateCameraAndRender(f2: Float, f3: Float) {
        if (!freelookToggled) return
        cameraYaw += f2 / 7
        cameraPitch = MathHelper.clamp_float((cameraPitch + f3 / 7), -90f, 90f)
    }

    fun calculateCameraDistance(d0: Double, d1: Double, d2: Double, d3: Double): Float {
        var dist = d3
        var f2 = cameraPitch

        if (mc.gameSettings.thirdPersonView == 2) f2 += 180.0f

        val d4 = (sin(cameraYaw / 180.0f * Math.PI.toFloat()) * cos(f2 / 180.0f * Math.PI.toFloat())).toDouble() * dist
        val d5 = (-cos(cameraYaw / 180.0f * Math.PI.toFloat()) * cos(f2 / 180.0f * Math.PI.toFloat())).toDouble() * dist
        val d6 = (-sin(f2 / 180.0f * Math.PI.toFloat())).toDouble() * dist

        for (i in 0..7) {
            var f3 = ((i and 1) * 2 - 1).toFloat()
            var f4 = ((i shr 1 and 1) * 2 - 1).toFloat()
            var f5 = ((i shr 2 and 1) * 2 - 1).toFloat()
            f3 *= .1f
            f4 *= .1f
            f5 *= .1f
            val movingObjectPosition = mc.theWorld?.rayTraceBlocks(
                Vec3(d0 + f3.toDouble(), d1 + f4.toDouble(), d2 + f5.toDouble()),
                Vec3(d0 - d4 + f3.toDouble() + f5.toDouble(), d1 - d6 + f4.toDouble(), d2 - d5 + f5.toDouble())
            )

            if (movingObjectPosition != null) {
                val d7 = movingObjectPosition.hitVec.distanceTo(Vec3(d0, d1, d2))

                if (d7 < dist) {
                    dist = d7
                }
            }
        }
        return dist.toFloat()
    }
}
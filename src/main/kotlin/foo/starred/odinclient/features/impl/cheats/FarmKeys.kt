/**
 * Taken from OdinClient 1.8.9
 * odtheking's BSD-3 Clause License applies to this file.
 *
 * BSD 3-Clause License
 *
 * Copyright (c) 2023-2025, odtheking
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package foo.starred.odinclient.features.impl.cheats

import com.mojang.blaze3d.platform.InputConstants
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.KeybindSetting
import com.odtheking.odin.features.Module
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW
import foo.starred.odinclient.mixin.accessors.KeyMappingAccessor
import foo.starred.odinclient.utils.Skit

object FarmKeys : Module(
    name = "Farm keys",
    description = "Temporarily changes your minecraft keybind configuration for farming in Skyblock.",
    category = Skit.CHEATS
) {
    private var prev: Int? = null
    private var prev0: Int? = null

    private val attackKey by KeybindSetting("Block breaking", GLFW.GLFW_KEY_UNKNOWN, "Changes the keybind for breaking blocks.")
    private val jumpKey by KeybindSetting("Jump", GLFW.GLFW_KEY_UNKNOWN, "Changes the keybind for jumping.")
    private val lockCamera by BooleanSetting("Lock camera", true, desc = "Locks your camera.")

    @JvmStatic
    val lock: Boolean
        get() = enabled && lockCamera

    override fun onEnable() {
        super.onEnable()

        prev = (mc.options?.keyAttack as? KeyMappingAccessor)?.boundKey?.value
        prev0 = (mc.options?.keyJump as? KeyMappingAccessor)?.boundKey?.value

        bind(attackKey.value, jumpKey.value)
    }

    override fun onDisable() {
        bind(prev ?: mc.options.keyAttack.defaultKey.value, prev0 ?: mc.options.keyJump.defaultKey.value)
        super.onDisable()
    }

    private fun bind(attackKeyCode: Int, jumpKeyCode: Int) {
        val options = mc.options ?: return

        val key0 = if (attackKeyCode > 0) InputConstants.Type.KEYSYM.getOrCreate(attackKeyCode) else InputConstants.Type.MOUSE.getOrCreate(attackKeyCode)
        val key1 = if (jumpKeyCode > 0) InputConstants.Type.KEYSYM.getOrCreate(jumpKeyCode) else InputConstants.Type.MOUSE.getOrCreate(jumpKeyCode)
        if (attackKey.value != GLFW.GLFW_KEY_UNKNOWN) options.keyAttack.setKey(key0)
        if (jumpKey.value != GLFW.GLFW_KEY_UNKNOWN) options.keyJump.setKey(key1)

        options.save()
        KeyMapping.resetMapping()
    }
}
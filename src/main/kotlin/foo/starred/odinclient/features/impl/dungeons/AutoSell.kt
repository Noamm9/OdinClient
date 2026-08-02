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

package foo.starred.odinclient.features.impl.dungeons

import com.odtheking.odin.clickgui.settings.impl.ActionSetting
import com.odtheking.odin.clickgui.settings.impl.ListSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.clickgui.settings.impl.SelectorSetting
import com.odtheking.odin.events.TickEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.lore
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.noControlCodes
import foo.starred.odinclient.OdinClient
import foo.starred.odinclient.utils.Category
import foo.starred.odinclient.utils.guiClick
import foo.starred.snowbird.utils.stripped
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.item.Items

object AutoSell : Module(
    name = "Auto Sell",
    description = "Automatically sell items in trades and cookie menus. (/autosell)",
    category = Category.CHEATS
) {
    val sellList by ListSetting("Sell list", mutableSetOf<String>())
    private val delay by NumberSetting("Delay", 6, 2, 10, 1, desc = "The delay between each sell action.", unit = " ticks")
    private val randomization by NumberSetting("Randomization", 1, 0, 5, 1, desc = "Random delay variance", unit = " ticks")
    private val clickType1 by SelectorSetting("Click Type", "Shift", arrayListOf("Shift", "Middle", "Left"), desc = "The type of click to use when selling items.")
    private val addDefaults by ActionSetting("Add defaults", desc = "Add default dungeon items to the auto sell list.") {
        sellList.addAll(defaultItems)
        modMessage("§aAdded default items to auto sell list")
        OdinClient.moduleConfig.save()
    }

    private var last = 0L
    private var next = 0L

    init {
        on<TickEvent.Start> {
            if (sellList.isEmpty()) return@on
            val menu = (mc.screen as? AbstractContainerScreen<*>)?.menu ?: return@on
            val now = System.currentTimeMillis()
            if (now - last < next) return@on

            val t0 = menu.slots.getOrNull(49)?.item
            val a = t0?.item == Items.HOPPER && t0.hoverName.stripped() == "Sell Item"
            val b = t0?.lore?.lastOrNull()?.stripped() == "Click to buyback!"
            if (!a && !b) return@on

            for (s in menu.slots) {
                if (s.container !is Inventory) continue

                val stack = s.item.takeIf { !it.isEmpty } ?: continue
                val name = stack.hoverName.string.noControlCodes

                if (!sellList.any { name.contains(it, true) }) continue
                if (blacklist.any { name.contains(it, true) }) continue

                guiClick(menu.containerId, s.index, clickType = clickType1.get())
                last = now
                delay()

                break
            }
        }
    }

    private fun delay() {
        next = ((delay + (0..randomization).random()) * 50).toLong()
    }

    private fun Int.get() = when (this) {
        0 -> ContainerInput.QUICK_MOVE
        1 -> ContainerInput.CLONE
        2 -> ContainerInput.PICKUP
        else -> ContainerInput.QUICK_MOVE
    }

    private val defaultItems = arrayOf(
        "enchanted ice", "superboom tnt", "rotten", "skeleton master", "skeleton grunt", "cutlass",
        "skeleton lord", "skeleton soldier", "zombie soldier", "zombie knight", "zombie commander", "zombie lord",
        "skeletor", "super heavy", "heavy", "sniper helmet", "dreadlord", "earth shard", "zombie commander whip",
        "machine gun", "sniper bow", "soulstealer bow", "silent death", "training weight",
        "beating heart", "premium flesh", "mimic fragment", "enchanted rotten flesh", "sign",
        "enchanted bone", "defuse kit", "optical lens", "tripwire hook", "button", "carpet", "lever", "diamond atom",
        "healing viii splash potion", "healing 8 splash potion", "candycomb"
    )

    private val blacklist = listOf("skeleton master chestplate")

}
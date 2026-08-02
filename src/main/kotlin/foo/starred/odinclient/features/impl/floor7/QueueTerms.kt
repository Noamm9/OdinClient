package foo.starred.odinclient.features.impl.floor7

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.features.Module
import foo.starred.odinclient.utils.Category
import foo.starred.odinclient.utils.notify
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import java.net.URI

object QueueTerms : Module(
    name = "Queue Terms",
    description = "Queues clicks in terminals to ensure every click is registered (only works in custom term gui).",
    category = Category.CHEATS
) {
    private val hover by BooleanSetting(
        "HOVER HERE!!!",
        true,
        "Please do not use this module, look into using another mod."
    )

    override fun onEnable() {
        super.onEnable()
        Component.literal("Queue Terms is slightly buggy in OdinClient, please refer to https://github.com/skies-starred/Nebulune for a better one.")
            .withStyle(Style.EMPTY.withClickEvent(ClickEvent.OpenUrl(URI("https://github.com/skies-starred/Nebulune"))).withHoverEvent(HoverEvent.ShowText(Component.literal("Click to open link."))))
            .notify()
    }
}
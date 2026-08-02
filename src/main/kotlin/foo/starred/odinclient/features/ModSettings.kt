package foo.starred.odinclient.features

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.features.Module
import foo.starred.odinclient.utils.Category

object ModSettings : Module(
    name = "--- OdinClient settings ---",
    description = "Settings for some of the internal stuff in OdinClient!",
    category = Category.CHEATS
) {
    val updateOnce by BooleanSetting("Only show update once", desc = "Toggle to only show the message to update once!")
    val important by BooleanSetting("Important thing", true, desc = "Disabling the important thing may break features!")
}
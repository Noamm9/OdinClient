package foo.starred.odinclient.features.impl.floor7

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.features.Module
import foo.starred.odinclient.utils.Skit

object AutoTerms : Module(
    name = "Auto Terms",
    description = "Automatically solves terminals.",
    category = Skit.CHEATS
) {
    private val hover by BooleanSetting("HOVER HERE!!!", true, "Please do not use this module, look into using another mod.")
}
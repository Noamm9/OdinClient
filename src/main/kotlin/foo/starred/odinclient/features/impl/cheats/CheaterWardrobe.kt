package foo.starred.odinclient.features.impl.cheats

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.features.Module
import foo.starred.odinclient.utils.Skit

object CheaterWardrobe : Module(
    name = "Cheater Wardrobe",
    description = "Automatically swaps wardrobe slots without interrupting movement or showing the GUI.",
    category = Skit.CHEATS
) {
    private val hover by BooleanSetting("HOVER HERE!!!", true, "Temporarily removed, will be back in the next release. Use Nebulune for now.")
}

//? if >= 26.1 {
package foo.starred.odinclient.features.impl.dungeons

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.features.Module
import foo.starred.odinclient.utils.Skit

object CheaterMap : Module(
    name = "Cheater map",
    description = "Modifications for the Odin map.",
    category = Skit.CHEATS
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
}
//? }
package me.odinclient.features

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import me.odinclient.OdinClient
import me.odinclient.features.settings.AlwaysActive
import me.odinclient.features.settings.Setting
import me.odinclient.utils.clock.Executable
import me.odinclient.utils.clock.Executor
import me.odinclient.utils.clock.Executor.Companion.executeAll
import me.odinclient.utils.skyblock.ChatUtils
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import kotlin.reflect.full.hasAnnotation

abstract class Module(
    name: String,
    keyCode: Int = Keyboard.KEY_NONE,
    category: Category = Category.GENERAL,
    toggled: Boolean = false,
    settings: ArrayList<Setting<*>> = ArrayList(),
    description: String = ""
) {

    @Expose
    @SerializedName("name")
    val name: String

    @Expose
    @SerializedName("key")
    var keyCode: Int
    val category: Category

    @Expose
    @SerializedName("enabled")
    var enabled: Boolean = toggled
        private set
    @Expose
    @SerializedName("settings")
    val settings: ArrayList<Setting<*>>

    /**
     * Will be used for a tooltip
     */
    var description: String

    init {
        this.name = name
        this.keyCode = keyCode
        this.category = category
        this.settings = settings
        this.description = description

        if (this::class.hasAnnotation<AlwaysActive>()) {
            MinecraftForge.EVENT_BUS.register(this)
        }
    }

    open fun onEnable() {
        MinecraftForge.EVENT_BUS.register(this)
    }
    open fun onDisable() {
        if (!this::class.hasAnnotation<AlwaysActive>()) {
            MinecraftForge.EVENT_BUS.unregister(this)
        }
    }

    /**
     * Call to perform the key bind action for this module.
     * By default, this will toggle the module and send a chat message.
     * It can be overwritten in the module to change that behaviour.
     */
    open fun keyBind() {
        toggle()
        ChatUtils.modMessage("$name ${if (enabled) "§aenabled" else "§cdisabled"}.")
    }

    /**
     * Will toggle the module
     */
    fun toggle() {
        enabled = !enabled
        if (enabled) onEnable()
        else onDisable()
    }

    fun <K: Setting<*>> register(setting: K): K {
        settings.add(setting)
        return setting
    }

    /**
     * Overloads the unaryPlus operator for [Setting] classes to register them to the module.
     * The following is an example of how it can be used to define a setting for a module.
     *
     *     private val feature = +BooleanSetting("Feature", true)
     *
     * @see register
     */
    operator fun <K: Setting<*>> K.unaryPlus(): K = register(this)

    fun getSettingByName(name: String): Setting<*>? {
        for (set in settings) {
            if (set.name.equals(name, ignoreCase = true)) {
                return set
            }
        }
        System.err.println("[" + OdinClient.NAME + "] Error Setting NOT found: '" + name + "'!")
        return null
    }

    fun getNameFromSettings(name: String): Boolean {
        for (set in settings) {
            if (set.name == name) return true
        }
        return false
    }

    internal fun isKeybindDown(): Boolean {
        return keyCode != 0 && (Keyboard.isKeyDown(keyCode) || Mouse.isButtonDown(keyCode + 100))
    }

    fun execute(delay: Long, func: Executable) {
        executors.add(Executor(delay, func))
    }

    fun execute(delay: Long, repeats: Int, func: Executable) {
        executors.add(Executor.LimitedExecutor(delay, repeats, func))
    }

    fun execute(delay: () -> Long, func: Executable) {
        executors.add(Executor.VaryingExecutor(delay, func))
    }

    private val executors = ArrayList<Executor>()

    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        executors.executeAll()
    }
}
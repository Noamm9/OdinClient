package me.odinmain.features.impl.dungeon.puzzlesolvers

import me.odinmain.events.impl.BlockChangeEvent
import me.odinmain.events.impl.DungeonEvents.RoomEnterEvent
import me.odinmain.features.Category
import me.odinmain.features.Module
import me.odinmain.features.impl.dungeon.puzzlesolvers.WaterSolver.waterInteract
import me.odinmain.features.settings.Setting.Companion.withDependency
import me.odinmain.features.settings.impl.*
import me.odinmain.ui.clickgui.util.ColorUtil.withAlpha
import me.odinmain.utils.profile
import me.odinmain.utils.render.Color
import me.odinmain.utils.render.Renderer
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object PuzzleSolvers : Module(
    name = "Puzzle Solvers",
    category = Category.DUNGEON,
    description = "Displays solutions for dungeon puzzles.",
    key = null
) {
    private val waterDropDown: Boolean by DropdownSetting("Water Board")
    private val waterSolver: Boolean by BooleanSetting("Water Board Solver", false, description = "Shows you the solution to the water puzzle.").withDependency { waterDropDown }
    val showOrder: Boolean by BooleanSetting("Show Order", true, description = "Shows the order of the levers to click.").withDependency { waterSolver && waterDropDown }
    val showTracer: Boolean by BooleanSetting("Show Tracer", true, description = "Shows a tracer to the next lever.").withDependency { waterSolver && waterDropDown }
    val tracerColorFirst: Color by ColorSetting("Tracer Color First", Color.GREEN, true, description = "Color for the first tracer.").withDependency { showTracer && waterDropDown }
    val tracerColorSecond: Color by ColorSetting("Tracer Color Second", Color.ORANGE, true, description = "Color for the second tracer.").withDependency { showTracer && waterDropDown }
    val reset: () -> Unit by ActionSetting("Reset", description = "Resets the solver.") {
        WaterSolver.reset()
    }.withDependency { waterSolver && waterDropDown }

    private val mazeDropDown: Boolean by DropdownSetting("TP Maze")
    private val tpMaze: Boolean by BooleanSetting("Teleport Maze", false, description = "Shows you the solution for the TP maze puzzle.").withDependency { mazeDropDown }
    val solutionThroughWalls: Boolean by BooleanSetting("Solution through walls", false, description = "Renders the final solution through walls.").withDependency { tpMaze && mazeDropDown }
    val mazeColorOne: Color by ColorSetting("Color for one solution", Color.GREEN.withAlpha(.5f), true, description = "Color for when there is a single solution.").withDependency { tpMaze && mazeDropDown }
    val mazeColorMultiple: Color by ColorSetting("Color for multiple solutions", Color.ORANGE.withAlpha(.5f), true, description = "Color for when there are multiple solutions.").withDependency { tpMaze && mazeDropDown }
    val mazeColorVisited: Color by ColorSetting("Color for visited", Color.RED.withAlpha(.5f), true, description = "Color for the already used TP pads.").withDependency { tpMaze && mazeDropDown }
    private val click: () -> Unit by ActionSetting("Reset", description = "Resets the solver.") {
        TPMazeSolver.reset()
    }.withDependency { tpMaze && mazeDropDown }

    private val iceFillDropDown: Boolean by DropdownSetting("Ice Fill")
    private val iceFillSolver: Boolean by BooleanSetting("Ice Fill Solver", false, description = "Solver for the ice fill puzzle.").withDependency { iceFillDropDown }
    private val iceFillColor: Color by ColorSetting("Ice Fill Color", Color.PINK, true, description = "Color for the ice fill solver.").withDependency { iceFillSolver && iceFillDropDown }
    private val action: () -> Unit by ActionSetting("Reset", description = "Resets the solver.") {
        IceFillSolver.reset()
    }.withDependency { iceFillSolver && iceFillDropDown }

    private val blazeDropDown: Boolean by DropdownSetting("Blaze")
    private val blazeSolver: Boolean by BooleanSetting("Blaze Solver", description = "Shows you the solution for the Blaze puzzle").withDependency { blazeDropDown }
    val blazeLineNext: Boolean by BooleanSetting("Blaze Solver Next Line", true, description = "Shows the next line to click.").withDependency { blazeSolver && blazeDropDown }
    val blazeLineAmount: Int by NumberSetting("Blaze Solver Lines", 1, 1, 10, 1, description = "Amount of lines to show.").withDependency { blazeSolver && blazeDropDown }
    val blazeStyle: Int by SelectorSetting("Style", "Filled", arrayListOf("Filled", "Outline", "Filled Outline"), description = "Whether or not the box should be filled.").withDependency { blazeSolver && blazeDropDown }
    val blazeFirstColor: Color by ColorSetting("First Color", Color.GREEN, true, description = "Color for the first blaze.").withDependency { blazeSolver && blazeDropDown }
    val blazeSecondColor: Color by ColorSetting("Second Color", Color.ORANGE, true, description = "Color for the second blaze.").withDependency { blazeSolver && blazeDropDown }
    val blazeAllColor: Color by ColorSetting("Other Color", Color.WHITE.withAlpha(.3f), true, description = "Color for the other blazes.").withDependency { blazeSolver && blazeDropDown }
    val blazeWidth: Double by NumberSetting("Box Width", 1.0, 0.5, 2.0, 0.1, description = "Width of the box.").withDependency { blazeSolver && blazeDropDown }
    val blazeHeight: Double by NumberSetting("Box Height", 2.0, 1.0, 3.0, 0.1, description = "Height of the box.").withDependency { blazeSolver && blazeDropDown }
    val blazeSendComplete: Boolean by BooleanSetting("Send Complete", false, description = "Send complete message.").withDependency { blazeSolver && blazeDropDown }
    private val blazeReset: () -> Unit by ActionSetting("Reset", description = "Resets the solver.") {
        BlazeSolver.reset()
    }.withDependency { blazeSolver && blazeDropDown }

    private val beamsDropDown: Boolean by DropdownSetting("Creeper Beams")
    private val beamsSolver: Boolean by BooleanSetting("Creeper Beams Solver", false, description = "Shows you the solution for the Creeper Beams puzzle.").withDependency { beamsDropDown }
    val beamStyle: Int by SelectorSetting("Style", "Filled", arrayListOf("Filled", "Outline", "Filled Outline"), description = "Whether or not the box should be filled.").withDependency { beamsSolver && beamsDropDown }
    val beamsDepth: Boolean by BooleanSetting("Depth", false, description = "Depth check for the beams puzzle.").withDependency { beamsSolver && beamsDropDown }
    val beamsTracer: Boolean by BooleanSetting("Tracer", false, description = "Shows a tracer to the next lantern.").withDependency { beamsSolver && beamsDropDown }
    val beamsAlpha: Float by NumberSetting("Color Alpha", .7f, 0f, 1f, .05f, description = "The alpha of the color.").withDependency { beamsSolver && beamsDropDown }
    private val beamsReset: () -> Unit by ActionSetting("Reset", description = "Resets the solver.") {
        BeamsSolver.reset()
    }.withDependency { beamsSolver && beamsDropDown }

    private val weirdosDropDown: Boolean by DropdownSetting("Three Weirdos")
    private val weirdosSolver: Boolean by BooleanSetting("Weirdos Solver", false, description = "Shows you the solution for the Weirdos puzzle.").withDependency { weirdosDropDown }
    val weirdosColor: Color by ColorSetting("Weirdos Color", Color.GREEN.withAlpha(0.7f), true, description = "Color for the weirdos solver.").withDependency { weirdosSolver && weirdosDropDown }
    val weirdosWrongColor: Color by ColorSetting("Weirdos Wrong Color", Color.RED.withAlpha(.7f), true,  description = "Color for the incorrect Weirdos.").withDependency { weirdosSolver && weirdosDropDown }
    val weirdosStyle: Int by SelectorSetting("Style", "Filled", arrayListOf("Filled", "Outline", "Filled Outline"), description = "Whether or not the box should be filled.").withDependency { weirdosSolver && weirdosDropDown }
    private val weirdosReset: () -> Unit by ActionSetting("Reset", description = "Resets the solver.") {
        WeirdosSolver.reset()
    }.withDependency { weirdosSolver && weirdosDropDown }

    private val quizDropdown: Boolean by DropdownSetting("Quiz")
    private val quizSolver: Boolean by BooleanSetting("Quiz Solver", false, description = "Solver for the trivia puzzle.").withDependency { quizDropdown }
    val quizDepth: Boolean by BooleanSetting("Quiz Depth", false, description = "Depth check for the trivia puzzle.").withDependency { quizDropdown && quizSolver }
    val quizReset: () -> Unit by ActionSetting("Reset", description = "Resets the solver.") {
        QuizSolver.reset()
    }.withDependency { quizDropdown && quizSolver }

    private val boulderDropDown: Boolean by DropdownSetting("Boulder")
    private val boulderSolver: Boolean by BooleanSetting("Boulder Solver", false, description = "Solver for the boulder puzzle.").withDependency { boulderDropDown }
    val showAllBoulderClicks: Boolean by DualSetting("Boulder clicks", "Only First", "All Clicks", false, description = "Shows all the clicks or only the first.").withDependency { boulderDropDown && boulderSolver }
    val boulderStyle: Int by SelectorSetting("Boulder Style", Renderer.DEFAULT_STYLE, Renderer.styles, description = Renderer.STYLE_DESCRIPTION).withDependency { boulderDropDown && boulderSolver }
    val boulderColor: Color by ColorSetting("Boulder Color", Color.GREEN.withAlpha(.5f), allowAlpha = true, description = "The color of the box.").withDependency { boulderDropDown && boulderSolver }
    val boulderLineWidth: Float by NumberSetting("Boulder Line Width", 2f, 0.1f, 10f, 0.1f, description = "The width of the box's lines.").withDependency { boulderDropDown && boulderSolver }

    init {
        execute(500) {
            if (waterSolver) WaterSolver.scan()
            if (blazeSolver) BlazeSolver.getBlaze()
        }

        onPacket(S08PacketPlayerPosLook::class.java) {
            if (tpMaze) TPMazeSolver.tpPacket(it)
        }

        onPacket(C08PacketPlayerBlockPlacement::class.java) {
            if (waterSolver) waterInteract(it)
        }

        onMessage(Regex("\\[NPC] (.+): (.+).?"), {enabled && weirdosSolver}) { str ->
            val (npc, message) = Regex("\\[NPC] (.+): (.+).?").find(str)?.destructured ?: return@onMessage
            WeirdosSolver.onNPCMessage(npc, message)
        }

        onMessage(Regex(".*"), {enabled && quizSolver}) {
            QuizSolver.onMessage(it)
        }

        onWorldLoad {
            WaterSolver.reset()
            TPMazeSolver.reset()
            IceFillSolver.reset()
            BlazeSolver.reset()
            BeamsSolver.reset()
            WeirdosSolver.reset()
            QuizSolver.reset()
            BoulderSolver.reset()
        }
    }

    @SubscribeEvent
    fun onWorldRender(event: RenderWorldLastEvent) {
        profile("Puzzle Solvers") {
            if (waterSolver) WaterSolver.waterRender()
            if (tpMaze) TPMazeSolver.tpRender()
            //if (tttSolver) TTTSolver.tttRenderWorld()
            if (iceFillSolver) IceFillSolver.onRenderWorldLast(iceFillColor)
            if (blazeSolver) BlazeSolver.renderBlazes()
            if (beamsSolver) BeamsSolver.onRenderWorld()
            if (weirdosSolver) WeirdosSolver.onRenderWorld()
            if (quizSolver) QuizSolver.renderWorldLastQuiz()
            if (boulderSolver) BoulderSolver.onRenderWorld()
        }
    }

    @SubscribeEvent
    fun onRoomEnter(event: RoomEnterEvent) {
        IceFillSolver.enterDungeonRoom(event)
        BeamsSolver.enterDungeonRoom(event)
        TTTSolver.tttRoomEnter(event)
        QuizSolver.enterRoomQuiz(event)
        BoulderSolver.onRoomEnter(event)
        TPMazeSolver.onRoomEnter(event)
    }

    @SubscribeEvent
    fun blockUpdateEvent(event: BlockChangeEvent) {
        BeamsSolver.onBlockChange(event)
    }

    @SubscribeEvent
    fun onInteract(event: PlayerInteractEvent) {
        BoulderSolver.playerInteract(event)
    }
}
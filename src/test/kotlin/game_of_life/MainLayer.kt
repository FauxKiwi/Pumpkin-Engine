package game_of_life

import com.pumpkin.core.Timestep
import com.pumpkin.core.event.*
import com.pumpkin.core.input.*
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.render.OrthographicCamera
import com.pumpkin.core.render.Renderer2D
import com.pumpkin.core.window.Window
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import imgui.ImGui
import kotlin.math.max
import kotlin.math.min

class MainLayer : Layer("Game of life") {
    private val camera = OrthographicCamera(-1f, 1f, -1f, 1f)
    private val playground = BooleanArray(100 * 100) { false }

    private var simulation = false
    private var timePassed = 0f
    private var speed = 10f

    private var arraySize = intArrayOf(100, 100)
    private var displaySize = intArrayOf(32 * 2, 18 * 2)

    private var dragging = false
    private val dragCopy = playground.copyOf()

    override fun onUpdate(ts: Timestep) {
        ///// Update /////
        if (simulation) {
            timePassed += ts
            if (timePassed >= (1f / speed)) {
                timePassed -= (1f / speed)
                onSecond()
            }
        }

        if (dragging) {
            val x = (displaySize[0] * Input.getMouseX() / Window.getWindow().width.toFloat()).toInt()
            val y = (displaySize[1] * Input.getMouseY() / Window.getWindow().height.toFloat()).toInt()
            val index = (x + arraySize[0]/2 + (y + arraySize[1]/2) * arraySize[0])
            playground[index] = !dragCopy[index]
        }

        ///// Render /////
        Renderer2D.beginScene(camera)
        for (x in -displaySize[0]/2 until displaySize[0]/2) for (y in -displaySize[1]/2 until displaySize[1]/2) {
            Renderer2D.drawQuad(
                Vec3(x * (2f / displaySize[0].toFloat()) + (0.5f / displaySize[1].toFloat()),
                    -y * (2f / displaySize[1].toFloat()) - (1f / displaySize[1].toFloat()), -0.1f),
                Vec2(1.75f / displaySize[0].toFloat(), 1.75f / displaySize[1].toFloat()),
                color = if (playground[(x + displaySize[0]/2 + arraySize[0]/2) +
                            ((y + displaySize[1]/2 + arraySize[1]/2) * arraySize[0])])
                    Vec4(0.8f, 0.8f, 0.8f, 1f)
                else
                    Vec4(0.2f, 0.2f, 0.2f, 1f)
            )
        }
        Renderer2D.endScene()
    }

    private fun onSecond() {
        val copy = playground.copyOf()
        for (i in 0 until arraySize[0] * arraySize[1]) {
            val cell = playground[i]
            var livingNeighbours = 0
            for (n in intArrayOf(-arraySize[0]-1, -arraySize[0], -arraySize[0]+1,
                -1, 1,
                arraySize[0]-1, arraySize[0], arraySize[0]+1)) {
                try {
                    if (playground[i + n]) livingNeighbours++
                } catch (e: ArrayIndexOutOfBoundsException) {
                }
            }
            if (!cell && (livingNeighbours == 3)) copy[i] = true
            if (cell && (livingNeighbours < 2)) copy[i] = false
            if (cell && (livingNeighbours > 3)) copy[i] = false
        }
        copy.copyInto(playground)
    }

    override fun onImGuiRender() {
        ImGui.begin("Game of Life")
        ImGui.dragFloat("Speed [</>]", ::speed, 0.1f, 0.1f, 20f, "%.2f")
        ImGui.checkbox("Simulation [Space]", ::simulation)
        ImGui.text("Reset [R]")
        ImGui.dragInt2("Array size", arraySize)
        ImGui.dragInt2("Display size", displaySize)
        ImGui.end()
    }

    override fun onEvent(event: Event) {
        val dispatcher = EventDispatcher(event)
        dispatcher.dispatch(::onMouseButtonPressed)
        dispatcher.dispatch(::onMouseButtonReleased)
        dispatcher.dispatch(::onKeyPressed)
    }

    private fun onMouseButtonPressed(event: MouseButtonPressedEvent): Boolean {
        if (event.button != 0) return false
        if (!simulation) {
            dragging = true
            playground.copyInto(dragCopy)
        }
        val x = (displaySize[0] * Input.getMouseX() / Window.getWindow().width.toFloat()).toInt()
        val y = (displaySize[1] * Input.getMouseY() / Window.getWindow().height.toFloat()).toInt()
        val index = (x + arraySize[0]/2 + (y + arraySize[1]/2) * arraySize[0])
        playground[index] = !playground[index]
        return false
    }

    private fun onMouseButtonReleased(event: MouseButtonReleasedEvent): Boolean {
        if (event.button == 0 && !simulation) {
            dragging = false
        }
        return false
    }

    private fun onKeyPressed(event: KeyPressedEvent): Boolean {
        when (event.keyCode) {
            PE_KEY_SPACE -> {
                simulation = !simulation
                timePassed = 0f
            }
            PE_KEY_RIGHT -> {
                speed += 0.1f
                speed = min(50f, speed)
            }
            PE_KEY_LEFT -> {
                speed -= 0.1f
                speed = max(0.1f, speed)
            }
            PE_KEY_R -> {
                playground.fill(false)
                simulation = false
            }
        }
       return false
    }
}
import com.pumpkin.core.*
import com.pumpkin.core.event.Event
import com.pumpkin.core.event.EventDispatcher
import com.pumpkin.core.event.MouseButtonPressedEvent
import com.pumpkin.core.event.MouseButtonReleasedEvent
import com.pumpkin.core.input.Input
import com.pumpkin.core.input.PE_MOUSE_BUTTON_LEFT
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.renderer.*
import com.pumpkin.core.window.Window
import glm_.glm
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import imgui.ImGui

class Sandbox2DLayer : Layer("Sandbox2D") {
    private val cameraController = OrthographicCameraController(16f / 9f, false)
    private val particleSystem = ParticleSystem()

    private var mouseClicked = false
    private val particleProps = ParticleProps(Vec2(0), Vec2(0f), Vec2(0.4f, 0.4f),
        Vec4(0.6f, 0f, 0.2f, 1f), Vec4(0f, 0f, 1f, 0.1f),
        0.1f, 0.001f, 0.05f, 1f
    )
    private var particleCountPerFrame = 1

    private val color = Vec4(0.3f, 0.2f, 0.8f, 1f)
    private val checkerboardPosition = Vec3(0f, 0f, -0.1f)
    private val checkerboardScale = Vec2(10f)
    private var checkerboardRotation = 0
    private val checkerboardTexture = Texture2D.create("./src/test/resources/textures/Checkerboard.png")
    private val checkerboardColor = Vec4(1f)
    private var checkerboardTiling = 1f
    private var checkerboardImportFilter = 1
        set(value) {
            field = value
            checkerboardTexture().setFilter(Texture2D.Filter.Linear, Texture2D.Filter.values()[value])
        }
    private var checkerboardImportWrap = 0
        set(value) {
            field = value
            checkerboardTexture().setWrap(Texture2D.WrapMode.values()[value])
        }

    override fun onDetach() {
        checkerboardTexture.release()
    }

    override fun onUpdate(ts: Timestep) {
        ////// Update //////
        cameraController.onUpdate(ts)

        if (mouseClicked) {
            val x = (2 * Input.getMouseX() / Window.getWindow().width - 1) * cameraController.zoomLevel * cameraController.aspectRatio + cameraController.cameraPosition.x
            val y = (-2 * Input.getMouseY() / Window.getWindow().height + 1) * cameraController.zoomLevel + cameraController.cameraPosition.y
            particleProps.position = Vec2(x, y)
            repeat(particleCountPerFrame) { particleSystem.emit(particleProps) }
        }

        ////// Render //////
        Renderer2D.beginScene(cameraController.camera)

        Renderer2D.drawQuad(Vec2(0.25f), Vec2(1.25f, 0.75f), color = color)
        Renderer2D.drawQuad(Vec2(-1f, 0f), Vec2(0.8f), color = Vec4(0.8f, 0.2f, 0.3f, 1f))
        Renderer2D.drawQuad(checkerboardPosition, checkerboardScale, glm.radians(checkerboardRotation.toFloat()), texture = checkerboardTexture(), color = checkerboardColor, tilingFactor = checkerboardTiling)
        Renderer2D.drawQuad(Vec2(0), size = Vec2(2f, 2f), radians = glm.radians(45f), texture = checkerboardTexture(), tilingFactor = 50f)

        //Renderer2D.drawQuad(p.position, Vec2(p.sizeBegin, p.sizeBegin), p.rotation, p.colorBegin)
        //Renderer2D.drawQuad(position = Vec3(0f, 0f, -0.1f), color = Vec4(1f))
        particleSystem.onUpdate(ts)

        Renderer2D.endScene()
    }

    override fun onImGuiRender() = with(ImGui) {
        begin("Square")
        colorEdit4("Color", color)
        end()
        begin("Checkerboard")
        text("Transform")
        dragVec3("Position", checkerboardPosition, 0.1f, format = "%.2f")
        dragVec2("Scale", checkerboardScale, 0.1f, 0f, 100f, format = "%.2f")
        dragInt("Rotation", ::checkerboardRotation, 1f, 0, 360)
        text("Texture")
        colorEdit4("Color", checkerboardColor)
        dragFloat("Tiling Factor", ::checkerboardTiling)
        text("Import Settings")
        combo("Filter Mode", ::checkerboardImportFilter, arrayOf("Linear", "Nearest"))
        combo("Wrap Mode", ::checkerboardImportWrap, arrayOf("Repeat", "Mirrored", "Clamp Edge", "Clamp Border"))
        end()
        begin("Particles")
        dragInt("Count per Frame", ::particleCountPerFrame)
        dragVec2("Velocity", particleProps.velocity, 0.1f, format = "%.1f")
        dragVec2("Velocity Variation", particleProps.velocityVariation, 0.1f, format = "%.1f")
        colorEdit4("Color Begin", particleProps.colorBegin)
        colorEdit4("Color End", particleProps.colorEnd)
        dragFloat("Size Begin", particleProps::sizeBegin, 0.1f, format = "%.1f")
        dragFloat("Size End", particleProps::sizeEnd, 0.001f, format = "%.3f")
        dragFloat("Size Variation", particleProps::sizeVariation, 0.01f, format = "%.2f")
        dragFloat("Lifetime", particleProps::lifeTime, 0.1f, format = "%.1f")
        end()
    }

    override fun onEvent(event: Event) {
        cameraController.onEvent(event)
        val dispatcher = EventDispatcher(event)
        dispatcher.dispatch<MouseButtonPressedEvent> { if (button != PE_MOUSE_BUTTON_LEFT) return@dispatch false; mouseClicked = true; false }
        dispatcher.dispatch<MouseButtonReleasedEvent> { if (button != PE_MOUSE_BUTTON_LEFT) return@dispatch false; mouseClicked = false; false }
    }
}

class Sandbox2DApp : Application() {

    override fun init() {
        pushLayer(Sandbox2DLayer())
    }

    override fun run() {
        RendererCommand.setClearColor(Vec4(0.1f, 0.1f, 0.1f, 1.0f))
        RendererCommand.clear()
    }
}

fun main() = Application.set(Sandbox2DApp())
import com.pumpkin.core.*
import com.pumpkin.core.event.Event
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.render.*
import glm_.vec2.Vec2
import glm_.vec4.Vec4
import imgui.ImGui

class Sandbox2DLayer : Layer("Sandbox2D") {
    private val cameraController = OrthographicCameraController(16f / 9f, false)
    private val color = Vec4(0.8f, 0.2f, 0.3f, 1f)

    override fun onDetach() {
        Renderer2D.shutdown() // temporary
    }

    override fun onUpdate(ts: Timestep) {
        ////// Update //////
        cameraController.onUpdate(ts)

        ////// Render //////
        Renderer2D.beginScene(cameraController.camera)

        Renderer2D.drawQuad(Vec2(0f), Vec2(1f), color)

        Renderer2D.endScene()
    }

    override fun onImGuiRender() {
        ImGui.begin("Square")
        ImGui.colorEdit4("Color", color)
        ImGui.end()
    }

    override fun onEvent(event: Event) {
        cameraController.onEvent(event)
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

fun main() {
    Application.set(Sandbox2DApp())
}
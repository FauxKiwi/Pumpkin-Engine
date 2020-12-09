import com.pumpkin.core.Application
import com.pumpkin.core.OrthographicCameraController
import com.pumpkin.core.Timestep
import com.pumpkin.core.event.Event
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.render.Renderer2D
import com.pumpkin.core.render.RendererCommand
import glm_.vec2.Vec2
import glm_.vec4.Vec4
import imgui.ImGui
import imgui.SliderFlag

class StresstestLayer : Layer("Stresstest") {
    private val cameraController = OrthographicCameraController(16f / 9f, false)

    private var sqrt = 10

    override fun onUpdate(ts: Timestep) {
        cameraController.onUpdate(ts)

        Renderer2D.beginScene(cameraController.camera)

        for (x in 0 until sqrt) for (y in 0 until sqrt) {
            Renderer2D.drawQuad(Vec2(x / 10f, y / 10f), Vec2(0.08f), color = Vec4(0.8f))
        }

        Renderer2D.endScene()
    }

    override fun onImGuiRender() {
        ImGui.begin("Stresstest")
        ImGui.dragInt("Number", ::sqrt, 1f, 1, 1000)
        ImGui.inputInt("Capacity", Renderer2D::maxQuads)
        ImGui.text("Drawing ${sqrt * sqrt} quads in ${(sqrt * sqrt - 1) / Renderer2D.maxQuads + 1} draw calls")
        ImGui.end()
    }

    override fun onEvent(event: Event) {
        cameraController.onEvent(event)
    }
}

class StresstestApp : Application() {

    override fun init() {
        pushLayer(StresstestLayer())
    }

    override fun run() {
        RendererCommand.setClearColor(Vec4(0.1f, 0.1f, 0.1f, 1.0f))
        RendererCommand.clear()
    }
}

fun main() {
    Application.set(StresstestApp())
}
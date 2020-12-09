import com.pumpkin.core.*
import com.pumpkin.core.event.Event
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.render.*
import glm_.glm
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import glm_.vec4.swizzle.zyxw
import imgui.ImGui

class Sandbox2DLayer : Layer("Sandbox2D") {
    private val cameraController = OrthographicCameraController(16f / 9f, false)
    private val color = Vec4(0.3f, 0.2f, 0.8f, 1f)

    private val checkerboardPosition = Vec3(0f, 0f, -0.1f)
    private val checkerboardScale = Vec2(10f)
    private var checkerboardRotation = 0
    private val checkerboardTexture = Texture2D.create("./src/test/resources/textures/Checkerboard.png")
    private val checkerboardColor = Vec4(1f)

    override fun onDetach() {
        checkerboardTexture.release()
    }

    override fun onUpdate(ts: Timestep) {
        ////// Update //////
        cameraController.onUpdate(ts)

        ////// Render //////
        Renderer2D.beginScene(cameraController.camera)

        Renderer2D.drawQuad(Vec2(0.25f), Vec2(1.25f, 0.75f), color = color)
        //Renderer2D.drawQuad(Vec2(-1f, 0f), Vec2(0.8f), color = Vec4(0.8f, 0.2f, 0.3f, 1f))
        //Renderer2D.drawQuad(checkerboardPosition, checkerboardScale, glm.radians(checkerboardRotation.toFloat()), texture = checkerboardTexture(), color = checkerboardColor)

        Renderer2D.endScene()
    }

    override fun onImGuiRender() {
        ImGui.begin("Square")
        ImGui.colorEdit4("Color", color)
        ImGui.end()
        ImGui.begin("Checkerboard")
        ImGui.text("Transform")
        dragFloat3("Position", checkerboardPosition, 0.1f, format = "%.2f")
        dragFloat2("Scale", checkerboardScale, 0.1f, 0f, 100f, format = "%.2f")
        ImGui.dragInt("Rotation", ::checkerboardRotation, 1f, 0, 360)
        ImGui.text("Texture")
        ImGui.colorEdit4("Color", checkerboardColor)
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

fun main() = Application.set(Sandbox2DApp())
import com.pumpkin.core.*
import com.pumpkin.core.event.Event
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.render.*
import com.pumpkin.platform.opengl.OpenGLShader
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import imgui.ImGui
import imgui.stb.te.delete

class Sandbox2DLayer : Layer("Sandbox2D") {
    private val cameraController = OrthographicCameraController(16f / 9f, false)
    private lateinit var squareVA: Ref<VertexArray>
    private lateinit var flatColorShader: Ref<Shader>
    private val color = Vec4()

    @ExperimentalUnsignedTypes
    override fun onAttach() {
        val squareVertices = floatArrayOf(
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f,
            0.5f, 0.5f, 0f,
            -0.5f, 0.5f, 0f,
        )

        val squareIndices = uintArrayOf(0u, 1u, 2u, 2u, 3u, 0u)

        squareVA = VertexArray.create()

        val squareVB = VertexBuffer.create(squareVertices)
        val squareLayout = BufferLayout(
            mutableListOf(
                BufferElement(ShaderDataType.Float3, "a_Position"),
            )
        )
        squareVB().layout = squareLayout
        squareVA().addVertexBuffer(squareVB.take())

        val squareIB = IndexBuffer.create(squareIndices)
        squareVA().indexBuffer = squareIB.take()

        flatColorShader = Shader.create("./src/test/resources/shaders/FlatColor.glsl")

        squareVB.release()
        squareIB.release()
    }

    override fun onDetach() {
        squareVA.release()
        flatColorShader.release()
    }

    override fun onUpdate(ts: Timestep) {
        ////// Update //////
        cameraController.onUpdate(ts)

        ////// Render //////
        Renderer.beginScene(cameraController.camera)

        flatColorShader().bind()
        (flatColorShader() as OpenGLShader).uploadUniform("u_Color", color)
        Renderer.submit(flatColorShader(), squareVA(), glm.scale(Mat4.identity, Vec3(1.5f)))

        Renderer.endScene()
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
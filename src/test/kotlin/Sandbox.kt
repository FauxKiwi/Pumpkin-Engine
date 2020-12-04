import com.pumpkin.core.*
import com.pumpkin.core.event.Event
import com.pumpkin.core.event.EventDispatcher
import com.pumpkin.core.event.KeyPressedEvent
import com.pumpkin.core.input.*
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.render.*
import com.pumpkin.core.window.Window
import com.pumpkin.platform.opengl.OpenGLShader
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import imgui.ImGui

class ExampleLayer : Layer() {
    private var cameraMoveSpeed = 30f
    private var cameraRotationSpeed = 180f

    private val cameraPosition = Vec3()
    private var cameraRotation = 0f

    private lateinit var shader: Ref<Shader>
    private lateinit var vertexArray: Ref<VertexArray>
    private lateinit var flatColorShader: Ref<Shader>
    private lateinit var squareVA: Ref<VertexArray>
    //private lateinit var textureShader: Ref<Shader>
    private lateinit var shaderLibrary: Ref<ShaderLibrary>

    private lateinit var texture: Ref<Texture2D>
    private lateinit var logoTexture: Ref<Texture2D>

    private val squareColor = Vec4(0.2f, 0.3f, 0.8f, 1.0f)

    private val scale: Mat4 = glm.scale(Mat4.identity, Vec3(0.1f))

    @ExperimentalUnsignedTypes
    override fun onAttach() {
        val vertices = floatArrayOf(
            -0.5f, -0.5f, 0f, 1f, 0f, 1f, 1f,
             0.5f, -0.5f, 0f, 0f, 1f, 1f, 1f,
             0.0f,  0.5f, 0f, 1f, 1f, 0f, 1f,
        )

        val indices = uintArrayOf(0u, 1u, 2u)

        val squareVertices = floatArrayOf(
            -0.5f, -0.5f, 0f, 0f, 0f, 0f, 0f,
             0.5f, -0.5f, 0f, 1f, 0f, 0f, 0f,
             0.5f,  0.5f, 0f, 1f, 1f, 0f, 0f,
            -0.5f,  0.5f, 0f, 0f, 1f, 0f, 0f,
        )

        val squareIndices = uintArrayOf(0u, 1u, 2u, 2u, 3u, 0u)


        vertexArray = VertexArray.create()

        val vertexBuffer = VertexBuffer.create(vertices)
        val layout = BufferLayout(
            mutableListOf(
                BufferElement(ShaderDataType.Float3, "a_Position"),
                BufferElement(ShaderDataType.Float4, "a_Color")
            )
        )
        vertexBuffer().layout = layout
        vertexArray().addVertexBuffer(vertexBuffer.take())

        val indexBuffer = IndexBuffer.create(indices)
        vertexArray().indexBuffer = indexBuffer.take()


        squareVA = VertexArray.create()

        val squareVB = VertexBuffer.create(squareVertices)
        val squareLayout = BufferLayout(
            mutableListOf(
                BufferElement(ShaderDataType.Float3, "a_Position"),
                BufferElement(ShaderDataType.Float4, "a_TexCoord")
            )
        )
        squareVB().layout = squareLayout
        squareVA().addVertexBuffer(squareVB.take())

        val squareIB = IndexBuffer.create(squareIndices)
        squareVA().indexBuffer = squareIB.take()


        val vertexSrc = """
                #version 330 core
                
                layout(location = 0) in vec3 a_Position;
                layout(location = 1) in vec4 a_Color;
                
                out vec4 v_Color;
                
                uniform mat4 u_ViewProjection;
                uniform mat4 u_Transform;
                
                void main() {
                    v_Color = a_Color;
                    gl_Position = u_ViewProjection * u_Transform * vec4(a_Position, 1.0);
                }
            """.trimIndent()

        val fragmentSrc = """
                #version 330 core
               
                in vec4 v_Color;
                
                out vec4 color;
                
                void main() {
                    color = v_Color;
                }
            """.trimIndent()

        shader = Shader.create("Triangle", vertexSrc, fragmentSrc)


        val flatColorVertexSrc = """
                #version 330 core
                
                layout(location = 0) in vec3 a_Position;
                
                uniform mat4 u_ViewProjection;
                uniform mat4 u_Transform;
                
                void main() {
                    gl_Position = u_ViewProjection * u_Transform * vec4(a_Position, 1.0);
                }
            """.trimIndent()

        val flatColorFragmentSrc = """
                #version 330 core
                
                out vec4 color;
                
                uniform vec4 u_Color;
                
                void main() {
                    color = u_Color;
                }
            """.trimIndent()

        flatColorShader = Shader.create("FlatColor", flatColorVertexSrc, flatColorFragmentSrc)

        shaderLibrary = Ref(ShaderLibrary())

        val textureShader = shaderLibrary().load("./src/test/resources/shaders/Texture.glsl")

        texture = Texture2D.create("./src/test/resources/textures/Checkerboard.png")
        logoTexture = Texture2D.create("./src/test/resources/textures/PumpkinLogo.png")

        textureShader().bind()
        (textureShader() as OpenGLShader).uploadUniform("u_Texture", 0)

        vertexBuffer.release()
        indexBuffer.release()
        squareVB.release()
        squareIB.release()
    }

    override fun onDetach() {
        shader.release()
        vertexArray.release()
        flatColorShader.release()
        squareVA.release()
        texture.release()
        logoTexture.release()
        shaderLibrary.release()
    }

    override fun onUpdate(ts: Timestep) {
        if (isKeyPressed(PE_KEY_A))
            cameraPosition -= Vec3(0.05f, 0f, 0f) * cameraMoveSpeed * ts

        if (isKeyPressed(PE_KEY_D))
            cameraPosition += Vec3(0.05f, 0f, 0f) * cameraMoveSpeed * ts

        if (isKeyPressed(PE_KEY_S))
            cameraPosition -= Vec3(0f, 0.05f, 0f) * cameraMoveSpeed * ts

        if (isKeyPressed(PE_KEY_W))
            cameraPosition += Vec3(0f, 0.05f, 0f) * cameraMoveSpeed * ts

        if (isKeyPressed(PE_KEY_Q))
            cameraRotation -= cameraRotationSpeed * ts

        if (isKeyPressed(PE_KEY_E))
            cameraRotation += cameraRotationSpeed * ts

        Application.get().camera.position = cameraPosition
        Application.get().camera.rotation = cameraRotation

        Renderer.beginScene(Application.get().camera)

        //Renderer.submit(blueShader, squareVA)
        flatColorShader().bind()
        (flatColorShader() as OpenGLShader).uploadUniform("u_Color", squareColor)
        for (y in 0..20) {
            for (x in 0..20) {
                val pos = Vec3(x * 0.11f, y * 0.11f, 0f)
                val transform: Mat4 = glm.translate(Mat4.identity, pos) * scale
                Renderer.submit(flatColorShader(), squareVA(), transform)
            }
        }

        val textureShader = shaderLibrary()["Texture"]
        texture().bind()
        Renderer.submit(textureShader, squareVA(), glm.scale(Mat4.identity, Vec3(1.5f)))
        logoTexture().bind()
        Renderer.submit(textureShader, squareVA(), glm.scale(Mat4.identity, Vec3(1.5f)))

        //Renderer.submit(shader, vertexArray)

        Renderer.endScene()
    }

    override fun onImGuiRender() {
        ImGui.begin("Camera: Transform")
        dragFloat3("Position", cameraPosition, 0.01f)
        ImGui.dragFloat("Rotation", ::cameraRotation, vMin = 0f, vMax = 360f)
        ImGui.end()
        ImGui.begin("Squares: Color")
        ImGui.colorEdit3("Color", squareColor)
        ImGui.end()
    }

    override fun onEvent(event: Event) {
        val dispatcher = EventDispatcher(event)
        dispatcher.dispatch<KeyPressedEvent> {
            if (keyCode == PE_KEY_TAB)
                logDebug("Tab is pressed (event)")
            false
        }
    }
}

class TestApplication : Application() {

    override fun init() {
        Window.getWindow().vSync = false
        pushLayer(ExampleLayer())
    }

    override fun run() {
        RendererCommand.setClearColor(Vec4(0.1f, 0.1f, 0.1f, 1.0f))
        RendererCommand.clear()
    }
}

fun main() {
    Application.set(TestApplication())
}

fun dragFloat3(label: String, vec: Vec3, speed: Float = 1f, min: Float = 0f, max: Float = 0f) {
    val vecRef = vec.toFloatArray()
    ImGui.dragFloat3(label, vecRef, speed, min, max)
    vec[0] = vecRef[0]
    vec[1] = vecRef[1]
    vec[2] = vecRef[2]
}
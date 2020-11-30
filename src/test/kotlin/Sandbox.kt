import com.pumpkin.core.*
import com.pumpkin.core.event.Event
import com.pumpkin.core.event.EventDispatcher
import com.pumpkin.core.event.KeyPressedEvent
import com.pumpkin.core.input.*
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.render.*
import com.pumpkin.core.window.Window
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import imgui.ImGui

class LogLayer : Layer() {
    private var cameraMoveSpeed = 30f
    private var cameraRotationSpeed = 180f

    private val cameraPosition = Vec3()
    private var cameraRotation = 0f

    override fun onAttach() {
        logDebug("Layer attached")
    }

    override fun onDetach() {
        logDebug("Layer detached")
    }

    override fun onUpdate(ts: Timestep) {
        if (isKeyPressed(PK_KEY_A))
            cameraPosition -= Vec3(0.05f, 0f, 0f) * cameraMoveSpeed * ts

        if (isKeyPressed(PK_KEY_D))
            cameraPosition += Vec3(0.05f, 0f, 0f) * cameraMoveSpeed * ts

        if (isKeyPressed(PK_KEY_S))
            cameraPosition -= Vec3(0f, 0.05f, 0f) * cameraMoveSpeed * ts

        if (isKeyPressed(PK_KEY_W))
            cameraPosition += Vec3(0f, 0.05f, 0f) * cameraMoveSpeed * ts

        if (isKeyPressed(PK_KEY_Q))
            cameraRotation -= cameraRotationSpeed * ts

        if (isKeyPressed(PK_KEY_E))
            cameraRotation += cameraRotationSpeed * ts

        Application.get().camera.position = cameraPosition
        Application.get().camera.rotation = cameraRotation
    }

    override fun onImGuiRender() {
        ImGui.begin("Camera: Transform")
        val cpRef = cameraPosition.toFloatArray()
        ImGui.dragFloat3("Position", cpRef, 0.01f)
        cameraPosition[0] = cpRef[0]
        cameraPosition[1] = cpRef[1]
        cameraPosition[2] = cpRef[2]
        ImGui.dragFloat("Rotation", ::cameraRotation)
        ImGui.end()
    }

    override fun onEvent(event: Event) {
        val dispatcher = EventDispatcher(event)
        dispatcher.dispatch<KeyPressedEvent> {
            if (keyCode == PK_KEY_TAB)
                logDebug("Tab is pressed (event)")
            false
        }
    }
}

class TestApplication : Application() {
    private lateinit var shader: Shader
    private lateinit var vertexArray: VertexArray
    private lateinit var blueShader: Shader
    private lateinit var squareVA: VertexArray

    private val scale: Mat4 = glm.scale(Mat4.identity, Vec3(0.1f))

    override fun init() {
        //Window.getWindow().setVSync(false)

        pushLayer(LogLayer())

        val vertices = floatArrayOf(
            -0.5f, -0.5f, 0f, 1f, 0f, 1f, 1f,
            0.5f, -0.5f, 0f, 0f, 1f, 1f, 1f,
            0.0f, 0.5f, 0f, 1f, 1f, 0f, 1f,
        )

        val indices = intArrayOf(0, 1, 2)

        val squareVertices = floatArrayOf(
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f,
            0.5f, 0.5f, 0f,
            -0.5f, 0.5f, 0f,
        )

        val squareIndices = intArrayOf(0, 1, 2, 2, 3, 0)


        vertexArray = VertexArray.create()

        val vertexBuffer = VertexBuffer.create(vertices)
        val layout = BufferLayout(
            mutableListOf(
                BufferElement(ShaderDataType.Float3, "a_Position"),
                BufferElement(ShaderDataType.Float4, "a_Color")
            )
        )
        vertexBuffer.layout = layout
        vertexArray.vertexBuffers.add(vertexBuffer)

        val indexBuffer = IndexBuffer.create(indices)
        vertexArray.indexBuffer = indexBuffer


        squareVA = VertexArray.create()

        val squareVB = VertexBuffer.create(squareVertices)
        val squareLayout = BufferLayout(
            mutableListOf(
                BufferElement(ShaderDataType.Float3, "a_Position")
            )
        )
        squareVB.layout = squareLayout
        squareVA.vertexBuffers.add(squareVB)

        val squareIB = IndexBuffer.create(squareIndices)
        squareVA.indexBuffer = squareIB


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

        shader = Shader.create(vertexSrc, fragmentSrc)

        val b_vertexSrc = """
                #version 330 core
                
                layout(location = 0) in vec3 a_Position;
                
                uniform mat4 u_ViewProjection;
                uniform mat4 u_Transform;
                
                void main() {
                    gl_Position = u_ViewProjection * u_Transform * vec4(a_Position, 1.0);
                }
            """.trimIndent()

        val b_fragmentSrc = """
                #version 330 core
                
                out vec4 color;
                
                void main() {
                    color = vec4(0.2, 0.3, 0.8, 1.0);
                }
            """.trimIndent()

        blueShader = Shader.create(b_vertexSrc, b_fragmentSrc)

    }

    override fun run() {
        RendererCommand.setClearColor(Vec4(0.1f, 0.1f, 0.1f, 1.0f))
        RendererCommand.clear()

        Renderer.beginScene(camera)

        //Renderer.submit(blueShader, squareVA)
        for (y in 0..20) {
            for (x in 0..20) {
                val pos = Vec3(x * 0.11f, y * 0.11f, 0f)
                val transform: Mat4 = glm.translate(Mat4.identity, pos) * scale
                Renderer.submit(blueShader, squareVA, transform)
            }
        }
        Renderer.submit(shader, vertexArray)

        Renderer.endScene()
    }
}

fun main() {
    Application.set(TestApplication())
}
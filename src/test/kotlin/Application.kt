import com.pumpkin.core.Application
import com.pumpkin.core.event.Event
import com.pumpkin.core.event.EventDispatcher
import com.pumpkin.core.event.KeyPressedEvent
import com.pumpkin.core.input.*
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.logDebug
import com.pumpkin.core.render.*
import glm_.vec3.Vec3
import glm_.vec4.Vec4

class LogLayer : Layer() {

    override fun onAttach() {
        logDebug("Layer attached")
    }

    override fun onDetach() {
        logDebug("Layer detached")
    }

    override fun onUpdate() {
        if (isKeyPressed(PK_KEY_A))
            Application.get().camera.position = Application.get().camera.position - Vec3(0.05f, 0f, 0f)

        if (isKeyPressed(PK_KEY_D))
            Application.get().camera.position = Application.get().camera.position + Vec3(0.05f, 0f, 0f)

        if (isKeyPressed(PK_KEY_S))
            Application.get().camera.position = Application.get().camera.position - Vec3(0f, 0.05f, 0f)

        if (isKeyPressed(PK_KEY_W))
            Application.get().camera.position = Application.get().camera.position + Vec3(0f, 0.05f, 0f)

    }

    override fun onEvent(event: Event) {
        val dispatcher = EventDispatcher(event)
        dispatcher.dispatch<KeyPressedEvent> {
            if (it.keyCode == PK_KEY_TAB)
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

    override fun init() {
        pushLayer(LogLayer())

        val vertices = floatArrayOf(
            -0.5f, -0.5f, 0f, 1f, 0f, 1f, 1f,
            0.5f, -0.5f, 0f, 0f, 1f, 1f, 1f,
            0.0f, 0.5f, 0f, 1f, 1f, 0f, 1f,
        )

        val indices = intArrayOf(0, 1, 2)

        val squareVertices = floatArrayOf(
            -0.75f, -0.75f, 0f,
            0.75f, -0.75f, 0f,
            0.75f, 0.75f, 0f,
            -0.75f, 0.75f, 0f,
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
                
                void main() {
                    v_Color = a_Color;
                    gl_Position = u_ViewProjection * vec4(a_Position, 1.0);
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
                
                void main() {
                    gl_Position = u_ViewProjection * vec4(a_Position, 1.0);
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

        Renderer.submit(blueShader, squareVA)
        Renderer.submit(shader, vertexArray)

        Renderer.endScene()
    }
}

fun main() {
    Application.set(TestApplication())
}
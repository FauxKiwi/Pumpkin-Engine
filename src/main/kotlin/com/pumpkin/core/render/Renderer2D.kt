package com.pumpkin.core.render

import com.pumpkin.core.Ref
import com.pumpkin.core.Scope
import com.pumpkin.core.stack
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import java.nio.ByteBuffer
import java.nio.FloatBuffer

object Renderer2D {
    lateinit var camera: OrthographicCamera
    lateinit var data: Scope<Renderer2DData>

    var maxQuads = 100000
        set(value) {
            field = value
            maxVertices = maxQuads * 4
            maxIndices = maxQuads * 4
            data().quadVertexBufferData = FloatBuffer.allocate(maxVertices * sizeOfQuadVertex)
        }
    var maxVertices = maxQuads * 4
    var maxIndices = maxQuads * 4
    var maxTextureSlots = 32

    fun init() {
        data =
            Scope(
                Renderer2DData(
                    VertexArray.create(),
                    VertexBuffer.create(maxVertices * sizeOfQuadVertex),
                    Shader.create("./src/main/resources/shaders/Texture.glsl"),
                    Texture2D.create(1, 1)
                )
            )
        data().textureSlots = arrayOfNulls(maxTextureSlots)

        val whiteTextureData = ByteBuffer.allocateDirect(4).apply {
            put(0, 0xff.toByte())
            put(1, 0xff.toByte())
            put(2, 0xff.toByte())
            put(3, 0xff.toByte())
        }
        data().whiteTexture().setData(whiteTextureData, whiteTextureData.capacity())

        val samplers = IntArray(maxTextureSlots) { it }


        data().quadVertexBuffer().layout = BufferLayout(
            mutableListOf(
                BufferElement(ShaderDataType.Float3, "a_Position"),
                BufferElement(ShaderDataType.Float4, "a_Color"),
                BufferElement(ShaderDataType.Float2, "a_TexCoord"),
                BufferElement(ShaderDataType.Float, "a_TexIndex"),
                BufferElement(ShaderDataType.Float, "a_TilingFactor"),
            )
        )
        data().quadVertexArray().addVertexBuffer(data().quadVertexBuffer.take())

        data().quadVertexBufferData = FloatBuffer.allocate(maxVertices * sizeOfQuadVertex)

        val quadIndices = UIntArray(maxIndices) { it.toUInt() }

        val quadIB = IndexBuffer.create(quadIndices)
        data().quadVertexArray().indexBuffer = quadIB.take()

        //data().textureShader().setInt("u_Texture", 0)
        data().textureShader().bind()
        data().textureShader().setIntArray("u_Textures", samplers)

        data().textureSlots[0] = data().whiteTexture()

        data().quadVertexBuffer.release()
        quadIB.release()
    }

    fun shutdown() = data.close()

    fun beginScene(camera: OrthographicCamera) {
        this.camera = camera
        data().textureShader().run {
            bind()
            setMat4("u_ViewProjection", camera.viewProjectionMatrix)
        }
        data().quadIndexCount = 0
        data().textureSlotIndex = 1
    }

    fun endScene() {
        val array = FloatArray(data().quadVertexBufferData.position())
        data().quadVertexBufferData.position(0)
        data().quadVertexBufferData[array, 0, array.size]
        data().quadVertexBufferData.position(0)
        data().quadVertexBuffer().setData(array)
        flush()
    }

    private fun flush() {
        for (i in 0 until data().textureSlotIndex) data().textureSlots[i]!!.bind(i)
        RendererCommand.drawIndexed(data().quadVertexArray(), data().quadIndexCount)
    }

    fun drawQuad(position: Vec2 = Vec2(0f), size: Vec2 = Vec2(1f), rotation: Float = 0f, color: Vec4) =
        drawQuad(Vec3(position, 0), size, rotation, color)

    private const val whiteTextureID = 0f
    private const val whiteTextureTilingFactor = 1f
    fun drawQuad(position: Vec3 = Vec3(0f), size: Vec2 = Vec2(1f), rotation: Float = 0f, color: Vec4) = stack {
        if (data().quadVertexBufferData.position() >= maxQuads) {
            endScene()
            beginScene(camera)
        }

        data().quadVertexBufferData.put(position.x); data().quadVertexBufferData.put(position.y); data().quadVertexBufferData.put(position.z)
        data().quadVertexBufferData.put(color)
        data().quadVertexBufferData.put(0f); data().quadVertexBufferData.put(0f)
        data().quadVertexBufferData.put(whiteTextureID)
        data().quadVertexBufferData.put(whiteTextureTilingFactor)

        data().quadVertexBufferData.put(position.x + size.x); data().quadVertexBufferData.put(position.y); data().quadVertexBufferData.put(position.z)
        data().quadVertexBufferData.put(color)
        data().quadVertexBufferData.put(1f); data().quadVertexBufferData.put(0f)
        data().quadVertexBufferData.put(whiteTextureID)
        data().quadVertexBufferData.put(whiteTextureTilingFactor)

        data().quadVertexBufferData.put(position.x + size.x); data().quadVertexBufferData.put(position.y + size.y); data().quadVertexBufferData.put(position.z)
        data().quadVertexBufferData.put(color)
        data().quadVertexBufferData.put(1f); data().quadVertexBufferData.put(1f)
        data().quadVertexBufferData.put(whiteTextureID)
        data().quadVertexBufferData.put(whiteTextureTilingFactor)

        data().quadVertexBufferData.put(position.x); data().quadVertexBufferData.put(position.y + size.y); data().quadVertexBufferData.put(position.z)
        data().quadVertexBufferData.put(color)
        data().quadVertexBufferData.put(0f); data().quadVertexBufferData.put(1f)
        data().quadVertexBufferData.put(whiteTextureID)
        data().quadVertexBufferData.put(whiteTextureTilingFactor)

        data().quadIndexCount += 4
    }

    fun drawQuad(position: Vec2 = Vec2(0f), size: Vec2 = Vec2(1f), rotation: Float = 0f, texture: Texture2D, color: Vec4 = Vec4(1f), tilingFactor: Float = 1f) =
        drawQuad(Vec3(position, 0), size, rotation, texture, color, tilingFactor)

    fun drawQuad(position: Vec3 = Vec3(0f), size: Vec2 = Vec2(1f), rotation: Float = 0f, texture: Texture2D, color: Vec4 = Vec4(1f), tilingFactor: Float = 1f) = stack {
        if (data().quadVertexBufferData.position() >= maxQuads) {
            endScene()
            beginScene(camera)
        }

        var textureIndex = 0f
        for (i in 0 until data().textureSlotIndex) {
            if (data().textureSlots[i]!!.equals(texture)) {
                textureIndex = i.toFloat()
                break
            }
        }

        if (textureIndex == 0f) {
            textureIndex = data().textureSlotIndex.toFloat()
            data().textureSlots[data().textureSlotIndex] = texture
            data().textureSlotIndex++
        }

        data().quadVertexBufferData.put(position.x); data().quadVertexBufferData.put(position.y); data().quadVertexBufferData.put(position.z)
        data().quadVertexBufferData.put(color)
        data().quadVertexBufferData.put(0f); data().quadVertexBufferData.put(0f)
        data().quadVertexBufferData.put(textureIndex)
        data().quadVertexBufferData.put(tilingFactor)

        data().quadVertexBufferData.put(position.x + size.x); data().quadVertexBufferData.put(position.y); data().quadVertexBufferData.put(position.z)
        data().quadVertexBufferData.put(color)
        data().quadVertexBufferData.put(1f); data().quadVertexBufferData.put(0f)
        data().quadVertexBufferData.put(textureIndex)
        data().quadVertexBufferData.put(tilingFactor)

        data().quadVertexBufferData.put(position.x + size.x); data().quadVertexBufferData.put(position.y + size.y); data().quadVertexBufferData.put(position.z)
        data().quadVertexBufferData.put(color)
        data().quadVertexBufferData.put(1f); data().quadVertexBufferData.put(1f)
        data().quadVertexBufferData.put(textureIndex)
        data().quadVertexBufferData.put(tilingFactor)

        data().quadVertexBufferData.put(position.x); data().quadVertexBufferData.put(position.y + size.y); data().quadVertexBufferData.put(position.z)
        data().quadVertexBufferData.put(color)
        data().quadVertexBufferData.put(0f); data().quadVertexBufferData.put(1f)
        data().quadVertexBufferData.put(textureIndex)
        data().quadVertexBufferData.put(tilingFactor)

        data().quadIndexCount += 4
    }
}

inline fun FloatBuffer.put(color: Vec4) {
    put(color.x); put(color.y); put(color.z); put(color.w)
}

// QuadVertex data class: Just for layout purposes
/* data class QuadVertex(
    val position: Vec3,
    val color: Vec4,
    val texCoord: Vec2,
    val texIndex: Float,
    val tilingFactor: Float
)*/
const val sizeOfQuadVertex = 11 // 3 + 4 + 2 + 1 + 1

data class Renderer2DData(
    var quadVertexArray: Ref<VertexArray>,
    var quadVertexBuffer: Ref<VertexBuffer>,
    var textureShader: Ref<Shader>,
    var whiteTexture: Ref<Texture2D>,
) : AutoCloseable {
    var quadIndexCount = 0
    lateinit var quadVertexBufferData: FloatBuffer
    lateinit var textureSlots: Array<Texture2D?>
    var textureSlotIndex = 1

    override fun close() {
        quadVertexArray.release()
        textureShader.release()
        whiteTexture.release()
    }
}
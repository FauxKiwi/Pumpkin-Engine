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

// QuadVertex data class: Just for layout purposes
/* data class QuadVertex(
    val position: Vec3,
    val color: Vec4,
    val texCoord: Vec2,
    val texIndex: Float,
    val tilingFactor: Float
)*/
const val sizeOfQuadVertex = 11 // 3 + 4 + 2 + 1 + 1

object Renderer2D {
    lateinit var camera: OrthographicCamera

    var quadIndexCount = 0
    lateinit var quadVertexBufferData: FloatBuffer
    lateinit var textureSlots: Array<Texture2D?>
    var textureSlotIndex = 1

    var maxQuads = 100000
        set(value) {
            field = value
            maxVertices = maxQuads * 4
            maxIndices = maxQuads * 4
            quadVertexBufferData = FloatBuffer.allocate(maxVertices * sizeOfQuadVertex)
        }
    var maxVertices = maxQuads * 4
    var maxIndices = maxQuads * 4
    var maxTextureSlots = 32

    var quadVertexArray = VertexArray.create()
    var quadVertexBuffer = VertexBuffer.create(maxVertices * sizeOfQuadVertex)
    var textureShader = Shader.create("./src/main/resources/shaders/Texture.glsl")
    var whiteTexture = Texture2D.create(1, 1)


    fun init() {
        textureSlots = arrayOfNulls(maxTextureSlots)

        val whiteTextureData = ByteBuffer.allocateDirect(4).apply {
            put(0, 0xff.toByte())
            put(1, 0xff.toByte())
            put(2, 0xff.toByte())
            put(3, 0xff.toByte())
        }
        whiteTexture().setData(whiteTextureData, whiteTextureData.capacity())

        val samplers = IntArray(maxTextureSlots) { it }

        quadVertexBuffer().layout = BufferLayout(
            mutableListOf(
                BufferElement(ShaderDataType.Float3, "a_Position"),
                BufferElement(ShaderDataType.Float4, "a_Color"),
                BufferElement(ShaderDataType.Float2, "a_TexCoord"),
                BufferElement(ShaderDataType.Float, "a_TexIndex"),
                BufferElement(ShaderDataType.Float, "a_TilingFactor"),
            )
        )
        quadVertexArray().addVertexBuffer(quadVertexBuffer.take())

        quadVertexBufferData = FloatBuffer.allocate(maxVertices * sizeOfQuadVertex)

        val quadIndices = UIntArray(maxIndices) { it.toUInt() }

        val quadIB = IndexBuffer.create(quadIndices)
        quadVertexArray().indexBuffer = quadIB.take()

        //textureShader().setInt("u_Texture", 0)
        textureShader().bind()
        textureShader().setIntArray("u_Textures", samplers)

        textureSlots[0] = whiteTexture()

        quadVertexBuffer.release()
        quadIB.release()
    }

    fun shutdown() {
        quadVertexArray.release()
        quadVertexBuffer.release()
        whiteTexture.release()
    }

    fun beginScene(camera: OrthographicCamera) {
        this.camera = camera
        textureShader().run {
            bind()
            setMat4("u_ViewProjection", camera.viewProjectionMatrix)
        }
        quadIndexCount = 0
        textureSlotIndex = 1
    }

    fun endScene() {
        val array = FloatArray(quadVertexBufferData.position())
        quadVertexBufferData.position(0)
        quadVertexBufferData[array, 0, array.size]
        quadVertexBufferData.position(0)
        quadVertexBuffer().setData(array)
        flush()
    }

    private fun flush() {
        for (i in 0 until textureSlotIndex) textureSlots[i]!!.bind(i)
        RendererCommand.drawIndexed(quadVertexArray(), quadIndexCount)
    }

    fun drawQuad(position: Vec2 = Vec2(0f), size: Vec2 = Vec2(1f), rotation: Float = 0f, color: Vec4) =
        drawQuad(Vec3(position, 0), size, rotation, color)

    private const val whiteTextureID = 0f
    private const val whiteTextureTilingFactor = 1f
    fun drawQuad(position: Vec3 = Vec3(0f), size: Vec2 = Vec2(1f), rotation: Float = 0f, color: Vec4) = stack {
        if (quadVertexBufferData.position() >= maxQuads) {
            endScene()
            beginScene(camera)
        }

        quadVertexBufferData.put(position.x); quadVertexBufferData.put(position.y); quadVertexBufferData.put(position.z)
        quadVertexBufferData.put(color)
        quadVertexBufferData.put(0f); quadVertexBufferData.put(0f)
        quadVertexBufferData.put(whiteTextureID)
        quadVertexBufferData.put(whiteTextureTilingFactor)

        quadVertexBufferData.put(position.x + size.x); quadVertexBufferData.put(position.y); quadVertexBufferData.put(position.z)
        quadVertexBufferData.put(color)
        quadVertexBufferData.put(1f); quadVertexBufferData.put(0f)
        quadVertexBufferData.put(whiteTextureID)
        quadVertexBufferData.put(whiteTextureTilingFactor)

        quadVertexBufferData.put(position.x + size.x); quadVertexBufferData.put(position.y + size.y); quadVertexBufferData.put(position.z)
        quadVertexBufferData.put(color)
        quadVertexBufferData.put(1f); quadVertexBufferData.put(1f)
        quadVertexBufferData.put(whiteTextureID)
        quadVertexBufferData.put(whiteTextureTilingFactor)

        quadVertexBufferData.put(position.x); quadVertexBufferData.put(position.y + size.y); quadVertexBufferData.put(position.z)
        quadVertexBufferData.put(color)
        quadVertexBufferData.put(0f); quadVertexBufferData.put(1f)
        quadVertexBufferData.put(whiteTextureID)
        quadVertexBufferData.put(whiteTextureTilingFactor)

        quadIndexCount += 4
    }

    fun drawQuad(position: Vec2 = Vec2(0f), size: Vec2 = Vec2(1f), rotation: Float = 0f, texture: Texture2D, color: Vec4 = Vec4(1f), tilingFactor: Float = 1f) =
        drawQuad(Vec3(position, 0), size, rotation, texture, color, tilingFactor)

    fun drawQuad(position: Vec3 = Vec3(0f), size: Vec2 = Vec2(1f), rotation: Float = 0f, texture: Texture2D, color: Vec4 = Vec4(1f), tilingFactor: Float = 1f) = stack {
        if (quadVertexBufferData.position() >= maxQuads) {
            endScene()
            beginScene(camera)
        }

        var textureIndex = 0f
        for (i in 0 until textureSlotIndex) {
            if (textureSlots[i]!!.equals(texture)) {
                textureIndex = i.toFloat()
                break
            }
        }

        if (textureIndex == 0f) {
            textureIndex = textureSlotIndex.toFloat()
            textureSlots[textureSlotIndex] = texture
            textureSlotIndex++
        }

        quadVertexBufferData.put(position.x); quadVertexBufferData.put(position.y); quadVertexBufferData.put(position.z)
        quadVertexBufferData.put(color)
        quadVertexBufferData.put(0f); quadVertexBufferData.put(0f)
        quadVertexBufferData.put(textureIndex)
        quadVertexBufferData.put(tilingFactor)

        quadVertexBufferData.put(position.x + size.x); quadVertexBufferData.put(position.y); quadVertexBufferData.put(position.z)
        quadVertexBufferData.put(color)
        quadVertexBufferData.put(1f); quadVertexBufferData.put(0f)
        quadVertexBufferData.put(textureIndex)
        quadVertexBufferData.put(tilingFactor)

        quadVertexBufferData.put(position.x + size.x); quadVertexBufferData.put(position.y + size.y); quadVertexBufferData.put(position.z)
        quadVertexBufferData.put(color)
        quadVertexBufferData.put(1f); quadVertexBufferData.put(1f)
        quadVertexBufferData.put(textureIndex)
        quadVertexBufferData.put(tilingFactor)

        quadVertexBufferData.put(position.x); quadVertexBufferData.put(position.y + size.y); quadVertexBufferData.put(position.z)
        quadVertexBufferData.put(color)
        quadVertexBufferData.put(0f); quadVertexBufferData.put(1f)
        quadVertexBufferData.put(textureIndex)
        quadVertexBufferData.put(tilingFactor)

        quadIndexCount += 4
    }
}

inline fun FloatBuffer.put(color: Vec4) {
    put(color.x); put(color.y); put(color.z); put(color.w)
}

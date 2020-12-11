package com.pumpkin.core.render

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
    private var quadIndexCount = 0
    private lateinit var quadVertexBufferData: FloatBuffer
    private lateinit var textureSlots: Array<Texture2D?>
    private var textureSlotIndex = 1

    var maxQuads = 300
        set(value) {
            field = value
            maxVertices = maxQuads * 4
            maxIndices = maxQuads * 4
            quadVertexBufferData = FloatBuffer.allocate(maxVertices * sizeOfQuadVertex)
        }
    private var maxVertices = maxQuads * 4
    private var maxIndices = maxQuads * 4
    var maxTextureSlots = 32

    private var quadVertexArray = VertexArray.create()
    private var quadVertexBuffer = VertexBuffer.create(maxVertices * sizeOfQuadVertex)
    private var textureShader = Shader.create("./src/main/resources/shaders/Texture.glsl")
    private var whiteTexture = Texture2D.create(1, 1)

    private lateinit var quadVertexPositions: Array<Vec4>

    var quadCount = 0
    var drawCalls = 0

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

        quadVertexPositions = arrayOf(
            Vec4(-0.5f, -0.5f, 0.0f, 1.0f),
            Vec4( 0.5f, -0.5f, 0.0f, 1.0f),
            Vec4( 0.5f,  0.5f, 0.0f, 1.0f),
            Vec4(-0.5f,  0.5f, 0.0f, 1.0f),
        )

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
        textureShader().run {
            bind()
            setMat4("u_ViewProjection", camera.viewProjectionMatrix)
        }
        quadIndexCount = 0
        textureSlotIndex = 1

        quadCount = 0
        drawCalls = 1
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

    private fun flushAndReset() {
        endScene()

        quadIndexCount = 0
        quadVertexBufferData.position(0)

        textureSlotIndex = 1

        drawCalls++
    }


    private val textureIndices = arrayOf(0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f)

    fun drawQuad(position: Vec2 = Vec2(0f), size: Vec2 = Vec2(1f), radians: Float = 0f, color: Vec4) =
        drawQuad(Vec3(position, 0), size, radians, color)

    private const val whiteTextureID = 0f
    private const val whiteTextureTilingFactor = 1f
    fun drawQuad(position: Vec3 = Vec3(0f), size: Vec2 = Vec2(1f), radians: Float = 0f, color: Vec4) = stack {
        if (quadVertexBufferData.position() >= maxVertices * sizeOfQuadVertex) {
            flushAndReset()
        }

        val transform = transformMatrix(position, size, radians)

        for (i in 0..3) {
            quadVertexBufferData.put(
                transform * quadVertexPositions[i],
                color,
                textureIndices[2*i],
                textureIndices[2*i + 1],
                whiteTextureID,
                whiteTextureTilingFactor
            )
        }

        quadIndexCount += 4

        quadCount++
    }

    fun drawQuad(position: Vec2 = Vec2(0f), size: Vec2 = Vec2(1f), radians: Float = 0f, texture: Texture2D, color: Vec4 = Vec4(1f), tilingFactor: Float = 1f) =
        drawQuad(Vec3(position, 0), size, radians, texture, color, tilingFactor)

    fun drawQuad(position: Vec3 = Vec3(0f), size: Vec2 = Vec2(1f), radians: Float = 0f, texture: Texture2D, color: Vec4 = Vec4(1f), tilingFactor: Float = 1f) = stack {
        if (quadVertexBufferData.position() >= maxQuads) {
            flushAndReset()
        }

        var textureIndex = 0f
        for (i in 0 until textureSlotIndex) {
            if (textureSlots[i]!! == texture) {
                textureIndex = i.toFloat()
                break
            }
        }

        if (textureIndex == 0f) {
            if (textureSlotIndex >= maxTextureSlots) {
                flushAndReset()
            }

            textureIndex = textureSlotIndex.toFloat()
            textureSlots[textureSlotIndex] = texture
            textureSlotIndex++
        }

        val transform = transformMatrix(position, size, radians)

        for (i in 0..3) {
            quadVertexBufferData.put(
                transform * quadVertexPositions[i],
                color,
                textureIndices[2*i],
                textureIndices[2*i + 1],
                textureIndex,
                tilingFactor
            )
        }

        quadIndexCount += 4

        quadCount++
    }
}

inline fun transformMatrix(position: Vec3, scale: Vec2, rotation: Float): Mat4 = if (rotation == 0f)
    glm.translate(Mat4.identity, position) * glm.scale(Mat4.identity, scale.x, scale.y, 1f)
else
    glm.translate(Mat4.identity, position) * glm.rotate(Mat4.identity, rotation, 0f, 0f, 1f) * glm.scale(Mat4.identity, scale.x, scale.y, 1f)

inline fun FloatBuffer.put(position: Vec4, color: Vec4, texCoordX: Float, texCoordY: Float, textureIndex: Float, tilingFactor: Float) {
    put(position.x); put(position.y); put(position.z);
    put(color.x); put(color.y); put(color.z); put(color.w);
    put(texCoordX); put(texCoordY);
    put(textureIndex)
    put(tilingFactor)
}

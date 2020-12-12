package com.pumpkin.core.renderer

import com.pumpkin.core.stack
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import java.nio.ByteBuffer
import java.nio.FloatBuffer

// QuadVertex data class: Just for layout purposes
/* data class QuadVertex(
    val position: Vec3,
    val size: Vec2,
    val rotation: Float,
    val color: Vec4,
    val texCoord: Vec4,
    val texIndex: Float,
    val tilingFactor: Float
)*/
const val sizeOfQuadVertex = 3 + 2 + 1 + 4 + 4 + 1 + 1

object Renderer2D {
    //private var quadIndexCount = 0
    private lateinit var quadVertexBufferData: FloatBuffer
    private lateinit var textureSlots: Array<Texture2D?>
    private var textureSlotIndex = 1

    var maxQuads = 1200
        set(value) {
            field = value
            maxVertices = maxQuads
            maxIndices = maxQuads
            quadVertexBufferData = FloatBuffer.allocate(maxVertices * sizeOfQuadVertex)
        }
    private var maxVertices = maxQuads
    private var maxIndices = maxQuads
    var maxTextureSlots = 32

    private var quadVertexArray = VertexArray.create()
    private var quadVertexBuffer = VertexBuffer.create(maxVertices * sizeOfQuadVertex)
    private var textureShader = Shader.create("./src/main/resources/shaders/Texture.glsl")
    private var whiteTexture = Texture2D.create(1, 1)

    //private lateinit var quadVertexPositions: Array<Vec4>

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
                BufferElement(ShaderDataType.Float2, "a_Size"),
                BufferElement(ShaderDataType.Float, "a_Rotation"),
                BufferElement(ShaderDataType.Float4, "a_Color"),
                BufferElement(ShaderDataType.Float4, "a_TexCoords"),
                BufferElement(ShaderDataType.Float, "a_TexIndex"),
                BufferElement(ShaderDataType.Float, "a_TilingFactor"),
            )
        )
        quadVertexArray().addVertexBuffer(quadVertexBuffer.take())

        quadVertexBufferData = FloatBuffer.allocate(maxVertices * sizeOfQuadVertex)

        /*quadVertexPositions = arrayOf(
            Vec4(-0.5f, -0.5f, 0.0f, 1.0f),
            Vec4( 0.5f, -0.5f, 0.0f, 1.0f),
            Vec4( 0.5f,  0.5f, 0.0f, 1.0f),
            Vec4(-0.5f,  0.5f, 0.0f, 1.0f),
        )*/

        //val quadIndices = UIntArray(maxIndices) { it.toUInt() }

        //val quadIB = IndexBuffer.create(quadIndices)
        //quadVertexArray().indexBuffer = quadIB.take()

        //textureShader().setInt("u_Texture", 0)
        textureShader().bind()
        textureShader().setIntArray("u_Textures", samplers)

        textureSlots[0] = whiteTexture()

        quadVertexBuffer.release()
        //quadIB.release()
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
        if (quadCount == 0) return
        for (i in 0 until textureSlotIndex) textureSlots[i]!!.bind(i)
        RendererCommand.drawIndexed(quadVertexArray(), quadCount)
    }

    fun flushAndReset() {
        endScene()

        quadVertexBufferData.position(0)

        textureSlotIndex = 1

        drawCalls++
    }

    fun drawQuad(position: Vec2 = Vec2(), size: Vec2 = Vec2(1f), radians: Float = 0f, color: Vec4) =
        drawQuad(Vec3(position, 0), size, radians, color)

    private const val whiteTextureID = 0f
    private const val whiteTextureTilingFactor = 1f
    fun drawQuad(position: Vec3, size: Vec2 = Vec2(1f), radians: Float = 0f, color: Vec4) = stack {
        if (quadVertexBufferData.position() >= maxVertices * sizeOfQuadVertex) {
            flushAndReset()
        }

        quadVertexBufferData.put(position.x); quadVertexBufferData.put(position.y); quadVertexBufferData.put(position.z)
        quadVertexBufferData.put(size.x); quadVertexBufferData.put(size.y)
        quadVertexBufferData.put(radians)
        quadVertexBufferData.put(color.x); quadVertexBufferData.put(color.y); quadVertexBufferData.put(color.z); quadVertexBufferData.put(color.w)
        quadVertexBufferData.put(0f); quadVertexBufferData.put(0f); quadVertexBufferData.put(1f); quadVertexBufferData.put(1f)
        quadVertexBufferData.put(whiteTextureID)
        quadVertexBufferData.put(whiteTextureTilingFactor)

        quadCount++
    }

    fun drawQuad(position: Vec2 = Vec2(), size: Vec2 = Vec2(1f), radians: Float = 0f, texture: Texture2D, color: Vec4 = Vec4(1f), tilingFactor: Float = 1f) =
        drawQuad(Vec3(position, 0), size, radians, texture, color, tilingFactor)

    fun drawQuad(position: Vec3, size: Vec2 = Vec2(1f), radians: Float = 0f, texture: Texture2D, color: Vec4 = Vec4(1f), tilingFactor: Float = 1f) = stack {
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

        quadVertexBufferData.put(position.x); quadVertexBufferData.put(position.y); quadVertexBufferData.put(position.z)
        quadVertexBufferData.put(size.x); quadVertexBufferData.put(size.y)
        quadVertexBufferData.put(radians)
        quadVertexBufferData.put(color.x); quadVertexBufferData.put(color.y); quadVertexBufferData.put(color.z); quadVertexBufferData.put(color.w)
        quadVertexBufferData.put(0f); quadVertexBufferData.put(0f); quadVertexBufferData.put(1f); quadVertexBufferData.put(1f)
        quadVertexBufferData.put(textureIndex)
        quadVertexBufferData.put(tilingFactor)

        quadCount++
    }

    fun drawQuad(position: Vec2 = Vec2(), size: Vec2 = Vec2(1f), radians: Float = 0f, texture: Texture2D, color: Vec4 = Vec4(1f), tilingFactor: Float = 1f,
                 subTextureCoord: Vec2, subTextureSize: Vec2, subTextureTileSize: Vec2) =
        drawQuad(Vec3(position, 0f), size, radians, texture, color, tilingFactor, subTextureCoord, subTextureSize, subTextureTileSize)

    fun drawQuad(position: Vec3, size: Vec2 = Vec2(1f), radians: Float = 0f, texture: Texture2D, color: Vec4 = Vec4(1f), tilingFactor: Float = 1f,
                 subTextureCoord: Vec2, subTextureSize: Vec2, subTextureTileSize: Vec2) = stack {
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

        val texCoords = floatArrayOf(
            (subTextureCoord.x * subTextureTileSize.x) / texture.width,
            (subTextureCoord.y * subTextureTileSize.y) / texture.height,
            ((subTextureCoord.x + subTextureSize.x) * subTextureTileSize.x) / texture.width,
            ((subTextureCoord.y + subTextureSize.y) * subTextureTileSize.y) / texture.height,
        )

        quadVertexBufferData.put(position.x); quadVertexBufferData.put(position.y); quadVertexBufferData.put(position.z)
        quadVertexBufferData.put(size.x); quadVertexBufferData.put(size.y)
        quadVertexBufferData.put(radians)
        quadVertexBufferData.put(color.x); quadVertexBufferData.put(color.y); quadVertexBufferData.put(color.z); quadVertexBufferData.put(color.w)
        quadVertexBufferData.put(texCoords[0]); quadVertexBufferData.put(texCoords[1]); quadVertexBufferData.put(texCoords[2]); quadVertexBufferData.put(texCoords[3])
        quadVertexBufferData.put(textureIndex)
        quadVertexBufferData.put(tilingFactor)

        quadCount++
    }
}
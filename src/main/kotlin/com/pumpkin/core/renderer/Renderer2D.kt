package com.pumpkin.core.renderer

import com.pumpkin.core.EditorCamera
import com.pumpkin.core.math.inverseTransform
import com.pumpkin.core.math.transform
import com.pumpkin.core.stack
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.quat.Quat
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer

// QuadVertex data class: Just for layout purposes
/* struct class QuadVertex(
    val position: Vec3,
    val color: Vec4,
    val texCoord: Vec2,
    val texIndex: Float,
    val tilingFactor: Float
)*/
const val sizeOfQuadVertex = 11 //3 + 4 + 2 + 1 + 1

object Renderer2D {
    private var quadIndexCount = 0
    private lateinit var quadVertexBufferData: FloatBuffer
    private lateinit var textureSlots: Array<Texture2D?>
    private var textureSlotIndex = 1

    var maxQuads = 300
        set(value) {
            field = value
            maxVertices = maxQuads * 4
            maxIndices = maxQuads * 6
            quadVertexBufferData = MemoryUtil.memAllocFloat(maxVertices * sizeOfQuadVertex)
        }
    private var maxVertices = maxQuads * 4
    private var maxIndices = maxQuads * 6
    var maxTextureSlots = 32

    private var quadVertexArray = VertexArray.create()
    private var quadVertexBuffer = VertexBuffer.create(maxVertices * sizeOfQuadVertex)
    private var textureShader = Shader.create("shaders/Texture.glsl")
    private var whiteTexture = Texture2D.create(1, 1)

    private val quadVertexPositions = arrayOf(
        Vec4(-0.5f, -0.5f, 0.0f, 1.0f),
        Vec4( 0.5f, -0.5f, 0.0f, 1.0f),
        Vec4( 0.5f,  0.5f, 0.0f, 1.0f),
        Vec4(-0.5f,  0.5f, 0.0f, 1.0f),
    )

    private var batchQuadCount = 0
    val quadCount get() = (drawCalls - 1) * maxQuads + batchQuadCount
    var drawCalls = 0

    fun init() {
        textureSlots = arrayOfNulls(maxTextureSlots)

        stack { stack ->
            val whiteTextureData = stack.malloc(4).apply {
                put(0, 0xff.toByte())
                put(1, 0xff.toByte())
                put(2, 0xff.toByte())
                put(3, 0xff.toByte())
            }
            whiteTexture().setData(whiteTextureData)
        }

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

        quadVertexBufferData = MemoryUtil.memAllocFloat(maxVertices * sizeOfQuadVertex)

        val quadIndices = UIntArray(maxIndices) {
            ((it / 6) * 4).toUInt() + (
            if (it % 6 > 2) ((it + 1) % 3).toUInt().let { u -> if (u > 0u) u + 1u else u }
            else (it % 6).toUInt())
        } // 0, 1, 2, 2, 3, 0, ...

        val quadIB = IndexBuffer.create(quadIndices)
        quadVertexArray().indexBuffer = quadIB.take()

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

        drawCalls = 0
        startBatch()
    }

    fun beginScene(camera: EditorCamera) {
        textureShader().apply {
            bind()
            setMat4("u_ViewProjection", camera.viewProjection)
        }

        drawCalls = 0
        startBatch()
    }

    fun beginScene(camera: Camera, transform: FloatArray) {
        val viewProj = camera.projection * inverseTransform(transform)

        textureShader().apply {
            bind()
            setMat4("u_ViewProjection", viewProj)
        }

        drawCalls = 0
        startBatch()
    }

    fun beginScene(camera: Camera, transform: Mat4) {
        val viewProj = camera.projection * transform.inverse()

        textureShader().apply {
            bind()
            setMat4("u_ViewProjection", viewProj)
        }

        drawCalls = 0
        startBatch()
    }

    fun endScene() {
        flush()
    }

    private fun startBatch() {
        textureSlotIndex = 1

        quadIndexCount = 0
        batchQuadCount = 0

        drawCalls++
    }

    private fun nextBatch() {
        flush()
        startBatch()
    }

    private fun flush() {
        if (batchQuadCount == 0) return

        val array = FloatArray(quadVertexBufferData.position())
        quadVertexBufferData.position(0)
        quadVertexBufferData.get(array, 0, array.size)
        quadVertexBufferData.position(0)
        quadVertexBuffer().setData(array)

        for (i in 0 until textureSlotIndex) textureSlots[i]!!.bind(i)

        RendererCommand.drawIndexed(quadVertexArray(), quadIndexCount)
    }

    fun drawQuad(position: Vec2 = Vec2(), size: Vec2 = Vec2(1f), radians: Float = 0f, color: Vec4) =
        drawQuad(Vec3(position, 0f), Vec3(size, 1f), Vec3(radians, 0f, 0f), color)
    fun drawQuad(position: Vec3, size: Vec2 = Vec2(1f), radians: Float = 0f, color: Vec4) =
        drawQuad(position, Vec3(size, 1f), Vec3(radians, 0f, 0f), color)

    /*fun drawQuad(position: Vec2 = Vec2(), size: Vec3 = Vec3(1f), radians: Vec3 = Vec3(), color: Vec4) =
        drawQuad(Vec3(position, 0f), size, radians, color)*/
    fun drawQuad(position: Vec3, size: Vec3 = Vec3(1f), radians: Vec3 = Vec3(), color: Vec4) =
        //drawQuad(glm.translate(glm.rotateXYZ(glm.scale(Mat4.identity, size), radians.x, radians.y, radians.z), position), color)
        drawQuad(transform(position, radians, size), color)

    //private val textureIndices = arrayOf(0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f)
    private const val whiteTextureID = 0f
    private const val whiteTextureTilingFactor = 1f
    fun drawQuad(transform: Mat4, color: Vec4) {
        if (quadIndexCount >= maxIndices) {
            nextBatch()
        }

        quadVertexBufferData.put(transform * quadVertexPositions[0], color, 0f, 0f)
        quadVertexBufferData.put(transform * quadVertexPositions[1], color, 1f, 0f)
        quadVertexBufferData.put(transform * quadVertexPositions[2], color, 1f, 1f)
        quadVertexBufferData.put(transform * quadVertexPositions[3], color, 0f, 1f)

        quadIndexCount += 6
        batchQuadCount++
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun FloatBuffer.put(
        transformedPos: Vec4,
        color: Vec4,
        texX: Float, texY: Float,
    ) {
        put(transformedPos.x)
        put(transformedPos.y)
        put(transformedPos.z)

        put(color.x)
        put(color.y)
        put(color.z)
        put(color.w)

        put(texX)
        put(texY)

        put(whiteTextureID)

        put(whiteTextureTilingFactor)
    }

    /*fun drawQuad(position: Vec2 = Vec2(), size: Vec2 = Vec2(1f), radians: Float = 0f, texture: Texture2D, color: Vec4 = Vec4(1f), tilingFactor: Float = 1f) =
        drawQuad(Vec3(position, 0f), size, radians, texture, color, tilingFactor)

    fun drawQuad(position: Vec3, size: Vec2 = Vec2(1f), radians: Float = 0f, texture: Texture2D, color: Vec4 = Vec4(1f), tilingFactor: Float = 1f) {
        if (quadVertexBufferData.position() >= maxQuads) {
            nextBatch()
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
                nextBatch()
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

        batchIndexCount++
    }

    fun drawQuad(position: Vec2 = Vec2(), size: Vec2 = Vec2(1f), radians: Float = 0f, texture: Texture2D, color: Vec4 = Vec4(1f), tilingFactor: Float = 1f,
                 subTextureCoord: Vec2, subTextureSize: Vec2, subTextureTileSize: Vec2) =
        drawQuad(Vec3(position, 0f), size, radians, texture, color, tilingFactor, subTextureCoord, subTextureSize, subTextureTileSize)

    fun drawQuad(position: Vec3, size: Vec2 = Vec2(1f), radians: Float = 0f, texture: Texture2D, color: Vec4 = Vec4(1f), tilingFactor: Float = 1f,
                 subTextureCoord: Vec2, subTextureSize: Vec2, subTextureTileSize: Vec2) {
        if (quadVertexBufferData.position() >= maxQuads) {
            nextBatch()
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
                nextBatch()
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

        batchIndexCount++
    }*/
}
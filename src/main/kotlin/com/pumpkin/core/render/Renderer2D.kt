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

    @ExperimentalUnsignedTypes
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
        val whiteTextureData = ByteBuffer.allocateDirect(4).apply {
            put(0, 0xff.toByte())
            put(1, 0xff.toByte())
            put(2, 0xff.toByte())
            put(3, 0xff.toByte())
        }
        data().whiteTexture().setData(whiteTextureData, whiteTextureData.capacity())

        data().quadVertexBuffer().layout = BufferLayout(
            mutableListOf(
                BufferElement(ShaderDataType.Float3, "a_Position"),
                BufferElement(ShaderDataType.Float4, "a_Color"),
                BufferElement(ShaderDataType.Float2, "a_TexCoord")
            )
        )
        data().quadVertexArray().addVertexBuffer(data().quadVertexBuffer.take())


        //data().quadVertexBufferBase = Array(data().maxVertices) { null }
        data().quadVertexBufferData = FloatBuffer.allocate(maxVertices * sizeOfQuadVertex)

        val quadIndices = UIntArray(maxIndices) { it.toUInt() }

        val quadIB = IndexBuffer.create(quadIndices)
        data().quadVertexArray().indexBuffer = quadIB.take()

        data().textureShader().setInt("u_Texture", 0)

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
    }

    fun endScene() {
        val array = FloatArray(data().quadVertexBufferData.position())
        data().quadVertexBufferData.position(0)
        data().quadVertexBufferData[array, 0, array.size]
        data().quadVertexBufferData.position(0)
        data().quadVertexBuffer().setData(array)
        flush()
    }

    fun flush() {
        RendererCommand.drawIndexed(data().quadVertexArray(), data().quadIndexCount)
    }

    fun drawQuad(position: Vec2 = Vec2(0f), size: Vec2 = Vec2(1f), rotation: Float = 0f, color: Vec4) =
        drawQuad(Vec3(position, 0), size, rotation, color)

    fun drawQuad(position: Vec3 = Vec3(0f), size: Vec2 = Vec2(1f), rotation: Float = 0f, color: Vec4) = stack {
        if (data().quadVertexBufferData.position() >= maxQuads) {
            endScene()
            beginScene(camera)
        }

        data().quadVertexBufferData.put(position.x); data().quadVertexBufferData.put(position.y); data().quadVertexBufferData.put(position.z)
        data().quadVertexBufferData.put(color.x); data().quadVertexBufferData.put(color.y); data().quadVertexBufferData.put(color.z); data().quadVertexBufferData.put(color.w)
        data().quadVertexBufferData.put(0f); data().quadVertexBufferData.put(0f)

        data().quadVertexBufferData.put(position.x + size.x); data().quadVertexBufferData.put(position.y); data().quadVertexBufferData.put(position.z)
        data().quadVertexBufferData.put(color.x); data().quadVertexBufferData.put(color.y); data().quadVertexBufferData.put(color.z); data().quadVertexBufferData.put(color.w)
        data().quadVertexBufferData.put(1f); data().quadVertexBufferData.put(0f)

        data().quadVertexBufferData.put(position.x + size.x); data().quadVertexBufferData.put(position.y + size.y); data().quadVertexBufferData.put(position.z)
        data().quadVertexBufferData.put(color.x); data().quadVertexBufferData.put(color.y); data().quadVertexBufferData.put(color.z); data().quadVertexBufferData.put(color.w)
        data().quadVertexBufferData.put(1f); data().quadVertexBufferData.put(1f)

        data().quadVertexBufferData.put(position.x); data().quadVertexBufferData.put(position.y + size.y); data().quadVertexBufferData.put(position.z)
        data().quadVertexBufferData.put(color.x); data().quadVertexBufferData.put(color.y); data().quadVertexBufferData.put(color.z); data().quadVertexBufferData.put(color.w)
        data().quadVertexBufferData.put(0f); data().quadVertexBufferData.put(1f)

        data().quadIndexCount += 4
    }

    fun drawQuad(position: Vec2 = Vec2(0f), size: Vec2 = Vec2(1f), rotation: Float = 0f, texture: Texture2D, color: Vec4 = Vec4(1f)) =
        drawQuad(Vec3(position, 0), size, rotation, texture, color)

    fun drawQuad(position: Vec3 = Vec3(0f), size: Vec2 = Vec2(1f), rotation: Float = 0f, texture: Texture2D, color: Vec4 = Vec4(1f)) = stack {
        data().textureShader().run {
            if (rotation == 0f)
                setMat4("u_Transform", glm.translate(Mat4.identity, position) * glm.scale(Mat4.identity, Vec3(size, 1f)))
            else
                setMat4("u_Transform", glm.translate(Mat4.identity, position) * glm.scale(Mat4.identity, Vec3(size, 1f)) * glm.rotate(Mat4.identity, rotation, Vec3(0f, 0f, 1f)))
            texture.bind()
            setFloat4("u_Color", color)
        }
        data().quadVertexArray().bind()
        RendererCommand.drawIndexed(data().quadVertexArray())
    }

}

data class Renderer2DData(
    var quadVertexArray: Ref<VertexArray>,
    var quadVertexBuffer: Ref<VertexBuffer>,
    var textureShader: Ref<Shader>,
    var whiteTexture: Ref<Texture2D>,
) : AutoCloseable {
    var quadIndexCount = 0
    lateinit var quadVertexBufferData: FloatBuffer

    override fun close() {
        quadVertexArray.release()
        textureShader.release()
        whiteTexture.release()
    }
}

val maxQuads = 10000
val maxVertices = maxQuads * 4
val maxIndices = maxQuads * 4
val sizeOfQuadVertex = 9
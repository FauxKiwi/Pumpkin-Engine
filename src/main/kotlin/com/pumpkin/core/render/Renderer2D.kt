package com.pumpkin.core.render

import com.pumpkin.core.Ref
import com.pumpkin.core.Scope
import com.pumpkin.core.stack
import com.pumpkin.platform.opengl.OpenGLShader
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import org.lwjgl.BufferUtils
import java.nio.ByteBuffer

object Renderer2D {
    lateinit var data: Scope<Renderer2DStorage>

    @ExperimentalUnsignedTypes
    fun init() {
        data =
            Scope(
                Renderer2DStorage(
                    VertexArray.create(),
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

        val squareVertices = floatArrayOf(
            -0.5f, -0.5f, 0f, 0f, 0f,
            0.5f, -0.5f, 0f, 1f, 0f,
            0.5f, 0.5f, 0f, 1f, 1f,
            -0.5f, 0.5f, 0f, 0f, 1f,
        )

        val squareIndices = uintArrayOf(0u, 1u, 2u, 3u)

        val squareVB = VertexBuffer.create(squareVertices)
        val squareLayout = BufferLayout(
            mutableListOf(
                BufferElement(ShaderDataType.Float3, "a_Position"),
                BufferElement(ShaderDataType.Float2, "a_TexCoord")
            )
        )
        squareVB().layout = squareLayout
        data().quadVertexArray().addVertexBuffer(squareVB.take())

        val squareIB = IndexBuffer.create(squareIndices)
        data().quadVertexArray().indexBuffer = squareIB.take()

        data().textureShader().setInt("u_Texture", 0)

        squareVB.release()
        squareIB.release()
    }

    fun shutdown() = data.close()

    fun beginScene(camera: OrthographicCamera) {
        data().textureShader().run {
            bind()
            setMat4("u_ViewProjection", camera.viewProjectionMatrix)
            setMat4("u_Transform", Mat4.identity)
        }
    }

    fun endScene() = Unit

    fun drawQuad(position: Vec2 = Vec2(0f), size: Vec2 = Vec2(1f), rotation: Float = 0f, color: Vec4) =
        drawQuad(Vec3(position, 0), size, rotation, color)

    fun drawQuad(position: Vec3 = Vec3(0f), size: Vec2 = Vec2(1f), rotation: Float = 0f, color: Vec4) = stack {
        data().textureShader().run {
            if (rotation == 0f)
                setMat4("u_Transform", glm.translate(Mat4.identity, position) * glm.scale(Mat4.identity, Vec3(size, 1f)))
            else
                setMat4("u_Transform", glm.translate(Mat4.identity, position) * glm.scale(Mat4.identity, Vec3(size, 1f)) * glm.rotate(Mat4.identity, rotation, Vec3(0f, 0f, 1f)))
            data().whiteTexture().bind()
            setFloat4("u_Color", color)
        }
        data().quadVertexArray().bind()
        RendererCommand.drawIndexed(data().quadVertexArray())
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

data class Renderer2DStorage(
    var quadVertexArray: Ref<VertexArray>,
    var textureShader: Ref<Shader>,
    var whiteTexture: Ref<Texture2D>,
) : AutoCloseable {

    override fun close() {
        quadVertexArray.release()
        textureShader.release()
        whiteTexture.release()
    }
}
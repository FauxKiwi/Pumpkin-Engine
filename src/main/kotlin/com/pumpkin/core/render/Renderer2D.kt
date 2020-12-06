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

object Renderer2D {
    lateinit var data: Scope<Renderer2DStorage>

    @ExperimentalUnsignedTypes
    fun init() {
        data =
            Scope(
                Renderer2DStorage(
                    VertexArray.create(),
                    Shader.create("./src/main/resources/shaders/FlatColor.glsl"),
                    Shader.create("./src/main/resources/shaders/Texture.glsl")
                )
            )

        val squareVertices = floatArrayOf(
            -0.5f, -0.5f, 0f, 0f, 0f,
            0.5f, -0.5f, 0f, 1f, 0f,
            0.5f, 0.5f, 0f, 1f, 1f,
            -0.5f, 0.5f, 0f, 0f, 1f,
        )

        val squareIndices = uintArrayOf(0u, 1u, 2u, 2u, 3u, 0u)

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
        data().flatColorShader().run {
            bind()
            setMat4("u_ViewProjection", camera.viewProjectionMatrix)
            setMat4("u_Transform", Mat4.identity)
        }
        data().textureShader().run {
            bind()
            setMat4("u_ViewProjection", camera.viewProjectionMatrix)
            setMat4("u_Transform", Mat4.identity)
        }
    }

    fun endScene() = Unit

    fun drawQuad(position: Vec2 = Vec2(0f), size: Vec2 = Vec2(1f), color: Vec4) =
        drawQuad(Vec3(position, 0), size, color)

    fun drawQuad(position: Vec3 = Vec3(0f), size: Vec2 = Vec2(1f), color: Vec4) = stack {
        data().flatColorShader().run {
            bind()
            setMat4(
                "u_Transform",
                glm.translate(Mat4.identity, position) * glm.scale(Mat4.identity, Vec3(size, 1f))
            )
            setFloat4("u_Color", color)
        }
        data().quadVertexArray().bind()
        RendererCommand.drawIndexed(data().quadVertexArray())
    }

    fun drawQuad(position: Vec2 = Vec2(0f), size: Vec2 = Vec2(1f), texture: Texture2D, color: Vec4 = Vec4(1f)) =
        drawQuad(Vec3(position, 0), size, texture, color)

    fun drawQuad(position: Vec3 = Vec3(0f), size: Vec2 = Vec2(1f), texture: Texture2D, color: Vec4 = Vec4(1f)) = stack {
        data().textureShader().run {
            bind()
            setMat4(
                "u_Transform",
                glm.translate(Mat4.identity, position) * glm.scale(Mat4.identity, Vec3(size, 1f))
            )
            texture.bind()
            setFloat4("u_Color", color)
        }
        data().quadVertexArray().bind()
        RendererCommand.drawIndexed(data().quadVertexArray())
    }

}

data class Renderer2DStorage(
    var quadVertexArray: Ref<VertexArray>,
    var flatColorShader: Ref<Shader>,
    var textureShader: Ref<Shader>
) : AutoCloseable {

    override fun close() {
        quadVertexArray.release()
        flatColorShader.release()
        textureShader.release()
    }
}
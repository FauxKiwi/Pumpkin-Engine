package com.pumpkin.core.render

import com.pumpkin.core.Ref
import com.pumpkin.core.Scope
import com.pumpkin.platform.opengl.OpenGLShader
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4

object Renderer2D {
    lateinit var data: Scope<Renderer2DStorage>

    @ExperimentalUnsignedTypes
    fun init() {
        data =
            Scope(Renderer2DStorage(VertexArray.create(), Shader.create("./src/test/resources/shaders/FlatColor.glsl")))

        val squareVertices = floatArrayOf(
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f,
            0.5f, 0.5f, 0f,
            -0.5f, 0.5f, 0f,
        )

        val squareIndices = uintArrayOf(0u, 1u, 2u, 2u, 3u, 0u)

        val squareVB = VertexBuffer.create(squareVertices)
        val squareLayout = BufferLayout(
            mutableListOf(
                BufferElement(ShaderDataType.Float3, "a_Position"),
            )
        )
        squareVB().layout = squareLayout
        data().quadVertexArray().addVertexBuffer(squareVB.take())

        val squareIB = IndexBuffer.create(squareIndices)
        data().quadVertexArray().indexBuffer = squareIB.take()

        squareVB.release()
        squareIB.release()
    }

    fun shutdown() = data.close()

    fun beginScene(camera: OrthographicCamera) = (data().flatColorShader() as OpenGLShader).run {
        bind()
        uploadUniform("u_ViewProjection", camera.viewProjectionMatrix)
        uploadUniform("u_Transform", Mat4.identity)
    }

    fun endScene() = Unit

    fun drawQuad(position: Vec2, size: Vec2, color: Vec4) = drawQuad(Vec3(position, 0), size, color)

    fun drawQuad(position: Vec3, size: Vec2, color: Vec4) {
        (data().flatColorShader() as OpenGLShader).run {
            bind()
            uploadUniform("u_Color", color)
        }
        data().quadVertexArray().bind()
        RendererCommand.drawIndexed(data().quadVertexArray())
    }
}

data class Renderer2DStorage(var quadVertexArray: Ref<VertexArray>, var flatColorShader: Ref<Shader>) : AutoCloseable {

    override fun close() {
        quadVertexArray.release()
        flatColorShader.release()
    }
}
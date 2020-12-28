package com.pumpkin.core.renderer

import glm.Mat4

object Renderer {

    @ExperimentalUnsignedTypes
    fun init() {
        RendererCommand.init()
        Renderer2D.init()
    }

    fun beginScene(camera: OrthographicCamera) {
        viewProjectionMatrix = camera.viewProjectionMatrix
    }

    fun endScene() = Unit

    fun submit(shader: Shader, vertexArray: VertexArray, transform: Mat4 = Mat4()) {

        shader.bind()
        shader.setMat4("u_ViewProjection", viewProjectionMatrix)
        shader.setMat4("u_Transform", transform)

        vertexArray.bind()
        RendererCommand.drawIndexed(vertexArray)
    }

    @Suppress("NOTHING_TO_INLINE") inline fun getAPI() = RendererCommand.rendererAPI.api

    fun onWindowResize(width: Int, height: Int) {
        RendererCommand.setViewport(0, 0, width, height)
    }

    var viewProjectionMatrix: Mat4 = Mat4(0f)
}
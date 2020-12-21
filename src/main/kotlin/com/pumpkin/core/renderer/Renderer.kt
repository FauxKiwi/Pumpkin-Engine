package com.pumpkin.core.renderer

import glm_.mat4x4.Mat4

object Renderer {

    @ExperimentalUnsignedTypes
    inline fun init() = RendererCommand.init().also { Renderer2D.init() }

    fun beginScene(camera: OrthographicCamera) {
        sceneData.viewProjectionMatrix = camera.viewProjectionMatrix
    }

    fun endScene() = Unit

    fun submit(shader: Shader, vertexArray: VertexArray, transform: Mat4 = Mat4.identity) {

        shader.bind()
        shader.setMat4("u_ViewProjection", sceneData.viewProjectionMatrix)
        shader.setMat4("u_Transform", transform)

        vertexArray.bind()
        RendererCommand.drawIndexed(vertexArray)
    }

    inline fun getAPI() = RendererCommand.rendererAPI.api

    fun onWindowResize(width: Int, height: Int) {
        RendererCommand.setViewport(0, 0, width, height)
    }

    data class SceneData(var viewProjectionMatrix: Mat4)

    val sceneData = SceneData(Mat4(0))
}
package com.pumpkin.core.render

import glm_.mat4x4.Mat4

object Renderer {

    fun beginScene(camera: OrthographicCamera) {
        sceneData.viewProjectionMatrix = camera.viewProjectionMatrix
    }

    fun endScene() = Unit

    fun submit(shader: Shader, vertexArray: VertexArray) {
        shader.bind()
        shader.uploadUniformMat4("u_ViewProjection", sceneData.viewProjectionMatrix)

        vertexArray.bind()
        RendererCommand.drawIndexed(vertexArray)
    }

    inline fun getAPI() = RendererCommand.rendererAPI.api

    data class SceneData(var viewProjectionMatrix: Mat4)

    val sceneData = SceneData(Mat4(0))
}
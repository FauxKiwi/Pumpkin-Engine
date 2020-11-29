package com.pumpkin.core.render

object Renderer {

    fun beginScene() = Unit

    fun endScene() = Unit

    fun submit(vertexArray: VertexArray) {
        vertexArray.bind()
        RendererCommand.drawIndexed(vertexArray)
    }

    inline fun getAPI() = RendererCommand.rendererAPI.api
}
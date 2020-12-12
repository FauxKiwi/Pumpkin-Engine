package com.pumpkin.core.renderer

import com.pumpkin.platform.opengl.OpenGLRendererAPI
import glm_.vec4.Vec4

object RendererCommand {
    val rendererAPI = OpenGLRendererAPI()

    inline fun init() = rendererAPI.init()

    inline fun setClearColor(color: Vec4) = rendererAPI.setClearColor(color)

    inline fun clear() = rendererAPI.clear()

    inline fun drawIndexed(vertexArray: VertexArray, count: Int = 0) = rendererAPI.drawIndexed(vertexArray, count)

    inline fun setViewport(x: Int, y: Int, width: Int, height: Int) {
        rendererAPI.setViewport(x, y, width, height)
    }
}
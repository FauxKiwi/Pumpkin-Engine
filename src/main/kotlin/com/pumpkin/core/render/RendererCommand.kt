package com.pumpkin.core.render

import com.pumpkin.platform.opengl.OpenGLRendererAPI
import glm_.vec4.Vec4

object RendererCommand {
    val rendererAPI = OpenGLRendererAPI()

    inline fun setClearColor(color: Vec4) = rendererAPI.setClearColor(color)

    inline fun clear() = rendererAPI.clear()

    inline fun drawIndexed(vertexArray: VertexArray) = rendererAPI.drawIndexed(vertexArray)
}
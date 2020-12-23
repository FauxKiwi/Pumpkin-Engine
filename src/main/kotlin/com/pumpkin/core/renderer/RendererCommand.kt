package com.pumpkin.core.renderer

import com.pumpkin.platform.opengl.OpenGLRendererAPI
import glm_.vec4.Vec4

object RendererCommand {
    val rendererAPI = OpenGLRendererAPI()

    @Suppress("NOTHING_TO_INLINE") inline fun init() = rendererAPI.init()

    @Suppress("NOTHING_TO_INLINE") inline fun setClearColor(color: Vec4) = rendererAPI.setClearColor(color)

    @Suppress("NOTHING_TO_INLINE") inline fun clear() = rendererAPI.clear()

    @Suppress("NOTHING_TO_INLINE") inline fun drawIndexed(vertexArray: VertexArray, count: Int = 0) = rendererAPI.drawIndexed(vertexArray, count)

    @Suppress("NOTHING_TO_INLINE") inline fun setViewport(x: Int, y: Int, width: Int, height: Int) {
        rendererAPI.setViewport(x, y, width, height)
    }
}
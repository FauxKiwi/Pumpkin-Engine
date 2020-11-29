package com.pumpkin.core.render

import com.pumpkin.core.logErrorCore
import com.pumpkin.platform.opengl.OpenGLVertexArray

interface VertexArray {
    val vertexBuffers: MutableList<VertexBuffer>
    var indexBuffer: IndexBuffer?

    companion object {
        fun create(): VertexArray = when (Renderer.getAPI()) {
            RenderAPI.None -> {
                logErrorCore("Having no render API is currently not supported")
                throw Throwable()
            }
            RenderAPI.OpenGL -> OpenGLVertexArray()
        }
    }

    fun bind()

    fun unbind()

    fun addVertexBuffer(vertexBuffer: VertexBuffer)
}
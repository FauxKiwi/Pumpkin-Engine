package com.pumpkin.core.render

import com.pumpkin.core.Ref
import com.pumpkin.core.logErrorCore
import com.pumpkin.platform.opengl.OpenGLVertexArray

interface VertexArray : AutoCloseable {
    val vertexBuffers: MutableList<Ref<VertexBuffer>>
    var indexBuffer: Ref<IndexBuffer>?

    companion object {
        fun create(): Ref<VertexArray> = when (Renderer.getAPI()) {
            RendererAPI.API.None -> {
                logErrorCore("Having no render API is currently not supported")
                throw Throwable()
            }
            RendererAPI.API.OpenGL -> Ref(OpenGLVertexArray())
        }
    }

    fun bind()

    fun unbind()
}
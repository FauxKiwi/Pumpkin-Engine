package com.pumpkin.core.render

import com.pumpkin.core.PumpkinError
import com.pumpkin.core.Ref
import com.pumpkin.platform.opengl.OpenGLVertexArray

interface VertexArray : AutoCloseable {
    var indexBuffer: Ref<IndexBuffer>?

    companion object {
        fun create(): Ref<VertexArray> = when (Renderer.getAPI()) {
            RendererAPI.API.None -> {
                throw PumpkinError("Having no render API is currently not supported")
            }
            RendererAPI.API.OpenGL -> Ref(OpenGLVertexArray())
        }
    }

    fun bind()

    fun unbind()

    fun getVertexBuffers(): MutableList<Ref<VertexBuffer>>

    fun addVertexBuffer(vertexBuffer: Ref<VertexBuffer>): Boolean
}
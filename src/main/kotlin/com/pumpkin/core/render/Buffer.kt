package com.pumpkin.core.render

import com.pumpkin.core.logErrorCore
import com.pumpkin.platform.opengl.OpenGLIndexBuffer
import com.pumpkin.platform.opengl.OpenGLVertexBuffer

interface VertexBuffer {
    companion object {
        fun create(vertices: FloatArray): VertexBuffer = when (Renderer.getAPI()) {
            RenderAPI.None -> {
                logErrorCore("Having no render API is currently not supported")
                throw Throwable()
            }
            RenderAPI.OpenGL -> OpenGLVertexBuffer(vertices)
        }
    }

    fun bind()

    fun unbind()
}

interface IndexBuffer {
    companion object {
        fun create(indices: IntArray /*UIntArray*/): IndexBuffer = when (Renderer.getAPI()) {
            RenderAPI.None -> {
                logErrorCore("Having no render API is currently not supported")
                throw Throwable()
            }
            RenderAPI.OpenGL -> OpenGLIndexBuffer(indices)
        }
    }

    fun bind()

    fun unbind()
}
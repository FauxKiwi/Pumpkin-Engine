package com.pumpkin.core.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.renderer.BufferLayout
import com.pumpkin.core.renderer.IndexBuffer
import com.pumpkin.core.renderer.VertexBuffer
import org.khronos.webgl.WebGLRenderingContext as GL

actual class OpenGLVertexBuffer : VertexBuffer {
    private val rendererID = gl.createBuffer()

    override var layout: BufferLayout? = null
        get() = field ?: Debug.exception("No Layout specified")

    actual constructor(vertices: FloatArray) {
        gl.bindBuffer(GL.ARRAY_BUFFER, rendererID)
        gl.bufferData(GL.ARRAY_BUFFER, vertices, GL.STATIC_DRAW)
    }

    actual constructor(size: Int) {
        gl.bindBuffer(GL.ARRAY_BUFFER, rendererID)
        gl.bufferData(GL.ARRAY_BUFFER, size, GL.DYNAMIC_DRAW)
    }

    override fun close() = gl.deleteBuffer(rendererID)

    override fun setData(data: FloatArray) {
        gl.bindBuffer(GL.ARRAY_BUFFER, rendererID)
        gl.bufferSubData(GL.ARRAY_BUFFER, 0, data)
    }

    override fun bind() = gl.bindBuffer(GL.ARRAY_BUFFER, rendererID)

    override fun unbind() = gl.bindBuffer(GL.ARRAY_BUFFER, 0)//glBindBuffer(GL_ARRAY_BUFFER, 0)
}

@ExperimentalUnsignedTypes
actual class OpenGLIndexBuffer actual constructor(indices: UIntArray) : IndexBuffer {
    override val count: Int = indices.size

    private val rendererID = gl.createBuffer()

    init {
        gl.bindBuffer(GL.ARRAY_BUFFER, rendererID)
        gl.bufferData(GL.ARRAY_BUFFER, indices, GL.STATIC_DRAW)
    }

    override fun close() = gl.deleteBuffer(rendererID)

    override fun bind() = gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, rendererID)

    override fun unbind() = gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, null)
}
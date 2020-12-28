package com.pumpkin.core.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.renderer.BufferLayout
import com.pumpkin.core.renderer.IndexBuffer
import com.pumpkin.core.renderer.VertexBuffer
import org.lwjgl.opengl.GL15C.*
import org.lwjgl.opengl.GL45C.glCreateBuffers

actual class OpenGLVertexBuffer : VertexBuffer {
    private val rendererID = glCreateBuffers()

    override var layout: BufferLayout? = null
        get() = field ?: Debug.exception("No Layout specified")

    actual constructor(vertices: FloatArray) {
        glBindBuffer(GL_ARRAY_BUFFER, rendererID)
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
    }

    actual constructor(size: Int) {
        glBindBuffer(GL_ARRAY_BUFFER, rendererID)
        glBufferData(GL_ARRAY_BUFFER, FloatArray(size), GL_DYNAMIC_DRAW)
    }

    override fun close() = glDeleteBuffers(rendererID)

    override fun setData(data: FloatArray) {
        glBindBuffer(GL_ARRAY_BUFFER, rendererID)
        glBufferSubData(GL_ARRAY_BUFFER, 0, data)
    }

    override fun bind() = glBindBuffer(GL_ARRAY_BUFFER, rendererID)

    override fun unbind() = glBindBuffer(GL_ARRAY_BUFFER, 0)
}

@ExperimentalUnsignedTypes
actual class OpenGLIndexBuffer actual constructor(indices: UIntArray) : IndexBuffer {
    override val count: Int = indices.size

    private val rendererID = glCreateBuffers()

    init {
        glBindBuffer(GL_ARRAY_BUFFER, rendererID)
        glBufferData(GL_ARRAY_BUFFER, indices.toIntArray(), GL_STATIC_DRAW)
    }

    override fun close() = glDeleteBuffers(rendererID)

    override fun bind() = glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, rendererID)

    override fun unbind() = glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
}
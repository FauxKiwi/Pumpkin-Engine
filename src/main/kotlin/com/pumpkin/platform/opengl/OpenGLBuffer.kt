package com.pumpkin.platform.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.renderer.BufferLayout
import com.pumpkin.core.renderer.IndexBuffer
import com.pumpkin.core.renderer.VertexBuffer
import gln.BufferTarget
import gln.gl
import gln.identifiers.GlBuffer
import org.lwjgl.opengl.GL15C

class OpenGLVertexBuffer : VertexBuffer {
    private val rendererID = gl.createBuffers()
    override var layout: BufferLayout? = null
    get() = field ?: Debug.exception("No Layout specified")

    constructor(vertices: FloatArray) {
        gl.bindBuffer(BufferTarget.ARRAY, rendererID)
        GL15C.glBufferData(GL15C.GL_ARRAY_BUFFER, vertices, GL15C.GL_STATIC_DRAW)
    }

    constructor(size: Int) {
        gl.bindBuffer(BufferTarget.ARRAY, rendererID)
        GL15C.glBufferData(GL15C.GL_ARRAY_BUFFER, FloatArray(size), GL15C.GL_DYNAMIC_DRAW)
    }

    override fun setData(data: FloatArray) {
        gl.bindBuffer(BufferTarget.ARRAY, rendererID)
        GL15C.glBufferSubData(GL15C.GL_ARRAY_BUFFER, 0, data)
    }

    override fun close() = gl.deleteBuffers(rendererID)

    override fun bind() = gl.bindBuffer(BufferTarget.ARRAY, rendererID)

    override fun unbind() = gl.bindBuffer(BufferTarget.ARRAY, GlBuffer())
}

@ExperimentalUnsignedTypes
class OpenGLIndexBuffer @ExperimentalUnsignedTypes constructor(indices: UIntArray) : IndexBuffer {
    override val count: Int = indices.size
    private val rendererID = gl.createBuffers()

    init {
        gl.bindBuffer(BufferTarget.ARRAY, rendererID)
        GL15C.glBufferData(GL15C.GL_ARRAY_BUFFER, indices.toIntArray(), GL15C.GL_STATIC_DRAW)
    }

    override fun close() = gl.deleteBuffers(rendererID)

    override fun bind() = gl.bindBuffer(BufferTarget.ELEMENT_ARRAY, rendererID)

    override fun unbind() = gl.bindBuffer(BufferTarget.ELEMENT_ARRAY, GlBuffer())
}
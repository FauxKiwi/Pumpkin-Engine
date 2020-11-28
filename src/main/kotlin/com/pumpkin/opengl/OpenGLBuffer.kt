package com.pumpkin.opengl

import com.pumpkin.render.IndexBuffer
import com.pumpkin.render.VertexBuffer
import gln.BufferTarget
import gln.gl
import gln.identifiers.GlBuffer
import org.lwjgl.opengl.GL15C

class OpenGLVertexBuffer(vertices: FloatArray) : VertexBuffer {
    private val rendererID = gl.createBuffers()

    init {
        gl.bindBuffer(BufferTarget.ARRAY, rendererID)
        GL15C.glBufferData(GL15C.GL_ARRAY_BUFFER, vertices, GL15C.GL_STATIC_DRAW)
    }

    protected fun finalize() {
        gl.deleteBuffers(rendererID)
    }

    override fun bind() {
        gl.bindBuffer(BufferTarget.ARRAY, rendererID)
    }

    override fun unbind() {
        gl.bindBuffer(BufferTarget.ARRAY, GlBuffer())
    }
}

class OpenGLIndexBuffer(indices: IntArray/*UIntArray*/) : IndexBuffer {
    private val rendererID = gl.createBuffers()

    init {
        gl.bindBuffer(BufferTarget.ELEMENT_ARRAY, rendererID)
        GL15C.glBufferData(GL15C.GL_ELEMENT_ARRAY_BUFFER, indices, GL15C.GL_STATIC_DRAW)
    }

    protected fun finalize() {
        gl.deleteBuffers(rendererID)
    }

    override fun bind() {
        gl.bindBuffer(BufferTarget.ELEMENT_ARRAY, rendererID)
    }

    override fun unbind() {
        gl.bindBuffer(BufferTarget.ELEMENT_ARRAY, GlBuffer())
    }
}
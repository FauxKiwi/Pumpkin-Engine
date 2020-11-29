package com.pumpkin.platform.opengl

import com.pumpkin.core.logErrorCore
import com.pumpkin.core.render.BufferLayout
import com.pumpkin.core.render.IndexBuffer
import com.pumpkin.core.render.VertexArray
import com.pumpkin.core.render.VertexBuffer
import gln.gl
import gln.identifiers.GlVertexArray

class OpenGLVertexArray : VertexArray {
    override val vertexBuffers: MutableList<VertexBuffer> = mutableListOf()
    override var indexBuffer: IndexBuffer? = null
    set(value) {
        if (value == null) {
            logErrorCore("Null passed as indexBuffer")
            throw Throwable()
        }
        gl.bindVertexArray(rendererID)
        value.bind()
        field = value
    }

    private var rendererID = gl.createVertexArrays()

    protected fun finalize() {
        gl.deleteVertexArrays(rendererID)
    }

    override fun bind() {
        gl.bindVertexArray(rendererID)
    }

    override fun unbind() {
        gl.bindVertexArray(GlVertexArray())
    }

    override fun addVertexBuffer(vertexBuffer: VertexBuffer) {
        if (vertexBuffer.layout == null) {
            logErrorCore("Vertex buffer has no layout")
            throw Throwable()
        }
        gl.bindVertexArray(rendererID)
        vertexBuffer.bind()
        val layout: BufferLayout = vertexBuffer.layout!!
        for ((i, element) in layout.withIndex()) {
            gl.enableVertexAttribArray(i)
            gl.vertexAttribPointer(
                i,
                element.dataType.componentCount(),
                element.dataType.toVertexAttrType(),
                element.normalized,
                layout.getStride(),
                element.offset
            )
        }
        vertexBuffers.add(vertexBuffer)
    }
}
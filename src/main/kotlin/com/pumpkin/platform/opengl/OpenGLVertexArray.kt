package com.pumpkin.platform.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.Ref
import com.pumpkin.core.renderer.BufferLayout
import com.pumpkin.core.renderer.IndexBuffer
import com.pumpkin.core.renderer.VertexArray
import com.pumpkin.core.renderer.VertexBuffer
import com.pumpkin.core.stack
import gln.gl
import gln.identifiers.GlVertexArray

class OpenGLVertexArray : VertexArray {
    private val vertexBuffers: MutableList<Ref<VertexBuffer>> = mutableListOf()

    override var indexBuffer: Ref<IndexBuffer>? = null
        set(value) {
            if (value == null) {
                Debug.exception("Index Buffer is null")
            }
            stack {
                gl.bindVertexArray(rendererID)
                value().bind()
            }
            field = value
        }


    private var rendererID = gl.createVertexArrays()
    private var vertexBufferIndex = 0

    override fun close() {
        vertexBuffers.forEach(Ref<VertexBuffer>::release)
        indexBuffer?.release()
        gl.deleteVertexArrays(rendererID)
    }

    override fun bind() = gl.bindVertexArray(rendererID)

    override fun unbind() = gl.bindVertexArray(GlVertexArray())

    override fun getVertexBuffers(): MutableList<Ref<VertexBuffer>> {
        return vertexBuffers
    }

    override fun addVertexBuffer(vertexBuffer: Ref<VertexBuffer>): Boolean {
        if (vertexBuffer().layout == null) {
            Debug.exception("Vertex buffer has no layout")
        }
        stack {
            gl.bindVertexArray(rendererID)
            vertexBuffer().bind()
            val layout: BufferLayout = vertexBuffer().layout!!
            for (bufferElement in layout) {
                gl.enableVertexAttribArray(vertexBufferIndex)
                gl.vertexAttribPointer(
                    vertexBufferIndex,
                    bufferElement.dataType.componentCount(),
                    bufferElement.dataType.toVertexAttrType(),
                    bufferElement.normalized,
                    layout.getStride(),
                    bufferElement.offset
                )
                vertexBufferIndex++
            }
        }
        return vertexBuffers.add(vertexBuffer)
    }
}
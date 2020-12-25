package com.pumpkin.platform.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.Ref
import com.pumpkin.core.renderer.BufferLayout
import com.pumpkin.core.renderer.IndexBuffer
import com.pumpkin.core.renderer.VertexArray
import com.pumpkin.core.renderer.VertexBuffer
import gln.gl
import gln.identifiers.GlVertexArray
import gln.vertexArray.glVertexAttribPointer
import org.lwjgl.opengl.GL30C.*
import org.lwjgl.opengl.GL45C.glCreateVertexArrays

class OpenGLVertexArray : VertexArray {
    private val vertexBuffers: MutableList<Ref<VertexBuffer>> = mutableListOf()

    override var indexBuffer: Ref<IndexBuffer>? = null
        set(value) {
            if (value == null) {
                Debug.exception("Index Buffer is null")
            }
            glBindVertexArray(rendererID) //gl.bindVertexArray(rendererID)
            value().bind()
            field = value
        }


    private var rendererID = glCreateVertexArrays() //gl.createVertexArrays()
    private var vertexBufferIndex = 0

    override fun close() {
        vertexBuffers.forEach(Ref<VertexBuffer>::release)
        indexBuffer?.release()
        glDeleteVertexArrays(rendererID) //gl.deleteVertexArrays(rendererID)
    }

    override fun bind() = glBindVertexArray(rendererID) //gl.bindVertexArray(rendererID)

    override fun unbind() = gl.bindVertexArray(GlVertexArray())

    override fun getVertexBuffers(): MutableList<Ref<VertexBuffer>> {
        return vertexBuffers
    }

    override fun addVertexBuffer(vertexBuffer: Ref<VertexBuffer>): Boolean {
        if (vertexBuffer().layout == null) {
            Debug.exception("Vertex buffer has no layout")
        }
        glBindVertexArray(rendererID) //gl.bindVertexArray(rendererID)
        vertexBuffer().bind()
        val layout: BufferLayout = vertexBuffer().layout!!
        for (bufferElement in layout) {
            glEnableVertexAttribArray(vertexBufferIndex) //gl.enableVertexAttribArray(vertexBufferIndex)
            glVertexAttribPointer( //gl.vertexAttribPointer(
                vertexBufferIndex,
                bufferElement.dataType.componentCount(),
                bufferElement.dataType.toVertexAttrType(),
                bufferElement.normalized,
                layout.getStride(),
                bufferElement.offset
            ) //TODO
            vertexBufferIndex++
        }
        return vertexBuffers.add(vertexBuffer)
    }
}
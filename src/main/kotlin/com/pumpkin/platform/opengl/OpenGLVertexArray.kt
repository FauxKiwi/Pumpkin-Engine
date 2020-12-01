package com.pumpkin.platform.opengl

import com.pumpkin.core.logErrorCore
import com.pumpkin.core.logFatal
import com.pumpkin.core.render.BufferLayout
import com.pumpkin.core.render.IndexBuffer
import com.pumpkin.core.render.VertexArray
import com.pumpkin.core.render.VertexBuffer
import gln.gl
import gln.identifiers.GlVertexArray

class OpenGLVertexArray : VertexArray {
    override val vertexBuffers: MutableList<VertexBuffer> = object : ArrayList<VertexBuffer>() {
        override fun add(element: VertexBuffer): Boolean {
            if (element.layout == null) {
                logErrorCore("Vertex buffer has no layout")
                throw Throwable()
            }
            gl.bindVertexArray(rendererID)
            element.bind()
            val layout: BufferLayout = element.layout!!
            for ((index, bufferElement) in layout.withIndex()) {
                gl.enableVertexAttribArray(index)
                gl.vertexAttribPointer(
                    index,
                    bufferElement.dataType.componentCount(),
                    bufferElement.dataType.toVertexAttrType(),
                    bufferElement.normalized,
                    layout.getStride(),
                    bufferElement.offset
                )
            }
            return super.add(element)
        }
    }
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

    override fun close() = gl.deleteVertexArrays(rendererID)

    override fun bind() = gl.bindVertexArray(rendererID)

    override fun unbind() = gl.bindVertexArray(GlVertexArray())
}
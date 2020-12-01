package com.pumpkin.platform.opengl

import com.pumpkin.core.Ref
import com.pumpkin.core.logErrorCore
import com.pumpkin.core.render.BufferLayout
import com.pumpkin.core.render.IndexBuffer
import com.pumpkin.core.render.VertexArray
import com.pumpkin.core.render.VertexBuffer
import gln.gl
import gln.identifiers.GlVertexArray

class OpenGLVertexArray : VertexArray {
    override val vertexBuffers: MutableList<Ref<VertexBuffer>> by lazy {
        object : ArrayList<Ref<VertexBuffer>>() {
            override fun add(element: Ref<VertexBuffer>): Boolean {
                if (element().layout == null) {
                    logErrorCore("Vertex buffer has no layout")
                    throw Throwable()
                }
                gl.bindVertexArray(rendererID)
                element().bind()
                val layout: BufferLayout = element().layout!!
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
    }
    override var indexBuffer: Ref<IndexBuffer>? = null
        set(value) {
            if (value == null) {
                logErrorCore("Null passed as indexBuffer")
                throw Throwable()
            }
            gl.bindVertexArray(rendererID)
            value().bind()
            field = value
        }


    private var rendererID = gl.createVertexArrays()

    override fun close() {
        vertexBuffers.forEach(Ref<VertexBuffer>::release)
        indexBuffer?.release()
        gl.deleteVertexArrays(rendererID)
    }

    override fun bind() = gl.bindVertexArray(rendererID)

    override fun unbind() = gl.bindVertexArray(GlVertexArray())
}
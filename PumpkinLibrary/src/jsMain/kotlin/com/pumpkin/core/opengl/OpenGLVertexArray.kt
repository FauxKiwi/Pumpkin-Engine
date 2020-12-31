package com.pumpkin.core.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.Ref
import com.pumpkin.core.renderer.*
import org.khronos.webgl.WebGLRenderingContext as GL

actual class OpenGLVertexArray : VertexArray {
    private var rendererID = gl.createVertexArrays()

    private val vertexBuffers = mutableListOf<Ref<VertexBuffer>>()
    private var vertexBufferIndex = 0

    override var indexBuffer: Ref<IndexBuffer>? = null
        set(value) {
            if (value == null) {
                Debug.exception("Index Buffer is null")
            }
            gl.bindVertexArray(rendererID)
            value().bind()
            field = value
        }

    override fun close() {
        vertexBuffers.forEach(Ref<VertexBuffer>::release)
        indexBuffer?.release()
        gl.deleteVertexArrays(rendererID)
    }

    override fun bind() = gl.bindVertexArray(rendererID)

    override fun unbind() = gl.bindVertexArray(0)

    override fun getVertexBuffers() = vertexBuffers

    override fun addVertexBuffer(vertexBuffer: Ref<VertexBuffer>): Boolean {
        if (vertexBuffer().layout == null) {
            Debug.exception("Vertex buffer has no layout")
        }
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
        return vertexBuffers.add(vertexBuffer)
    }
}

fun ShaderDataType.toVertexAttrType() = when (this) {
    ShaderDataType.Float, ShaderDataType.Float2, ShaderDataType.Float3,
    ShaderDataType.Float4, ShaderDataType.Mat3, ShaderDataType.Mat4 -> GL.FLOAT
    ShaderDataType.Int, ShaderDataType.Int2, ShaderDataType.Int3, ShaderDataType.Int4 -> GL.INT
    ShaderDataType.Bool -> GL.BOOL
}
package com.pumpkin.core.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.Ref
import com.pumpkin.core.renderer.*
import org.lwjgl.opengl.GL20C.*
import org.lwjgl.opengl.GL30C.glBindVertexArray
import org.lwjgl.opengl.GL30C.glDeleteVertexArrays
import org.lwjgl.opengl.GL45C.glCreateVertexArrays

actual class OpenGLVertexArray : VertexArray {
    private var rendererID = glCreateVertexArrays()

    private val vertexBuffers = mutableListOf<Ref<VertexBuffer>>()
    private var vertexBufferIndex = 0

    override var indexBuffer: Ref<IndexBuffer>? = null
        set(value) {
            if (value == null) {
                Debug.exception("Index Buffer is null")
            }
            glBindVertexArray(rendererID)
            value().bind()
            field = value
        }

    override fun close() {
        vertexBuffers.forEach(Ref<VertexBuffer>::release)
        indexBuffer?.release()
        glDeleteVertexArrays(rendererID)
    }

    override fun bind() = glBindVertexArray(rendererID)

    override fun unbind() = glBindVertexArray(0)

    override fun getVertexBuffers() = vertexBuffers

    override fun addVertexBuffer(vertexBuffer: Ref<VertexBuffer>): Boolean {
        if (vertexBuffer().layout == null) {
            Debug.exception("Vertex buffer has no layout")
        }
        glBindVertexArray(rendererID)
        vertexBuffer().bind()
        val layout: BufferLayout = vertexBuffer().layout!!
        for (bufferElement in layout) {
            glEnableVertexAttribArray(vertexBufferIndex)
            glVertexAttribPointer(
                vertexBufferIndex,
                bufferElement.dataType.componentCount(),
                bufferElement.dataType.toVertexAttrType(),
                bufferElement.normalized,
                layout.getStride(),
                bufferElement.offset.toLong()
            )
            vertexBufferIndex++
        }
        return vertexBuffers.add(vertexBuffer)
    }
}

fun ShaderDataType.toVertexAttrType() = when (this) {
    ShaderDataType.Float, ShaderDataType.Float2, ShaderDataType.Float3,
    ShaderDataType.Float4, ShaderDataType.Mat3, ShaderDataType.Mat4 -> GL_FLOAT
    ShaderDataType.Int, ShaderDataType.Int2, ShaderDataType.Int3, ShaderDataType.Int4 -> GL_INT
    ShaderDataType.Bool -> GL_BOOL
}
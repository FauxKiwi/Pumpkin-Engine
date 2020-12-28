package com.pumpkin.platform.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.renderer.BufferLayout
import com.pumpkin.core.renderer.IndexBuffer
import com.pumpkin.core.renderer.ShaderDataType
import com.pumpkin.core.renderer.VertexBuffer
import org.lwjgl.opengl.GL15C.*
import org.lwjgl.opengl.GL20C.GL_BOOL
import org.lwjgl.opengl.GL45C.glCreateBuffers

fun ShaderDataType.toVertexAttrType() = when (this) {
    ShaderDataType.Float, ShaderDataType.Float2, ShaderDataType.Float3, ShaderDataType.Float4, ShaderDataType.Mat3, ShaderDataType.Mat4 -> GL_FLOAT
    ShaderDataType.Int, ShaderDataType.Int2, ShaderDataType.Int3, ShaderDataType.Int4 -> GL_INT
    ShaderDataType.Bool -> GL_BOOL
}

class OpenGLVertexBuffer : VertexBuffer {
    private val rendererID = glCreateBuffers() //gl.createBuffers()
    override var layout: BufferLayout? = null
    get() = field ?: Debug.exception("No Layout specified")

    constructor(vertices: FloatArray) {
        glBindBuffer(GL_ARRAY_BUFFER, rendererID) //gl.bindBuffer(BufferTarget.ARRAY, rendererID)
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
    }

    constructor(size: Int) {
        glBindBuffer(GL_ARRAY_BUFFER, rendererID) //gl.bindBuffer(BufferTarget.ARRAY, rendererID)
        glBufferData(GL_ARRAY_BUFFER, FloatArray(size), GL_DYNAMIC_DRAW)
    }

    override fun setData(data: FloatArray) {
        glBindBuffer(GL_ARRAY_BUFFER, rendererID) //gl.bindBuffer(BufferTarget.ARRAY, rendererID)
        glBufferSubData(GL_ARRAY_BUFFER, 0, data)
    }

    override fun close() = glDeleteBuffers(rendererID) //gl.deleteBuffers(rendererID)

    override fun bind() = glBindBuffer(GL_ARRAY_BUFFER, rendererID) //gl.bindBuffer(BufferTarget.ARRAY, rendererID)

    override fun unbind() = glBindBuffer(GL_ARRAY_BUFFER, 0) //gl.bindBuffer(BufferTarget.ARRAY, GlBuffer())
}

@ExperimentalUnsignedTypes
class OpenGLIndexBuffer @ExperimentalUnsignedTypes constructor(indices: UIntArray) : IndexBuffer {
    override val count: Int = indices.size
    private val rendererID = glCreateBuffers() //gl.createBuffers()

    init {
        glBindBuffer(GL_ARRAY_BUFFER, rendererID) //gl.bindBuffer(BufferTarget.ARRAY, rendererID)
        glBufferData(GL_ARRAY_BUFFER, indices.toIntArray(), GL_STATIC_DRAW)
    }

    override fun close() = glDeleteBuffers(rendererID) //gl.deleteBuffers(rendererID)

    override fun bind() = glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, rendererID) //gl.bindBuffer(BufferTarget.ELEMENT_ARRAY, rendererID)

    override fun unbind() = glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0) //gl.bindBuffer(BufferTarget.ELEMENT_ARRAY, GlBuffer())
}
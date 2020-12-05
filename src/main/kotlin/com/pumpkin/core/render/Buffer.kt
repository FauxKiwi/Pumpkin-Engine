package com.pumpkin.core.render

import com.pumpkin.core.PumpkinError
import com.pumpkin.core.Ref
import com.pumpkin.platform.opengl.OpenGLIndexBuffer
import com.pumpkin.platform.opengl.OpenGLVertexBuffer
import gln.VertexAttrType
import org.lwjgl.opengl.GL20

enum class ShaderDataType {
    Float, Float2, Float3, Float4,
    Mat3, Mat4,
    Int, Int2, Int3, Int4,
    Bool;

    fun toVertexAttrType() = when (this) {
        Float, Float2, Float3, Float4, Mat3, Mat4 -> VertexAttrType.FLOAT
        Int, Int2, Int3, Int4 -> VertexAttrType.INT
        Bool -> VertexAttrType(GL20.GL_BOOL)
    }

    fun size() = when (this) {
        Float, Int -> 4
        Float2, Int2 -> 4 * 2
        Float3, Int3 -> 4 * 3
        Float4, Int4 -> 4 * 4
        Mat3 -> 4 * 3 * 3
        Mat4 -> 4 * 4 * 4
        Bool -> 1
    }

    fun componentCount() = when (this) {
        Float, Int, Bool -> 1
        Float2, Int2 -> 2
        Float3, Int3 -> 3
        Float4, Int4 -> 4
        Mat3 -> 3 * 3
        Mat4 -> 4 * 4
    }
}

data class BufferElement(val dataType: ShaderDataType, val name: String, val normalized: Boolean = false) {
    val size = dataType.size()
    var offset = 0
}

class BufferLayout(private val elements: MutableList<BufferElement>) : Iterable<BufferElement> {
    private var stride = 0

    init {
        calculateOffsetsAndStride()
    }

    private fun calculateOffsetsAndStride() {
        var offset = 0
        stride = 0
        for (element in elements) {
            element.offset = offset
            offset += element.size
            stride += element.size
        }
    }

    fun getStride() = stride

    override fun iterator(): Iterator<BufferElement> = elements.iterator()
}

interface VertexBuffer : AutoCloseable {
    var layout: BufferLayout?

    companion object {
        fun create(vertices: FloatArray): Ref<VertexBuffer> = when (Renderer.getAPI()) {
            RendererAPI.API.None -> {
                throw PumpkinError("Having no render API is currently not supported")
            }
            RendererAPI.API.OpenGL -> Ref(OpenGLVertexBuffer(vertices))
        }
    }

    fun bind()

    fun unbind()
}

interface IndexBuffer : AutoCloseable{
    val count: Int

    companion object {
        @ExperimentalUnsignedTypes
        fun create(indices: UIntArray): Ref<IndexBuffer> = when (Renderer.getAPI()) {
            RendererAPI.API.None -> {
                throw PumpkinError("Having no render API is currently not supported")
            }
            RendererAPI.API.OpenGL -> Ref(OpenGLIndexBuffer(indices))
        }
    }

    fun bind()

    fun unbind()
}
package com.pumpkin.core.render

import com.pumpkin.core.PumpkinError
import com.pumpkin.core.Ref
import com.pumpkin.platform.opengl.OpenGLTexture2D
import java.nio.ByteBuffer

interface Texture : AutoCloseable {
    val width: Int
    val height: Int

    fun bind(slot: Int = 0)
}

interface Texture2D : Texture {
    companion object {
        fun create(path: String): Ref<Texture2D> = when (Renderer.getAPI()) {
            RendererAPI.API.None -> {
                throw PumpkinError("Having no render API is currently not supported")
            }
            RendererAPI.API.OpenGL -> Ref(OpenGLTexture2D(path))
        }

        fun create(width: Int, height: Int): Ref<Texture2D> = when (Renderer.getAPI()) {
            RendererAPI.API.None -> {
                throw PumpkinError("Having no render API is currently not supported")
            }
            RendererAPI.API.OpenGL -> Ref(OpenGLTexture2D(width, height))
        }
    }

    fun setData(data: ByteBuffer, size: Int)

    fun setFilter(filter: Filter)

    fun setWrap(wrapMode: WrapMode)

    enum class Filter { Linear, Nearest }

    enum class WrapMode { Repeat, Clamp }
}

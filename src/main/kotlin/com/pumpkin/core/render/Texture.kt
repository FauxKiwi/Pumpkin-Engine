package com.pumpkin.core.render

import com.pumpkin.core.PumpkinError
import com.pumpkin.core.Ref
import com.pumpkin.platform.opengl.OpenGLTexture2D

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
    }

    fun setFilter(filter: Filter)

    fun setWrap(wrapMode: WrapMode)

    enum class Filter { Linear, Nearest }

    enum class WrapMode { Repeat, Clamp }
}

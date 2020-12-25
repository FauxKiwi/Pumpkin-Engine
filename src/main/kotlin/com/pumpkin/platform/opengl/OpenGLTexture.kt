package com.pumpkin.platform.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.renderer.Texture2D
import gli_.gli
import org.lwjgl.opengl.GL45C.*
import java.nio.ByteBuffer

class OpenGLTexture2D : Texture2D {
    var format: Pair<Int, Int>

    override var width = 0
    override var height = 0
    override val rendererID/*: Int
        get() = _rendererID.name

    private val _rendererID: GlTexture Int*/ = glCreateTextures(GL_TEXTURE_2D) //gl.createTextures(TextureTarget._2D)

    constructor(path: String) {
        val data = gli.load(ClassLoader.getSystemResource(path).toURI(), true) //TODO
        width = data.extent()[0]
        height = data.extent()[1]

        format = when (data.format.blockSize) {
            3 -> Pair(GL_RGB8, GL_RGB)
            4 -> Pair(GL_RGBA8, GL_RGBA)
            else -> Debug.exception("Impossible number of channels")
        }

        glTextureStorage2D(rendererID, 1, format.first, width, height)

        setFilter(Texture2D.Filter.Linear, Texture2D.Filter.Nearest)
        setWrap(Texture2D.WrapMode.Repeat)

        glTextureSubImage2D(rendererID, 0, 0, 0, width, height, format.second, GL_UNSIGNED_BYTE, data.data())
    }

    constructor(width: Int, height: Int) {
        this.width = width
        this.height = height
        format = Pair(GL_RGBA8, GL_RGBA)
        glTextureStorage2D(rendererID, 1, format.first, width, height)

        setFilter(Texture2D.Filter.Linear, Texture2D.Filter.Nearest)
        setWrap(Texture2D.WrapMode.Repeat)
    }

    override fun setData(data: ByteBuffer) {
        glTextureSubImage2D(rendererID, 0, 0, 0, width, height, format.second, GL_UNSIGNED_BYTE, data)
    }

    override fun close() = glDeleteTextures(rendererID) //gl.deleteTexture(rendererID)

    override fun bind(slot: Int) = glBindTextureUnit(slot, rendererID) //gl.bindTextureUnit(slot, rendererID)

    override fun equals(other: Any?) = if (other is OpenGLTexture2D) rendererID == other.rendererID else false

    override fun setFilter(minFilter: Texture2D.Filter, magFilter: Texture2D.Filter) {
        glTextureParameteri(rendererID, GL_TEXTURE_MIN_FILTER, when (minFilter) {
            Texture2D.Filter.Linear -> GL_LINEAR
            Texture2D.Filter.Nearest -> GL_NEAREST
        })
        glTextureParameteri(
            rendererID, GL_TEXTURE_MAG_FILTER, when (magFilter) {
                Texture2D.Filter.Linear -> GL_LINEAR
                Texture2D.Filter.Nearest -> GL_NEAREST
            }
        )
    }

    override fun setWrap(wrapMode: Texture2D.WrapMode) {
        val glWrapMode = when (wrapMode) {
            Texture2D.WrapMode.Repeat -> GL_REPEAT
            Texture2D.WrapMode.Mirror -> GL_MIRRORED_REPEAT
            Texture2D.WrapMode.ClampEdge -> GL_CLAMP_TO_EDGE
            Texture2D.WrapMode.ClampBorder -> GL_CLAMP_TO_BORDER
        }
        glTextureParameteri(rendererID, GL_TEXTURE_WRAP_S, glWrapMode)
        glTextureParameteri(rendererID, GL_TEXTURE_WRAP_T, glWrapMode)
    }
}
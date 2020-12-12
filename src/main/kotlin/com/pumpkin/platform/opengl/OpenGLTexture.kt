package com.pumpkin.platform.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.renderer.Texture2D
import com.pumpkin.core.stack
import gli_.gli
import gln.TextureTarget
import gln.gl
import gln.identifiers.GlTexture
import org.lwjgl.opengl.GL45C
import java.nio.ByteBuffer

class OpenGLTexture2D : Texture2D {
    lateinit var format: Pair<Int, Int>

    override var width = 0
    override var height = 0
    override val rendererID: Int
        get() = _rendererID.name

    private val _rendererID: GlTexture = gl.createTextures(TextureTarget._2D)

    constructor(path: String) {
        stack {
            val data = gli.load(ClassLoader.getSystemResource(path).toURI(), true)
            width = data.extent()[0]
            height = data.extent()[1]

            format = when (data.format.blockSize) {
                3 -> Pair(GL45C.GL_RGB8, GL45C.GL_RGB)
                4 -> Pair(GL45C.GL_RGBA8, GL45C.GL_RGBA)
                else -> Debug.exception("Impossible number of channels")
            }

            GL45C.glTextureStorage2D(_rendererID.name, 1, format.first, width, height)

            setFilter(Texture2D.Filter.Linear, Texture2D.Filter.Nearest)
            setWrap(Texture2D.WrapMode.Repeat)

            GL45C.glTextureSubImage2D(_rendererID.name, 0, 0, 0, width, height, format.second, GL45C.GL_UNSIGNED_BYTE, data.data())
        }
    }

    constructor(width: Int, height: Int) {
        this.width = width
        this.height = height
        stack {
            format = Pair(GL45C.GL_RGBA8, GL45C.GL_RGBA)
            GL45C.glTextureStorage2D(_rendererID.name, 1, format.first, width, height)

            setFilter(Texture2D.Filter.Linear, Texture2D.Filter.Nearest)
            setWrap(Texture2D.WrapMode.Repeat)
        }
    }

    override fun setData(data: ByteBuffer, size: Int) = stack {
        GL45C.glTextureSubImage2D(_rendererID.name, 0, 0, 0, width, height, format.second, GL45C.GL_UNSIGNED_BYTE, data)
    }

    override fun close() = gl.deleteTexture(_rendererID)

    override fun bind(slot: Int) = gl.bindTextureUnit(slot, _rendererID)

    override fun equals(other: Any?) = if (other is OpenGLTexture2D) rendererID == other.rendererID else false

    override fun setFilter(minFilter: Texture2D.Filter, magFilter: Texture2D.Filter) {
        GL45C.glTextureParameteri(_rendererID.name, GL45C.GL_TEXTURE_MIN_FILTER, when (minFilter) {
            Texture2D.Filter.Linear -> GL45C.GL_LINEAR
            Texture2D.Filter.Nearest -> GL45C.GL_NEAREST
        })
        GL45C.glTextureParameteri(
            _rendererID.name, GL45C.GL_TEXTURE_MAG_FILTER, when (magFilter) {
                Texture2D.Filter.Linear -> GL45C.GL_LINEAR
                Texture2D.Filter.Nearest -> GL45C.GL_NEAREST
            }
        )
    }

    override fun setWrap(wrapMode: Texture2D.WrapMode) {
        val glWrapMode = when (wrapMode) {
            Texture2D.WrapMode.Repeat -> GL45C.GL_REPEAT
            Texture2D.WrapMode.Mirror -> GL45C.GL_MIRRORED_REPEAT
            Texture2D.WrapMode.ClampEdge -> GL45C.GL_CLAMP_TO_EDGE
            Texture2D.WrapMode.ClampBorder -> GL45C.GL_CLAMP_TO_BORDER
        }
        GL45C.glTextureParameteri(_rendererID.name, GL45C.GL_TEXTURE_WRAP_S, glWrapMode)
        GL45C.glTextureParameteri(_rendererID.name, GL45C.GL_TEXTURE_WRAP_T, glWrapMode)
    }
}
package com.pumpkin.platform.opengl

import com.pumpkin.core.PumpkinError
import com.pumpkin.core.render.Texture2D
import com.pumpkin.core.stack
import gli_.gli
import gln.TextureTarget
import gln.gl
import gln.identifiers.GlTexture
import org.lwjgl.opengl.GL45C

class OpenGLTexture2D(private val path: String) : Texture2D {
    override var width = 0
    override var height = 0

    private val rendererID: GlTexture = gl.createTextures(TextureTarget._2D)
    private var slot = 0

    init {
        stack {
            val data = gli.load(path, true)
            width = data.extent()[0]
            height = data.extent()[1]

            val format: Pair<Int, Int> = when (data.format.blockSize) {
                3 -> Pair(GL45C.GL_RGB8, GL45C.GL_RGB)
                4 -> Pair(GL45C.GL_RGBA8, GL45C.GL_RGBA)
                else -> throw PumpkinError("Impossible number of channels")
            }

            GL45C.glTextureStorage2D(rendererID.name, 1, format.first, width, height)

            setFilter(Texture2D.Filter.Nearest)
            setWrap(Texture2D.WrapMode.Repeat)

            GL45C.glTextureSubImage2D(rendererID.name, 0, 0, 0, width, height, format.second, GL45C.GL_UNSIGNED_BYTE, data.data())
        }
    }

    override fun close() =  gl.deleteTexture(rendererID)

    override fun bind(slot: Int) = gl.bindTextureUnit(slot, rendererID)

    override fun setFilter(filter: Texture2D.Filter) {
        GL45C.glTextureParameteri(rendererID.name, GL45C.GL_TEXTURE_MIN_FILTER, GL45C.GL_LINEAR)
        GL45C.glTextureParameteri(rendererID.name, GL45C.GL_TEXTURE_MAG_FILTER, when (filter) {
            Texture2D.Filter.Linear -> GL45C.GL_LINEAR
            Texture2D.Filter.Nearest -> GL45C.GL_NEAREST
        })
    }

    override fun setWrap(wrapMode: Texture2D.WrapMode) {
        val glWrapMode = when (wrapMode) {
            Texture2D.WrapMode.Repeat -> GL45C.GL_REPEAT
            Texture2D.WrapMode.Clamp -> GL45C.GL_CLAMP_TO_EDGE
        }
        GL45C.glTextureParameteri(rendererID.name, GL45C.GL_TEXTURE_WRAP_S, glWrapMode)
        GL45C.glTextureParameteri(rendererID.name, GL45C.GL_TEXTURE_WRAP_T, glWrapMode)
    }
}
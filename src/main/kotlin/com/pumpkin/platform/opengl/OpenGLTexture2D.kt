package com.pumpkin.platform.opengl

import com.pumpkin.core.logErrorCore
import com.pumpkin.core.render.Texture2D
import com.pumpkin.core.stack
import gli_.gli
import gln.TextureTarget
import gln.gl
import gln.identifiers.GlTexture
import org.lwjgl.opengl.GL45C
import java.io.File
import java.net.URI
import java.nio.file.Path

class OpenGLTexture2D(private val path: String) : Texture2D {
    override var width = 0
    override var height = 0

    private val rendererID: GlTexture = gl.createTextures(TextureTarget._2D)

    init {
        stack {
            val data = gli.load(path, true)
            if (data.empty())
                throw Throwable().also { logErrorCore("Could not load texture") }
            width = data.extent()[0]
            height = data.extent()[1]

            GL45C.glTextureStorage2D(rendererID.name, 1, GL45C.GL_RGB8, width, height)
            GL45C.glTextureParameteri(rendererID.name, GL45C.GL_TEXTURE_MIN_FILTER, GL45C.GL_LINEAR)
            GL45C.glTextureParameteri(rendererID.name, GL45C.GL_TEXTURE_MAG_FILTER, GL45C.GL_NEAREST)
            GL45C.glTextureSubImage2D(rendererID.name, 0, 0, 0, width, height, GL45C.GL_RGB, GL45C.GL_UNSIGNED_BYTE, data.data())
        }
    }

    override fun close() {
        gl.deleteTexture(rendererID)
    }

    override fun bind(slot: Int) {
        gl.bindTextureUnit(slot, rendererID)
    }
}
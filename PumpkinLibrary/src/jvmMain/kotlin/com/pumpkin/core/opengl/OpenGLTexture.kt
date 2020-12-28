package com.pumpkin.core.opengl

import com.pumpkin.core.ByteBuffer
import com.pumpkin.core.Debug
import com.pumpkin.core.renderer.Texture2D
import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL45C.*
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.system.MemoryStack
import java.nio.file.Path

actual class OpenGLTexture2D : Texture2D {
    private var format: Pair<Int, Int>

    override var width = 0
    override var height = 0
    override val rendererID = glCreateTextures(GL_TEXTURE_2D)

    actual constructor(path: String) {
        var channels = 0
        val data = MemoryStack.stackPush().use { stack ->
            val xBuffer = stack.mallocInt(1)
            val yBuffer = stack.mallocInt(1)
            val channelsBuffer = stack.mallocInt(1)
            stbi_load(
                Path.of(ClassLoader.getSystemResource(path).toURI()).toString(),
                xBuffer, yBuffer, channelsBuffer, 0
            ).also {
                width = xBuffer.get(0)
                height = yBuffer.get(0)
                channels = channelsBuffer.get(0)
            }
        }
        Debug.assert(data, "Could not load image (${path})")

        format = when (channels) {
            3 -> Pair(GL_RGB8, GL_RGB)
            4 -> Pair(GL_RGBA8, GL_RGBA)
            else -> Debug.exception("Impossible number of channels")
        }

        glTextureStorage2D(rendererID, 1, format.first, width, height)

        setFilter(Texture2D.Filter.Linear, Texture2D.Filter.Nearest)
        setWrap(Texture2D.WrapMode.Repeat)

        glTextureSubImage2D(rendererID, 0, 0, 0, width, height, format.second, GL_UNSIGNED_BYTE, data!!)
    }

    actual constructor(width: Int, height: Int) {
        this.width = width
        this.height = height
        format = Pair(GL_RGBA8, GL_RGBA)
        glTextureStorage2D(rendererID, 1, format.first, width, height)

        setFilter(Texture2D.Filter.Linear, Texture2D.Filter.Nearest)
        setWrap(Texture2D.WrapMode.Repeat)
    }

    override fun close() = glDeleteTextures(rendererID)

    override fun bind(slot: Int) = glBindTextureUnit(slot, rendererID)

    override fun equals(other: Any?): Boolean = if (other is OpenGLTexture2D) rendererID == other.rendererID else false

    override fun setData(data: ByteBuffer) = glTextureSubImage2D(rendererID, 0, 0, 0, width, height, format.second, GL_UNSIGNED_BYTE, data.buffer)

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
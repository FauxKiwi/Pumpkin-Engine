package com.pumpkin.core.opengl

import com.pumpkin.core.ByteBuffer
import com.pumpkin.core.Debug
import com.pumpkin.core.MemoryStack
import com.pumpkin.core.renderer.Texture2D
import com.pumpkin.core.use
import org.khronos.webgl.WebGLRenderingContext as GL

actual class OpenGLTexture2D : Texture2D { //TODO
    private var format: Pair<Int, Int>

    override var width = 0
    override var height = 0
    override val rendererID = gl.createTexture()

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
            3 -> Pair(GL.RGB8, GL.RGB)
            4 -> Pair(GL.RGBA8, GL.RGBA)
            else -> Debug.exception("Impossible number of channels")
        }

        gl.textureStorage2D(rendererID, 1, format.first, width, height)

        setFilter(Texture2D.Filter.Linear, Texture2D.Filter.Nearest)
        setWrap(Texture2D.WrapMode.Repeat)

        gl.texSubImage2D(rendererID, 0, 0, 0, width, height, format.second, GL.UNSIGNED_BYTE, data!!)
    }

    actual constructor(width: Int, height: Int) {
        this.width = width
        this.height = height
        format = Pair(GL.RGBA8, GL.RGBA)
        gl.textureStorage2D(rendererID, 1, format.first, width, height)

        setFilter(Texture2D.Filter.Linear, Texture2D.Filter.Nearest)
        setWrap(Texture2D.WrapMode.Repeat)
    }

    override fun close() = gl.deleteTexture(rendererID)

    override fun bind(slot: Int) = gl.bindTextureUnit(slot, rendererID)

    override fun equals(other: Any?): Boolean = if (other is OpenGLTexture2D) rendererID == other.rendererID else false

    override fun setData(data: ByteBuffer) = gl.texSubImage2D(rendererID, 0, 0, 0, width, height, format.second, GL.UNSIGNED_BYTE, data.buffer)

    override fun setFilter(minFilter: Texture2D.Filter, magFilter: Texture2D.Filter) {
        gl.texParameteri(rendererID, GL.TEXTURE_MIN_FILTER, when (minFilter) {
            Texture2D.Filter.Linear -> GL.LINEAR
            Texture2D.Filter.Nearest -> GL.NEAREST
        })
        gl.texParameteri(
            rendererID, GL.TEXTURE_MAG_FILTER, when (magFilter) {
                Texture2D.Filter.Linear -> GL.LINEAR
                Texture2D.Filter.Nearest -> GL.NEAREST
            }
        )
    }

    override fun setWrap(wrapMode: Texture2D.WrapMode) {
        val glWrapMode = when (wrapMode) {
            Texture2D.WrapMode.Repeat -> GL.REPEAT
            Texture2D.WrapMode.Mirror -> GL.MIRRORED_REPEAT
            Texture2D.WrapMode.ClampEdge -> GL.CLAMP_TO_EDGE
            Texture2D.WrapMode.ClampBorder -> GL.CLAMP_TO_BORDER
        }
        gl.texParameteri(rendererID, GL.TEXTURE_WRAP_S, glWrapMode)
        gl.texParameteri(rendererID, GL.TEXTURE_WRAP_T, glWrapMode)
    }
}
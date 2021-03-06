package com.pumpkin.core.opengl

//import com.pumpkin.core.Debug
import com.pumpkin.core.renderer.Texture2D
//import com.pumpkin.core.stack
/*import org.lwjgl.opengl.GL45C.*
import org.lwjgl.stb.STBImage.stbi_load
import java.nio.ByteBuffer
import java.nio.file.Path*/

expect class OpenGLTexture2D : Texture2D {
    //var format: Pair<Int, Int>

    //override var width = 0
    //override var height = 0
    //override val rendererID/*: Int
    //    get() = _rendererID.name

    //private val _rendererID: GlTexture Int*/ = glCreateTextures(GL_TEXTURE_2D) //gl.createTextures(TextureTarget._2D)

    constructor(path: String) //{
        /*val data = gli.load(ClassLoader.getSystemResource(path).toURI(), true)
        width = data.extent()[0]
        height = data.extent()[1]*/
        /*var channels = 0
        val data = stack { stack ->
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

        format = when (/*data.format.blockSize*/ channels) {
            3 -> Pair(GL_RGB8, GL_RGB)
            4 -> Pair(GL_RGBA8, GL_RGBA)
            else -> Debug.exception("Impossible number of channels")
        }

        glTextureStorage2D(rendererID, 1, format.first, width, height)

        setFilter(Texture2D.Filter.Linear, Texture2D.Filter.Nearest)
        setWrap(Texture2D.WrapMode.Repeat)

        glTextureSubImage2D(rendererID, 0, 0, 0, width, height, format.second, GL_UNSIGNED_BYTE, data!! /*data.data()*/)*/
    //}

    constructor(width: Int, height: Int)/* {
        this.width = width
        this.height = height
        format = Pair(GL_RGBA8, GL_RGBA)
        glTextureStorage2D(rendererID, 1, format.first, width, height)

        setFilter(Texture2D.Filter.Linear, Texture2D.Filter.Nearest)
        setWrap(Texture2D.WrapMode.Repeat)
    }*/

    /*override fun setData(data: ByteBuffer) {
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
    }*/
}
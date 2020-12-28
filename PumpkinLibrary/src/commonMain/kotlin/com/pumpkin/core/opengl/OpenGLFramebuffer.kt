package com.pumpkin.core.opengl

//import com.pumpkin.core.Debug
import com.pumpkin.core.Debug
import com.pumpkin.core.renderer.Framebuffer
import com.pumpkin.core.renderer.FramebufferSpecification
//import org.lwjgl.opengl.GL45C.*
//import java.nio.IntBuffer

expect class OpenGLFramebuffer(/*override val*/ specification: FramebufferSpecification) : Framebuffer {
    //private var rendererID: Int = 0
    //private var colorAttachment: Int = 0
    //private var depthAttachment: Int = 0

    //override val colorAttachmentID: Int
    //    get() = colorAttachment

    /*init {
        invalidate()
    }*/

    /*override fun close() {
        glDeleteFramebuffers(rendererID)
        glDeleteTextures(colorAttachment)
        glDeleteTextures(depthAttachment)
    }*/

    fun invalidate() /*{
        if (rendererID != 0) {
            glDeleteFramebuffers(rendererID)
            glDeleteTextures(colorAttachment)
            glDeleteTextures(depthAttachment)
        }
        rendererID = glCreateFramebuffers()
        glBindFramebuffer(GL_FRAMEBUFFER, rendererID)

        colorAttachment = glCreateTextures(GL_TEXTURE_2D)
        glBindTexture(GL_TEXTURE_2D, colorAttachment)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, specification.width, specification.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null as IntBuffer?)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorAttachment, 0)

        depthAttachment = glCreateTextures(GL_TEXTURE_2D)
        glBindTexture(GL_TEXTURE_2D, depthAttachment)
        glTexStorage2D(GL_TEXTURE_2D, 1, GL_DEPTH24_STENCIL8, specification.width, specification.height)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depthAttachment, 0)

        Debug.assert(glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE, "Framebuffer is incomplete!")

        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }*/

    /*override fun bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, rendererID)
        glViewport(0, 0, specification.width, specification.height)
    }

    override fun unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }*/
}

fun OpenGLFramebuffer.resizeUni(width: Int, height: Int) {
    if (width <= 0 || height <= 0) {
        Debug.logWarnCore("Couldn't resize framebuffer to $width, $height")
        return
    }
    specification.width = width
    specification.height = height
    invalidate()
}
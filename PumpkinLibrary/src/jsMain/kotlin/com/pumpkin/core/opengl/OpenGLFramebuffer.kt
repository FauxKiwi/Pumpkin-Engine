package com.pumpkin.core.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.renderer.Framebuffer
import com.pumpkin.core.renderer.FramebufferSpecification
import org.khronos.webgl.WebGLFramebuffer
import org.khronos.webgl.WebGLTexture
import org.khronos.webgl.WebGLRenderingContext as GL

actual class OpenGLFramebuffer actual constructor(override val specification: FramebufferSpecification) : Framebuffer {
    private var rendererID: WebGLFramebuffer? = null
    private var colorAttachment: WebGLTexture? = null
    private var depthAttachment: WebGLTexture? = null

    override val colorAttachmentID: Int
        get() = 0 //colorAttachment

    init {
        invalidate()
    }

    override fun close() {
        gl.deleteFramebuffer(rendererID)
        gl.deleteTexture(colorAttachment)
        gl.deleteTexture(depthAttachment)
    }

    actual fun invalidate() {
        if (rendererID != null) {
            gl.deleteFramebuffer(rendererID)
            gl.deleteTexture(colorAttachment)
            gl.deleteTexture(depthAttachment)
        }
        rendererID = gl.createFramebuffer()
        gl.bindFramebuffer(GL.FRAMEBUFFER, rendererID)

        colorAttachment = gl.createTexture()
        gl.bindTexture(GL.TEXTURE_2D, colorAttachment)
        gl.texImage2D(GL.TEXTURE_2D, 0, GL.RGBA8, specification.width, specification.height, 0, GL.RGBA, GL.UNSIGNED_BYTE, null)
        gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MIN_FILTER, GL.LINEAR)
        gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MAG_FILTER, GL.LINEAR)

        gl.framebufferTexture2D(GL.FRAMEBUFFER, GL.COLOR_ATTACHMENT0, GL.TEXTURE_2D, colorAttachment, 0)

        depthAttachment = gl.createTexture()
        gl.bindTexture(GL.TEXTURE_2D, depthAttachment)
        gl.texStorage2D(GL.TEXTURE_2D, 1, GL.DEPTH24_STENCIL8, specification.width, specification.height)
        gl.framebufferTexture2D(GL.FRAMEBUFFER, GL.DEPTH_STENCIL_ATTACHMENT, GL.TEXTURE_2D, depthAttachment, 0)

        Debug.assert(gl.checkFramebufferStatus(GL.FRAMEBUFFER) == GL.FRAMEBUFFER_COMPLETE, "Framebuffer is incomplete!")

        gl.bindFramebuffer(GL.FRAMEBUFFER, null)
    }

    override fun resize(width: Int, height: Int) {
        resizeUni(width, height)
    }

    override fun bind() {
        gl.bindFramebuffer(GL.FRAMEBUFFER, rendererID)
        gl.viewport(0, 0, specification.width, specification.height)
    }

    override fun unbind() = gl.bindFramebuffer(GL.FRAMEBUFFER, null)
}
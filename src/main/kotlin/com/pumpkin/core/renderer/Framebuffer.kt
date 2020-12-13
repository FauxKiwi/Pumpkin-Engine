package com.pumpkin.core.renderer

import com.pumpkin.core.Debug
import com.pumpkin.platform.opengl.OpenGLFrameBuffer

data class FramebufferSpecification(
    var width: Int = 0,
    var height: Int = 0,
    val samples: Int = 1,
    val swapChainTarget: Boolean = false,
)

interface Framebuffer : AutoCloseable {
    companion object {
        fun create(specification: FramebufferSpecification) = when (Renderer.getAPI()) {
            RendererAPI.API.None -> Debug.exception("Having no renderer api is currently not supported")
            RendererAPI.API.OpenGL -> OpenGLFrameBuffer(specification)
        }
    }

    val colorAttachmentID: Int
    val specification: FramebufferSpecification

    fun resize(width: Int, height: Int)

    fun bind()

    fun unbind()
}
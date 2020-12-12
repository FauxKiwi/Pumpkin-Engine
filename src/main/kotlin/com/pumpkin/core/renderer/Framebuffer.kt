package com.pumpkin.core.renderer

import com.pumpkin.core.Debug
import com.pumpkin.platform.opengl.OpenGLFrameBuffer

data class FramebufferSpecification(
    val width: Int,
    val height: Int,
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

    fun bind()

    fun unbind()
}
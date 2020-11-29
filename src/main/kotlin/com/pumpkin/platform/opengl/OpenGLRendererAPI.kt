package com.pumpkin.platform.opengl

import com.pumpkin.core.logErrorCore
import com.pumpkin.core.render.RendererAPI
import com.pumpkin.core.render.VertexArray
import glm_.vec4.Vec4
import gln.ClearBufferMask
import gln.DrawMode
import gln.gl

class OpenGLRendererAPI : RendererAPI {
    override val api = RendererAPI.API.OpenGL

    override fun setClearColor(color: Vec4) {
        gl.clearColor(color.r, color.g, color.b, color.a)
    }

    override fun clear() {
        gl.clear(ClearBufferMask.COLOR_BUFFER_BIT or ClearBufferMask.DEPTH_BUFFER_BIT)
    }

    override fun drawIndexed(vertexArray: VertexArray) {
        vertexArray.indexBuffer?.let { gl.drawElements(DrawMode.TRIANGLES, it.count) } ?: throw Throwable().also { logErrorCore("No index buffer!") }
    }
}
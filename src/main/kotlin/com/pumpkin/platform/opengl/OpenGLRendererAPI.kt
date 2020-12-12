package com.pumpkin.platform.opengl

import com.pumpkin.core.render.RendererAPI
import com.pumpkin.core.render.VertexArray
import com.pumpkin.core.stack
import glm_.vec4.Vec4
import gln.*
import gln.identifiers.GlTexture
import org.lwjgl.opengl.GL11C

class OpenGLRendererAPI : RendererAPI {
    override val api = RendererAPI.API.OpenGL

    override fun init() {
        stack {
            gl.enable(State.BLEND)
            gl.blendFunc(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA)

            gl.enable(State.DEPTH_TEST)
        }
    }

    override fun setClearColor(color: Vec4) = gl.clearColor(color.r, color.g, color.b, color.a)

    override fun clear() = gl.clear(ClearBufferMask.COLOR_BUFFER_BIT or ClearBufferMask.DEPTH_BUFFER_BIT)

    override fun drawIndexed(vertexArray: VertexArray, count: Int) {
        gl.drawArrays(DrawMode.POINTS, 0, if(count == 0) vertexArray.indexBuffer!!().count else count)
        gl.bindTexture(TextureTarget._2D, GlTexture())
    }

    override fun setViewport(x: Int, y: Int, width: Int, height: Int) {
        gl.viewport(x, y, width, height)
    }
}
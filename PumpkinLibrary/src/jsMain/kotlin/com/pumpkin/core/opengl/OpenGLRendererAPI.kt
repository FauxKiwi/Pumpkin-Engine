package com.pumpkin.core.opengl

import com.pumpkin.core.renderer.RendererAPI
import com.pumpkin.core.renderer.VertexArray
import glm.Vec4
import org.khronos.webgl.WebGLRenderingContext as GL

actual class OpenGLRendererAPI : RendererAPI {
    override val api = RendererAPI.API.OpenGL

    override fun init() {
        gl.enable(GL.BLEND)
        gl.blendFunc(GL.SRC_ALPHA, GL.ONE_MINUS_SRC_ALPHA)

        gl.enable(GL.DEPTH_TEST)
    }

    override fun setClearColor(color: Vec4) = gl.clearColor(color.x, color.y, color.z, color.w)

    override fun clear() = gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)

    override fun drawIndexed(vertexArray: VertexArray, count: Int) {
        gl.drawElements(GL.TRIANGLES, if (count == 0) vertexArray.indexBuffer!!().count else count, GL.UNSIGNED_INT, 0)
        gl.bindTexture(GL.TEXTURE_2D, null)
    }

    override fun setViewport(x: Int, y: Int, width: Int, height: Int) = gl.viewport(x, y, width, height)
}
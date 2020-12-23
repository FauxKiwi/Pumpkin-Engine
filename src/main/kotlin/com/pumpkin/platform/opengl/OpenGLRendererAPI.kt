package com.pumpkin.platform.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.renderer.RendererAPI
import com.pumpkin.core.renderer.VertexArray
import glm_.vec4.Vec4
import gln.*
import gln.identifiers.GlTexture
import gln.misc.GlDebugSeverity
import gln.misc.GlDebugSource
import gln.misc.GlDebugType
import org.lwjgl.opengl.GL15C
import org.lwjgl.opengl.GL45C
import org.lwjgl.system.MemoryUtil

class OpenGLRendererAPI : RendererAPI {
    override val api = RendererAPI.API.OpenGL

    private fun messageCallback(source: Int, type: Int, id: Int, severity: Int, length: Int, message: Long, userParam: Long) {
        when (severity) {
            GL45C.GL_DEBUG_SEVERITY_HIGH -> Debug.logFatalCore(MemoryUtil.memUTF8(message, length))
            GL45C.GL_DEBUG_SEVERITY_MEDIUM -> Debug.logErrorCore(MemoryUtil.memUTF8(message, length))
            GL45C.GL_DEBUG_SEVERITY_LOW -> Debug.logWarnCore(MemoryUtil.memUTF8(message, length))
            GL45C.GL_DEBUG_SEVERITY_NOTIFICATION -> Debug.logTraceCore(MemoryUtil.memUTF8(message, length))
        }
    }

    override fun init() {
        //#ifdef DEBUG
        gl.enable(State.DEBUG_OUTPUT)
        gl.enable(State.DEBUG_OUTPUT_SYNCHRONOUS)
        gl.debugMessageCallback(::messageCallback, 0L)
        //gl.debugMessageControl(GlDebugSource.DONT_CARE, GlDebugType.DONT_CARE, GlDebugSeverity.NOTIFICATION, 0, false)
        //#endif

        gl.enable(State.BLEND)
        gl.blendFunc(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA)

        gl.enable(State.DEPTH_TEST)
    }

    override fun setClearColor(color: Vec4) = gl.clearColor(color.r, color.g, color.b, color.a)

    override fun clear() = gl.clear(ClearBufferMask.COLOR_BUFFER_BIT or ClearBufferMask.DEPTH_BUFFER_BIT)

    override fun drawIndexed(vertexArray: VertexArray, count: Int) {
        GL15C.glDrawElements(GL15C.GL_TRIANGLES, if (count == 0) vertexArray.indexBuffer!!().count else count, GL15C.GL_UNSIGNED_INT, 0L)//(DrawMode.TRIANGLES, if (count == 0) vertexArray.indexBuffer!!().count else count)
        gl.bindTexture(TextureTarget._2D, GlTexture())
    }

    override fun setViewport(x: Int, y: Int, width: Int, height: Int) {
        gl.viewport(x, y, width, height)
    }
}
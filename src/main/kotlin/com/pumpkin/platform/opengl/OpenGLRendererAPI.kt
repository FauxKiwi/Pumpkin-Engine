package com.pumpkin.platform.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.renderer.RendererAPI
import com.pumpkin.core.renderer.VertexArray
import glm_.vec4.Vec4
import gln.*
import org.lwjgl.opengl.GL43C.*
import org.lwjgl.system.MemoryUtil

class OpenGLRendererAPI : RendererAPI {
    override val api = RendererAPI.API.OpenGL

    private fun messageCallback(source: Int, type: Int, id: Int, severity: Int, length: Int, message: Long, userParam: Long) {
        when (severity) {
            GL_DEBUG_SEVERITY_HIGH -> Debug.logFatalCore(MemoryUtil.memUTF8(message, length))
            GL_DEBUG_SEVERITY_MEDIUM -> Debug.logErrorCore(MemoryUtil.memUTF8(message, length))
            GL_DEBUG_SEVERITY_LOW -> Debug.logWarnCore(MemoryUtil.memUTF8(message, length))
            GL_DEBUG_SEVERITY_NOTIFICATION -> Debug.logTraceCore(MemoryUtil.memUTF8(message, length))
        }
    }

    override fun init() {
        //#ifdef DEBUG
        glEnable(GL_DEBUG_OUTPUT) //gl.enable(State.DEBUG_OUTPUT)
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS) //gl.enable(State.DEBUG_OUTPUT_SYNCHRONOUS)
        glDebugMessageCallback(::messageCallback, 0L) //gl.debugMessageCallback(::messageCallback, 0L)
        //gl.debugMessageControl(GlDebugSource.DONT_CARE, GlDebugType.DONT_CARE, GlDebugSeverity.NOTIFICATION, 0, false)
        //#endif

        glEnable(GL_BLEND) //gl.enable(State.BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA) //gl.blendFunc(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA)

        glEnable(GL_DEPTH_TEST) //gl.enable(State.DEPTH_TEST)
    }

    override fun setClearColor(color: Vec4) = gl.clearColor(color.r, color.g, color.b, color.a)

    override fun clear() = gl.clear(ClearBufferMask.COLOR_BUFFER_BIT or ClearBufferMask.DEPTH_BUFFER_BIT)

    override fun drawIndexed(vertexArray: VertexArray, count: Int) {
        glDrawElements(GL_TRIANGLES, if (count == 0) vertexArray.indexBuffer!!().count else count, GL_UNSIGNED_INT, 0L)
        glBindTexture(GL_TEXTURE_2D, 0) //gl.bindTexture(TextureTarget._2D, GlTexture())
    }

    override fun setViewport(x: Int, y: Int, width: Int, height: Int) {
        glViewport(x, y, width, height) //gl.viewport(x, y, width, height)
    }
}
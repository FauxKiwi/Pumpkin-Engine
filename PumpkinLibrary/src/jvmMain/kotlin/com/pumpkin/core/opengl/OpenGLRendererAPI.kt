package com.pumpkin.core.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.renderer.RendererAPI
import com.pumpkin.core.renderer.VertexArray
import glm.Vec4
import org.lwjgl.opengl.GL43C.*
import org.lwjgl.system.MemoryUtil

actual class OpenGLRendererAPI : RendererAPI {
    override val api = RendererAPI.API.OpenGL

    private fun messageCallback(severity: Int, length: Int, message: Long) {
        when (severity) {
            GL_DEBUG_SEVERITY_HIGH -> Debug.logFatalCore(MemoryUtil.memUTF8(message, length))
            GL_DEBUG_SEVERITY_MEDIUM -> Debug.logErrorCore(MemoryUtil.memUTF8(message, length))
            GL_DEBUG_SEVERITY_LOW -> Debug.logWarnCore(MemoryUtil.memUTF8(message, length))
            GL_DEBUG_SEVERITY_NOTIFICATION -> Debug.logTraceCore(MemoryUtil.memUTF8(message, length))
        }
    }

    override fun init() {
        //#ifdef DEBUG
        glEnable(GL_DEBUG_OUTPUT)
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS)
        glDebugMessageCallback(
            { _, _, _, severity, length, message, _ -> messageCallback(severity, length, message) }, 0L
        )
        //#endif

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glEnable(GL_DEPTH_TEST)
    }

    override fun setClearColor(color: Vec4) = glClearColor(color.x, color.y, color.z, color.w)

    override fun clear() = glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

    override fun drawIndexed(vertexArray: VertexArray, count: Int) {
        glDrawElements(GL_TRIANGLES, if (count == 0) vertexArray.indexBuffer!!().count else count, GL_UNSIGNED_INT, 0L)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    override fun setViewport(x: Int, y: Int, width: Int, height: Int) = glViewport(x, y, width, height)
}
package com.pumpkin.core.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.renderer.GraphicsContext
import org.khronos.webgl.WebGLRenderingContext as GL

inline val gl: GL get() = com.pumpkin.platform.webglWindow.webgl

actual class OpenGLContext(private val windowHandle: Long) : GraphicsContext {
    override fun init() {
        //glfwMakeContextCurrent(windowHandle)
        //GL.createCapabilities()

        Debug.logInfoCore("OpenGL Info:")
        Debug.logInfoCore("  Vendor: ${getVendor()}")
        Debug.logInfoCore("  Renderer: ${getRenderer()}")
        Debug.logInfoCore("  Version: ${getVersion()}")
    }

    override fun swapBuffers() = TODO()//glfwSwapBuffers(windowHandle)

    private fun getVendor() = GL.VENDOR //glGetString(GL_VENDOR)

    private fun getRenderer() = GL.RENDERER //glGetString(GL_RENDERER)

    private fun getVersion() = GL.VERSION //glGetString(GL_VERSION)
}
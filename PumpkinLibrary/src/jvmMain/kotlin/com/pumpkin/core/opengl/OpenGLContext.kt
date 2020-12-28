package com.pumpkin.core.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.renderer.GraphicsContext
import org.lwjgl.glfw.GLFW.glfwMakeContextCurrent
import org.lwjgl.glfw.GLFW.glfwSwapBuffers
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11C.*

actual class OpenGLContext(private val windowHandle: Long) : GraphicsContext {
    override fun init() {
        glfwMakeContextCurrent(windowHandle)
        GL.createCapabilities()

        Debug.logInfoCore("OpenGL Info:")
        Debug.logInfoCore("  Vendor: ${getVendor()}")
        Debug.logInfoCore("  Renderer: ${getRenderer()}")
        Debug.logInfoCore("  Version: ${getVersion()}")
    }

    override fun swapBuffers() = glfwSwapBuffers(windowHandle)

    private fun getVendor() = glGetString(GL_VENDOR)

    private fun getRenderer() = glGetString(GL_RENDERER)

    private fun getVersion() = glGetString(GL_VERSION)
}
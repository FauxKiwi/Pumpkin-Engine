package com.pumpkin.platform.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.renderer.GraphicsContext
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11C.*
import uno.glfw.GlfwWindow

class OpenGLContext(private val windowHandle: GlfwWindow) : GraphicsContext {

    override fun init() {
        windowHandle.makeContextCurrent()
        GL.createCapabilities()


        Debug.logInfoCore("OpenGL Info:")
        Debug.logInfoCore("  Vendor: ${getVendor()}")
        Debug.logInfoCore("  Renderer: ${getRenderer()}")
        Debug.logInfoCore("  Version: ${getVersion()}")
    }

    override fun swapBuffers() = windowHandle.swapBuffers()

    private fun getVendor() = glGetString(GL_VENDOR)

    private fun getRenderer() = glGetString(GL_RENDERER)

    private fun getVersion() = glGetString(GL_VERSION)
}
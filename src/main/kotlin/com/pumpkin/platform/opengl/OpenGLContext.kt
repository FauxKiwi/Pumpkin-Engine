package com.pumpkin.platform.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.render.GraphicsContext
import com.pumpkin.core.stack
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11C
import uno.glfw.GlfwWindow

class OpenGLContext(private val windowHandle: GlfwWindow) : GraphicsContext {

    override fun init() {
        stack {
            windowHandle.makeContextCurrent()
            GL.createCapabilities()
        }

        Debug.logInfoCore("OpenGL Info:")
        Debug.logInfoCore("  Vendor: ${getVendor()}")
        Debug.logInfoCore("  Renderer: ${getRenderer()}")
        Debug.logInfoCore("  Version: ${getVersion()}")
    }

    override fun swapBuffers() = windowHandle.swapBuffers()

    private fun getVendor() = GL11C.glGetString(GL11C.GL_VENDOR)

    private fun getRenderer() = GL11C.glGetString(GL11C.GL_RENDERER)

    private fun getVersion() = GL11C.glGetString(GL11C.GL_VERSION)
}
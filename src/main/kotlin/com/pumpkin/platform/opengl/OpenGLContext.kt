package com.pumpkin.platform.opengl

import com.pumpkin.core.logInfoCore
import com.pumpkin.core.render.GraphicsContext
import gln.gl
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11C
import uno.glfw.GlfwWindow

class OpenGLContext(private val windowHandle: GlfwWindow) : GraphicsContext {

    override fun init() {
        windowHandle.makeContextCurrent()
        GL.createCapabilities()

        logInfoCore("OpenGL Info:")
        logInfoCore("  Vendor: ${getVendor()}")
        logInfoCore("  Renderer: ${getRenderer()}")
        logInfoCore("  Version: ${getVersion()}")
    }

    override fun swapBuffers() {
        windowHandle.swapBuffers()
    }

    private fun getVendor() = GL11C.glGetString(GL11C.GL_VENDOR)

    private fun getRenderer() = GL11C.glGetString(GL11C.GL_RENDERER)

    private fun getVersion() = GL11C.glGetString(GL11C.GL_VERSION)
}
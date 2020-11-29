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
        logInfoCore("  Vendor: ${GL11C.glGetString(GL11C.GL_VENDOR)}")
        logInfoCore("  Renderer: ${GL11C.glGetString(GL11C.GL_RENDERER)}")
        logInfoCore("  Version: ${GL11C.glGetString(GL11C.GL_VERSION)}")
    }

    override fun swapBuffers() {
        windowHandle.swapBuffers()
    }
}
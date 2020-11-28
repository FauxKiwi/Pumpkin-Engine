package com.pumpkin.platform.opengl

import com.pumpkin.core.logInfoCore
import com.pumpkin.core.render.GraphicsContext
import gln.VERSION
import uno.glfw.GlfwWindow

class OpenGLContext(private val windowHandle: GlfwWindow) : GraphicsContext {

    override fun init() {
        windowHandle.makeContextCurrent()

        logInfoCore("OpenGL Info:")
        logInfoCore("  Version: $VERSION")
    }

    override fun swapBuffers() {
        windowHandle.swapBuffers()
    }
}
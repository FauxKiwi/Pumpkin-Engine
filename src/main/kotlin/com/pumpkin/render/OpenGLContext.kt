package com.pumpkin.render

import com.pumpkin.logInfoCore
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
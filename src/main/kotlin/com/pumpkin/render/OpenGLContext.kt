package com.pumpkin.render

import uno.glfw.GlfwWindow

class OpenGLContext(private val windowHandle: GlfwWindow) : GraphicsContext {

    override fun init() {
        windowHandle.makeContextCurrent()
    }

    override fun swapBuffers() {
        windowHandle.swapBuffers()
    }
}
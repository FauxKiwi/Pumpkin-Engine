package com.pumpkin.platform.windows

import com.pumpkin.core.input.AbstractInput
import com.pumpkin.core.input.instance
import com.pumpkin.core.window.Window
import glm_.vec2.Vec2
import org.lwjgl.glfw.GLFW

object WindowsInput : AbstractInput {

    init {
        instance = WindowsInput
    }

    override fun isKeyPressed(keycode: Int): Boolean {
        val state = GLFW.glfwGetKey(Window.getWindow().window.handle.value, keycode)
        return state == GLFW.GLFW_PRESS || state == GLFW.GLFW_REPEAT
    }

    override fun isMouseButtonPressed(button: Int): Boolean {
        val state = GLFW.glfwGetMouseButton(Window.getWindow().window.handle.value, button)
        return state == GLFW.GLFW_PRESS
    }

    override fun getMousePosition(): Vec2 {
        val pos = Window.getWindow().window.cursorPos
        return Vec2(pos[0].toFloat(), pos[1].toFloat())
    }

    override fun getMouseX() = getMousePosition()[0]

    override fun getMouseY() = getMousePosition()[1]
}
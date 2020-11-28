package com.pumpkin.windows

import com.pumpkin.input.AbstractInput
import com.pumpkin.input.instance
import com.pumpkin.window.Window
import glm_.vec2.Vec2

object WindowsInput : AbstractInput {

    init {
        instance = WindowsInput
    }

    override fun isKeyPressed(keycode: Int): Boolean {
        val state = org.lwjgl.glfw.GLFW.glfwGetKey(Window.getWindow().window.handle.value, keycode)
        return state == org.lwjgl.glfw.GLFW.GLFW_PRESS || state == org.lwjgl.glfw.GLFW.GLFW_REPEAT
    }

    override fun isMouseButtonPressed(button: Int): Boolean {
        val state = org.lwjgl.glfw.GLFW.glfwGetMouseButton(Window.getWindow().window.handle.value, button)
        return state == org.lwjgl.glfw.GLFW.GLFW_PRESS
    }

    override fun getMousePosition(): Vec2 {
        val pos = Window.getWindow().window.cursorPos
        return Vec2(pos[0].toFloat(), pos[1].toFloat())
    }

    override fun getMouseX(): Float {
        return getMousePosition()[0]
    }

    override fun getMouseY(): Float {
        return getMousePosition()[1]
    }
}
package com.pumpkin.input

import com.pumpkin.window.window
import glm_.vec2.Vec2

object InputImpl : AbstractInput {

    init {
        instance = InputImpl
    }

    override fun isKeyPressed(keycode: Int): Boolean {
        val state = org.lwjgl.glfw.GLFW.glfwGetKey(window.window.handle.value, keycode)
        return state == org.lwjgl.glfw.GLFW.GLFW_PRESS || state == org.lwjgl.glfw.GLFW.GLFW_REPEAT
    }

    override fun isMouseButtonPressed(button: Int): Boolean {
        val state = org.lwjgl.glfw.GLFW.glfwGetMouseButton(window.window.handle.value, button)
        return state == org.lwjgl.glfw.GLFW.GLFW_PRESS
    }

    override fun getMousePosition(): Vec2 {
        val pos = window.window.cursorPos
        return Vec2(pos[0].toFloat(), pos[1].toFloat())
    }

    override fun getMouseX(): Float {
        return getMousePosition()[0]
    }

    override fun getMouseY(): Float {
        return getMousePosition()[1]
    }
}
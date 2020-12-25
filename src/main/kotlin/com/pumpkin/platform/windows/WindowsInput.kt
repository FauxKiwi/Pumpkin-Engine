package com.pumpkin.platform.windows

import com.pumpkin.core.stack
import com.pumpkin.core.window.Window
import glm_.vec2.Vec2
import org.lwjgl.glfw.GLFW.*

object WindowsInput {

    fun isKeyPressed(keycode: Int): Boolean {
        val state = glfwGetKey(Window.getWindow().window, keycode)
        return state == GLFW_PRESS || state == GLFW_REPEAT
    }

    fun isMouseButtonPressed(button: Int): Boolean {
        val state = glfwGetMouseButton(Window.getWindow().window, button)
        return state == GLFW_PRESS
    }

    fun getMousePosition(): Vec2 = stack { stack ->
        val x = stack.mallocDouble(1)
        val y = stack.mallocDouble(1)
        glfwGetCursorPos(Window.getWindow().window, x, y)
        return Vec2(x.get(0).toFloat(), y.get(0).toFloat())
    } /*{
        val pos = Window.getWindow().window.cursorPos
        return Vec2(pos[0].toFloat(), pos[1].toFloat())
    }*/

    fun getMouseX() = getMousePosition()[0]

    fun getMouseY() = getMousePosition()[1]
}
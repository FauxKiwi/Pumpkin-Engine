package com.pumpkin.platform

import com.pumpkin.core.window.Window
import glm.Vec2
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryStack

actual object PlatformInput {
    actual fun isKeyPressed(keycode: Int): Boolean {
        val state = glfwGetKey(Window.getWindow().getWindow(), keycode)
        return state == GLFW_PRESS || state == GLFW_REPEAT
    }

    actual fun isMouseButtonPressed(button: Int): Boolean {
        val state = glfwGetMouseButton(Window.getWindow().getWindow(), button)
        return state == GLFW_PRESS
    }

    actual fun getMousePosition(): Vec2 = MemoryStack.stackPush().use { stack ->
        val x = stack.mallocDouble(1)
        val y = stack.mallocDouble(1)
        glfwGetCursorPos(Window.getWindow().getWindow(), x, y)
        return Vec2(x.get(0).toFloat(), y.get(0).toFloat())
    }
}
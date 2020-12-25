package com.pumpkin.core.input

import com.pumpkin.platform.windows.WindowsInput
import glm.Vec2

object Input {
    fun isKeyPressed(keycode: Int): Boolean {
        return WindowsInput.isKeyPressed(keycode)
    }

    fun isMouseButtonPressed(button: Int): Boolean {
        return WindowsInput.isMouseButtonPressed(button)
    }

    fun getMousePosition(): Vec2 {
        return WindowsInput.getMousePosition()
    }

    fun getMouseX(): Float {
        return WindowsInput.getMouseX()
    }

    fun getMouseY(): Float {
        return WindowsInput.getMouseY()
    }
}
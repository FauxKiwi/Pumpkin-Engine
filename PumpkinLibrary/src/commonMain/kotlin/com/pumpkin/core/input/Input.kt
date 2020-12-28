package com.pumpkin.core.input

import com.pumpkin.platform.PlatformInput
import com.pumpkin.platform.getMouseX
import com.pumpkin.platform.getMouseY
import glm.Vec2

object Input {
    fun isKeyPressed(keycode: Int): Boolean {
        return PlatformInput.isKeyPressed(keycode)
    }

    fun isMouseButtonPressed(button: Int): Boolean {
        return PlatformInput.isMouseButtonPressed(button)
    }

    fun getMousePosition(): Vec2 {
        return PlatformInput.getMousePosition()
    }

    fun getMouseX(): Float {
        return PlatformInput.getMouseX()
    }

    fun getMouseY(): Float {
        return PlatformInput.getMouseY()
    }
}
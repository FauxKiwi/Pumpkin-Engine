package com.pumpkin.core.input

import com.pumpkin.platform.windows.WindowsInput
import glm_.vec2.Vec2

internal var instance: AbstractInput = WindowsInput

fun isKeyPressed(keycode: Int): Boolean {
    return instance.isKeyPressed(keycode)
}

fun isMouseButtonPressed(button: Int): Boolean {
    return instance.isMouseButtonPressed(button)
}

fun getMousePosition(): Vec2 {
    return instance.getMousePosition()
}

fun getMouseX(): Float {
    return instance.getMouseX()
}

fun getMouseY(): Float {
    return instance.getMouseY()
}

interface AbstractInput {

    fun isKeyPressed(keycode: Int): Boolean

    fun isMouseButtonPressed(button: Int): Boolean

    fun getMousePosition(): Vec2

    fun getMouseX(): Float

    fun getMouseY(): Float
}
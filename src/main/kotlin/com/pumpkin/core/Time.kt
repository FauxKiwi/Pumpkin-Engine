package com.pumpkin.core

import org.lwjgl.glfw.GLFW.glfwGetTime

object Time {
    val current get() = glfwGetTime()

    var speed = 1f
}
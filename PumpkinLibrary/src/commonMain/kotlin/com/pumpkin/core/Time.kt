package com.pumpkin.core

//import org.lwjgl.glfw.GLFW.glfwGetTime

object Time {
    var speed = 1f
}

expect val Time.current: Float // get() = glfwGetTime()
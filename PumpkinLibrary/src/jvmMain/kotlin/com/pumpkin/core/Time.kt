package com.pumpkin.core

import org.lwjgl.glfw.GLFW.glfwGetTime

actual val Time.current: Float get() = glfwGetTime().toFloat()
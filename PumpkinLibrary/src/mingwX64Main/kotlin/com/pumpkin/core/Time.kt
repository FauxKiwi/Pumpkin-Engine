package com.pumpkin.core

import glfw.glfwGetTime

actual val Time.current get() = glfwGetTime().toFloat()
package com.pumpkin.platform

import com.pumpkin.core.Application
import com.pumpkin.core.Debug
import com.pumpkin.core.event.*
import com.pumpkin.core.window.EventCallbackFunction
import com.pumpkin.core.window.Window
import com.pumpkin.core.window.WindowProps
import glm.Vec2
import glm.Vec2d
import glm.Vec2i
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11C.glViewport
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.file.Path

actual class PlatformWindow : Window {
    private lateinit var data: WindowData

    private val widthBuffer = MemoryUtil.memAllocInt(1)
    private val heightBuffer = MemoryUtil.memAllocInt(1)

    override val width: Int get() = widthBuffer.get(0)
    override val height: Int get() = heightBuffer.get(0)
    override var vSync: Boolean = false; set(value) {
        field = value
        glfwSwapInterval(if (value) 1 else 0)
    }

    var windowHandle = 0L
    actual fun getWindow() = windowHandle

    override fun init(windowProps: WindowProps) {
        data = WindowData(windowProps.title, windowProps.width, windowProps.height, true, null)

        glfwInit()
        glfwSetErrorCallback(::errorCallback)

        windowHandle = glfwCreateWindow(data.width, data.height, data.title, 0L, 0L)
        glfwMaximizeWindow(windowHandle)
        glfwMakeContextCurrent(windowHandle)

        MemoryStack.stackPush().use { stack ->
            val xBuffer = stack.mallocInt(1)
            val yBuffer = stack.mallocInt(1)
            val channelsBuffer = stack.mallocInt(1)
            val icon = stbi_load(
                Path.of(ClassLoader.getSystemResource("textures/PumpkinLogo.png").toURI()).toString(),
                xBuffer, yBuffer, channelsBuffer, 0
            )
            if (icon != null) {
                val iconImage = GLFWImage.malloc()
                val iconImageBuffer = GLFWImage.malloc(1)
                iconImage.set(xBuffer.get(0), yBuffer.get(0), icon)
                iconImageBuffer.put(0, iconImage)
                glfwSetWindowIcon(windowHandle, iconImageBuffer)
                iconImage.free()
                iconImageBuffer.free()
            } else Debug.logErrorCore("Could not load icon image")
        }

        Debug.logInfoCore("Created Window \"${data.title}\" (${data.width} x ${data.height})")

        vSync = data.vSync

        GL.createCapabilities()

        glfwSetWindowSizeCallback(windowHandle) { _, width, height -> windowSizeCallback(Vec2i(width, height)) }
        glfwSetWindowCloseCallback(windowHandle) { windowCloseCallback() }

        glfwSetKeyCallback(windowHandle) { _, key, _, action, _ -> keyCallback(key, action) }

        glfwSetMouseButtonCallback(windowHandle) { _, button, action, _ -> mouseButtonCallback(button, action) }
        glfwSetScrollCallback(windowHandle) { _, offsetX, offsetY -> scrollCallback(Vec2d(offsetX, offsetY)) }
        glfwSetCursorPosCallback(windowHandle) {_, x, y -> cursorPosCallback(Vec2(x.toFloat(), y.toFloat())) }

        Debug.logInfoCore("Installed Callbacks for Window \"${data.title}\"")
    }

    override fun run() = Application.get().runI().also { Application.get().shutdownI() }

    override fun onUpdate() {
        glfwPollEvents()
        glfwSwapBuffers(windowHandle)
        glfwGetFramebufferSize(windowHandle, widthBuffer, heightBuffer)
        glViewport(0, 0, width, height)
    }

    override fun shutdown() {
        glfwTerminate()
        MemoryUtil.memFree(widthBuffer)
        MemoryUtil.memFree(heightBuffer)
    }

    override fun setEventCallback(callback: EventCallbackFunction) {
        data.eventCallback = callback
    }

    private fun errorCallback(error: Int, message: Long) = Debug.logErrorCore("GLFW error ($error): ${MemoryUtil.memUTF8(message)}")

    private fun windowSizeCallback(size: Vec2i) {
        data.width = size[0]
        data.height = size[1]

        val event = WindowResizeEvent(data.width, data.height)
        data.eventCallback?.let { it(event) }
    }

    private fun windowCloseCallback() {
        val event = WindowCloseEvent()
        data.eventCallback?.let { it(event) }
    }

    private fun keyCallback(key: Int, action: Int) {
        val event = when (action) {
            GLFW_PRESS -> KeyPressedEvent(key, 0)
            GLFW_RELEASE -> KeyReleasedEvent(key)
            GLFW_REPEAT -> KeyPressedEvent(key, 1)
            else -> null
        }
        data.eventCallback?.let {
            if (event != null) {
                it(event)
            }
        }
    }

    private fun mouseButtonCallback(button: Int, action: Int) {
        val event = when (action) {
            GLFW_PRESS -> MouseButtonPressedEvent(button)
            GLFW_RELEASE -> MouseButtonReleasedEvent(button)
            else -> null
        }
        data.eventCallback?.let {
            if (event != null) {
                it(event)
            }
        }
    }

    private fun scrollCallback(offset: Vec2d) {
        val event = MouseScrolledEvent(offset[0].toFloat(), offset[1].toFloat())
        data.eventCallback?.let { it(event) }
    }

    private fun cursorPosCallback(pos: Vec2) {
        val event = MouseMoveEvent(pos[0], pos[1])
        data.eventCallback?.let { it(event) }
    }
}
package com.pumpkin.platform.windows

import com.pumpkin.core.*
import com.pumpkin.core.event.*
import com.pumpkin.core.render.GraphicsContext
import com.pumpkin.platform.opengl.OpenGLContext
import com.pumpkin.core.window.EventCallbackFunction
import com.pumpkin.core.window.Window
import com.pumpkin.core.window.WindowProps
import gli_.gli
import glm_.vec2.Vec2
import glm_.vec2.Vec2d
import glm_.vec2.Vec2i
import gln.glViewport
import imgui.DEBUG
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.opengl.GL
import uno.glfw.GlfwWindow
import uno.glfw.VSync
import uno.glfw.glfw

class WindowsWindow : Window {
    private lateinit var data: WindowData

    lateinit var window: GlfwWindow
    private lateinit var context: GraphicsContext

    override val width: Int
        get() = window.size[0]

    override val height: Int
        get() = window.size[1]

    override var vSync: Boolean
        get() = glfw.swapInterval == VSync.ON
        set(value) {
            glfw.swapInterval = if (value) VSync.ON else VSync.OFF
        }

    override fun init(windowProps: WindowProps) {
        stack {
            data = WindowData(windowProps.title, windowProps.width, windowProps.height, true, null)

            glfw.init()
            glfw.errorCallback = this::errorCallback

            window = GlfwWindow(data.width, data.height, data.title)
            context = OpenGLContext(window)
            context.init()

            val icon = gli.load("./src/test/resources/textures/PumpkinLogo.png")
            val iconImage = GLFWImage.malloc();
            val iconImageBuffer = GLFWImage.malloc(1)
            iconImage.set(icon.extent()[0], icon.extent()[1], icon.data())
            iconImageBuffer.put(0, iconImage)
            window.setIcon(iconImageBuffer)

            Debug.logInfoCore("Created Window \"${data.title}\" (${data.width} x ${data.height})")

            vSync = data.vSync

            GL.createCapabilities()

            window.windowSizeCB = this::windowSizeCallback
            window.windowCloseCB = this::windowCloseCallback

            window.keyCB = { key, _, action, _ ->
                keyCallback(key, action)
            }

            window.mouseButtonCB = { button, action, _ ->
                mouseButtonCallback(button, action)
            }
            window.scrollCB = this::scrollCallback
            window.cursorPosCB = this::cursorPosCallback

            Debug.logInfoCore("Installed Callbacks for Window \"${data.title}\"")

            DEBUG = false
        }
    }

    override fun run() = Application.get().runI().also { Application.get().shutdownI() }

    override fun onUpdate() {
        glfw.pollEvents()
        context.swapBuffers()
        glViewport(window.framebufferSize)
    }

    override fun shutdown() = glfw.terminate()

    override fun setEventCallback(callback: EventCallbackFunction) {
        data.eventCallback = callback
    }

    private fun errorCallback(error: glfw.Error, message: String) = Debug.logErrorCore("GLFW error (${error.name}): $message")

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
            GLFW.GLFW_PRESS -> KeyPressedEvent(key, 0)
            GLFW.GLFW_RELEASE -> KeyReleasedEvent(key)
            GLFW.GLFW_REPEAT -> KeyPressedEvent(key, 1)
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
            GLFW.GLFW_PRESS -> MouseButtonPressedEvent(button)
            GLFW.GLFW_RELEASE -> MouseButtonReleasedEvent(button)
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


data class WindowData(
    var title: String,
    var width: Int,
    var height: Int,
    var vSync: Boolean,
    var eventCallback: EventCallbackFunction?
)
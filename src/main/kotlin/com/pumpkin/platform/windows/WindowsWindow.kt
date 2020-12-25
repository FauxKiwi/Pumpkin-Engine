package com.pumpkin.platform.windows

import com.pumpkin.core.Application
import com.pumpkin.core.Debug
import com.pumpkin.core.event.*
import com.pumpkin.core.renderer.GraphicsContext
import com.pumpkin.core.stack
import com.pumpkin.core.window.EventCallbackFunction
import com.pumpkin.core.window.Window
import com.pumpkin.core.window.WindowProps
import glm_.vec2.Vec2
import glm_.vec2.Vec2d
import glm_.vec2.Vec2i
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11C.glViewport
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.system.MemoryUtil
import java.nio.file.Path

class WindowsWindow : Window {
    private lateinit var data: WindowData

    /*lateinit*/ var window: Long = 0L//GlfwWindow
    private lateinit var context: GraphicsContext

    val widthBuffer = MemoryUtil.memAllocInt(1)
    val heightBuffer = MemoryUtil.memAllocInt(1)

    override val width: Int
        get() = widthBuffer.get(0) //window.size[0]

    override val height: Int
        get() = heightBuffer.get(0) //window.size[1]

    override var vSync: Boolean = false
        //get() = glfw.swapInterval == VSync.ON
        set(value) {
            field = value
            glfwSwapInterval(if (value) 1 else 0) //glfw.swapInterval = if (value) VSync.ON else VSync.OFF
        }

    override fun init(windowProps: WindowProps) {
        data = WindowData(windowProps.title, windowProps.width, windowProps.height, true, null)

        glfwInit() //glfw.init()
        glfwSetErrorCallback(::errorCallback) //glfw.errorCallback = this::errorCallback

        window = glfwCreateWindow(data.width, data.height, data.title, 0L, 0L) //GlfwWindow(data.width, data.height, data.title)
        glfwMaximizeWindow(window) //window.maximize()
        /*context = OpenGLContext(window)
        context.init()*/
        glfwMakeContextCurrent(window)

        stack { stack ->
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
                glfwSetWindowIcon(window, iconImageBuffer) //window.setIcon(iconImageBuffer)
                iconImage.free()
                iconImageBuffer.free()
            } else Debug.logErrorCore("Could not load icon image")
        }
        /*val icon = gli.load("./src/test/resources/textures/PumpkinLogo.png")
        val iconImage = GLFWImage.malloc()
        val iconImageBuffer = GLFWImage.malloc(1)
        iconImage.set(icon.extent()[0], icon.extent()[1], icon.data())
        iconImageBuffer.put(0, iconImage)
        glfwSetWindowIcon(window, iconImageBuffer) //window.setIcon(iconImageBuffer)
        iconImage.free()
        iconImageBuffer.free()*/

        Debug.logInfoCore("Created Window \"${data.title}\" (${data.width} x ${data.height})")

        vSync = data.vSync

        GL.createCapabilities()

        glfwSetWindowSizeCallback(window) { _, width, height -> windowSizeCallback(Vec2i(width, height)) } //window.windowSizeCB = this::windowSizeCallback
        glfwSetWindowCloseCallback(window) { windowCloseCallback() } //window.windowCloseCB = this::windowCloseCallback

        glfwSetKeyCallback(window) /*window.keyCB =*/ { _, key, _, action, _ -> keyCallback(key, action) }

        glfwSetMouseButtonCallback(window) /*window.mouseButtonCB =*/ { _, button, action, _ -> mouseButtonCallback(button, action) }
        glfwSetScrollCallback(window) { _, offsetX, offsetY -> scrollCallback(Vec2d(offsetX, offsetY)) } //window.scrollCB = this::scrollCallback
        glfwSetCursorPosCallback(window) {_, x, y -> cursorPosCallback(Vec2(x.toFloat(), y.toFloat())) } //window.cursorPosCB = this::cursorPosCallback

        Debug.logInfoCore("Installed Callbacks for Window \"${data.title}\"")

        imgui.DEBUG = false
    }

    override fun run() = Application.get().runI().also { Application.get().shutdownI() }

    override fun onUpdate() {
        glfwPollEvents() //glfw.pollEvents()
        glfwSwapBuffers(window) //context.swapBuffers()
        //glViewport(window.framebufferSize)
        glfwGetFramebufferSize(window, widthBuffer, heightBuffer)
        glViewport(0, 0, width, height)
    }

    override fun shutdown() {
        glfwTerminate()
        MemoryUtil.memFree(widthBuffer)
        MemoryUtil.memFree(heightBuffer)
        //glfw.terminate()
    }

    override fun setEventCallback(callback: EventCallbackFunction) {
        data.eventCallback = callback
    }

    private fun errorCallback(error: /*glfw.Error*/ Int, message: /*String*/ Long) = Debug.logErrorCore("GLFW error ($error): ${MemoryUtil.memUTF8(message)}" /*"GLFW error (${error.name}): $message"*/)

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


data class WindowData(
    var title: String,
    var width: Int,
    var height: Int,
    var vSync: Boolean,
    var eventCallback: EventCallbackFunction?
)
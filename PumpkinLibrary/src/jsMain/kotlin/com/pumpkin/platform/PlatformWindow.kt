package com.pumpkin.platform

import com.pumpkin.core.*
import com.pumpkin.core.window.EventCallbackFunction
import com.pumpkin.core.window.Window
import com.pumpkin.core.window.WindowProps
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement
import org.khronos.webgl.WebGLRenderingContext as GL

actual class PlatformWindow : Window {
    private lateinit var data: WindowData

    private val canvas: HTMLCanvasElement = document.getElementById("webglCanvas") as HTMLCanvasElement
    val webgl: GL = canvas.getContext("webgl") as GL

    actual fun getWindow(): Long = 0L

    override val width: Int = 1280
    override val height: Int = 720
    override var vSync: Boolean = false

    override fun init(windowProps: WindowProps) {
        data.width = windowProps.width
        data.height = windowProps.height
        data.title = windowProps.title
        data.vSync = false

        Debug.logInfoCore("Created WebGL canvas window")

        Debug.logWarnCore("No input for WebGL atm") //TODO

        window.requestAnimationFrame { run() }
    }

    override fun run() = Application.get().runI().also { Application.get().shutdownI() }

    override fun onUpdate() { window.requestAnimationFrame {
        frameTime = it
    }}

    override fun shutdown() {
        //
    }

    override fun setEventCallback(callback: EventCallbackFunction) {
        data.eventCallback = callback
    }
}

val webglWindow = PlatformWindow()

fun main() {
    document.body?.onload = {
        window.requestAnimationFrame { webglWindow.init(WindowProps()) }
    }
}
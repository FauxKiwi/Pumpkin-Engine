package com.pumpkin.core.window

import com.pumpkin.core.event.Event
import com.pumpkin.platform.PlatformWindow

interface Window {
    companion object {
        private lateinit var window: PlatformWindow

        fun createWindow(): PlatformWindow {
            window = PlatformWindow()
            return window
        }

        fun getWindow() = window
    }

    val width: Int
    val height: Int
    var vSync: Boolean

    fun init(windowProps: WindowProps)
    fun run()
    fun onUpdate()
    fun shutdown()

    fun setEventCallback(callback: EventCallbackFunction)
}

typealias EventCallbackFunction = (Event) -> Unit

data class WindowProps(var title: String = "Pumpkin Game Engine", var width: Int = 1280, var height: Int = 720)
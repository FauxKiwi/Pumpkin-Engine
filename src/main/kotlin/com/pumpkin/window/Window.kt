package com.pumpkin.window

import com.pumpkin.event.Event
import com.pumpkin.windows.WindowsWindow

interface Window {

    companion object {
        private lateinit var window: WindowsWindow

        fun createWindow(): WindowsWindow {
            window = WindowsWindow()
            return window
        }

        fun getWindow() = window
    }

    fun init(windowProps: WindowProps)
    fun run()
    fun onUpdate()
    fun shutdown()

    fun getWidth(): Int
    fun getHeight(): Int

    fun setEventCallback(callback: EventCallbackFunction)

    fun setVSync(vSync: Boolean)
    fun isVSync(): Boolean
}

typealias EventCallbackFunction = (Event) -> Unit

data class WindowProps(var title: String = "Pumpkin Game Engine", var width: Int = 1280, var height: Int = 720)
package com.pumpkin.window

import com.pumpkin.event.Event
import org.lwjgl.system.MemoryStack

internal lateinit var window: Window

fun createWindow(): Window {
    window = Window()
    return window
}

interface IWindow {

    fun init(windowProps: WindowProps)
    fun run()
    fun onUpdate(stack: MemoryStack)
    fun shutdown()

    fun getWidth(): Int
    fun getHeight(): Int

    fun setEventCallback(callback: EventCallbackFunction)

    fun setVSync(vSync: Boolean)
    fun isVSync(): Boolean
}

typealias EventCallbackFunction = (Event) -> Unit

data class WindowProps(var title: String = "Pumpkin Game Engine", var width: Int = 1280, var height: Int = 720)
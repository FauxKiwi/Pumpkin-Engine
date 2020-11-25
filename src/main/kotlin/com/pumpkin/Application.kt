package com.pumpkin

import com.pumpkin.event.Event
import com.pumpkin.event.EventDispatcher
import com.pumpkin.event.EventType
import com.pumpkin.event.WindowCloseEvent
import com.pumpkin.imgui.ImGuiLayer
import com.pumpkin.layer.Layer
import com.pumpkin.layer.LayerStack
import com.pumpkin.window.Window
import com.pumpkin.window.WindowProps
import com.pumpkin.window.createWindow
import org.lwjgl.system.MemoryStack

open class Application {
    private var running: Boolean = false
    private lateinit var window: Window
    private lateinit var layerStack: LayerStack
    private lateinit var imGuiLayer: ImGuiLayer

    internal fun initI() {
        running = true
        layerStack = LayerStack()
        imGuiLayer = ImGuiLayer()
        window = createWindow()

        window.init(WindowProps())

        window.setEventCallback(::onEvent)

        logInfoCore("Initialized Program")

        pushOverlay(imGuiLayer)
        init()

        window.run()
    }

    open fun init() = Unit

    fun pushLayer(layer: Layer) {
        layerStack.pushLayer(layer)
    }

    fun pushOverlay(layer: Layer) {
        layerStack.pushOverlay(layer)
    }

    private fun onEvent(event: Event) {
        val dispatcher = EventDispatcher(event)
        dispatcher.dispatch(EventType.WindowClose) {
            running = false
            return@dispatch true
        }
        if (event.handled) {
            return
        }
        for (layer in layerStack.layersReversed) {
            layer.onEvent(event)
            if (event.handled) {
                break
            }
        }
    }

    internal fun runI(stack: MemoryStack) {
        window.onUpdate()
        for (layer in layerStack.layers) {
            layer.onUpdate()
        }

        imGuiLayer.begin()
        for (layer in layerStack.layers) {
            layer.onImGuiRender()
        }
        imGuiLayer.end()

        run()
    }

    open fun run() = Unit

    internal fun shutdownI() {
        val layers = layerStack.getInsert()
        for ((i, layer) in layerStack.layers.withIndex()) {
            if (i < layers) {
                layerStack.popLayer(layer)
            } else {
                layerStack.popOverlay(layer)
            }
        }
        window.shutdown()
        shutdown()
    }

    open fun shutdown() = Unit
}

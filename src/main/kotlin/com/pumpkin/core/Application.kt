package com.pumpkin.core

import com.pumpkin.core.event.Event
import com.pumpkin.core.event.EventDispatcher
import com.pumpkin.core.event.WindowCloseEvent
import com.pumpkin.core.imgui.ImGuiLayer
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.layer.LayerStack
import com.pumpkin.core.render.OrthographicCamera
import com.pumpkin.core.window.Window
import com.pumpkin.core.window.WindowProps

open class Application {
    companion object {
        private lateinit var application: Application

        infix fun set(application: Application) {
            this.application = application
            main()
        }

        fun get(): Application = application

        private fun main() {
            application.initI()
        }
    }

    private var running: Boolean = false
    private lateinit var window: Window
    private lateinit var layerStack: LayerStack
    private lateinit var imGuiLayer: ImGuiLayer

    protected val camera = OrthographicCamera(-1.6f, 1.6f, -0.9f, 0.9f)

    internal fun initI() {
        running = true
        layerStack = LayerStack()
        imGuiLayer = ImGuiLayer()
        window = Window.createWindow()

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
        dispatcher.dispatch<WindowCloseEvent> {
            running = false
            true
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

    internal fun runI() {
        while (running) {
            run()

            for (layer in layerStack.layers) {
                layer.onUpdate()
            }

            imGuiLayer.begin()
            for (layer in layerStack.layers) {
                layer.onImGuiRender()
            }
            imGuiLayer.end()

            window.onUpdate()
        }
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

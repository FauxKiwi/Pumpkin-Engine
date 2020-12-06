package com.pumpkin.core

import com.pumpkin.core.event.Event
import com.pumpkin.core.event.EventDispatcher
import com.pumpkin.core.event.WindowCloseEvent
import com.pumpkin.core.event.WindowResizeEvent
import com.pumpkin.core.imgui.ImGuiLayer
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.layer.LayerStack
import com.pumpkin.core.render.OrthographicCamera
import com.pumpkin.core.render.Renderer
import com.pumpkin.core.render.Renderer2D
import com.pumpkin.core.window.Window
import com.pumpkin.core.window.WindowProps
import org.lwjgl.system.MemoryStack
import uno.glfw.glfw
import uno.glfw.stak

open class Application {
    companion object {
        private lateinit var application: Application

        fun set(application: Application) {
            this.application = application
            main()
        }

        fun get(): Application = application

        private fun main() {
            application.initI()
        }
    }

    private var running = false
    private var lastFrameTime = 0f

    private lateinit var window: Window
    private var minimized = false
    private lateinit var layerStack: LayerStack
    private lateinit var imGuiLayer: ImGuiLayer

    internal fun initI() {
        running = true
        layerStack = LayerStack()
        imGuiLayer = ImGuiLayer()
        window = Window.createWindow()

        window.init(WindowProps())

        window.setEventCallback(::onEvent)

        logInfoCore("Initialized Program")

        Renderer.init()

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
        dispatcher.dispatch(::onWindowResize)
        for (layer in layerStack.layersReversed) {
            layer.onEvent(event)
            if (event.handled) {
                break
            }
        }
    }

    internal fun runI() {
        while (running) {
            stack {
                val time: Float = glfw.time.toFloat()
                val timestep: Timestep = time - lastFrameTime
                lastFrameTime = time

                run()

                if (!minimized)
                    for (layer in layerStack.layers) {
                        layer.onUpdate(timestep)
                    }

                imGuiLayer.begin()
                for (layer in layerStack.layers) {
                    layer.onImGuiRender()
                }
                imGuiLayer.end()

                window.onUpdate()
            }
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
        Renderer2D.shutdown()
        window.shutdown()
        shutdown()
    }

    open fun shutdown() = Unit

    private fun onWindowResize(event: WindowResizeEvent): Boolean {
        if (event.width == 0 || event.height == 0) {
            minimized = true
            return false
        }
        minimized = false
        Renderer.onWindowResize(event.width, event.height)
        return false
    }
}
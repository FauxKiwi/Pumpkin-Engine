package com.pumpkin.core

import com.pumpkin.core.event.Event
import com.pumpkin.core.event.EventDispatcher
import com.pumpkin.core.event.EventType
import com.pumpkin.core.event.WindowCloseEvent
import com.pumpkin.core.imgui.ImGuiLayer
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.layer.LayerStack
import com.pumpkin.core.render.*
import com.pumpkin.core.window.Window
import com.pumpkin.core.window.WindowProps
import glm_.vec4.Vec4
import gln.*
import gln.identifiers.GlVertexArray

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

    private lateinit var shader: Shader
    private lateinit var vertexArray: VertexArray
    /*private lateinit var blueShader: Shader
    private lateinit var squareVA: VertexArray*/

    internal fun initI() {
        running = true
        layerStack = LayerStack()
        imGuiLayer = ImGuiLayer()
        window = Window.createWindow()

        window.init(WindowProps())

        window.setEventCallback(::onEvent)

        logInfoCore("Initialized Program")

        pushOverlay(imGuiLayer)

        val vertices = floatArrayOf(
            -0.5f, -0.5f, 0f, 1f, 0f, 1f, 1f,
            0.5f, -0.5f, 0f, 0f, 1f, 1f, 1f,
            0.0f, 0.5f, 0f, 1f, 1f, 0f, 1f,
        )

        val indices = intArrayOf(0, 1, 2)

        gl {
            vertexArray = VertexArray.create()

            val vertexBuffer = VertexBuffer.create(vertices)
            val layout = BufferLayout(
                mutableListOf(
                    BufferElement(ShaderDataType.Float3, "a_Position"),
                    BufferElement(ShaderDataType.Float4, "a_Color")
                )
            )
            vertexBuffer.layout = layout
            vertexArray.addVertexBuffer(vertexBuffer)

            val indexBuffer = IndexBuffer.create(indices)
            vertexArray.indexBuffer = indexBuffer

            val vertexSrc = """
                #version 330 core
                
                layout(location = 0) in vec3 a_Position;
                layout(location = 1) in vec4 a_Color;
                
                out vec4 v_Color;
                
                void main() {
                    v_Color = a_Color;
                    gl_Position = vec4(a_Position, 1.0);
                }
            """.trimIndent()

            val fragmentSrc = """
                #version 330 core
               
                in vec4 v_Color;
                
                out vec4 color;
                
                void main() {
                    color = v_Color;
                }
            """.trimIndent()

            shader = Shader.create(vertexSrc, fragmentSrc)
        }

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
            glClearColor(Vec4(0.1f, 0.1f, 0.1f, 1.0f))
            gl.clear(ClearBufferMask.COLOR_BUFFER_BIT)

            shader.bind()

            vertexArray.bind()
            gl.drawElements(DrawMode.TRIANGLES, 3)

            for (layer in layerStack.layers) {
                layer.onUpdate()
            }

            imGuiLayer.begin()
            for (layer in layerStack.layers) {
                layer.onImGuiRender()
            }
            imGuiLayer.end()

            run()

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

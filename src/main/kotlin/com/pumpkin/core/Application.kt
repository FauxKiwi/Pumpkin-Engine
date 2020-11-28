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

    private var vertexArray = GlVertexArray()
    private lateinit var vertexBuffer: VertexBuffer /*= GlBuffer()*/
    private lateinit var indexBuffer: IndexBuffer /*= GlBuffer()*/
    private lateinit var shader: Shader

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
            -0.5f, -0.5f, 0f,   1f, 0f, 1f, 1f,
            0.5f, -0.5f, 0f,    0f, 1f, 1f, 1f,
            0.0f, 0.5f, 0f,     1f, 1f, 0f, 1f,
        )

        val indices = intArrayOf(0, 1, 2)

        gl {
            genVertexArrays(::vertexArray)
            bindVertexArray(vertexArray)

            //genBuffers(::vertexBuffer)
            //bindBuffer(BufferTarget.ARRAY, vertexBuffer)
            vertexBuffer = VertexBuffer.create(vertices)

            //GL15C.glBufferData(GL15C.GL_ARRAY_BUFFER, vertices, GL15C.GL_STATIC_DRAW)
            //enableVertexAttribArray(0)
            //vertexAttribPointer(0, 3, VertexAttrType.FLOAT, false, 0, 0)
            val layout = BufferLayout(mutableListOf(
                BufferElement(ShaderDataType.Float3, "a_Position"),
                BufferElement(ShaderDataType.Float4, "a_Color")
            ))
            vertexBuffer.setLayout(layout)

            val layout2 = vertexBuffer.getLayout()
            for ((i, element) in layout2.withIndex()) {
                enableVertexAttribArray(i)
                vertexAttribPointer(i, element.dataType.componentCount(), element.dataType.toVertexAttrType(), element.normalized, layout2.getStride(), element.offset)
            }

            //genBuffers(::indexBuffer)
            //bindBuffer(BufferTarget.ELEMENT_ARRAY, indexBuffer)
            indexBuffer = IndexBuffer.create(indices)

            //GL15C.glBufferData(GL15C.GL_ELEMENT_ARRAY_BUFFER, indices, GL15C.GL_STATIC_DRAW)

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

    internal fun runI() {
        while (running) {
            glClearColor(Vec4(0.1f, 0.1f, 0.1f, 1.0f))
            gl.clear(ClearBufferMask.COLOR_BUFFER_BIT)

            shader.bind()

            gl.bindVertexArray(vertexArray)
            gl.drawElements(DrawMode.TRIANGLES, 3)
            //gl.drawArrays(DrawMode.TRIANGLES, 0, 3)
            //GL15.glDrawElements(GL15C.GL_TRIANGLES, 3, GL15C.GL_UNSIGNED_INT, 0)

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

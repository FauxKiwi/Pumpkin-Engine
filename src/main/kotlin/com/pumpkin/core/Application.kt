package com.pumpkin.core

import com.pumpkin.core.event.Event
import com.pumpkin.core.event.EventDispatcher
import com.pumpkin.core.event.EventType
import com.pumpkin.core.imgui.ImGuiLayer
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.layer.LayerStack
import com.pumpkin.core.render.IndexBuffer
import com.pumpkin.core.render.Shader
import com.pumpkin.core.render.VertexBuffer
import com.pumpkin.core.window.Window
import com.pumpkin.core.window.WindowProps
import glm_.vec4.Vec4
import gln.*
import gln.identifiers.GlVertexArray

open class Application {
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
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f,
            0.0f, 0.5f, 0.0f
        )

        val indices = intArrayOf(0, 1, 2)

        gl {
            genVertexArrays(::vertexArray)
            bindVertexArray(vertexArray)

            //genBuffers(::vertexBuffer)
            //bindBuffer(BufferTarget.ARRAY, vertexBuffer)
            vertexBuffer = VertexBuffer.create(vertices)

            //GL15C.glBufferData(GL15C.GL_ARRAY_BUFFER, vertices, GL15C.GL_STATIC_DRAW)
            enableVertexAttribArray(0)
            vertexAttribPointer(0, 3, VertexAttrType.FLOAT, false, 0, 0)

            //genBuffers(::indexBuffer)
            //bindBuffer(BufferTarget.ELEMENT_ARRAY, indexBuffer)
            indexBuffer = IndexBuffer.create(indices)

            //GL15C.glBufferData(GL15C.GL_ELEMENT_ARRAY_BUFFER, indices, GL15C.GL_STATIC_DRAW)

            val vertexSrc = """
                #version 330 core
                
                layout(location = 0) in vec3 position;
                
                out vec3 positionOut;
                
                void main() {
                    positionOut = position * 0.5 + 0.5;
                    
                    gl_Position = vec4(position, 1.0);
                }
            """.trimIndent()

            val fragmentSrc = """
                #version 330 core
               
                in vec3 positionOut;
                
                out vec4 color;
                
                void main() {
                    color = vec4(positionOut, 1.0);
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

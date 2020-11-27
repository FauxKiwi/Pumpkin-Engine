package com.pumpkin

import com.pumpkin.event.Event
import com.pumpkin.event.EventDispatcher
import com.pumpkin.event.EventType
import com.pumpkin.imgui.ImGuiLayer
import com.pumpkin.layer.Layer
import com.pumpkin.layer.LayerStack
import com.pumpkin.render.Shader
import com.pumpkin.window.Window
import com.pumpkin.window.WindowProps
import com.pumpkin.window.createWindow
import glm_.vec4.Vec4
import gln.*
import gln.identifiers.GlBuffer
import gln.identifiers.GlVertexArray
import org.lwjgl.opengl.GL15C

open class Application {
    private var running: Boolean = false
    private lateinit var window: Window
    private lateinit var layerStack: LayerStack
    private lateinit var imGuiLayer: ImGuiLayer

    private var vertexArray = GlVertexArray()
    private var vertexBuffer = GlBuffer()
    private var indexBuffer = GlBuffer()
    private lateinit var shader: Shader

    internal fun initI() {
        running = true
        layerStack = LayerStack()
        imGuiLayer = ImGuiLayer()
        window = createWindow()

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

            genBuffers(::vertexBuffer)
            bindBuffer(BufferTarget.ARRAY, vertexBuffer)

            GL15C.glBufferData(GL15C.GL_ARRAY_BUFFER, vertices, GL15C.GL_STATIC_DRAW)
            enableVertexAttribArray(0)
            vertexAttribPointer(0, 3, VertexAttrType.FLOAT, false, 0, 0)

            genBuffers(::indexBuffer)
            bindBuffer(BufferTarget.ELEMENT_ARRAY, indexBuffer)

            GL15C.glBufferData(GL15C.GL_ELEMENT_ARRAY_BUFFER, indices, GL15C.GL_STATIC_DRAW)

            val vertexSrc = """
                #version 330 core
                
                layout(location = 0) in vec3 position;
                
                void main() {
                    gl_Position = vec4(position, 1.0);
                }
            """.trimIndent()

            val fragmentSrc = """
               #version 330 core
                
               out vec4 color;
                
                void main() {
                    color = vec4(1.0, 0.0, 0.0, 1.0);
                }
            """.trimIndent()

            shader = Shader(vertexSrc, fragmentSrc)
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

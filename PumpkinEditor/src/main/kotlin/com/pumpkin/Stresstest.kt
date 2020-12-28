package com.pumpkin

import com.pumpkin.core.Application
import com.pumpkin.core.OrthographicCameraController
import com.pumpkin.core.Timestep
import com.pumpkin.core.event.Event
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.renderer.Renderer2D
import com.pumpkin.core.renderer.RendererCommand
import com.pumpkin.editor.imgui.ImGuiLayer
import com.pumpkin.editor.imgui.ImGuiProfiler
import glm.Vec2
import glm.Vec4
import imgui.ImGui
import imgui.type.ImInt

class StresstestLayer : Layer("Stresstest") {
    private val cameraController = OrthographicCameraController(16f / 9f, false)

    private var sqrt = 10

    override fun onAttach() {
        ImGuiProfiler.onAttach()
    }

    override fun onDetach() {
        ImGuiProfiler.onDetach()
    }

    override fun onUpdate(ts: Timestep) {
        ImGuiProfiler.onUpdate(ts)
        cameraController.onUpdate(ts)

        Renderer2D.beginScene(cameraController.camera)

        for (x in 0 until sqrt) for (y in 0 until sqrt) {
            Renderer2D.drawQuad(Vec2(x * 0.1f, y * 0.1f), Vec2(0.09f), color = Vec4(0.8f))
        }

        Renderer2D.endScene()

        ImGuiLayer.begin()
        onImGuiRender()
        ImGuiLayer.end()
    }

    override fun onImGuiRender() {
        ImGuiProfiler.onImGuiRender()
        ImGui.begin("Stresstest")
        val number = intArrayOf(sqrt)
        ImGui.dragInt("Number", number, 1f, 1f, 1000f)
        sqrt = number[0]
        //ImGui.inputInt("Capacity", Renderer2D::maxQuads)
        ImGui.end()
    }

    override fun onEvent(event: Event) {
        cameraController.onEvent(event)
    }
}

class StresstestApp : Application() {

    override fun init() {
        pushOverlay(ImGuiLayer)
        pushLayer(StresstestLayer())
    }

    override fun run() {
        RendererCommand.setClearColor(Vec4(0.1f, 0.1f, 0.1f, 1.0f))
        RendererCommand.clear()
    }
}

val ImGuiLayer = ImGuiLayer()

fun main() {
    Application.set(StresstestApp())
}
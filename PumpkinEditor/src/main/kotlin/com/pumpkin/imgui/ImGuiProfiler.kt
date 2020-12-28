package com.pumpkin.imgui

import com.pumpkin.core.Timestep
import com.pumpkin.core.renderer.Renderer2D
import com.pumpkin.core.window.Window
import glm_.vec2.Vec2
import imgui.ImGui
import imgui.WindowFlag
import imgui.max

object ImGuiProfiler {
    var showProfiler = false

    private var values0 = FloatArray(100)
    private var valuesOffset = 0

    fun onAttach() {
        showProfiler = true
    }

    fun onDetach() {
        showProfiler = false
    }

    fun onUpdate(ts: Timestep) {
        values0[valuesOffset] = if (ImGui.io.framerate == 0f) values0[max(valuesOffset-1, 0)] else 1000 / ImGui.io.framerate
        valuesOffset++
        if (valuesOffset >= 100) valuesOffset = 0
    }

    fun onImGuiRender() = with(ImGui) {
        ImGui.begin("Profiler", ::showProfiler, WindowFlag.NoCollapse.i)
        ImGui.text("Your Framerate is: ${ImGui.io.framerate}")
        ImGui.text("Update time: ${1000 / ImGui.io.framerate}")
        val overlay = "min ${String.format("%.3f", values0.min())}   max ${String.format("%.3f", values0.max())}"
        ImGui.plotLines(
            "Frametime",
            values0,
            valuesOffset/*, scaleMin = 0f, scaleMax = 60f*/,
            overlayText = overlay,
            graphSize = Vec2(0f, 80f)
        )
        ImGui.checkbox("VSync", Window.getWindow()::vSync)
        ImGui.text("Drawing ${Renderer2D.quadCount} quads (${Renderer2D.quadCount * 4} vertices, ${Renderer2D.quadCount * 6} indices)")
        ImGui.text("Draw calls: ${Renderer2D.drawCalls}")
        ImGui.end()
    }
}
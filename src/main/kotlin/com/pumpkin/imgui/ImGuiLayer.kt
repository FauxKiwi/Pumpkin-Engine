package com.pumpkin.imgui

import com.pumpkin.layer.Layer
import com.pumpkin.window.window
import imgui.ConfigFlag
import imgui.ImGui
import imgui.classes.Context
import imgui.impl.gl.ImplGL3
import imgui.impl.glfw.ImplGlfw

class ImGuiLayer : Layer("ImGui") {
    private var showDemoWindow = true

    private lateinit var context: Context
    private lateinit var implGL3: ImplGL3
    private lateinit var implGlfw: ImplGlfw

    override fun onAttach() {
        context = Context()
        implGL3 = ImplGL3()
        implGlfw = ImplGlfw(window.window)

        ImGui.io.configFlags = ImGui.io.configFlags or ConfigFlag.NavEnableKeyboard.i or ConfigFlag.NavEnableGamepad.i

        ImGui.styleColorsDark()
    }

    override fun onDetach() {
        implGlfw.shutdown()
        implGL3.shutdown()
        context.destroy()
    }

    override fun onImGuiRender() {
        if (showDemoWindow) {
            ImGui.showDemoWindow(::showDemoWindow)
        }
        ImGui.begin("Test")
        ImGui.text("Hello world!")
        ImGui.end()
    }

    fun begin() {
        implGL3.newFrame()
        implGlfw.newFrame()
        ImGui.newFrame()
    }

    fun end() {
        ImGui.render()

        ImGui.drawData?.let { implGL3.renderDrawData(it) }
    }
}
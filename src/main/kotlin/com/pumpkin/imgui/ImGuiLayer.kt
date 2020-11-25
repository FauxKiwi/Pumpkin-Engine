package com.pumpkin.imgui

import com.pumpkin.event.Event
import com.pumpkin.layer.Layer
import com.pumpkin.window.window
import imgui.ImGui

class ImGuiLayer : Layer("ImGui") {
    private val ia = intArrayOf((window.clearColor[0] * 255).toInt(), (window.clearColor[1] * 255).toInt(), (window.clearColor[2] * 255).toInt())

    private var showDemoWindow = true

    override fun onAttach() {
        ImGui.styleColorsDark()
    }

    override fun onDetach() {

    }

    override fun onUpdate() {
        ImGui.run {
            newFrame()

            if (showDemoWindow)
                showDemoWindow(::showDemoWindow)


            begin("Test Window")
            checkbox("Demo window", ::showDemoWindow)
            dragInt3("BGColor", ia, 1f, 0, 255)
            window.clearColor[0] = ia[0] / 255f
            window.clearColor[1] = ia[1] / 255f
            window.clearColor[2] = ia[2] / 255f
            end()
        }

        ImGui.render()

        window.implGl3.renderDrawData(ImGui.drawData!!)
    }

    override fun onEvent(event: Event) {

    }
}
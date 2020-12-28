package com.pumpkin

import com.pumpkin.core.Application
import com.pumpkin.imgui.ImGuiLayer
import com.pumpkin.settings.SettingsSerializer

internal lateinit var imGuiLayer: ImGuiLayer

class EditorApp : Application() {

    override fun init() {
        imGuiLayer = ImGuiLayer()
        pushOverlay(imGuiLayer)

        if (!SettingsSerializer.load()) SettingsSerializer.save()
        pushLayer(EditorLayer())
    }
}

fun main() {
    Application.set(EditorApp())
}
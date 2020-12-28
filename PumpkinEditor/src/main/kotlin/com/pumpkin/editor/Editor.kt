package com.pumpkin.editor

import com.pumpkin.core.Application
import com.pumpkin.editor.imgui.ImGuiLayer
import com.pumpkin.editor.settings.SettingsSerializer

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
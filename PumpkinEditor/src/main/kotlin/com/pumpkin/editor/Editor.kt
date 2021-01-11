package com.pumpkin.editor

import com.pumpkin.core.Application
import com.pumpkin.editor.imgui.ImGuiLayer
import com.pumpkin.editor.project.ProjectStructure
import com.pumpkin.editor.settings.SettingsSerializer

internal lateinit var imGuiLayer: ImGuiLayer
internal lateinit var editorLayer: EditorLayer

class EditorApp(private val sceneName: String?) : Application() {
    override fun init() {
        imGuiLayer = ImGuiLayer()
        pushOverlay(imGuiLayer)

        if (!SettingsSerializer.load()) SettingsSerializer.save()

        editorLayer = EditorLayer()
        pushLayer(editorLayer)

        if (sceneName != null) {
            //editorLayer.newScene()
            editorLayer.deserialize(sceneName)
        }
    }
}

fun main(args: Array<String>) {
    //ProjectStructure.generateStructure("Test", "com.test", "./generatedProject/")
    //return

    val sceneName = if (args.isNotEmpty()) args[0] else null
    Application.set(EditorApp(sceneName))
}
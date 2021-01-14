package com.pumpkin.editor.project

import com.pumpkin.editor.imgui.ImGuiWindow
import imgui.ImGui
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImBoolean

class BuildSettings(project: Project) {

    private val open = ImBoolean(false)

    fun open() {
        open.set(true)
        if (!loadSerialized()) saveSerialized()
    }

    private var showSceneChild = false
    fun onImGuiRender() {
        ImGuiWindow("Build Settings", open, ImGuiWindowFlags.NoCollapse) {
            val childFlags = ImGuiWindowFlags.NoCollapse or ImGuiWindowFlags.NoTitleBar or ImGuiWindowFlags.NoBackground or ImGuiWindowFlags.NoResize

            val collapseSceneChild = ImGui.getWindowHeight() < 500f
            showSceneChild = if (collapseSceneChild) {
                ImGui.collapsingHeader("Scenes")
            } else {
                ImGui.beginChild("ProjectScenes", -1f, 150f, true, childFlags)
                true
            }
            if (showSceneChild) {
                ImGui.text("Test")
            }
            if (!collapseSceneChild)
                ImGui.endChild()
            ImGui.separator()

            val columnHeight = ImGui.getWindowHeight() - 77 - if (collapseSceneChild) 100 else 500

            ImGui.columns(2)
            ImGui.setColumnWidth(-1, glm.min(250f, ImGui.getWindowWidth() / 3f))
            ImGui.beginChild("SelectPlatform", ImGui.getColumnWidth(-1) - 15f, columnHeight, false, childFlags)

            ImGui.text("Select Platform")

            ImGui.endChild()
            ImGui.nextColumn()
            ImGui.beginChild("SettingsDisplay", 0f, columnHeight, false, childFlags)

            ImGui.text("Real Build Settings")

            ImGui.endChild()
        }
    }

    fun loadSerialized(): Boolean {
        return true
    }

    fun saveSerialized() {

    }
}
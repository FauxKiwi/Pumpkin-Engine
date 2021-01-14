package com.pumpkin.editor.project

import com.pumpkin.editor.imgui.ImGuiWindow
import com.pumpkin.editor.imgui.fontAwesomeSymbol
import imgui.ImGui
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImBoolean

class BuildSettings(project: Project) {
    private var currentPlatform = 'j'

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
            val leftColumnWidth = glm.min(250f, ImGui.getWindowWidth() / 3f)
            ImGui.setColumnWidth(-1, leftColumnWidth)
            ImGui.beginChild("SelectPlatform", ImGui.getColumnWidth(-1) - 15f, columnHeight, false, childFlags)

            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 8f, 8f)

            val calcItemWidth = ImGui.calcItemWidth()
            val slOffset = ImGui.getWindowWidth() - calcItemWidth

            if (currentPlatform == 'j')
                ImGui.pushStyleColor(ImGuiCol.Button, ImGui.getColorU32(ImGuiCol.Header))
            fontAwesomeSymbol('', 4, 0xff2098f8.toInt())
            ImGui.sameLine(slOffset)
            ImGui.button(if (currentPlatform == 'j') "In use" else "Change", calcItemWidth,40f)
            ImGui.separator()
            if (currentPlatform == 'j')
                ImGui.popStyleColor()

            fontAwesomeSymbol('', 4, 0xff264ce3.toInt())
            ImGui.sameLine(slOffset)
            ImGui.button(if (currentPlatform == 'h') "In use" else "Change", calcItemWidth,40f)
            ImGui.separator()

            fontAwesomeSymbol('', 4, 0xffffa502.toInt())
            ImGui.sameLine(slOffset)
            ImGui.button(if (currentPlatform == 'w') "In use" else "Change", calcItemWidth,40f)
            ImGui.separator()

            fontAwesomeSymbol('', 4)
            ImGui.sameLine(slOffset)
            ImGui.button(if (currentPlatform == 'l') "In use" else "Change", calcItemWidth,40f)
            ImGui.separator()

            fontAwesomeSymbol('', 4, 0xff7f7f7f.toInt())
            ImGui.sameLine(slOffset)
            ImGui.button(if (currentPlatform == 'm') "In use" else "Change", calcItemWidth,40f)
            ImGui.separator()

            fontAwesomeSymbol('', 4, 0xff84dc3d.toInt())
            ImGui.sameLine(slOffset)
            ImGui.button(if (currentPlatform == 'a') "In use" else "Change", calcItemWidth,40f)

            ImGui.popStyleVar()

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
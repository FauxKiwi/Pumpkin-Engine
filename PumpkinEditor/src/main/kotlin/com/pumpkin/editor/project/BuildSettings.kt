package com.pumpkin.editor.project

import com.pumpkin.editor.FileDialog
import com.pumpkin.editor.SceneSerializer
import com.pumpkin.editor.activeProject
import com.pumpkin.editor.imgui.ImGuiWindow
import com.pumpkin.editor.imgui.fontAwesomeSymbol
import imgui.ImGui
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImBoolean

class BuildSettings {
    private var currentPlatform = 'j'

    private val open = ImBoolean(false)

    fun open() {
        open.set(true)
        if (!loadSerialized()) saveSerialized()
    }

    private var showSceneChild = false
    fun onImGuiRender() {
        ImGui.setNextWindowSize(450f, 600f)
        ImGuiWindow("Build Settings", open, ImGuiWindowFlags.NoCollapse or ImGuiWindowFlags.NoResize) {
            val childFlags =
                ImGuiWindowFlags.NoCollapse or ImGuiWindowFlags.NoTitleBar or ImGuiWindowFlags.NoBackground or ImGuiWindowFlags.NoResize

            ImGui.beginChild("ProjectScenes", -1f, 150f, false, childFlags)

            ImGui.text("Scenes in Build:")
            ImGui.separator()

            activeProject?.scenes?.let {
                for (scene in it) {
                    ImGui.checkbox("   ${scene.second}", true)
                }
            }
            if (ImGui.button("Add Scene")) run {
                val filepath = FileDialog.open("Select a scene", FileDialog.FileFilter("Pumpkin Scene", "pumpkin"))
                activeProject?.scenes?.add((filepath ?: return@run) to (SceneSerializer.getSceneName(filepath) ?: return@run))
            }

            ImGui.endChild()
            ImGui.separator()

            val columnHeight = 400f

            ImGui.columns(2)
            ImGui.setColumnWidth(-1, 150f)
            ImGui.beginChild("SelectPlatform", ImGui.getColumnWidth(-1) - 15f, columnHeight, false, childFlags)

            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 8f, 8f)

            val calcItemWidth = ImGui.calcItemWidth()
            val slOffset = ImGui.getWindowWidth() - calcItemWidth

            if (currentPlatform == 'j')
                ImGui.pushStyleColor(ImGuiCol.Button, ImGui.getColorU32(ImGuiCol.Header))
            fontAwesomeSymbol('', 4, 0xff2098f8.toInt())
            ImGui.sameLine(slOffset)
            ImGui.button(if (currentPlatform == 'j') "In Use" else "Change", calcItemWidth, 40f)
            ImGui.separator()
            if (currentPlatform == 'j')
                ImGui.popStyleColor()

            fontAwesomeSymbol('', 4, 0xff264ce3.toInt())
            ImGui.sameLine(slOffset)
            ImGui.button(if (currentPlatform == 'h') "In use" else "Change", calcItemWidth, 40f)
            ImGui.separator()

            fontAwesomeSymbol('', 4, 0xffffa502.toInt())
            ImGui.sameLine(slOffset)
            ImGui.button(if (currentPlatform == 'w') "In use" else "Change", calcItemWidth, 40f)
            ImGui.separator()

            fontAwesomeSymbol('', 4)
            ImGui.sameLine(slOffset)
            ImGui.button(if (currentPlatform == 'l') "In use" else "Change", calcItemWidth, 40f)
            ImGui.separator()

            fontAwesomeSymbol('', 4, 0xff7f7f7f.toInt())
            ImGui.sameLine(slOffset)
            ImGui.button(if (currentPlatform == 'm') "In use" else "Change", calcItemWidth, 40f)
            ImGui.separator()

            fontAwesomeSymbol('', 4, 0xff84dc3d.toInt())
            ImGui.sameLine(slOffset)
            ImGui.button(if (currentPlatform == 'a') "In use" else "Change", calcItemWidth, 40f)

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
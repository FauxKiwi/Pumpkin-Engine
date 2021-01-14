package com.pumpkin.editor.panels

import com.pumpkin.core.Application
import com.pumpkin.editor.EditorLayer
import com.pumpkin.editor.activeProject
import com.pumpkin.editor.editorLogger
import com.pumpkin.editor.imgui.ImGuiMenuItem
import com.pumpkin.editor.imgui.fontAwesomeSymbolSL
import com.pumpkin.editor.settings.Settings
import imgui.ImGui

class MenuBar(private val editorLayer: EditorLayer) {

    fun showMenuBar() {
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("File")) {
                fontAwesomeSymbolSL('\uf15b'); if (ImGui.menuItem("New", "Ctrl+N")) { editorLayer.newScene() }
                fontAwesomeSymbolSL('\uf07c'); if (ImGui.menuItem("Open...", "Ctrl+O")) { editorLayer.openScene() }
                fontAwesomeSymbolSL('\uf07c'); if (ImGui.menuItem("Open Recent")) {}
                fontAwesomeSymbolSL('\uf410'); if (ImGui.menuItem("Close Project", "Ctrl+W")) {}
                ImGui.separator()
                fontAwesomeSymbolSL('\uf013'); if (ImGui.menuItem("Settings", "Ctrl+Alt+S")) { Settings.open() }
                fontAwesomeSymbolSL('\uf085'); ImGuiMenuItem("Build Settings", "Ctrl+Alt+B") {
                    activeProject?.apply { buildSettings.open() } ?: editorLogger.warn("No Project opened")
                }
                ImGui.separator()
                fontAwesomeSymbolSL('\uf0c7'); if (ImGui.menuItem("Save Scene", "Ctrl+S")) { editorLayer.saveSceneAs() }
                fontAwesomeSymbolSL('\uf0c7'); if (ImGui.menuItem("Save As...", "Ctrl+Shift+S")) { editorLayer.saveSceneAs() }
                fontAwesomeSymbolSL('\uf01e'); if (ImGui.menuItem("Reload")) {}
                ImGui.separator()
                fontAwesomeSymbolSL('\uf011'); if (ImGui.menuItem("Exit")) { Application.get().close() }
                ImGui.endMenu()
            }

            if (ImGui.beginMenu("Edit")) {
                fontAwesomeSymbolSL('\uf0e2'); if (ImGui.menuItem("Undo", "Ctrl+Z")) {}
                fontAwesomeSymbolSL('\uf01e'); if (ImGui.menuItem("Redo", "Ctrl+Y")) {}
                ImGui.separator()
                fontAwesomeSymbolSL('\uf0c4'); if (ImGui.menuItem("Cut", "Ctrl+X")) {}
                fontAwesomeSymbolSL('\uf328'); if (ImGui.menuItem("Copy", "Ctrl+C")) {}
                fontAwesomeSymbolSL('\uf0ea'); if (ImGui.menuItem("Paste", "Ctrl+V")) {}
                fontAwesomeSymbolSL('\uf24d'); if (ImGui.menuItem("Clone", "Ctrl+D")) {}
                fontAwesomeSymbolSL('\uf1f8'); if (ImGui.menuItem("Delete", "Del")) {}
                ImGui.separator()
                fontAwesomeSymbolSL('\uf002'); if (ImGui.menuItem("Find", "Ctrl+F")) {}
                ImGui.endMenu()
            }

            if (ImGui.beginMenu("View")) {
                fontAwesomeSymbolSL('\uf2d2', 2); if (ImGui.beginMenu("Windows")) {
                    ImGuiMenuItem("Scene", "", editorLayer.showSceneView)
                    ImGuiMenuItem("Hierarchy", "", editorLayer.panels::showHierarchyPanel)
                    ImGuiMenuItem("Inspector", "", editorLayer.panels::showInspectorPanel)
                    ImGuiMenuItem("Console", "", editorLayer.panels::showConsolePanel)
                    ImGui.endMenu()
                }
                fontAwesomeSymbolSL('\uf55d'); if (ImGui.menuItem("Appearance")) {}
                ImGui.endMenu()
            }

            if (ImGui.beginMenu("Build & Run")) {
                fontAwesomeSymbolSL('\uf04b'); if (ImGui.menuItem("Run")) {}
                fontAwesomeSymbolSL('\uf6e3'); if (ImGui.menuItem("Build")) {}
                ImGui.endMenu()
            }

            if (ImGui.beginMenu("Tools")) {
                fontAwesomeSymbolSL('\uf0ad'); if (ImGui.menuItem("Tasks")) {}
                fontAwesomeSymbolSL(''); ImGuiMenuItem("Demo") { editorLayer.imGuiDemo.set(true) }
                ImGui.separator()
                //Plugins
                ImGui.endMenu()
            }

            if (ImGui.beginMenu("Help")) {
                fontAwesomeSymbolSL('\uf059'); if (ImGui.menuItem("Help")) {}
                ImGui.endMenu()
            }

            ImGui.endMainMenuBar()
        }
    }
}
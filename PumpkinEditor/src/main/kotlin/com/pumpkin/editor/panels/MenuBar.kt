package com.pumpkin.editor.panels

import com.pumpkin.core.Application
import com.pumpkin.editor.EditorLayer
import com.pumpkin.editor.imgui.ImGuiMenuItem
import com.pumpkin.editor.imgui.fontAwesomeSymbol
import com.pumpkin.editor.settings.Settings
import imgui.ImGui

class MenuBar(val editorLayer: EditorLayer) {

    fun showMenuBar() {
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("File")) {
                fontAwesomeSymbol('\uf15b'); if (ImGui.menuItem("New", "Ctrl+N")) { editorLayer.newScene() }
                fontAwesomeSymbol('\uf07c'); if (ImGui.menuItem("Open...", "Ctrl+O")) { editorLayer.openScene() }
                fontAwesomeSymbol('\uf07c'); if (ImGui.menuItem("Open Recent")) {}
                fontAwesomeSymbol('\uf410'); if (ImGui.menuItem("Close Project", "Ctrl+W")) {}
                ImGui.separator()
                fontAwesomeSymbol('\uf013'); if (ImGui.menuItem("Settings", "Ctrl+Alt+S")) { Settings.open() }
                fontAwesomeSymbol('\uf085'); if (ImGui.menuItem("Build Settings", "Ctrl+Alt+B")) {}
                ImGui.separator()
                fontAwesomeSymbol('\uf0c7'); if (ImGui.menuItem("Save Scene", "Ctrl+S")) { editorLayer.saveSceneAs() }
                fontAwesomeSymbol('\uf0c7'); if (ImGui.menuItem("Save As...", "Ctrl+Shift+S")) { editorLayer.saveSceneAs() }
                fontAwesomeSymbol('\uf01e'); if (ImGui.menuItem("Reload")) {}
                ImGui.separator()
                fontAwesomeSymbol('\uf011'); if (ImGui.menuItem("Exit")) { Application.get().close() }
                ImGui.endMenu()
            }

            if (ImGui.beginMenu("Edit")) {
                fontAwesomeSymbol('\uf0e2'); if (ImGui.menuItem("Undo", "Ctrl+Z")) {}
                fontAwesomeSymbol('\uf01e'); if (ImGui.menuItem("Redo", "Ctrl+Y")) {}
                ImGui.separator()
                fontAwesomeSymbol('\uf0c4'); if (ImGui.menuItem("Cut", "Ctrl+X")) {}
                fontAwesomeSymbol('\uf328'); if (ImGui.menuItem("Copy", "Ctrl+C")) {}
                fontAwesomeSymbol('\uf0ea'); if (ImGui.menuItem("Paste", "Ctrl+V")) {}
                fontAwesomeSymbol('\uf24d'); if (ImGui.menuItem("Clone", "Ctrl+D")) {}
                fontAwesomeSymbol('\uf1f8'); if (ImGui.menuItem("Delete", "Del")) {}
                ImGui.separator()
                fontAwesomeSymbol('\uf002'); if (ImGui.menuItem("Find", "Ctrl+F")) {}
                ImGui.endMenu()
            }

            if (ImGui.beginMenu("View")) {
                fontAwesomeSymbol('\uf2d2', 2); if (ImGui.beginMenu("Windows")) {
                    ImGuiMenuItem("Hierarchy", "", editorLayer.panels::showHierarchyPanel)
                    ImGuiMenuItem("Inspector", "", editorLayer.panels::showInspectorPanel)
                    ImGuiMenuItem("Console", "", editorLayer.panels::showConsolePanel)
                    ImGui.endMenu()
                }
                fontAwesomeSymbol('\uf55d'); if (ImGui.menuItem("Appearance")) {}
                ImGui.endMenu()
            }

            if (ImGui.beginMenu("Build & Run")) {
                fontAwesomeSymbol('\uf04b'); if (ImGui.menuItem("Run")) {}
                fontAwesomeSymbol('\uf6e3'); if (ImGui.menuItem("Build")) {}
                ImGui.endMenu()
            }

            if (ImGui.beginMenu("Tools")) {
                fontAwesomeSymbol('\uf0ad'); if (ImGui.menuItem("Tasks")) {}
                fontAwesomeSymbol(''); ImGuiMenuItem("Demo") { editorLayer.imGuiDemo.set(true) }
                ImGui.separator()
                //Plugins
                ImGui.endMenu()
            }

            if (ImGui.beginMenu("Help")) {
                fontAwesomeSymbol('\uf059'); if (ImGui.menuItem("Help")) {}
                ImGui.endMenu()
            }

            ImGui.endMainMenuBar()
        }
    }
}
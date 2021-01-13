package com.pumpkin.editor.panels

import com.pumpkin.core.scene.Scene

class Panels(activeScene: Scene) {
    internal val hierarchyPanel: HierarchyPanel = HierarchyPanel(activeScene)
    private val inspectorPanel: InspectorPanel = InspectorPanel(hierarchyPanel)
    private val consolePanel = ConsolePanel()

    internal var showHierarchyPanel = true
    internal var showInspectorPanel = true
    internal var showConsolePanel = true

    fun onImGuiRender() {
        hierarchyPanel.onImGuiRender(::showHierarchyPanel)
        inspectorPanel.onImGuiRender(::showInspectorPanel)
        consolePanel.onImGuiRender(::showConsolePanel)
    }
}
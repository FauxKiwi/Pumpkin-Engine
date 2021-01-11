package com.pumpkin.editor.panels

import com.pumpkin.core.scene.Scene

class Panels(activeScene: Scene) {
    internal val hierarchyPanel: HierarchyPanel = HierarchyPanel(activeScene)
    private val inspectorPanel: InspectorPanel = InspectorPanel(hierarchyPanel)
    private val consolePanel = ConsolePanel()

    fun onImGuiRender() {
        hierarchyPanel.onImGuiRender()
        inspectorPanel.onImGuiRender()
        consolePanel.onImGuiRender()
    }
}
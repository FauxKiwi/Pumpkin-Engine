package com.pumpkin.editor.panels

import com.pumpkin.core.scene.Scene

object Panels {
    internal lateinit var hierarchyPanel: HierarchyPanel
    private lateinit var inspectorPanel: InspectorPanel
    private val consolePanel = ConsolePanel()

    fun init(activeScene: Scene) {
        hierarchyPanel = HierarchyPanel(activeScene)
        inspectorPanel = InspectorPanel(hierarchyPanel)
    }

    fun onImGuiRender() {
        hierarchyPanel.onImGuiRender()
        inspectorPanel.onImGuiRender()
        consolePanel.onImGuiRender()
    }
}
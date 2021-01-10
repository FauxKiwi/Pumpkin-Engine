package com.pumpkin.editor.panels

import com.pumpkin.core.Debug
import com.pumpkin.editor.imgui.ImGuiWindow
import imgui.ImGui

class ConsolePanel {
    private var log = mutableListOf<Pair<Int, String>>()

    init {
        Debug.subscribe { level, message -> log.add(level.color().toColorInt() to message) }
        Debug.logInfo("Thank you for using the Pumpkin engine")
    }

    fun onImGuiRender() {
        ImGuiWindow("Console") {
            for ((c, m) in log) {
                ImGui.textColored(c, m)
            }
        }
    }
}
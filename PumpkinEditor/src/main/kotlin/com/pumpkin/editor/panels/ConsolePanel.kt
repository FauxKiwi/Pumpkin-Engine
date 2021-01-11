package com.pumpkin.editor.panels

import com.pumpkin.core.Debug
import com.pumpkin.core.LogLevel
import com.pumpkin.editor.imGuiLayer
import com.pumpkin.editor.imgui.ImGuiWindow
import imgui.ImGui
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiWindowFlags

class ConsolePanel {
    private var log = mutableListOf<Pair<LogLevel, String>>()

    init {
        Debug.subscribe { level, message -> log.add(level to message) }
        Debug.logInfo("Thank you for using the Pumpkin engine")

        Debug.logTrace("Trace test")
        Debug.logDebug("Debug test")
        Debug.logInfo("Info test")
        Debug.logWarn("Warn test")
        Debug.logError("Error test")
        Debug.logFatal("Fatal test")
    }

    private var minLevel = 0

    fun onImGuiRender() {
        ImGuiWindow("Console", windowFlags = ImGuiWindowFlags.MenuBar) {
            ImGui.beginMenuBar()
            ImGui.pushFont(imGuiLayer.fonts[1])

            ImGui.pushStyleColor(ImGuiCol.Text, LogLevel.TRACE.colorInt())
            ImGui.button("\uf4ad") // Trace
            ImGui.popStyleColor()

            ImGui.pushStyleColor(ImGuiCol.Text, LogLevel.DEBUG.colorInt())
            ImGui.button("\uf188") // Debug
            ImGui.popStyleColor()

            ImGui.pushStyleColor(ImGuiCol.Text, LogLevel.INFO.colorInt())
            ImGui.button("\uf05a") // Info
            ImGui.popStyleColor()

            ImGui.pushStyleColor(ImGuiCol.Text, LogLevel.WARN.colorInt())
            ImGui.button("\uf071") // Warn
            ImGui.popStyleColor()

            ImGui.pushStyleColor(ImGuiCol.Text, LogLevel.ERROR.colorInt())
            ImGui.button("\uf06a") // Error
            ImGui.popStyleColor()

            ImGui.pushStyleColor(ImGuiCol.Text, LogLevel.FATAL.colorInt())
            ImGui.button("\uf1e2") // Fatal
            ImGui.popStyleColor()

            ImGui.popFont()
            ImGui.endMenuBar()

            for ((l, m) in log) {
                if (l.ordinal < minLevel) continue
                ImGui.textColored(l.colorInt(), m)
            }
        }
    }
}
package com.pumpkin.editor.panels

import com.pumpkin.core.Debug
import com.pumpkin.core.LogLevel
import com.pumpkin.editor.imGuiLayer
import com.pumpkin.editor.imgui.*
import imgui.ImGui
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiStyleVar
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
            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0f, 8f)

            ImGui.pushStyleColor(ImGuiCol.Text, LogLevel.TRACE.colorInt())
            ImGui.button(TraceIconChar.toString()) // Trace
            ImGui.popStyleColor()

            ImGui.pushStyleColor(ImGuiCol.Text, LogLevel.DEBUG.colorInt())
            ImGui.button(DebugIconChar.toString()) // Debug
            ImGui.popStyleColor()

            ImGui.pushStyleColor(ImGuiCol.Text, LogLevel.INFO.colorInt())
            ImGui.button(InfoIconChar.toString()) // Info
            ImGui.popStyleColor()

            ImGui.pushStyleColor(ImGuiCol.Text, LogLevel.WARN.colorInt())
            ImGui.button(WarnIconChar.toString()) // Warn
            ImGui.popStyleColor()

            ImGui.pushStyleColor(ImGuiCol.Text, LogLevel.ERROR.colorInt())
            ImGui.button(ErrorIconChar.toString()) // Error
            ImGui.popStyleColor()

            ImGui.pushStyleColor(ImGuiCol.Text, LogLevel.FATAL.colorInt())
            ImGui.button(FatalIconChar.toString()) // Fatal
            ImGui.popStyleColor()

            ImGui.popStyleVar()
            ImGui.popFont()
            ImGui.endMenuBar()


            //ImGui.pushStyleVar(ImGuiStyleVar.)
            //ImGui.showDemoWindow()
            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 8f, 10f)

            for ((l, m) in log) {
                if (l.ordinal < minLevel) continue
                fontAwesomeSymbol(l.iconChar, color = l.colorInt())
                ImGui.textColored(if(l == LogLevel.TRACE || l == LogLevel.FATAL) l.colorInt() else -1, m)
                ImGui.separator()
            }

            ImGui.popStyleVar()
        }
    }
}
package com.pumpkin.editor.panels

import com.pumpkin.core.Debug
import com.pumpkin.core.LogLevel
import com.pumpkin.editor.editorLogger
import com.pumpkin.editor.imGuiLayer
import com.pumpkin.editor.imgui.*
import imgui.ImGui
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiInputTextFlags
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImString
import kotlin.reflect.KMutableProperty0

class ConsolePanel {
    private var log = mutableListOf<Pair<LogLevel, String>>()

    init {
        val callback: (LogLevel, String) -> Unit = { level, message -> log.add(level to message) }
        Debug.subscribe(callback)
        editorLogger.addSubscriber(callback)

        editorLogger.info("Thank you for using the Pumpkin engine")

        /*editorLogger.trace("Trace test")
        editorLogger.debug("Debug test")
        editorLogger.info("Info test")
        editorLogger.warn("Warn test")
        editorLogger.error("Error test")
        editorLogger.fatal("Fatal test")*/
    }

    private var minLevel = 0

    fun onImGuiRender(show: KMutableProperty0<Boolean>) {
        ImGuiWindow("Console", show, ImGuiWindowFlags.MenuBar) {
            ImGui.beginMenuBar()
            ImGui.pushFont(imGuiLayer.fonts[1])
            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0f, 8f)

            ImGui.pushStyleColor(ImGuiCol.Text, LogLevel.TRACE.colorInt())
            if (ImGui.button(TraceIconChar.toString())) // Trace
                setAllLoggersMinLevel(LogLevel.TRACE)
            ImGui.popStyleColor()

            ImGui.pushStyleColor(ImGuiCol.Text, LogLevel.DEBUG.colorInt())
            if (ImGui.button(DebugIconChar.toString())) // Debug
                setAllLoggersMinLevel(LogLevel.DEBUG)
            ImGui.popStyleColor()

            ImGui.pushStyleColor(ImGuiCol.Text, LogLevel.INFO.colorInt())
            if (ImGui.button(InfoIconChar.toString())) // Info
                setAllLoggersMinLevel(LogLevel.INFO)
            ImGui.popStyleColor()

            ImGui.pushStyleColor(ImGuiCol.Text, LogLevel.WARN.colorInt())
            if (ImGui.button(WarnIconChar.toString())) // Warn
                setAllLoggersMinLevel(LogLevel.WARN)
            ImGui.popStyleColor()

            ImGui.pushStyleColor(ImGuiCol.Text, LogLevel.ERROR.colorInt())
            if (ImGui.button(ErrorIconChar.toString())) // Error
                setAllLoggersMinLevel(LogLevel.ERROR)
            ImGui.popStyleColor()

            ImGui.pushStyleColor(ImGuiCol.Text, LogLevel.FATAL.colorInt())
            if (ImGui.button(FatalIconChar.toString())) // Fatal
                setAllLoggersMinLevel(LogLevel.FATAL)
            ImGui.popStyleColor()

            ImGui.popStyleVar()

            ImGui.text("   \uf002")

            ImGui.popFont()

            //ImGui.pushStyleColor(ImGuiCol)
            ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 16f)
            ImGui.pushItemWidth(ImGui.calcItemWidth() - 200f)

            val searchFor = ImString("Search...")
            ImGui.inputText("", searchFor, ImGuiInputTextFlags.AutoSelectAll /*or ImGuiInputTextFlags.CallbackAlways*/)

            ImGui.popItemWidth()
            ImGui.popStyleVar()

            ImGui.button("Clear")

            ImGui.endMenuBar()


            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 8f, 8f)
            ImGui.pushStyleColor(ImGuiCol.Separator, ImGui.getColorU32(ImGuiCol.Button))

            for ((l, m) in log) {
                if (l.ordinal < minLevel) continue
                fontAwesomeSymbol(l.iconChar, color = l.colorInt())
                if(l == LogLevel.TRACE || l == LogLevel.FATAL)
                    ImGui.textColored( l.colorInt(), m)
                else
                    ImGui.text(m)
                ImGui.separator()
            }

            ImGui.popStyleColor()
            ImGui.popStyleVar()
        }
    }

    private fun setAllLoggersMinLevel(level: LogLevel) {
        Debug.setLogMinLevel(level)
        editorLogger.minLevel = level
    }
}
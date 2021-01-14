package com.pumpkin.editor.imgui

import com.pumpkin.core.LogLevel
import com.pumpkin.editor.imGuiLayer
import glm.f
import glm.floor
import glm.max
import imgui.ImGui
import imgui.flag.ImGuiCol
import java.io.ByteArrayOutputStream

fun loadFromResources(fileName: String): ByteArray? {
    ClassLoader.getSystemResourceAsStream(fileName).use { inputStream ->
        ByteArrayOutputStream().use { buffer ->
            val data = ByteArray(16384)
            var nRead: Int
            while (inputStream!!.read(data, 0, data.size).also { nRead = it } != -1) {
                buffer.write(data, 0, nRead)
            }
            return buffer.toByteArray()
        }
    }
}

fun pushMultiItemsWidths(components: Int, wFull: Float) {
    //val window = currentWindow
    val wItemOne = 1f max floor((wFull - (ImGui.getStyle().itemInnerSpacingX) * (components - 1)) / components.f)
    val wItemLast = 1f max floor(wFull - (wItemOne + ImGui.getStyle().itemInnerSpacingX) * (components - 1))
    ImGui.pushItemWidth(wItemLast) //window.dc.itemWidthStack.push(wItemLast)
    for (i in 0 until components - 1)
        ImGui.pushItemWidth(wItemLast) //window.dc.itemWidthStack.push(wItemOne)
    //window.dc.itemWidth = window.dc.itemWidthStack.last()
    //g.nextItemData.flags = g.nextItemData.flags wo NextItemDataFlag.HasWidth
}

@Deprecated("Does 'same line'")
fun fontAwesomeSymbolSL(character: Char, fontIndex: Int = 1, color: Int = ImGui.getColorU32(ImGuiCol.Text)) {
    ImGui.pushFont(imGuiLayer.fonts[fontIndex])
    if (color == -1)
        ImGui.text(character.toString())
    else
        ImGui.textColored(color, character.toString())
    ImGui.popFont()
    ImGui.sameLine()
}

fun fontAwesomeSymbol(character: Char, fontIndex: Int = 1, color: Int = ImGui.getColorU32(ImGuiCol.Text)) {
    ImGui.pushFont(imGuiLayer.fonts[fontIndex])
    if (color == -1)
        ImGui.text(character.toString())
    else
        ImGui.textColored(color, character.toString())
    ImGui.popFont()
    //ImGui.sameLine()
}

val LogLevel.iconChar get() = when (this) {
    LogLevel.TRACE -> TraceIconChar
    LogLevel.DEBUG -> DebugIconChar
    LogLevel.INFO -> InfoIconChar
    LogLevel.WARN -> WarnIconChar
    LogLevel.ERROR -> ErrorIconChar
    LogLevel.FATAL -> FatalIconChar
}

const val TraceIconChar = ''
const val DebugIconChar = ''
const val InfoIconChar = ''
const val WarnIconChar = ''
const val ErrorIconChar = ''
const val FatalIconChar = ''
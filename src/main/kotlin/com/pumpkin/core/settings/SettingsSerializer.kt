package com.pumpkin.core.settings

import com.pumpkin.core.jsonFormat
import imgui.ImGui
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object SettingsSerializer {

    fun save() {
        val jsonObject = buildJsonObject {
            putJsonObject("Appearance") {
                put("Theme", Theme.current)
            }
        }

        val jsonString = jsonFormat.encodeToString(jsonObject)
        val file = File("./editorconfig.json")
        if (!file.exists()) file.createNewFile()
        val fileWriter = FileWriter(file)
        fileWriter.use {
            it.write(jsonString)
        }
    }

    fun load(): Boolean {
        val file = File("./editorconfig.json")
        if (!file.exists()) return false
        val fileReader = FileReader(file)
        val text: String
        fileReader.use {
            text = it.readText()
        }
        val jsonObject = jsonFormat.parseToJsonElement(text).jsonObject

        val appearanceObject = jsonObject["Appearance"]!!.jsonObject
        Theme.current = appearanceObject["Theme"]!!.jsonPrimitive.int
        ImGui.currentContext?.style = Theme[Theme.current].style
        return true
    }
}
package com.pumpkin.editor.settings

import com.pumpkin.core.Debug
import com.pumpkin.core.jsonFormat
import glm.Vec4
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
            putJsonObject("EditorCamera") {
                put("ClearColor", Settings.editorCameraClearColor)
                put("Fov", Settings.editorCameraFov)
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

    private fun JsonObjectBuilder.put(key: String, element: Vec4) {
        putJsonArray(key) {
            add(element.x)
            add(element.y)
            add(element.z)
            add(element.w)
        }
    }

    fun load(): Boolean {
        try {
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
            //ImGui.currentContext?.style = Theme[Theme.current].style
            Theme[Theme.current].apply(ImGui.getStyle())
            val editorCameraObject = jsonObject["EditorCamera"]!!.jsonObject
            val editorCameraClearColor = editorCameraObject["ClearColor"]!!.jsonArray
            Settings.editorCameraClearColor = Vec4(
                editorCameraClearColor[0].jsonPrimitive.float,
                editorCameraClearColor[1].jsonPrimitive.float,
                editorCameraClearColor[2].jsonPrimitive.float,
                editorCameraClearColor[3].jsonPrimitive.float
            )
            Settings.editorCameraFov = editorCameraObject["Fov"]!!.jsonPrimitive.float
            Settings.uEditorCameraView = true
            return true
        } catch (e: Exception) {
            Debug.logWarn("Could not parse editorconfig")
            return false
        }
    }
}
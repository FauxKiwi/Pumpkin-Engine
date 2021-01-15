package com.pumpkin.editor.project

import kotlinx.serialization.json.*

class ProjectSerializer(var project: Project) {

    fun serialize(absoluteFilepath: String) {
        val jsonObject = buildJsonObject {
            put("Project", project.name)
            putJsonArray("Scenes") {
                project.scenes.forEach {
                    add(it.second)
                }
            }
            putJsonObject("Settings") {
                putJsonObject("EditorCamera") {

                }
            }
        }
    }
}
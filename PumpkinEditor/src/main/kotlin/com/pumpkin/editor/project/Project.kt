package com.pumpkin.editor.project

class Project(val name: String, val scenes: MutableList<Pair<String /*relative filepath*/, String /*name*/>> = mutableListOf()) {
    val buildSettings = BuildSettings()
    init { buildSettings.loadSerialized() }
}
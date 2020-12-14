package editor

import com.pumpkin.core.Application

class EditorApp : Application() {

    override fun init() {
        pushLayer(EditorLayer())
    }
}

fun main() {
    Application.set(EditorApp())
}
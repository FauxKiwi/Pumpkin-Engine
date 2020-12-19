package editor

import com.pumpkin.core.Application
import com.pumpkin.core.settings.SettingsSerializer

class EditorApp : Application() {

    override fun init() {
        if (!SettingsSerializer.load()) SettingsSerializer.save()
        pushLayer(EditorLayer())
    }
}

fun main() {
    Application.set(EditorApp())
}
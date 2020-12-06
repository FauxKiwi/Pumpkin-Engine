package game_of_life

import com.pumpkin.core.render.RendererCommand
import glm_.vec4.Vec4
import com.pumpkin.core.Application as PEApp

class Application : PEApp() {

    override fun init() {
        pushLayer(MainLayer())
    }

    override fun run() {
        RendererCommand.setClearColor(Vec4(0.1f, 0.1f, 0.1f, 1.0f))
        RendererCommand.clear()
    }
}

fun main() {
    PEApp.set(Application())
}
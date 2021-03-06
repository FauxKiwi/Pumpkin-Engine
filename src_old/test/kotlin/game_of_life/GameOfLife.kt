package game_of_life

import com.pumpkin.core.renderer.RendererCommand
import glm.Vec4
import com.pumpkin.core.Application as PEApp

class GameOfLife : PEApp() {

    override fun init() {
        pushLayer(MainLayer())
    }

    override fun run() {
        RendererCommand.setClearColor(Vec4(0.1f, 0.1f, 0.1f, 1.0f))
        RendererCommand.clear()
    }
}

fun main() {
    PEApp.set(GameOfLife())
}
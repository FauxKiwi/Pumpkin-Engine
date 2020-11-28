package com.pumpkin.render

enum class RenderAPI {
    None, OpenGL
}

class Renderer {
    companion object {
        private val renderAPI = RenderAPI.OpenGL

        fun getAPI() = renderAPI
    }
}
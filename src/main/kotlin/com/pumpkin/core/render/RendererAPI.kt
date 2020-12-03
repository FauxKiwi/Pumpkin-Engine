package com.pumpkin.core.render

import glm_.vec4.Vec4

interface RendererAPI {
    enum class API {
        None, OpenGL
    }

    val api: API

    fun init()

    fun setClearColor(color: Vec4)

    fun clear()

    fun drawIndexed(vertexArray: VertexArray)
}
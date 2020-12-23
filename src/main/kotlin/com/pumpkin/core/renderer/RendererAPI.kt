package com.pumpkin.core.renderer

import glm_.vec4.Vec4

interface RendererAPI {
    enum class API {
        None, OpenGL
    }

    val api: API

    fun init()

    fun setClearColor(color: Vec4)

    fun clear()

    fun drawIndexed(vertexArray: VertexArray, count: Int = 0)

    fun setViewport(x: Int, y: Int, width: Int, height: Int)
}
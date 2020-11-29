package com.pumpkin.core.render

import com.pumpkin.core.logErrorCore
import com.pumpkin.platform.opengl.OpenGLShader
import glm_.mat4x4.Mat4

interface Shader {
    companion object {
        fun create(vertexSrc: String, fragmentSrc: String): Shader = when (Renderer.getAPI()) {
            RendererAPI.API.None -> {
                logErrorCore("Having no render API is currently not supported")
                throw Throwable()
            }
            RendererAPI.API.OpenGL -> OpenGLShader(vertexSrc, fragmentSrc)
        }
    }

    fun bind()

    fun unbind()

    fun uploadUniformMat4(name: String, matrix: Mat4)
}
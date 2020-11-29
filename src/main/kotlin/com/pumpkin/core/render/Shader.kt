package com.pumpkin.core.render

import com.pumpkin.core.logErrorCore
import com.pumpkin.platform.opengl.OpenGLShader

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
}
package com.pumpkin.core.render

import com.pumpkin.core.logErrorCore
import com.pumpkin.platform.opengl.OpenGLShader

interface Shader {
    companion object {
        fun create(vertexSrc: String, fragmentSrc: String): Shader = when (Renderer.getAPI()) {
            RenderAPI.None -> {
                logErrorCore("Having no render API is currently not supported")
                throw Throwable()
            }
            RenderAPI.OpenGL -> OpenGLShader(vertexSrc, fragmentSrc)
        }
    }

    fun bind()

    fun unbind()
}
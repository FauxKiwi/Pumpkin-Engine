package com.pumpkin.core.render

import com.pumpkin.core.Ref
import com.pumpkin.core.logErrorCore
import com.pumpkin.platform.opengl.OpenGLShader
import glm_.mat4x4.Mat4

interface Shader : AutoCloseable {
    companion object {
        fun create(vertexSrc: String, fragmentSrc: String): Ref<Shader> = when (Renderer.getAPI()) {
            RendererAPI.API.None -> {
                logErrorCore("Having no render API is currently not supported")
                throw Throwable()
            }
            RendererAPI.API.OpenGL -> Ref(OpenGLShader(vertexSrc, fragmentSrc))
        }

        fun create(filepath: String): Ref<Shader> = when (Renderer.getAPI()) {
            RendererAPI.API.None -> {
                logErrorCore("Having no render API is currently not supported")
                throw Throwable()
            }
            RendererAPI.API.OpenGL -> Ref(OpenGLShader(filepath))
        }
    }

    fun bind()

    fun unbind()
}
package com.pumpkin.core.render

import com.pumpkin.core.PumpkinError
import com.pumpkin.core.Ref
import com.pumpkin.platform.opengl.OpenGLShader

interface Shader : AutoCloseable {
    companion object {
        fun create(name: String, vertexSrc: String, fragmentSrc: String): Ref<Shader> = when (Renderer.getAPI()) {
            RendererAPI.API.None -> {
                throw PumpkinError("Having no render API is currently not supported")
            }
            RendererAPI.API.OpenGL -> Ref(OpenGLShader(name, vertexSrc, fragmentSrc))
        }

        fun create(filepath: String): Ref<Shader> = when (Renderer.getAPI()) {
            RendererAPI.API.None -> {
                throw PumpkinError("Having no render API is currently not supported")
            }
            RendererAPI.API.OpenGL -> Ref(OpenGLShader(filepath))
        }
    }

    val name: String

    fun bind()

    fun unbind()
}

class ShaderLibrary : AutoCloseable {
    private val shaders = hashMapOf<String, Ref<Shader>>()

    override fun close() {
        shaders.forEach { (_, shader) ->
            shader.release()
        }
    }

    fun add(name: String, shader: Ref<Shader>) {
        if(exists(name)) throw PumpkinError("Shader already exists")
        else shaders[name] = shader
    }

    fun add(shader: Ref<Shader>) {
        if(exists(shader().name)) throw PumpkinError("Shader already exists")
        else shaders[shader().name] = shader
    }

    fun load(name: String, filepath: String) = Shader.create(filepath).also { add(name, it) }

    fun load(filepath: String) = Shader.create(filepath).also { add(it) }

    inline operator fun set(name: String, shader: Ref<Shader>) = add(name, shader)

    inline operator fun plusAssign(shader: Ref<Shader>) = add(shader)

    operator fun get(name: String): Shader = if(!exists(name)) throw PumpkinError("Shader does not exist") else shaders[name]!!()

    fun exists(name: String) = shaders.containsKey(name)
}
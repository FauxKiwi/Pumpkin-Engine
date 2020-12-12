package com.pumpkin.core.renderer

import com.pumpkin.core.Debug
import com.pumpkin.core.Ref
import com.pumpkin.platform.opengl.OpenGLShader
import glm_.mat3x3.Mat3
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4

interface Shader : AutoCloseable {
    companion object {
        fun create(name: String, vertexSrc: String, fragmentSrc: String): Ref<Shader> = when (Renderer.getAPI()) {
            RendererAPI.API.None -> {
                Debug.exception("Having no render API is currently not supported")
            }
            RendererAPI.API.OpenGL -> Ref(OpenGLShader(name, vertexSrc, fragmentSrc))
        }

        fun create(filepath: String): Ref<Shader> = when (Renderer.getAPI()) {
            RendererAPI.API.None -> {
                Debug.exception("Having no render API is currently not supported")
            }
            RendererAPI.API.OpenGL -> Ref(OpenGLShader(filepath))
        }
    }

    val name: String

    fun bind()

    fun unbind()

    fun setFloat(name: String, value: Float)
    fun setFloat2(name: String, value: Vec2)
    fun setFloat3(name: String, value: Vec3)
    fun setFloat4(name: String, value: Vec4)

    fun setMat3(name: String, value: Mat3)
    fun setMat4(name: String, value: Mat4)

    fun setInt(name: String, value: Int)
    fun setIntArray(name: String, value: IntArray)
}

class ShaderLibrary : AutoCloseable {
    private val shaders = hashMapOf<String, Ref<Shader>>()

    override fun close() {
        shaders.forEach { (_, shader) ->
            shader.release()
        }
    }

    fun add(name: String, shader: Ref<Shader>) {
        if(exists(name)) Debug.exception("Shader already exists")
        else shaders[name] = shader
    }

    fun add(shader: Ref<Shader>) {
        if(exists(shader().name)) Debug.exception("Shader already exists")
        else shaders[shader().name] = shader
    }

    fun load(name: String, filepath: String) = Shader.create(filepath).also { add(name, it) }

    fun load(filepath: String) = Shader.create(filepath).also { add(it) }

    inline operator fun set(name: String, shader: Ref<Shader>) = add(name, shader)

    inline operator fun plusAssign(shader: Ref<Shader>) = add(shader)

    operator fun get(name: String): Shader = if(!exists(name)) Debug.exception("Shader does not exist") else shaders[name]!!()

    fun exists(name: String) = shaders.containsKey(name)
}
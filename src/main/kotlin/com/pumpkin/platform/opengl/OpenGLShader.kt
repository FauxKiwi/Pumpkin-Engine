package com.pumpkin.platform.opengl

import com.pumpkin.core.logDebugCore
import com.pumpkin.core.logErrorCore
import com.pumpkin.core.logFatal
import com.pumpkin.core.render.Shader
import glm_.mat3x3.Mat3
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import gln.ShaderType
import gln.gl
import gln.identifiers.GlProgram

class OpenGLShader(vertexSrc: String, fragmentSrc: String) : Shader {
    private val rendererID: GlProgram

    init {
        val vertexShader = gl.createShader(ShaderType.VERTEX_SHADER)

        gl.shaderSource(vertexShader, vertexSrc)

        gl.compileShader(vertexShader)


        if (!vertexShader.compileStatus) {
            logErrorCore("Vertex shader compile error: ${gl.getShaderInfoLog(vertexShader)}")

            gl.deleteShader(vertexShader)
        }

        val fragmentShader = gl.createShader(ShaderType.FRAGMENT_SHADER)

        gl.shaderSource(fragmentShader, fragmentSrc)

        gl.compileShader(fragmentShader)

        if (!fragmentShader.compileStatus) {
            logErrorCore("Fragment shader compile error: ${gl.getShaderInfoLog(fragmentShader)}")

            gl.deleteShader(fragmentShader)
            gl.deleteShader(vertexShader)
        }

        rendererID = gl.createProgram()

        gl.attachShader(rendererID, vertexShader)
        gl.attachShader(rendererID, fragmentShader)

        gl.linkProgram(rendererID)

        if (!rendererID.linkStatus) {
            logErrorCore("Shader link error ${gl.getProgramInfoLog(rendererID)}")

            gl.deleteProgram(rendererID)
            gl.deleteShader(vertexShader)
            gl.deleteShader(fragmentShader)
        }

        gl.detachShader(rendererID, vertexShader)
        gl.detachShader(rendererID, fragmentShader)
    }

    override fun close() = gl.deleteProgram(rendererID)

    override fun bind() = gl.useProgram(rendererID)

    override fun unbind() = gl.useProgram(GlProgram.NULL)

    fun uploadUniform(name: String, value: Float) = gl.uniform(gl.getUniformLocation(rendererID, name), value)

    fun uploadUniform(name: String, value: Vec2) = gl.uniform(gl.getUniformLocation(rendererID, name), value)

    fun uploadUniform(name: String, value: Vec3) = gl.uniform(gl.getUniformLocation(rendererID, name), value)

    fun uploadUniform(name: String, value: Vec4) = gl.uniform(gl.getUniformLocation(rendererID, name), value)

    fun uploadUniform(name: String, value: Mat3) = gl.uniform(gl.getUniformLocation(rendererID, name), value)

    fun uploadUniform(name: String, value: Mat4) = gl.uniform(gl.getUniformLocation(rendererID, name), value)

    fun uploadUniform(name: String, value: Int) = gl.uniform(gl.getUniformLocation(rendererID, name), value)
}
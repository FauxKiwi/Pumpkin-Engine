package com.pumpkin.platform.opengl

import com.pumpkin.core.PumpkinError
import com.pumpkin.core.logErrorCore
import com.pumpkin.core.render.Shader
import com.pumpkin.core.stack
import glm_.mat3x3.Mat3
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import gln.ShaderType
import gln.gl
import gln.identifiers.GlProgram
import gln.identifiers.GlShader
import java.io.FileReader
import kotlin.math.max

class OpenGLShader : Shader {
    override val name: String

    private var rendererID: GlProgram? = null

    constructor(filepath: String) {
        stack {
            val source = FileReader(filepath).readText()
            val shaderSources = preProcess(source)
            compile(shaderSources)
        }
        val lastSlash = max(filepath.lastIndexOf('/'), filepath.lastIndexOf('\\'))
        val lastDot = filepath.lastIndexOf('.')
        name = filepath.substring(lastSlash + 1, lastDot)
    }

    constructor(name: String, vertexSrc: String, fragmentSrc: String) {
        stack {
            compile(hashMapOf(Pair(ShaderType.VERTEX_SHADER, vertexSrc), Pair(ShaderType.FRAGMENT_SHADER, fragmentSrc)))
        }
        this.name = name
    }

    private fun preProcess(source: String): HashMap<ShaderType, String> {
        val shaderSources = HashMap<ShaderType, String>()
        stack {
            val typeToken = "#type"
            val typeTokenLength = typeToken.length
            var pos = source.indexOf(typeToken)
            while (pos != -1) {
                val eol = source.indexOf('\r', pos)
                if (eol == -1) throw PumpkinError().also { logErrorCore("Syntax error") }
                val begin = pos + typeTokenLength + 1
                val type = source.substring(begin, eol)
                if (shaderTypeFromString(type) == null) throw PumpkinError().also { logErrorCore("No valid shader type specified") }
                var nextLinePos = eol + 2
                while (source[nextLinePos + 1] == '\n') nextLinePos += 2
                pos = source.indexOf(typeToken, nextLinePos)
                shaderSources[shaderTypeFromString(type)!!] =
                    source.substring(nextLinePos, if (pos == -1) source.length - 1 else pos - 1).trim()
            }
        }
        return shaderSources
    }

    private fun shaderTypeFromString(string: String): ShaderType? = when (string) {
        "vertex" -> ShaderType.VERTEX_SHADER
        "fragment", "pixel" -> ShaderType.FRAGMENT_SHADER
        else -> null
    }

    private fun ShaderType.name() = when (this.i) {
        ShaderType.VERTEX_SHADER.i -> "Vertex"
        ShaderType.FRAGMENT_SHADER.i -> "Fragment"
        else -> "Unknown"
    }

    private fun compile(sources: HashMap<ShaderType, String>) {
        stack {
            val program = gl.createProgram()
            val shaderIDs = mutableListOf<GlShader>()
            for ((shaderType, shaderSource) in sources) {
                val shader = gl.createShader(shaderType)

                gl.shaderSource(shader, shaderSource)
                gl.compileShader(shader)

                if (!shader.compileStatus) {
                    logErrorCore("Shader (${shaderType.name()}) compile error: ${gl.getShaderInfoLog(shader)}")
                    gl.deleteShader(shader)
                }

                gl.attachShader(program, shader)
                shaderIDs.add(shader)
            }

            rendererID = program
            gl.linkProgram(rendererID!!)

            if (!rendererID!!.linkStatus) {
                logErrorCore("Shader link error ${gl.getProgramInfoLog(rendererID!!)}")

                gl.deleteProgram(rendererID!!)
                for (shader in shaderIDs) gl.deleteShader(shader)
            }

            for (shader in shaderIDs) gl.detachShader(rendererID!!, shader).also { gl.deleteShader(shader) }
        }
    }

    override fun close() = gl.deleteProgram(rendererID!!)

    override fun bind() = gl.useProgram(rendererID!!)

    override fun unbind() = gl.useProgram(GlProgram.NULL)

    override fun setFloat(name: String, value: Float) {
        uploadUniformF(name, value)
    }

    override fun setFloat2(name: String, value: Vec2) {
        uploadUniformF2(name, value)
    }

    override fun setFloat3(name: String, value: Vec3) {
        uploadUniformF3(name, value)
    }

    override fun setFloat4(name: String, value: Vec4) {
        uploadUniformF4(name, value)
    }

    override fun setMat3(name: String, value: Mat3) {
        uploadUniformM3(name, value)
    }

    override fun setMat4(name: String, value: Mat4) {
        uploadUniformM4(name, value)
    }

    override fun setInt(name: String, value: Int) {
        uploadUniformI(name, value)
    }

    fun uploadUniformF(name: String, value: Float) = gl.uniform(gl.getUniformLocation(rendererID!!, name), value)

    fun uploadUniformF2(name: String, value: Vec2) = gl.uniform(gl.getUniformLocation(rendererID!!, name), value)

    fun uploadUniformF3(name: String, value: Vec3) = gl.uniform(gl.getUniformLocation(rendererID!!, name), value)

    fun uploadUniformF4(name: String, value: Vec4) = gl.uniform(gl.getUniformLocation(rendererID!!, name), value)

    fun uploadUniformM3(name: String, value: Mat3) = gl.uniform(gl.getUniformLocation(rendererID!!, name), value)

    fun uploadUniformM4(name: String, value: Mat4) = gl.uniform(gl.getUniformLocation(rendererID!!, name), value)

    fun uploadUniformI(name: String, value: Int) = gl.uniform(gl.getUniformLocation(rendererID!!, name), value)
}
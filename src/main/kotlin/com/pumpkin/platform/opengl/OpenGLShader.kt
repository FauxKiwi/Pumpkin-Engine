package com.pumpkin.platform.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.renderer.Shader
import glm_.mat3x3.Mat3
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import gln.gl
import org.lwjgl.opengl.GL20C.*
import org.lwjgl.opengl.GL32C.GL_GEOMETRY_SHADER
import org.lwjgl.opengl.GL43C.GL_COMPUTE_SHADER
import java.io.File
import java.io.FileReader
import kotlin.math.max

class OpenGLShader : Shader {
    override val name: String

    private var rendererID = 0 //: GlProgram? = null

    constructor(filepath: String) {
        val source = FileReader(File(ClassLoader.getSystemResource(filepath).toURI())).readText()
        val shaderSources = preProcess(source)
        compile(shaderSources)

        val lastSlash = max(filepath.lastIndexOf('/'), filepath.lastIndexOf('\\'))
        val lastDot = filepath.lastIndexOf('.')
        name = filepath.substring(lastSlash + 1, lastDot)
    }

    constructor(name: String, vertexSrc: String, fragmentSrc: String) {
        compile(hashMapOf(GL_VERTEX_SHADER to vertexSrc, GL_FRAGMENT_SHADER to fragmentSrc))
        this.name = name
    }

    private fun preProcess(source: String): HashMap<Int, String> {
        val shaderSources = HashMap<Int, String>()
        val typeToken = "#type"
        val typeTokenLength = typeToken.length
        var pos = source.indexOf(typeToken)
        while (pos != -1) {
            val eol = source.indexOf('\r', pos)
            if (eol == -1) Debug.exception("Syntax error")
            val begin = pos + typeTokenLength + 1
            val type = source.substring(begin, eol)
            if (shaderTypeFromString(type) == null) Debug.exception("No valid shader type specified")
            var nextLinePos = eol + 2
            while (source[nextLinePos + 1] == '\n') nextLinePos += 2
            pos = source.indexOf(typeToken, nextLinePos)
            shaderSources[shaderTypeFromString(type)!!] =
                source.substring(nextLinePos, if (pos == -1) source.length - 1 else pos - 1).trim()
        }
        return shaderSources
    }

    private fun shaderTypeFromString(string: String): Int /*ShaderType?*/ = when (string) {
        "vertex" -> GL_VERTEX_SHADER //ShaderType.VERTEX_SHADER
        "geometry" -> GL_GEOMETRY_SHADER //ShaderType.GEOMETRY_SHADER
        "fragment" -> GL_FRAGMENT_SHADER //ShaderType.FRAGMENT_SHADER
        "compute" -> GL_COMPUTE_SHADER //ShaderType.COMPUTE_SHADER
        else -> 0
    }

    private fun /*ShaderType.*/ name(shaderType: Int) = when (shaderType) {
        GL_VERTEX_SHADER -> "Vertex"
        GL_GEOMETRY_SHADER -> "Geometry"
        GL_FRAGMENT_SHADER -> "Fragment"
        GL_COMPUTE_SHADER -> "Compute"
        else -> "Unknown"
    }

    private fun compile(sources: HashMap<Int, String>) {
        val program = glCreateProgram() //gl.createProgram()
        val shaderIDs = mutableListOf<Int>()
        for ((shaderType, shaderSource) in sources) {
            val shader = glCreateShader(shaderType) //gl.createShader(shaderType)

            glShaderSource(shader, shaderSource) //gl.shaderSource(shader, shaderSource)
            glCompileShader(shader) //gl.compileShader(shader)

            /*if (!shader.compileStatus) {
                Debug.logErrorCore("Shader (${shaderType.name()}) compile error: ${gl.getShaderInfoLog(shader)}")
                gl.deleteShader(shader)
            } else {
                Debug.logInfoCore("Compiled Shader (${shaderType.name()})")
            }*/ //TODO

            glAttachShader(program, shader)//gl.attachShader(program, shader)
            shaderIDs.add(shader)
        }

        rendererID = program
        glLinkProgram(rendererID) //gl.linkProgram(rendererID!!)

        /*if (!rendererID!!.linkStatus) {
            Debug.logErrorCore("Shader link error ${gl.getProgramInfoLog(rendererID!!)}")

            gl.deleteProgram(rendererID!!)
            for (shader in shaderIDs) gl.deleteShader(shader)
        } else {
            Debug.logInfoCore("Linked Shader")
        }*/ //TODO

        for (shader in shaderIDs) glDetachShader(rendererID, shader).also { glDeleteShader(shader) } //gl.detachShader(rendererID!!, shader).also { gl.deleteShader(shader) }
    }

    override fun close() = glDeleteProgram(rendererID) //gl.deleteProgram(rendererID!!)

    override fun bind() = glUseProgram(rendererID) //gl.useProgram(rendererID!!)

    override fun unbind() = glUseProgram(0) //gl.useProgram(GlProgram.NULL)

    override fun setFloat(name: String, value: Float) {
        glUniform1f(glGetUniformLocation(rendererID, name), value) //gl.uniform(rendererID!![name], value)
    }

    override fun setFloat2(name: String, value: Vec2) {
        glUniform2f(glGetUniformLocation(rendererID, name), value.x, value.y) //gl.uniform(rendererID!![name], value)
    }

    override fun setFloat3(name: String, value: Vec3) {
        glUniform3f(glGetUniformLocation(rendererID, name), value.x, value.y, value.z) //gl.uniform(rendererID!![name], value)
    }

    override fun setFloat4(name: String, value: Vec4) {
        glUniform4f(glGetUniformLocation(rendererID, name), value.x, value.y, value.z, value.w) //gl.uniform(rendererID!![name], value)
    }

    override fun setMat3(name: String, value: Mat3) {
        gl.uniform(glGetUniformLocation(rendererID, name), value) //gl.uniform(rendererID!![name], value) //TODO
    }

    override fun setMat4(name: String, value: Mat4) {
        gl.uniform(glGetUniformLocation(rendererID, name), value) //gl.uniform(rendererID!![name], value) //TODO
    }

    override fun setInt(name: String, value: Int) {
        glUniform1i(glGetUniformLocation(rendererID, name), value) //gl.uniform(rendererID!![name], value)
    }

    override fun setIntArray(name: String, value: IntArray) {
        for (i in value.indices) {
            glUniform1i(glGetUniformLocation(rendererID, name) + i, value[i]) //gl.uniform(rendererID!![name] + i, value[i])
        }
    }
}
package com.pumpkin.core.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.renderer.Shader
import com.pumpkin.core.toFloatBuffer
import org.lwjgl.opengl.GL20C.*
import org.lwjgl.opengl.GL32C.GL_GEOMETRY_SHADER
import org.lwjgl.opengl.GL43C.GL_COMPUTE_SHADER
import org.lwjgl.system.MemoryStack
import java.io.File
import java.io.FileReader

actual class OpenGLShader : Shader {
    override val name: String

    private var rendererID = 0

    actual constructor(filepath: String) {
        val source = FileReader(File(ClassLoader.getSystemResource(filepath).toURI())).readText()
        val shaderSources = preProcess(source)
        compile(shaderSources)

        val lastSlash = kotlin.math.max(filepath.lastIndexOf('/'), filepath.lastIndexOf('\\'))
        val lastDot = filepath.lastIndexOf('.')
        name = filepath.substring(lastSlash + 1, lastDot)
    }

    actual constructor(name: String, vertexSrc: String, fragmentSrc: String) {
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
            if (shaderTypeFromString(type) == 0) Debug.exception("No valid shader type specified")
            var nextLinePos = eol + 2
            while (source[nextLinePos + 1] == '\n') nextLinePos += 2
            pos = source.indexOf(typeToken, nextLinePos)
            shaderSources[shaderTypeFromString(type)] =
                source.substring(nextLinePos, if (pos == -1) source.length - 1 else pos - 1).trim()
        }
        return shaderSources
    }

    private fun shaderTypeFromString(string: String): Int = when (string) {
        "vertex" -> GL_VERTEX_SHADER
        "geometry" -> GL_GEOMETRY_SHADER
        "fragment" -> GL_FRAGMENT_SHADER
        "compute" -> GL_COMPUTE_SHADER
        else -> 0
    }

    private fun name(shaderType: Int) = when (shaderType) {
        GL_VERTEX_SHADER -> "Vertex"
        GL_GEOMETRY_SHADER -> "Geometry"
        GL_FRAGMENT_SHADER -> "Fragment"
        GL_COMPUTE_SHADER -> "Compute"
        else -> "Unknown"
    }

    private fun compile(sources: HashMap<Int, String>) {
        val program = glCreateProgram()
        val shaderIDs = mutableListOf<Int>()
        for ((shaderType, shaderSource) in sources) {
            val shader = glCreateShader(shaderType)

            glShaderSource(shader, shaderSource)
            glCompileShader(shader)

            MemoryStack.stackPush().use { stack ->
                val isCompiled = stack.mallocInt(1)
                glGetShaderiv(shader, GL_COMPILE_STATUS, isCompiled)
                if (isCompiled.get(0) == GL_FALSE) {
                    Debug.logErrorCore("Shader (${name(shaderType)}) compile error: ${glGetShaderInfoLog(shader)}")
                    glDeleteShader(shader)
                } else {
                    Debug.logInfoCore("Compiled Shader (${name(shaderType)})")
                }
            }

            glAttachShader(program, shader)
            shaderIDs.add(shader)
        }

        rendererID = program
        glLinkProgram(rendererID)

        MemoryStack.stackPush().use { stack ->
            val isLinked = stack.mallocInt(1)
            glGetProgramiv(rendererID, GL_LINK_STATUS, isLinked)
            if (isLinked.get(0) == GL_FALSE) {
                Debug.logErrorCore("Shader link error ${glGetProgramInfoLog(rendererID)}")
                glDeleteProgram(rendererID)
                shaderIDs.forEach { glDeleteShader(it) }
            } else {
                Debug.logInfoCore("Linked Shader")
            }
        }

        for (shader in shaderIDs) glDetachShader(rendererID, shader).also { glDeleteShader(shader) }
    }

    override fun close() = glDeleteProgram(rendererID)

    override fun bind() = glUseProgram(rendererID)

    override fun unbind() = glUseProgram(0)

    override fun setFloat(name: String, value: Float) = glUniform1f(glGetUniformLocation(rendererID, name), value)

    override fun setFloat2(name: String, value: glm.Vec2) = glUniform2f(glGetUniformLocation(rendererID, name), value.x, value.y)

    override fun setFloat3(name: String, value: glm.Vec3) = glUniform3f(glGetUniformLocation(rendererID, name), value.x, value.y, value.z)

    override fun setFloat4(name: String, value: glm.Vec4) = glUniform4f(glGetUniformLocation(rendererID, name), value.x, value.y, value.z, value.w)

    override fun setMat3(name: String, value: glm.Mat3) = MemoryStack.stackPush().use { stack ->
        glUniformMatrix3fv(glGetUniformLocation(rendererID, name), false, value.toFloatBuffer(stack))
    }

    override fun setMat4(name: String, value: glm.Mat4) = MemoryStack.stackPush().use{ stack ->
        glUniformMatrix4fv(glGetUniformLocation(rendererID, name), false, value.toFloatBuffer(stack))
    }

    override fun setInt(name: String, value: Int) = glUniform1i(glGetUniformLocation(rendererID, name), value)

    override fun setIntArray(name: String, value: IntArray) {
        for (i in value.indices) {
            glUniform1i(glGetUniformLocation(rendererID, name) + i, value[i])
        }
    }
}
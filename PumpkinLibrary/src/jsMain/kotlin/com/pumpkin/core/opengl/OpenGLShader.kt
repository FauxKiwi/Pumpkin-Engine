package com.pumpkin.core.opengl

import com.pumpkin.core.Debug
import com.pumpkin.core.renderer.Shader
import com.pumpkin.core.toFloat32Array
import org.khronos.webgl.WebGLProgram
import org.khronos.webgl.WebGLShader
import org.w3c.files.File
import org.w3c.files.FileReader
import org.khronos.webgl.WebGLRenderingContext as GL

actual class OpenGLShader : Shader {
    override val name: String

    private var rendererID: WebGLProgram? = null

    actual constructor(filepath: String) {
        val source: String = FileReader(File(/*ClassLoader.getSystemResource(filepath).toURI()*/))//.readText()
        val shaderSources = preProcess(source)
        compile(shaderSources)

        val lastSlash = kotlin.math.max(filepath.lastIndexOf('/'), filepath.lastIndexOf('\\'))
        val lastDot = filepath.lastIndexOf('.')
        name = filepath.substring(lastSlash + 1, lastDot)
    }

    actual constructor(name: String, vertexSrc: String, fragmentSrc: String) {
        compile(hashMapOf(GL.VERTEX_SHADER to vertexSrc, GL.FRAGMENT_SHADER to fragmentSrc))
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
        "vertex" -> GL.VERTEX_SHADER
        "geometry" -> 0.also { Debug.logWarnCore("Geometry shaders are not supported in WebGL") } //GL.GEOMETRY_SHADER
        "fragment" -> GL.FRAGMENT_SHADER
        "compute" -> 0.also { Debug.logWarnCore("Compute shaders are not supported in WebGL") } //GL.COMPUTE_SHADER
        else -> 0
    }

    private fun name(shaderType: Int) = when (shaderType) {
        GL.VERTEX_SHADER -> "Vertex"
        //GL.GEOMETRY_SHADER -> "Geometry"
        GL.FRAGMENT_SHADER -> "Fragment"
        //GL.COMPUTE_SHADER -> "Compute"
        else -> "Unknown"
    }

    private fun compile(sources: HashMap<Int, String>) {
        val program = gl.createProgram()
        val shaderIDs = mutableListOf<WebGLShader?>()
        for ((shaderType, shaderSource) in sources) {
            val shader = gl.createShader(shaderType)

            gl.shaderSource(shader, shaderSource)
            gl.compileShader(shader)

            val isCompiled = gl.getShaderParameter(shader, GL.COMPILE_STATUS)
            if (isCompiled == 0) {
                Debug.logErrorCore("Shader (${name(shaderType)}) compile error: ${gl.getShaderInfoLog(shader)}")
                gl.deleteShader(shader)
            } else {
                Debug.logInfoCore("Compiled Shader (${name(shaderType)})")
            }


            gl.attachShader(program, shader)
            shaderIDs.add(shader)
        }

        rendererID = program
        gl.linkProgram(rendererID)

        val isLinked = gl.getProgramParameter(rendererID, GL.LINK_STATUS)
        if (isLinked == 0) {
            Debug.logErrorCore("Shader link error ${gl.getProgramInfoLog(rendererID)}")
            gl.deleteProgram(rendererID)
            shaderIDs.forEach { gl.deleteShader(it) }
        } else {
            Debug.logInfoCore("Linked Shader")
        }

        for (shader in shaderIDs) gl.detachShader(rendererID, shader).also { gl.deleteShader(shader) }
    }

    override fun close() = gl.deleteProgram(rendererID)

    override fun bind() = gl.useProgram(rendererID)

    override fun unbind() = gl.useProgram(null)

    override fun setFloat(name: String, value: Float) = gl.uniform1f(gl.getUniformLocation(rendererID, name), value)

    override fun setFloat2(name: String, value: glm.Vec2) = gl.uniform2f(gl.getUniformLocation(rendererID, name), value.x, value.y)

    override fun setFloat3(name: String, value: glm.Vec3) = gl.uniform3f(gl.getUniformLocation(rendererID, name), value.x, value.y, value.z)

    override fun setFloat4(name: String, value: glm.Vec4) = gl.uniform4f(gl.getUniformLocation(rendererID, name), value.x, value.y, value.z, value.w)

    override fun setMat3(name: String, value: glm.Mat3) = gl.uniformMatrix3fv(gl.getUniformLocation(rendererID, name), false, value.toFloat32Array())

    override fun setMat4(name: String, value: glm.Mat4) = gl.uniformMatrix4fv(gl.getUniformLocation(rendererID, name), false, value.toFloat32Array())

    override fun setInt(name: String, value: Int) = gl.uniform1i(gl.getUniformLocation(rendererID, name), value)

    override fun setIntArray(name: String, value: IntArray) {
        for (i in value.indices) {
            gl.uniform1i(gl.getUniformLocation(rendererID, name), value[i]) //TODO
        }
    }
}
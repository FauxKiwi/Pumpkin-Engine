package com.pumpkin.platform.opengl

import com.pumpkin.core.logErrorCore
import com.pumpkin.core.render.Shader
import glm_.mat4x4.Mat4
import gln.ShaderType
import gln.gl
import gln.identifiers.GlProgram

class OpenGLShader(vertexSrc: String, fragmentSrc: String) : Shader {
    private val rendererID: GlProgram

    init {
        val vertexShader = gl.createShader(ShaderType.VERTEX_SHADER)

        gl.shaderSource(vertexShader, vertexSrc)

        gl.compileShader(vertexShader)


        if (!vertexShader.compileStatus)
        {
            logErrorCore("Vertex shader compile error: ${gl.getShaderInfoLog(vertexShader)}")

            gl.deleteShader(vertexShader)
        }

        val fragmentShader = gl.createShader(ShaderType.FRAGMENT_SHADER)

        gl.shaderSource(fragmentShader, fragmentSrc)

        gl.compileShader(fragmentShader)

        if (!fragmentShader.compileStatus)
        {
            logErrorCore("Fragment shader compile error: ${gl.getShaderInfoLog(fragmentShader)}")

            gl.deleteShader(fragmentShader)
            gl.deleteShader(vertexShader)
        }

        rendererID = gl.createProgram()

        gl.attachShader(rendererID, vertexShader)
        gl.attachShader(rendererID, fragmentShader)

        gl.linkProgram(rendererID);

        if (!rendererID.linkStatus)
        {
            logErrorCore("Shader link error ${gl.getProgramInfoLog(rendererID)}")

            gl.deleteProgram(rendererID);
            gl.deleteShader(vertexShader);
            gl.deleteShader(fragmentShader);
        }

        gl.detachShader(rendererID, vertexShader);
        gl.detachShader(rendererID, fragmentShader);
    }

    protected fun finalize() {
        gl.deleteProgram(rendererID)
    }

    override fun bind() {
        gl.useProgram(rendererID)
    }

    override fun unbind() {
        gl.useProgram(GlProgram.NULL)
    }

    override fun uploadUniformMat4(name: String, matrix: Mat4) {
        val location = gl.getUniformLocation(rendererID, name)
        gl.uniform(location, matrix)
    }
}
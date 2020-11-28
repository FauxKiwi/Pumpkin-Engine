package com.pumpkin.opengl

import com.pumpkin.logErrorCore
import com.pumpkin.render.Shader
import gln.ShaderType
import gln.gl
import gln.identifiers.GlProgram
import org.lwjgl.opengl.GL20

class OpenGLShader(vertexSrc: String, fragmentSrc: String) : Shader {
    private val rendererID: GlProgram

    init {
        val vertexShader = gl.createShader(ShaderType.VERTEX_SHADER)

        gl.shaderSource(vertexShader, vertexSrc)

        gl.compileShader(vertexShader)


        if (!vertexShader.compileStatus)
        {
            logErrorCore("Shader compile error: ${gl.getShaderInfoLog(vertexShader)}")

            gl.deleteShader(vertexShader)
        }

        val fragmentShader = gl.createShader(ShaderType.FRAGMENT_SHADER)

        gl.shaderSource(fragmentShader, fragmentSrc)

        gl.compileShader(fragmentShader)

        if (!fragmentShader.compileStatus)
        {
            logErrorCore("Shader compile error: ${gl.getShaderInfoLog(fragmentShader)}")

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
}
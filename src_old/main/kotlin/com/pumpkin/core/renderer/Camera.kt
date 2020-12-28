package com.pumpkin.core.renderer

import glm.Mat4

open class Camera {
    var projection: Mat4 = Mat4()
}

enum class ProjectionType {
    Orthographic, Perspective;

    companion object {
        val projectionTypes = arrayOf("Orthographic", "Perspective")
    }
}
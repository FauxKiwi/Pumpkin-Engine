package com.pumpkin.core.renderer

import glm_.mat4x4.Mat4

open class Camera {
    var projection: Mat4 = Mat4.identity
}

enum class ProjectionType {
    Orthographic, Perspective;

    companion object {
        val projectionTypes = arrayOf("Orthographic", "Perspective")
    }
}
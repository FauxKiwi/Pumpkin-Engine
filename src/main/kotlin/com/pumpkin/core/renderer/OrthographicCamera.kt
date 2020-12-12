package com.pumpkin.core.renderer

import com.pumpkin.core.stack
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3

class OrthographicCamera(left: Float, right: Float, bottom: Float, top: Float) {
    private var projectionMatrix: Mat4 = glm.ortho(left, right, bottom, top, -1f, 1f)
    private var viewMatrix: Mat4 = Mat4.identity
    var viewProjectionMatrix: Mat4 = projectionMatrix * viewMatrix
    var position: Vec3 = Vec3()
        set(value) {
            field = value
            recalculateViewMatrix()
        }
    var rotation: Float = 0f
        set(value) {
            field = value
            recalculateViewMatrix()
        }

    fun setProjection(left: Float, right: Float, bottom: Float, top: Float) {
        projectionMatrix = glm.ortho(left, right, bottom, top, -1f, 1f)
    }

    private fun recalculateViewMatrix() {
        stack {
            viewMatrix = glm.rotate(Mat4.identity, -glm.radians(rotation), Vec3(0, 0, 1)) * glm.translate(Mat4.identity, -position)
            viewProjectionMatrix = projectionMatrix * viewMatrix
        }
    }
}
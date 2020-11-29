package com.pumpkin.core.render

import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3

class OrthographicCamera(left: Float, right: Float, bottom: Float, top: Float) {
    val projectionMatrix: Mat4 = glm.ortho(left, right, bottom, top, -1f, 1f)
    var viewMatrix: Mat4 = Mat4.identity
    var viewProjectionMatrix: Mat4 = projectionMatrix * viewMatrix

    var position: Vec3 = Vec3(0f)
        set(value) {
            field = value
            recalculateViewMatrix()
        }
    var rotation: Float = 0f
        set(value) {
            field = value
            recalculateViewMatrix()
        }

    private fun recalculateViewMatrix() {
        viewMatrix = glm.translate(Mat4.identity, -position) * glm.rotate(Mat4.identity, -rotation, Vec3(0, 0, 1))
        viewProjectionMatrix = projectionMatrix * viewMatrix
    }
}
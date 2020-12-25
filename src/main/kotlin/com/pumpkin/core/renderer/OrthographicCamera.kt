package com.pumpkin.core.renderer

import glm.Mat4
import glm.Vec3

class OrthographicCamera(left: Float, right: Float, bottom: Float, top: Float) {
    private var projectionMatrix: Mat4 = glm.ortho(left, right, bottom, top, -1f, 1f)
    private var viewMatrix: Mat4 = Mat4()
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
        viewMatrix = glm.rotate(-glm.radians(rotation), Vec3(0f, 0f, 1f)) * glm.translate(-position)
        viewProjectionMatrix = projectionMatrix * viewMatrix
    }
}
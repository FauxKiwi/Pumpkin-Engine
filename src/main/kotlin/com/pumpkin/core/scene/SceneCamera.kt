package com.pumpkin.core.scene

import com.pumpkin.core.renderer.Camera
import glm_.glm

class SceneCamera : Camera() {
    var othographicSize = 10f
        set(value) {
            field = value
            recalculateProjection()
        }
    private var orthographicNear = -1f
    private var orthographicFar = 1f
    private var aspectRatio = 0f

    init {
        recalculateProjection()
    }

    fun setOrthographic(size: Float, nearClip: Float, farClip: Float) {
        orthographicNear = nearClip
        orthographicFar = farClip
        othographicSize = size
    }

    fun setViewportSize(width: Int, height: Int) {
        setViewportSize(width.toFloat(), height.toFloat())
    }

    fun setViewportSize(width: Float, height: Float) {
        aspectRatio = width / height
        recalculateProjection()
    }

    private fun recalculateProjection() {
        projection = glm.ortho(
            -othographicSize * aspectRatio * 0.5f,
            othographicSize * aspectRatio * 0.5f,
            -othographicSize * 0.5f,
            othographicSize * 0.5f,
            orthographicNear,
            orthographicFar
        )
    }
}
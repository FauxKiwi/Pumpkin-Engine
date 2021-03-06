package com.pumpkin.core.scene

import com.pumpkin.core.renderer.Camera
import com.pumpkin.core.renderer.ProjectionType
import glm.Vec4

class SceneCamera : Camera() {
    var projectionType = ProjectionType.Orthographic
        set(value) { field = value; recalculateProjection() }
    var projectionTypePtr: Int
        get() = projectionType.ordinal
        set(value) { projectionType = ProjectionType.values()[value] }

    internal var _orthographicSize = 10f
    var orthographicSize
        get() = _orthographicSize
        set(value) { _orthographicSize = value; recalculateProjection() }
    internal var _orthographicNear = -1f
    var orthographicNear
        get() = _orthographicNear
        set(value) { _orthographicNear = value; recalculateProjection() }
    internal var _orthographicFar = 1f
    var orthographicFar
        get() = _orthographicFar
        set(value) { _orthographicFar = value; recalculateProjection() }

    internal var _perspectiveFov = 45f
    var perspectiveFov
        get() = _perspectiveFov
        set(value) { _perspectiveFov = value; recalculateProjection() }
    internal var _perspectiveNear = 0.01f
    var perspectiveNear
        get() = _perspectiveNear
        set(value) { _perspectiveNear = value; recalculateProjection() }
    internal var _perspectiveFar = 1000f
    var perspectiveFar
        get() = _perspectiveFar
        set(value) { _perspectiveFar = value; recalculateProjection() }

    var aspectRatio = 0f

    val clearColor = Vec4(0.25f, 0.3f, 0.655f, 1.0f)

    init {
        recalculateProjection()
    }

    fun setOrthographic(size: Float, nearClip: Float, farClip: Float) {
        _orthographicNear = nearClip
        _orthographicFar = farClip
        _orthographicSize = size
        projectionType = ProjectionType.Orthographic
    }

    fun setPerspective(verticalFovDeg: Float, nearClip: Float, farClip: Float) {
        _perspectiveNear = nearClip
        _perspectiveFar = farClip
        _perspectiveFov = verticalFovDeg
        projectionType = ProjectionType.Perspective
    }

    fun setViewportSize(width: Int, height: Int) {
        setViewportSize(width.toFloat(), height.toFloat())
    }

    fun setViewportSize(width: Float, height: Float) {
        aspectRatio = width / height
        recalculateProjection()
    }

    private fun recalculateProjection() {
        projection =
        if (projectionType == ProjectionType.Orthographic) glm.ortho(
            -_orthographicSize * aspectRatio * 0.5f,
            _orthographicSize * aspectRatio * 0.5f,
            -_orthographicSize * 0.5f,
            _orthographicSize * 0.5f,
            _orthographicNear,
            _orthographicFar
        ) else glm.perspective(
            glm.radians(perspectiveFov),
            aspectRatio,
            perspectiveNear,
            perspectiveFar
        )
    }
}
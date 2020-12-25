package com.pumpkin.core

import com.pumpkin.core.event.Event
import com.pumpkin.core.event.EventDispatcher
import com.pumpkin.core.event.MouseScrolledEvent
import com.pumpkin.core.input.Input
import com.pumpkin.core.input.KeyCode
import com.pumpkin.core.input.MouseButton
import com.pumpkin.core.renderer.Camera
import com.pumpkin.core.renderer.ProjectionType
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.quat.Quat
import glm_.vec2.Vec2
import glm_.vec3.Vec3

class EditorCamera(
    var fov: Float = 45f,
    private var aspectRatio: Float = 1.778f,
    private val nearClip: Float = 0.1f,
    private val farClip: Float = 1000.0f,
) : Camera() {
    var projectionType = ProjectionType.Perspective
        set(value) { field = value; updateProjection() }
    var sceneProjection = 3
        set(value) {
            if (field != value) {
                if (value == 2) {
                    last3dPos = Vec3(_position)
                    last3dDist = distance
                    last3dYaw = _yaw; last3dPitch = _pitch

                    _position.z = 0f
                    _yaw = 0f; _pitch = 0f
                } else if (value == 3) {
                    _position = last3dPos
                    distance = last3dDist
                    _yaw = last3dYaw; _pitch = last3dPitch
                }
                field = value
                updateView(); updateProjection()
            }
        }
    private var last3dPos = Vec3()
    private var last3dDist = 10f
    private var last3dYaw = 0f; private var last3dPitch = 0f

    var orthoSize = 10f; set(value) { field = value; updateProjection() }

    private val rotationSpeed = 0.8f
    private val zoomSpeed get(): Float {
        var distance = distance * 0.2f
        distance = kotlin.math.max(distance, 0.0f)
        var speed = distance * distance
        speed = kotlin.math.min(speed, 100.0f) // max speed = 100
        return speed
    }
    private val panSpeed: Pair<Float, Float> get() {
        val x = kotlin.math.min(viewportWidth / 1000.0f, 2.4f) // max = 2.4f
        val xFactor = 0.0366f * (x * x) - 0.1778f * x + 0.3021f

        val y = kotlin.math.min(viewportHeight / 1000.0f, 2.4f) // max = 2.4f
        val yFactor = 0.0366f * (y * y) - 0.1778f * y + 0.3021f

        return xFactor to yFactor
    }
    val upDirection: Vec3 get() = glm.rotate(orientation, Vec3(0.0f, 1.0f, 0.0f))
    val rightDirection: Vec3 get() = glm.rotate(orientation, Vec3(1.0f, 0.0f, 0.0f))
    val forwardDirection: Vec3 get() = glm.rotate(orientation, Vec3(0.0f, 0.0f, -1.0f))
    private val calculatePosition: Vec3 get() = focalPoint - forwardDirection * distance
    val orientation: Quat get() = Quat(Vec3(-_pitch, -_yaw, 0.0f))

    private lateinit var viewMatrix: Mat4
    val view get() = viewMatrix
    val viewProjection get() = projection * viewMatrix
    private var _position = Vec3()
    val position get() = _position
    private val focalPoint = Vec3()

    private var initialMousePosition = Vec2()

    var distance = 10.0f
    private var _pitch = 0.0f
    val pitch get() = _pitch
    private var _yaw = 0.0f
    val yaw get() = _yaw

    var viewportWidth = 1280f
    var viewportHeight = 720f
    fun setViewportSize(width: Float, height: Float) { viewportWidth = width; viewportHeight = height; updateProjection() }

    init {
        projection = glm.perspective(glm.radians(fov), aspectRatio, nearClip, farClip)
        updateView()
    }

    fun updateProjection() {
        aspectRatio = viewportWidth / viewportHeight
        projection = if (projectionType == ProjectionType.Perspective)
            glm.perspective(glm.radians(fov), aspectRatio, nearClip, farClip) else
                glm.ortho(orthoSize * aspectRatio * -0.5f, orthoSize * aspectRatio * 0.5f,
                    orthoSize * -0.5f, orthoSize * 0.5f, nearClip, farClip)
    }

    fun updateView() {
        // m_Yaw = m_Pitch = 0.0f; // Lock the camera's rotation
        _position = calculatePosition

        val orientation = orientation
        viewMatrix = glm.translate(Mat4.identity, _position) * glm.toMat4(orientation)
        viewMatrix = glm.inverse(viewMatrix)
    }

    fun onUpdate(ts: Timestep) {
        if (Input.isKeyPressed(KeyCode.LEFT_ALT)) {
            val mouse = Vec2(Input.getMouseX(), Input.getMouseY())
            val delta = (mouse - initialMousePosition) * 0.003f
            initialMousePosition = mouse

            if (Input.isMouseButtonPressed(MouseButton.MIDDLE))
                mousePan(delta)
            else if (Input.isMouseButtonPressed(MouseButton.LEFT))
                mouseRotate(delta)
            else if (Input.isMouseButtonPressed(MouseButton.RIGHT))
                mouseZoom(delta.y)
        }

        updateView()
    }

    fun onEvent(e: Event) {
        val dispatcher = EventDispatcher(e)
        dispatcher.dispatch(::onMouseScroll)
    }

    private fun onMouseScroll(e: MouseScrolledEvent): Boolean {
        val delta = e.yOffset * 0.1f
        mouseZoom(delta)
        updateView()
        return false
    }

    private fun mousePan(delta: Vec2) {
        val (xSpeed, ySpeed) = panSpeed
        focalPoint += -rightDirection * delta.x * xSpeed * distance
        focalPoint += upDirection * delta.y * ySpeed * distance
    }

    private fun mouseRotate(delta: Vec2) {
        if (sceneProjection == 3) {
            val yawSign = if (upDirection.y < 0) -1.0f else 1.0f
            _yaw += yawSign * delta.x * rotationSpeed
            _pitch += delta.y * rotationSpeed
        }
    }

    private fun mouseZoom(delta: Float) {
        distance -= delta * zoomSpeed
        if (distance < 1.0f) {
            focalPoint += forwardDirection
            distance = 1.0f
        }
    }
}
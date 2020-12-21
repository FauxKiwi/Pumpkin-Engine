package com.pumpkin.core

import com.pumpkin.core.event.Event
import com.pumpkin.core.event.EventDispatcher
import com.pumpkin.core.event.MouseScrolledEvent
import com.pumpkin.core.input.Input
import com.pumpkin.core.input.KeyCode
import com.pumpkin.core.input.MouseButton
import com.pumpkin.core.renderer.Camera
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
    private var _position = Vec3(0.0f, 0.0f, 0.0f)
    val position get() = _position
    private val focalPoint = Vec3(0.0f, 0.0f, 0.0f)

    private var initialMousePosition = Vec2(0.0f, 0.0f)

    var distance = 10.0f
    private var _pitch = 0.0f
    val pitch get() = _pitch
    private var _yaw = 0.0f
    val yaw get() = _yaw

    var viewportWidth = 1280f
    var viewportHeight = 720f
    fun setViewportSize(width: Float, height: Float) { viewportHeight = width; viewportHeight = height; updateProjection() }

    init {
        projection = glm.perspective(glm.radians(fov), aspectRatio, nearClip, farClip)
        updateView()
    }

    fun updateProjection()
    {
        aspectRatio = viewportWidth / viewportHeight
        projection = glm.perspective(glm.radians(fov), aspectRatio, nearClip, farClip)
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

    fun onEvent(e: Event)
    {
        val dispatcher = EventDispatcher(e)
        dispatcher.dispatch(::onMouseScroll)
    }

    private fun onMouseScroll(e: MouseScrolledEvent): Boolean
    {
        val delta = e.yOffset * 0.1f
        mouseZoom(delta)
        updateView()
        return false
    }

    private fun mousePan(delta: Vec2)
    {
        val (xSpeed, ySpeed) = panSpeed
        focalPoint += -rightDirection * delta.x * xSpeed * distance
        focalPoint += upDirection * delta.y * ySpeed * distance
    }

    private fun mouseRotate(delta: Vec2)
    {
        val yawSign = if (upDirection.y < 0) -1.0f else 1.0f
        _yaw += yawSign * delta.x * rotationSpeed
        _pitch += delta.y * rotationSpeed
    }

    private fun mouseZoom(delta: Float)
    {
        distance -= delta * zoomSpeed
        if (distance < 1.0f)
        {
            focalPoint += forwardDirection
            distance = 1.0f;
        }
    }
}
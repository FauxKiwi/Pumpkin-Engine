package com.pumpkin.core

import com.pumpkin.core.event.Event
import com.pumpkin.core.event.EventDispatcher
import com.pumpkin.core.event.MouseScrolledEvent
import com.pumpkin.core.event.WindowResizeEvent
import com.pumpkin.core.input.*
import com.pumpkin.core.renderer.OrthographicCamera
import glm_.vec3.Vec3
import kotlin.math.max

class OrthographicCameraController(aspectRatio: Float, var rotate: Boolean) {
    var aspectRatio = aspectRatio
        set(value) {
            camera.setProjection(-aspectRatio * zoomLevel, aspectRatio * zoomLevel, -zoomLevel, zoomLevel)
            field = value
        }
    var zoomLevel = 1f
        set(value) {
            camera.setProjection(-aspectRatio * zoomLevel, aspectRatio * zoomLevel, -zoomLevel, zoomLevel)
            field = value
        }
    val camera = OrthographicCamera(-aspectRatio * zoomLevel, aspectRatio * zoomLevel, -zoomLevel, zoomLevel)
    val cameraPosition = Vec3(0f)
    var cameraRotation = 0f
    var translationSpeed = 30f
    var rotationSpeed = 180f

    fun onUpdate(ts: Timestep) {
        if (Input.isKeyPressed(KeyCode.A))
            cameraPosition -= Vec3(0.05f, 0f, 0f) * translationSpeed * zoomLevel * ts
        else if (Input.isKeyPressed(KeyCode.D))
            cameraPosition += Vec3(0.05f, 0f, 0f) * translationSpeed * zoomLevel * ts

        if (Input.isKeyPressed(KeyCode.S))
            cameraPosition -= Vec3(0f, 0.05f, 0f) * translationSpeed * zoomLevel * ts
        else if (Input.isKeyPressed(KeyCode.W))
            cameraPosition += Vec3(0f, 0.05f, 0f) * translationSpeed * zoomLevel * ts

        if (rotate) {
            if (Input.isKeyPressed(KeyCode.Q))
                cameraRotation -= rotationSpeed * ts
            else if (Input.isKeyPressed(KeyCode.E))
                cameraRotation += rotationSpeed * ts

            camera.rotation = cameraRotation
        }

        camera.position = cameraPosition
    }

    fun onEvent(event: Event) {
        val dispatcher = EventDispatcher(event)
        dispatcher.dispatch(::onMouseScrolled)
        dispatcher.dispatch(::onWindowResized)
    }

    fun onResize(width: Float, height: Float) {
        aspectRatio = width / height
        camera.setProjection(-aspectRatio * zoomLevel, aspectRatio * zoomLevel, -zoomLevel, zoomLevel)
    }

    private fun onMouseScrolled(event: MouseScrolledEvent): Boolean {
        val zoomLevel = this.zoomLevel - event.yOffset * 0.25f
        this.zoomLevel = max(zoomLevel, 0.25f)
        return false
    }

    private fun onWindowResized(event: WindowResizeEvent): Boolean {
        onResize(event.width.toFloat(), (event.height.toFloat()))
        return false
    }
}
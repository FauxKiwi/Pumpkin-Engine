package com.pumpkin.core.scene

import com.pumpkin.core.Referencable
import com.pumpkin.core.Timestep
import com.pumpkin.core.renderer.Camera
import com.pumpkin.core.renderer.Renderer2D
import com.pumpkin.ecs.Registry
import glm_.mat4x4.Mat4

class Scene : Referencable() {
    val registry: Registry = Registry()

    var viewportWidth: Int = 0
    var viewportHeight: Int = 0

    override fun destruct() {

    }

    fun createEntity(name: String? = null): Entity {
        val entity = Entity(registry.create(), this)
        entity.addComponent<TransformComponent>(floatArrayOf(0f, 0f, 0f, 1f, 1f, 0f))
        entity.addComponent<TagComponent>(name ?: "Entity")
        return entity
    }

    fun onUpdate(ts: Timestep) {
        run {
            registry.view<NativeScriptComponent>().forEach { (entity, nsc) ->
                if (nsc.i == null) {
                    nsc.instantiateScript(Entity(entity, this))
                    nsc.instance.onCreate()
                }
                nsc.instance.onUpdate(ts)
            }
        }
        var mainCamera: Camera? = null
        var cameraTransform: Mat4? = null
        val cameraGroup = registry.group<TransformComponent, CameraComponent>()
        for (entity in cameraGroup) {
            val (transform, camera) = cameraGroup.get(entity)
            if (camera.primary) {
                mainCamera = camera.camera
                cameraTransform = transform.transform
                break
            }
        }
        if (mainCamera != null) {
            Renderer2D.beginScene(mainCamera, cameraTransform!!)
            val renderingGroup = registry.group<TransformComponent, SpriteRendererComponent>()
            for (entity in renderingGroup) {
                val (transform, sprite) = renderingGroup.get(entity)
                Renderer2D.drawQuad(transform.position, transform.scale, transform.rotation, sprite.color)
            }
            Renderer2D.endScene()
        }
    }

    fun onViewportResize(width: Int, height: Int) {
        viewportWidth = width
        viewportHeight = height

        val view = registry.view<CameraComponent>()
        for (entity in view) {
            val cameraComponent = view.get(entity)
            if (!cameraComponent.fixedAspectRatio) {
                cameraComponent.camera.setViewportSize(width, height)
            }
        }
    }
}
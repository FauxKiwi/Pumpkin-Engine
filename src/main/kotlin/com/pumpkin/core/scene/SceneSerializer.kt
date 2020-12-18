package com.pumpkin.core.scene

import kotlinx.serialization.json.*
import com.pumpkin.ecs.Entity as EnTT

class SceneSerializer(private val scene: Scene) {

    private fun serializeEntity(entity: EnTT): JsonObject = buildJsonObject {
        put("Entity", 198932095031L)
        if (scene.registry.has<TagComponent>(entity)) {
            putJsonObject("TagComponent") {
                put("Tag", scene.registry.get<TagComponent>(entity).trimTag)
            }
        }
        if (scene.registry.has<TransformComponent>(entity)) {
            putJsonObject("TransformComponent") {
                putJsonArray("Transform") {
                    scene.registry.get<TransformComponent>(entity).t.forEach { add(it) }
                }
            }
        }
        if (scene.registry.has<CameraComponent>(entity)) {
            putJsonObject("CameraComponent") {
                val cameraComponent = scene.registry.get<CameraComponent>(entity)
                putJsonObject("Camera") {
                    val camera = cameraComponent.camera
                    put("ProjectionType", camera.projectionTypePtr)
                    put("OrthographicSize", camera.orthographicSize)
                    put("OrthographicNear", camera.orthographicNear)
                    put("OrthographicFar", camera.orthographicFar)
                    put("PerspectiveFov", camera.perspectiveFov)
                    put("PerspectiveNear", camera.perspectiveNear)
                    put("PerspectiveFar", camera.perspectiveFar)
                    put("AspectRatio", camera.aspectRatio)
                    putJsonArray("ClearColor") {
                        add(camera.clearColor.r)
                        add(camera.clearColor.g)
                        add(camera.clearColor.b)
                    }
                }
                put("Primary", cameraComponent.primary)
                put("FixedAspectRatio", cameraComponent.fixedAspectRatio)
            }
        }
        if (scene.registry.has<SpriteRendererComponent>(entity)) {
            putJsonObject("SpriteRendererComponent") {
                putJsonArray("Color") {
                    scene.registry.get<SpriteRendererComponent>(entity).c.forEach { add(it) }
                }
            }
        }
    }

    fun serialize(absoluteFilepath: String) {
        val jsonObject = buildJsonObject {
            put("Scene", JsonPrimitive("Untitled"))
            putJsonArray("Entities") {
                for (entity in scene.registry.entities) {
                    add(serializeEntity(entity))
                }
            }
        }
        val jsonString = jsonObject.toString()
        println(jsonString)
    }
}
package com.pumpkin.core.scene

import com.pumpkin.core.renderer.ProjectionType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import com.pumpkin.ecs.Entity as EnTT

private val jsonFormat = Json { prettyPrint = true }

class SceneSerializer(var scene: Scene) {

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
        val jsonString = jsonFormat.encodeToString(jsonObject)
        val file = File(absoluteFilepath)
        if (!file.exists()) file.createNewFile()
        val fileWriter = FileWriter(file)
        fileWriter.use {
            it.write(jsonString)
        }
    }

    private fun deserializeEntity(entityObject: JsonObject) {
        val name = if (entityObject.containsKey("TagComponent"))
            entityObject["TagComponent"]!!.jsonObject["Tag"]!!.jsonPrimitive.content
        else "Entity"
        val entity = scene.createEntity(name)
        if (entityObject.containsKey("TransformComponent")) {
            for ((i, f) in entityObject["TransformComponent"]!!.jsonObject["Transform"]!!.jsonArray.withIndex()) {
                entity.getComponent<TransformComponent>().t[i] = f.jsonPrimitive.float
            }
        } else entity.removeComponent<TransformComponent>()
        if (entityObject.containsKey("CameraComponent")) {
            val cameraObject = entityObject["CameraComponent"]!!.jsonObject
            val sceneCameraObject = cameraObject["Camera"]!!.jsonObject
            entity.addComponent<CameraComponent>(SceneCamera().apply {
                _orthographicSize = sceneCameraObject["OrthographicSize"]!!.jsonPrimitive.float
                _orthographicNear = sceneCameraObject["OrthographicNear"]!!.jsonPrimitive.float
                _orthographicFar = sceneCameraObject["OrthographicFar"]!!.jsonPrimitive.float
                _perspectiveFov = sceneCameraObject["PerspectiveFov"]!!.jsonPrimitive.float
                _perspectiveNear = sceneCameraObject["PerspectiveNear"]!!.jsonPrimitive.float
                _perspectiveFar = sceneCameraObject["PerspectiveFar"]!!.jsonPrimitive.float
                aspectRatio = sceneCameraObject["AspectRatio"]!!.jsonPrimitive.float
                val clearColorArray = sceneCameraObject["ClearColor"]!!.jsonArray
                clearColor.apply {
                    x = clearColorArray[0].jsonPrimitive.float
                    y = clearColorArray[1].jsonPrimitive.float
                    z = clearColorArray[2].jsonPrimitive.float
                }
                projectionType = ProjectionType.values()[sceneCameraObject["ProjectionType"]!!.jsonPrimitive.int]
            })
            entity.getComponent<CameraComponent>().apply {
                primary = cameraObject["Primary"]!!.jsonPrimitive.boolean
                fixedAspectRatio = cameraObject["FixedAspectRatio"]!!.jsonPrimitive.boolean
            }
        }
        if (entityObject.containsKey("SpriteRendererComponent")) {
            val colorArray = entityObject["SpriteRendererComponent"]!!.jsonObject["Color"]!!.jsonArray
            entity.addComponent<SpriteRendererComponent>(floatArrayOf(
                colorArray[0].jsonPrimitive.float,
                colorArray[1].jsonPrimitive.float,
                colorArray[2].jsonPrimitive.float,
                colorArray[3].jsonPrimitive.float
            ))
        }
    }

    fun deserialize(absoluteFilepath: String) {
        val file = File(absoluteFilepath)
        if (!file.exists()) return
        val fileReader = FileReader(file)
        val text: String
        fileReader.use {
            text = it.readText()
        }
        val jsonObject = jsonFormat.parseToJsonElement(text).jsonObject
        val entities = jsonObject["Entities"]?.jsonArray ?: return
        for (entity in entities) {
            deserializeEntity(entity.jsonObject)
        }
    }
}
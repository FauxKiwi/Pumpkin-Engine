package com.pumpkin.editor

import com.pumpkin.core.Application
import com.pumpkin.core.EditorCamera
import com.pumpkin.core.Timestep
import com.pumpkin.core.renderer.Renderer2D
import com.pumpkin.core.renderer.RendererCommand
import com.pumpkin.core.scene.*
import com.pumpkin.editor.imgui.ImGuiLayer
import com.pumpkin.editor.settings.Settings
import glm.Vec4

fun Scene.onUpdateEditor(ts: Timestep, camera: EditorCamera) {
    RendererCommand.setClearColor(Settings.editorCameraClearColor)
    RendererCommand.clear()
    Renderer2D.beginScene(camera)
    val group = registry.group<TransformComponent, SpriteRendererComponent>()
    for (entity in group) {
        val (transform, sprite) = group.get(entity)
        if (registry.has<ParentComponent>(entity))
            Renderer2D.drawQuad((transform + registry.get<TransformComponent>(registry.get<ParentComponent>(entity).parent)).transform, sprite.color)
        else
            Renderer2D.drawQuad(transform.transform, sprite.color)
    }
    Renderer2D.endScene()
}

fun Scene.onPreviewCamera(camera: CameraComponent, transform: TransformComponent) {
    RendererCommand.setClearColor(camera.camera.clearColor)
    RendererCommand.clear()
    Renderer2D.beginScene(camera.camera, transform.t)
    val renderingGroup = registry.group<TransformComponent, SpriteRendererComponent>()
    for (entity in renderingGroup) {
        val (entityTransform, entitySprite) = renderingGroup.get(entity)
        Renderer2D.drawQuad(entityTransform.transform, entitySprite.color)
    }
    Renderer2D.endScene()
}

fun Application.getImGuiLayer(): ImGuiLayer = imGuiLayer
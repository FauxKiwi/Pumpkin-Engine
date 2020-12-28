package com.pumpkin

import com.pumpkin.core.Application
import com.pumpkin.core.EditorCamera
import com.pumpkin.core.Timestep
import com.pumpkin.core.renderer.Renderer2D
import com.pumpkin.core.renderer.RendererCommand
import com.pumpkin.imgui.ImGuiLayer
import com.pumpkin.core.scene.Scene
import com.pumpkin.core.scene.SpriteRendererComponent
import com.pumpkin.core.scene.TransformComponent
import com.pumpkin.settings.Settings

fun Scene.onUpdateEditor(ts: Timestep, camera: EditorCamera) {
    RendererCommand.setClearColor(Settings.editorCameraClearColor)
    RendererCommand.clear()
    Renderer2D.beginScene(camera)
    val group = registry.group<TransformComponent, SpriteRendererComponent>()
    for (entity in group) {
        val (transform, sprite) = group.get(entity)
        Renderer2D.drawQuad(transform.transform, sprite.color)
    }
    Renderer2D.endScene()
}

fun Application.getImGuiLayer(): ImGuiLayer = imGuiLayer
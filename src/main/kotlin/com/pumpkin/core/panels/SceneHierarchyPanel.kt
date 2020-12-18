package com.pumpkin.core.panels

import com.pumpkin.core.renderer.ProjectionType
import com.pumpkin.core.scene.*
import com.pumpkin.ecs.Entity
import com.pumpkin.ecs.Registry
import glm_.glm
import imgui.ImGui
import imgui.TreeNodeFlag
import imgui.TreeNodeFlags
import imgui.toByteArray

class SceneHierarchyPanel(var context: Scene) {
    private val registry: Registry get() = context.registry
    private var selectionContext: Entity? = null

    fun onImGuiRender() {
        ImGui.begin("Hierarchy")
        registry.each(::drawEntityNode)
        ImGui.end()

        ImGui.begin("Inspector")
        if (selectionContext != null) {
            drawProperties(selectionContext!!)
        }
        ImGui.end()

        //ImGui.showDemoWindow(booleanArrayOf(true))
    }

    private fun drawEntityNode(entity: Entity) {
        val tag = registry.get<TagComponent>(entity).tag
        val flags: TreeNodeFlags = (if (selectionContext == entity) TreeNodeFlag.Selected.i else 0) or TreeNodeFlag.OpenOnArrow.i
        val opened = ImGui.treeNodeEx(tag, flags)
        if (ImGui.isItemClicked())
            selectionContext = entity
        if (opened) {
            ImGui.treePop()
        }
    }

    private fun drawProperties(entity: Entity) {
        if (
            registry.has<TagComponent>(entity)
        ) {
            val tag = registry.get<TagComponent>(entity)
            ImGui.inputText("Name", tag.byteArray)
        }

        if (
            registry.has<TransformComponent>(entity) &&
            ImGui.treeNodeEx("Transform", TreeNodeFlag.DefaultOpen.i)
        ) {
            val transform = registry.get<TransformComponent>(entity)
            val position = transform.position
            ImGui.dragVec3("Position", position, 0.01f, format = "%.2f")
            transform.position = position
            val scale = transform.scale
            ImGui.dragVec2("Scale", scale, 0.1f, format = "%.1f")
            transform.scale = scale
            val rotation = intArrayOf(glm.degrees(transform.rotation).toInt())
            ImGui.dragInt("Rotation", rotation, 0)
            transform.rotation = glm.radians(rotation[0].toFloat())
            ImGui.treePop()
        }

        if (
            registry.has<CameraComponent>(entity) &&
            ImGui.treeNodeEx("Camera", TreeNodeFlag.DefaultOpen.i)
        ) {
            val camera = registry.get<CameraComponent>(entity)
            ImGui.checkbox("Primary Camera", camera::primary)
            ImGui.combo("Projection Type", camera.camera::projectionTypePtr, ProjectionType.projectionTypes)
            if (camera.camera.projectionType == ProjectionType.Orthographic) {
                ImGui.dragFloat("Orthographic Size", camera.camera::orthographicSize)
                ImGui.dragFloat("Near Clip", camera.camera::orthographicNear)
                ImGui.dragFloat("Far Clip", camera.camera::orthographicFar)
            } else {
                ImGui.dragFloat("Field of View", camera.camera::perspectiveFov)
                ImGui.dragFloat("Near Clip", camera.camera::perspectiveNear)
                ImGui.dragFloat("Far Clip", camera.camera::perspectiveFar)
            }
            ImGui.checkbox("Fixed Aspect Ratio", camera::fixedAspectRatio)
            ImGui.colorEdit3("Clear Color", camera.camera.clearColor)
            ImGui.treePop()
        }

        if (
            registry.has<SpriteRendererComponent>(entity) &&
            ImGui.treeNodeEx("Sprite Renderer", TreeNodeFlag.DefaultOpen.i)
        ) {
            val spriteRenderer = registry.get<SpriteRendererComponent>(entity)
            val color = spriteRenderer.color
            ImGui.colorEdit4("Color", color)
            spriteRenderer.color = color
        }

        if (
            registry.has<NativeScriptComponent>(entity) &&
            ImGui.treeNodeEx("Native Script", TreeNodeFlag.DefaultOpen.i)
        ) {
            val script = registry.get<NativeScriptComponent>(entity)
            ImGui.text(script.instance::class.qualifiedName ?: "")
        }
    }
}
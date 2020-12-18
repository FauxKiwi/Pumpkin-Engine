package com.pumpkin.core.panels

import com.pumpkin.core.renderer.ProjectionType
import com.pumpkin.core.scene.*
import com.pumpkin.ecs.Entity
import com.pumpkin.ecs.Registry
import glm_.glm
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec3.swizzle.xy
import glm_.vec4.Vec4
import imgui.*

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
            //ImGui.dragVec3("Position", position, 0.01f, format = "%.2f")
            drawVec3Control("Position", position)
            transform.position = position
            val scale = Vec3(transform.scale, 1f)
            //ImGui.dragVec2("Scale", scale, 0.1f, format = "%.1f")
            drawVec3Control("Scale", scale)
            transform.scale = scale.xy
            val rotation = Vec3(0f, 0f, transform.rotation)
            //ImGui.dragInt("Rotation", rotation, 0)
            drawVec3Control("Rotation", rotation)
            transform.rotation = rotation.z
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

    private fun drawVec3Control(label: String, values: Vec3, resetValue: Float = 0f, columnWidth: Float = 100f) {
        ImGui.pushID(label)

        ImGui.columns(2)
        ImGui.setColumnWidth(0, columnWidth)
        ImGui.text(label)
        ImGui.nextColumn()

        ImGui.pushMultiItemsWidths(3, ImGui.calcItemWidth())
        ImGui.pushStyleVar(StyleVar.ItemSpacing, Vec2(0, 0))

        val lineHeight = ImGui.font.fontSize + ImGui.style.framePadding.y * 2f
        val buttonSize = Vec2(lineHeight + 3.0f, lineHeight)

        ImGui.pushStyleColor(Col.Button, Vec4(0.8f, 0.1f, 0.15f, 1f))
        ImGui.pushStyleColor(Col.ButtonHovered, Vec4(0.9f, 0.2f, 0.2f, 1f))
        ImGui.pushStyleColor(Col.ButtonActive, Vec4(0.8f, 0.1f, 0.15f, 1f))
        if (ImGui.button("X", buttonSize))
            values.x = resetValue
        ImGui.popStyleColor(3)

        ImGui.sameLine()
        ImGui.dragFloat("##X", values::x, 0.1f, 0.0f, 0.0f, "%.2f")
        ImGui.popItemWidth()
        ImGui.sameLine()

        ImGui.pushStyleColor(Col.Button, Vec4(0.2f, 0.7f, 0.2f, 1f))
        ImGui.pushStyleColor(Col.ButtonHovered, Vec4(0.3f, 0.8f, 0.3f, 1f))
        ImGui.pushStyleColor(Col.ButtonActive, Vec4(0.2f, 0.7f, 0.2f, 1f))
        if (ImGui.button("Y", buttonSize))
            values.y = resetValue
        ImGui.popStyleColor(3)

        ImGui.sameLine()
        ImGui.dragFloat("##Y", values::y, 0.1f, 0.0f, 0.0f, "%.2f")
        ImGui.popItemWidth()
        ImGui.sameLine()

        ImGui.pushStyleColor(Col.Button, Vec4(0.1f, 0.25f, 0.8f, 1f))
        ImGui.pushStyleColor(Col.ButtonHovered, Vec4(0.2f, 0.35f, 0.9f, 1f))
        ImGui.pushStyleColor(Col.ButtonActive, Vec4(0.1f, 0.25f, 0.8f, 1f))
        if (ImGui.button("Z", buttonSize))
            values.y = resetValue
        ImGui.popStyleColor(3)

        ImGui.sameLine()
        ImGui.dragFloat("##Z", values::z, 0.1f, 0.0f, 0.0f, "%.2f")
        ImGui.popItemWidth()

        ImGui.popStyleVar()

        ImGui.columns(1)

        ImGui.popID()
    }
}
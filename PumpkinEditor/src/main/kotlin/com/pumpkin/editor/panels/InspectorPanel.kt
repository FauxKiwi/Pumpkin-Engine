package com.pumpkin.editor.panels

import com.pumpkin.core.renderer.ProjectionType
import com.pumpkin.core.scene.*
import com.pumpkin.editor.imGuiLayer
import com.pumpkin.editor.imgui.*
import entt.Entity
import glm.Vec2
import glm.Vec4
import imgui.ImGui
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiTreeNodeFlags
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImBoolean
import kotlin.reflect.KClass

class InspectorPanel(private val hierarchyPanel: HierarchyPanel) {

    fun onImGuiRender() {
        ImGui.begin("Inspector", ImBoolean(true), ImGuiWindowFlags.NoCollapse)
        if (hierarchyPanel.selectionContext != null) {
            drawComponents(hierarchyPanel.selectionContext!!)
        }
        ImGui.end()
    }

    inline fun <reified T : Any> drawComponent(name: String, entity: Entity, noinline uiFunction: (T) -> Unit) =
        drawComponent(T::class, name, entity, uiFunction)

    fun <T : Any> drawComponent(clazz: KClass<T>, name: String, entity: Entity, uiFunction: (T) -> Unit) {
        val treeNodeFlags = ImGuiTreeNodeFlags.DefaultOpen or ImGuiTreeNodeFlags.Framed or ImGuiTreeNodeFlags.SpanAvailWidth or ImGuiTreeNodeFlags.AllowItemOverlap or ImGuiTreeNodeFlags.FramePadding;
        if (hierarchyPanel.registry.has(clazz, entity)) {
            val component = hierarchyPanel.registry.get(clazz, entity)
            val contentRegionAvailable = Vec2(ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY())

            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 4f, 4f)
            val lineHeight = ImGui.getFont().fontSize + ImGui.getStyle().framePaddingY * 2.0f
            ImGui.separator()
            val open = ImGui.treeNodeEx(name, treeNodeFlags)
            ImGui.popStyleVar()
            ImGui.sameLine(contentRegionAvailable.x - lineHeight * 0.5f)
            ImGui.pushFont(imGuiLayer.fonts[1])
            if (ImGui.button("\uf141", lineHeight, lineHeight).also { ImGui.popFont() }) {
                ImGui.openPopup("ComponentSettings")
            }

            var removeComponent = false
            if (ImGui.beginPopup("ComponentSettings")) {
                if (ImGui.menuItem("Remove component"))
                    removeComponent = true

                ImGui.endPopup()
            }

            if (open) {
                uiFunction(component)
                ImGui.treePop()
            }

            if (removeComponent)
                hierarchyPanel.registry.remove(clazz, entity)
        }
    }

    private fun drawComponents(entity: Entity) {
        if (hierarchyPanel.registry.has<TagComponent>(entity)) {
            val tag = hierarchyPanel.registry.get<TagComponent>(entity)
            editString("##Tag", tag::str)
        }
        ImGui.sameLine()
        ImGui.pushItemWidth(-1f)
        if (ImGui.button("Add Component"))
            ImGui.openPopup("AddComponent")

        if (ImGui.beginPopup("AddComponent")) {
            if (!hierarchyPanel.registry.has<TransformComponent>(hierarchyPanel.selectionContext!!) && ImGui.menuItem("Transform")) {
                hierarchyPanel.registry.insert(hierarchyPanel.selectionContext!!, TransformComponent(floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 1f, 1f, 1f)))
                ImGui.closeCurrentPopup()
            }
            if (!hierarchyPanel.registry.has<CameraComponent>(hierarchyPanel.selectionContext!!) && ImGui.menuItem("Camera")) {
                hierarchyPanel.registry.insert(hierarchyPanel.selectionContext!!, CameraComponent(SceneCamera()))
                ImGui.closeCurrentPopup()
            }
            if (!hierarchyPanel.registry.has<SpriteRendererComponent>(hierarchyPanel.selectionContext!!) && ImGui.menuItem("Sprite Renderer")) {
                hierarchyPanel.registry.insert(hierarchyPanel.selectionContext!!, SpriteRendererComponent(floatArrayOf(1f, 1f, 1f, 1f)))
                ImGui.closeCurrentPopup()
            }
            ImGui.endPopup()
        }
        ImGui.popItemWidth()

        drawComponent<TransformComponent>("Transform", entity) { transform ->
            val position = transform.position
            drawVec3Control("Position", position)
            transform.position = position
            val scale = transform.scale
            drawVec3Control("Scale", scale, resetValue = 1f)
            transform.scale = scale
            val rotation = transform.rotation
            drawVec3Control("Rotation", rotation)
            transform.rotation = rotation
        }

        drawComponent<CameraComponent>("Camera", entity) { camera ->
            editBoolean("Primary Camera", camera::primary)
            editEnum<ProjectionType>("Projection Type", camera.camera::projectionTypePtr)
            if (camera.camera.projectionType == ProjectionType.Orthographic) {
                editFloat("Orthographic Size", camera.camera::orthographicSize)
                editFloat("Near Clip", camera.camera::orthographicNear)
                editFloat("Far Clip", camera.camera::orthographicFar)
            } else {
                editFloat("Field of View", camera.camera::perspectiveFov)
                editFloat("Near Clip", camera.camera::perspectiveNear)
                editFloat("Far Clip", camera.camera::perspectiveFar)
            }
            editBoolean("Fixed Aspect Ratio", camera::fixedAspectRatio)
            editColor3("Clear Color", camera.camera.clearColor)
        }

        drawComponent<SpriteRendererComponent>("Sprite Renderer", entity) { spriteRenderer ->
            val color = spriteRenderer.color
            editColor4("Color", color)
            spriteRenderer.color = color
        }

        drawComponent<NativeScriptComponent>("Native Script", entity) { script ->
            ImGui.text(script.instance::class.qualifiedName ?: "")
        }
    }

    private fun drawVec3Control(label: String, values: glm.Vec3, resetValue: Float = 0f, columnWidth: Float = 100f) {
        val font = imGuiLayer.fonts[0]

        ImGui.pushID(label)

        ImGui.columns(2)
        ImGui.setColumnWidth(0, columnWidth)
        ImGui.text(label)
        ImGui.nextColumn()

        pushMultiItemsWidths(3, ImGui.calcItemWidth())
        ImGui.pushItemWidth(ImGui.calcItemWidth())
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0f, 0f)

        val lineHeight = ImGui.getFont().fontSize + ImGui.getStyle().framePaddingY * 2f
        val buttonSize = Vec2(lineHeight + 3.0f, lineHeight)

        ImGui.pushStyleColor(ImGuiCol.Button, Vec4(0.8f, 0.1f, 0.15f, 1f).toColorInt())
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, Vec4(0.9f, 0.2f, 0.2f, 1f).toColorInt())
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, Vec4(0.8f, 0.1f, 0.15f, 1f).toColorInt())
        ImGui.pushStyleColor(ImGuiCol.Text, Vec4(1f, 1f, 1f, 1f).toColorInt())
        ImGui.pushFont(font)
        if (ImGui.button("X", buttonSize.x, buttonSize.y))
            values.x = resetValue
        ImGui.popFont()
        ImGui.popStyleColor(4)

        ImGui.sameLine()
        editFloat("##X", values::x, 0.1f, 0.0f, 0.0f, "%.2f")
        ImGui.popItemWidth()
        ImGui.sameLine()

        ImGui.pushStyleColor(ImGuiCol.Button, Vec4(0.2f, 0.7f, 0.2f, 1f).toColorInt())
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, Vec4(0.3f, 0.8f, 0.3f, 1f).toColorInt())
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, Vec4(0.2f, 0.7f, 0.2f, 1f).toColorInt())
        ImGui.pushStyleColor(ImGuiCol.Text, Vec4(1f, 1f, 1f, 1f).toColorInt())
        ImGui.pushFont(font)
        if (ImGui.button("Y", buttonSize.x, buttonSize.y))
            values.y = resetValue
        ImGui.popFont()
        ImGui.popStyleColor(4)

        ImGui.sameLine()
        editFloat("##Y", values::y, 0.1f, 0.0f, 0.0f, "%.2f")
        ImGui.popItemWidth()
        ImGui.sameLine()

        ImGui.pushStyleColor(ImGuiCol.Button, Vec4(0.1f, 0.25f, 0.8f, 1f).toColorInt())
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, Vec4(0.2f, 0.35f, 0.9f, 1f).toColorInt())
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, Vec4(0.1f, 0.25f, 0.8f, 1f).toColorInt())
        ImGui.pushStyleColor(ImGuiCol.Text, Vec4(1f, 1f, 1f, 1f).toColorInt())
        ImGui.pushFont(font)
        if (ImGui.button("Z", buttonSize.x, buttonSize.y))
            values.z = resetValue
        ImGui.popFont()
        ImGui.popStyleColor(4)

        ImGui.sameLine()
        editFloat("##Z", values::z, 0.1f, 0.0f, 0.0f, "%.2f")
        ImGui.popItemWidth()

        ImGui.popStyleVar()

        ImGui.columns(1)

        ImGui.popID()
    }
}
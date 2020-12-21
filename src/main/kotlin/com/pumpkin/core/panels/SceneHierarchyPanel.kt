package com.pumpkin.core.panels

import com.pumpkin.core.renderer.ProjectionType
import com.pumpkin.core.scene.*
import com.pumpkin.ecs.Entity
import com.pumpkin.ecs.Registry
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec3.swizzle.xy
import glm_.vec4.Vec4
import imgui.*
import kotlin.reflect.KClass

class SceneHierarchyPanel(var context: Scene) {
    private val registry: Registry get() = context.registry
    private var selectionContext: Entity? = null

    fun onImGuiRender() {
        ImGui.begin("Hierarchy")
        registry.each(::drawEntityNode)
        if (ImGui.beginPopupContextWindow(popupFlags = PopupFlag.MouseButtonRight.i or PopupFlag.NoOpenOverItems.i)) {
            if (ImGui.menuItem("Create Empty Entity"))
                context.createEntity("Empty Entity")
            ImGui.endPopup()
        }
        ImGui.end()

        ImGui.begin("Inspector")
        if (selectionContext != null) {
            drawComponents(selectionContext!!)
        }
        ImGui.end()

        //ImGui.showDemoWindow(booleanArrayOf(true))
    }

    private fun drawEntityNode(entity: Entity) {
        val tag = registry.get<TagComponent>(entity).tag
        val flags: TreeNodeFlags =
            (if (selectionContext == entity) TreeNodeFlag.Selected.i else 0) or
            TreeNodeFlag.OpenOnArrow.i or
            TreeNodeFlag.SpanAvailWidth
        val opened = ImGui.treeNodeEx(tag, flags)
        if (ImGui.isItemClicked())
            selectionContext = entity

        var entityDeleted = false
        if (ImGui.beginPopupContextItem()) {
            if (ImGui.menuItem("Delete Entity"))
                entityDeleted = true
            ImGui.endPopup()
        }

        if (opened) {
            ImGui.treePop()
        }

        if (entityDeleted) {
            registry.destroy(entity)
            if (selectionContext == entity)
                selectionContext = null
        }
    }

    inline fun <reified T : Any> drawComponent(name: String, entity: Entity, noinline uiFunction: (T) -> Unit) =
        drawComponent(T::class, name, entity, uiFunction)

    fun <T : Any> drawComponent(clazz: KClass<T>, name: String, entity: Entity, uiFunction: (T) -> Unit) {
            val treeNodeFlags = TreeNodeFlag.DefaultOpen or TreeNodeFlag.Framed or TreeNodeFlag.SpanAvailWidth or TreeNodeFlag.AllowItemOverlap or TreeNodeFlag.FramePadding;
            if (registry.has(clazz, entity)) {
                val component = registry.get(clazz, entity)
                val contentRegionAvailable = ImGui.contentRegionAvail

                ImGui.pushStyleVar(StyleVar.FramePadding, Vec2(4, 4))
                val lineHeight = ImGui.font.fontSize + ImGui.style.framePadding.y * 2.0f
                ImGui.separator()
                //ImGui.treeNodeEx(name, treeNodeFlags)
                val open = ImGui.treeNodeEx(name, treeNodeFlags)
                ImGui.popStyleVar()
                ImGui.sameLine(contentRegionAvailable.x - lineHeight * 0.5f)
                if (ImGui.button("+", Vec2(lineHeight, lineHeight))) {
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
                    registry.remove(clazz, entity)
            }
        }

    private fun drawComponents(entity: Entity) {
        if (
            registry.has<TagComponent>(entity)
        ) {
            val tag = registry.get<TagComponent>(entity)
            ImGui.inputText("##Tag", tag.byteArray)
        }
        ImGui.sameLine()
        ImGui.pushItemWidth(-1)
        if (selectionContext != null && ImGui.button("Add Component"))
            ImGui.openPopup("AddComponent")

        if (ImGui.beginPopup("AddComponent")) {
            if (!registry.has<TransformComponent>(selectionContext!!) && ImGui.menuItem("Transform")) {
                registry.emplace<TransformComponent>(selectionContext!!, floatArrayOf(0f, 0f, 0f, 1f, 1f, 0f))
                ImGui.closeCurrentPopup()
            }
            if (!registry.has<CameraComponent>(selectionContext!!) && ImGui.menuItem("Camera")) {
                registry.emplace<CameraComponent>(selectionContext!!, SceneCamera())
                ImGui.closeCurrentPopup()
            }
            if (!registry.has<SpriteRendererComponent>(selectionContext!!) && ImGui.menuItem("Sprite Renderer")) {
                registry.emplace<SpriteRendererComponent>(selectionContext!!, floatArrayOf(1f, 1f, 1f, 1f))
                ImGui.closeCurrentPopup()
            }
            ImGui.endPopup()
        }
        ImGui.popItemWidth()

        drawComponent<TransformComponent>("Transform", entity) { transform ->
            val position = transform.position
            drawVec3Control("Position", position)
            transform.position = position
            val scale = Vec3(transform.scale, 1f)
            drawVec3Control("Scale", scale)
            transform.scale = scale.xy
            val rotation = Vec3(0f, 0f, transform.rotation)
            drawVec3Control("Rotation", rotation)
            transform.rotation = rotation.z
        }

        drawComponent<CameraComponent>("Camera", entity) { camera ->
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
        }

        drawComponent<SpriteRendererComponent>("Sprite Renderer", entity) { spriteRenderer ->
            val color = spriteRenderer.color
            ImGui.colorEdit4("Color", color)
            spriteRenderer.color = color
        }

        drawComponent<NativeScriptComponent>("Native Script", entity) { script ->
            ImGui.text(script.instance::class.qualifiedName ?: "")
        }
    }

    private fun drawVec3Control(label: String, values: Vec3, resetValue: Float = 0f, columnWidth: Float = 100f) {
        val font = ImGui.io.fonts.fonts[0]

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
        ImGui.pushStyleColor(Col.Text, Vec4(1f, 1f, 1f, 1f))
        ImGui.pushFont(font)
        if (ImGui.button("X", buttonSize))
            values.x = resetValue
        ImGui.popFont()
        ImGui.popStyleColor(4)

        ImGui.sameLine()
        ImGui.dragFloat("##X", values::x, 0.1f, 0.0f, 0.0f, "%.2f")
        ImGui.popItemWidth()
        ImGui.sameLine()

        ImGui.pushStyleColor(Col.Button, Vec4(0.2f, 0.7f, 0.2f, 1f))
        ImGui.pushStyleColor(Col.ButtonHovered, Vec4(0.3f, 0.8f, 0.3f, 1f))
        ImGui.pushStyleColor(Col.ButtonActive, Vec4(0.2f, 0.7f, 0.2f, 1f))
        ImGui.pushStyleColor(Col.Text, Vec4(1f, 1f, 1f, 1f))
        ImGui.pushFont(font)
        if (ImGui.button("Y", buttonSize))
            values.y = resetValue
        ImGui.popFont()
        ImGui.popStyleColor(4)

        ImGui.sameLine()
        ImGui.dragFloat("##Y", values::y, 0.1f, 0.0f, 0.0f, "%.2f")
        ImGui.popItemWidth()
        ImGui.sameLine()

        ImGui.pushStyleColor(Col.Button, Vec4(0.1f, 0.25f, 0.8f, 1f))
        ImGui.pushStyleColor(Col.ButtonHovered, Vec4(0.2f, 0.35f, 0.9f, 1f))
        ImGui.pushStyleColor(Col.ButtonActive, Vec4(0.1f, 0.25f, 0.8f, 1f))
        ImGui.pushStyleColor(Col.Text, Vec4(1f, 1f, 1f, 1f))
        ImGui.pushFont(font)
        if (ImGui.button("Z", buttonSize))
            values.y = resetValue
        ImGui.popFont()
        ImGui.popStyleColor(4)

        ImGui.sameLine()
        ImGui.dragFloat("##Z", values::z, 0.1f, 0.0f, 0.0f, "%.2f")
        ImGui.popItemWidth()

        ImGui.popStyleVar()

        ImGui.columns(1)

        ImGui.popID()
    }
}
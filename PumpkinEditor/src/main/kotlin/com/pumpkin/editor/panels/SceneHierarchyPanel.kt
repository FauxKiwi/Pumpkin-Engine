package com.pumpkin.editor.panels

import com.pumpkin.core.renderer.ProjectionType
import com.pumpkin.core.scene.*
import com.pumpkin.ecs.Entity
import com.pumpkin.ecs.Registry
import com.pumpkin.editor.imgui.*
import glm.Vec2
import glm.Vec4
import imgui.*
import imgui.flag.*
import imgui.type.ImBoolean
import imgui.type.ImInt
import kotlin.reflect.KClass

class SceneHierarchyPanel(var context: Scene) {
    private val registry: Registry get() = context.registry
    var selectionContext: Entity? = null

    fun onImGuiRender() {
        ImGui.begin("Hierarchy", ImBoolean(true), ImGuiWindowFlags.NoCollapse)
        registry.each(::drawEntityNode)
        if (ImGui.beginPopupContextWindow(ImGuiPopupFlags.MouseButtonRight or ImGuiPopupFlags.NoOpenOverItems)) {
            if (ImGui.menuItem("Create Empty")) {
                selectionContext = context.createEntity("Empty Entity").entityHandle
            }
            if (ImGui.menuItem("Camera")) {
                selectionContext = context.createEntity("Camera").also {
                    it.addComponent(CameraComponent(SceneCamera()))
                }.entityHandle
            }
            if (ImGui.menuItem("2D Sprite")) {
                selectionContext = context.createEntity("Sprite").also {
                    it.addComponent(SpriteRendererComponent(floatArrayOf(1f, 1f, 1f, 1f)))
                }.entityHandle
            }
            ImGui.endPopup()
        }
        ImGui.end()

        ImGui.begin("Inspector", ImBoolean(true), ImGuiWindowFlags.NoCollapse)
        if (selectionContext != null) {
            drawComponents(selectionContext!!)
        }
        ImGui.end()
    }

    private fun drawEntityNode(entity: Entity) {
        val tag = registry.get<TagComponent>(entity).str
        val flags =
            (if (selectionContext == entity) ImGuiTreeNodeFlags.Selected else 0) or
                    ImGuiTreeNodeFlags.OpenOnArrow or
                    ImGuiTreeNodeFlags.SpanAvailWidth
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
        val treeNodeFlags = ImGuiTreeNodeFlags.DefaultOpen or ImGuiTreeNodeFlags.Framed or ImGuiTreeNodeFlags.SpanAvailWidth or ImGuiTreeNodeFlags.AllowItemOverlap or ImGuiTreeNodeFlags.FramePadding;
        if (registry.has(clazz, entity)) {
            val component = registry.get(clazz, entity)
            val contentRegionAvailable = Vec2(ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY())

            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 4f, 4f)
            val lineHeight = ImGui.getFont().fontSize + ImGui.getStyle().framePaddingY * 2.0f
            ImGui.separator()
            val open = ImGui.treeNodeEx(name, treeNodeFlags)
            ImGui.popStyleVar()
            ImGui.sameLine(contentRegionAvailable.x - lineHeight * 0.5f)
            if (ImGui.button("+", lineHeight, lineHeight)) {
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
            editString("##Tag", tag::str)
        }
        ImGui.sameLine()
        ImGui.pushItemWidth(-1f)
        if (ImGui.button("Add Component"))
            ImGui.openPopup("AddComponent")

        if (ImGui.beginPopup("AddComponent")) {
            if (!registry.has<TransformComponent>(selectionContext!!) && ImGui.menuItem("Transform")) {
                registry.insert(selectionContext!!, TransformComponent(floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 1f, 1f, 1f)))
                ImGui.closeCurrentPopup()
            }
            if (!registry.has<CameraComponent>(selectionContext!!) && ImGui.menuItem("Camera")) {
                registry.insert(selectionContext!!, CameraComponent(SceneCamera()))
                ImGui.closeCurrentPopup()
            }
            if (!registry.has<SpriteRendererComponent>(selectionContext!!) && ImGui.menuItem("Sprite Renderer")) {
                registry.insert(selectionContext!!, SpriteRendererComponent(floatArrayOf(1f, 1f, 1f, 1f)))
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
        val font = ImGui.getFont()

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
/*package com.pumpkin.editor.panels

import com.pumpkin.core.renderer.ProjectionType
import com.pumpkin.core.scene.*
import com.pumpkin.ecs.Entity
import com.pumpkin.ecs.Registry
import com.pumpkin.editor.imgui.*
import glm.Vec2
import glm.Vec4
import imgui.*
import imgui.flag.*
import imgui.type.ImBoolean
import imgui.type.ImInt
import imgui.type.ImString
import kotlin.reflect.KClass

class SceneHierarchyPanel(var context: Scene) {
    private val registry: Registry get() = context.registry
    var selectionContext: Entity? = null

    fun onImGuiRender() {

        ImGuiWindow("Hierarchy", null, ImGuiWindowFlags.NoCollapse) {
            registry.each(::drawEntityNode)
            ImGuiPopupContextWindow(ImGuiPopupFlags.MouseButtonRight or ImGuiPopupFlags.NoOpenOverItems) {
                ImGuiMenuItem("Create Empty") {
                    selectionContext = context.createEntity("Empty Entity").entityHandle
                }
                ImGuiMenuItem("Camera") {
                    selectionContext = context.createEntity("Camera").also {
                        it.addComponent(CameraComponent(SceneCamera()))
                    }.entityHandle
                }
                ImGuiMenuItem("2D Sprite") {
                    selectionContext = context.createEntity("Sprite").also {
                        it.addComponent(SpriteRendererComponent(floatArrayOf(1f, 1f, 1f, 1f)))
                    }.entityHandle
                }
            }
        }

        ImGuiWindow("Inspector", null, ImGuiWindowFlags.NoCollapse) {
            if (selectionContext != null) {
                drawComponents(selectionContext!!)
            }
        }

        //ImGui.showDemoWindow(booleanArrayOf(true))
    }

    private fun drawEntityNode(entity: Entity) {
        val tag = registry.get<TagComponent>(entity).str
        val flags =
            (if (selectionContext == entity) ImGuiTreeNodeFlags.Selected else 0) or
                    ImGuiTreeNodeFlags.OpenOnArrow or
                    ImGuiTreeNodeFlags.SpanAvailWidth
        val opened = ImGui.treeNodeEx(tag, flags)
        if (ImGui.isItemClicked())
            selectionContext = entity

        var entityDeleted = false
        ImGuiPopupContextItem {
            if (ImGui.menuItem("Delete Entity"))
                entityDeleted = true
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
            val treeNodeFlags = ImGuiTreeNodeFlags.DefaultOpen or ImGuiTreeNodeFlags.Framed or ImGuiTreeNodeFlags.SpanAvailWidth or ImGuiTreeNodeFlags.AllowItemOverlap or ImGuiTreeNodeFlags.FramePadding;
            if (registry.has(clazz, entity)) {
                val component = registry.get(clazz, entity)
                val contentRegionAvailable = Vec2(ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY())

                ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 4f, 4f)
                val lineHeight = ImGui.getFont().fontSize + ImGui.getStyle().framePaddingY * 2.0f
                ImGui.separator()
                val open = ImGui.treeNodeEx(name, treeNodeFlags)
                ImGui.popStyleVar()
                ImGui.sameLine(contentRegionAvailable.x - lineHeight * 0.5f)
                if (ImGui.button("+", lineHeight, lineHeight)) {
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
        //ImGui.text("Full ID: ${entity.fullID} -> ID: ${entity.id} | Version: ${entity.version}")
        if (
            registry.has<TagComponent>(entity)
        ) {
            val tag = registry.get<TagComponent>(entity)
            ImGui.inputText("##Tag", ImString(0)/*tag.byteArray TODO*/)
        }
        ImGui.sameLine()
        withItemWidth(-1f) {
            if (ImGui.button("Add Component"))
                ImGui.openPopup("AddComponent")

            ImGuiPopup("AddComponent") {
                if (!registry.has<TransformComponent>(selectionContext!!) && ImGui.menuItem("Transform")) {
                    registry.insert(
                        selectionContext!!,
                        TransformComponent(floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 1f, 1f, 1f))
                    )
                    ImGui.closeCurrentPopup()
                }
                if (!registry.has<CameraComponent>(selectionContext!!) && ImGui.menuItem("Camera")) {
                    registry.insert(selectionContext!!, CameraComponent(SceneCamera()))
                    ImGui.closeCurrentPopup()
                }
                if (!registry.has<SpriteRendererComponent>(selectionContext!!) && ImGui.menuItem("Sprite Renderer")) {
                    registry.insert(selectionContext!!, SpriteRendererComponent(floatArrayOf(1f, 1f, 1f, 1f)))
                    ImGui.closeCurrentPopup()
                }
            }
        }

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
            ImGui.checkbox("Primary Camera", camera.primary)
            ImGui.combo("Projection Type", ImInt( camera.camera.projectionTypePtr), /*ProjectionType.projectionTypes*/"Orthographic\u0000Perspective")
            if (camera.camera.projectionType == ProjectionType.Orthographic) {
                ImGui.dragFloat("Orthographic Size", floatArrayOf(camera.camera.orthographicSize))
                ImGui.dragFloat("Near Clip", floatArrayOf(camera.camera.orthographicNear))
                ImGui.dragFloat("Far Clip", floatArrayOf(camera.camera.orthographicFar))
            } else {
                ImGui.dragFloat("Field of View", floatArrayOf(camera.camera.perspectiveFov))
                ImGui.dragFloat("Near Clip", floatArrayOf(camera.camera.perspectiveNear))
                ImGui.dragFloat("Far Clip", floatArrayOf(camera.camera.perspectiveFar))
            }
            ImGui.checkbox("Fixed Aspect Ratio", camera.fixedAspectRatio)
            ImGui.colorEdit3("Clear Color", camera.camera.clearColor.array)
        }

        drawComponent<SpriteRendererComponent>("Sprite Renderer", entity) { spriteRenderer ->
            val color = spriteRenderer.color
            ImGui.colorEdit4("Color", color.array)
            spriteRenderer.color = color
        }

        drawComponent<NativeScriptComponent>("Native Script", entity) { script ->
            ImGui.text(script.instance::class.qualifiedName ?: "")
        }
    }

    private fun drawVec3Control(label: String, values: glm.Vec3, resetValue: Float = 0f, columnWidth: Float = 100f) {
        val font = ImGui.getFont()

        ImGui.pushID(label)

        ImGui.columns(2)
        ImGui.setColumnWidth(0, columnWidth)
        ImGui.text(label)
        ImGui.nextColumn()

        pushMultiItemsWidths(3, ImGui.calcItemWidth())
        ImGui.pushItemWidth(ImGui.calcItemWidth())
        withStyleVar(ImGuiStyleVar.ItemSpacing, 0f, 0f) {

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
        ImGui.dragFloat("##X", floatArrayOf(values.x), 0.1f, 0.0f, 0.0f, "%.2f")
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
        ImGui.dragFloat("##Y", floatArrayOf(values.y), 0.1f, 0.0f, 0.0f, "%.2f")
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
        ImGui.dragFloat("##Z", floatArrayOf(values.z), 0.1f, 0.0f, 0.0f, "%.2f")
        ImGui.popItemWidth()
        }

        ImGui.columns(1)

        ImGui.popID()
    }

    /*fun ImGui.colorEdit3(label: String, color: glm.Vec4) {
        val fa = floatArrayOf(color.r, color.g, color.b)
        this.colorEdit4(label, fa)
        color.r = fa[0]; color.g = fa[1]; color.b = fa[2]
    }

    fun ImGui.colorEdit4(label: String, color: glm.Vec4) {
        val fa = floatArrayOf(color.r, color.g, color.b, color.a)
        colorEdit4(label, fa)
        color.r = fa[0]; color.g = fa[1]; color.b = fa[2]; color.a = fa[3]
    }*/
}*/

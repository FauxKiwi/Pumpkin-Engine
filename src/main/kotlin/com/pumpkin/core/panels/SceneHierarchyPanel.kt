package com.pumpkin.core.panels

import com.pumpkin.core.scene.Scene
import com.pumpkin.core.scene.TagComponent
import com.pumpkin.ecs.Entity
import imgui.ImGui
import imgui.TreeNodeFlag
import imgui.TreeNodeFlags

class SceneHierarchyPanel(var context: Scene) {
    private var selectionContext: Entity? = null

    fun onImGuiRender() = with(ImGui) {
        begin("Hierarchy")
        context.registry.each(::drawEntityNode)
        end()
    }

    private fun drawEntityNode(entity: Entity) {
        val tag = context.registry.get<TagComponent>(entity).tag
        val flags: TreeNodeFlags = (if (selectionContext == entity) TreeNodeFlag.Selected.i else 0) or TreeNodeFlag.OpenOnArrow.i
        val opened = ImGui.treeNodeEx(tag, flags)
        if (ImGui.isItemClicked())
            selectionContext = entity
        if (opened) {
            ImGui.treePop()
        }
    }
}
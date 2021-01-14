package com.pumpkin.editor.settings

import com.pumpkin.editor.imgui.editEnum
import glm.Vec4
import imgui.ImGui
import imgui.ImGuiStyle
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiCond
import imgui.flag.ImGuiTreeNodeFlags
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImBoolean
import imgui.type.ImInt

object Settings {
    var editorCameraClearColor = Vec4(0.1f, 0.1f, 0.1f, 1f)
    var editorCameraFov = 45f; var uEditorCameraView = false

    private val open = ImBoolean(false)

    fun open() {
        open.set(true)
        if (!SettingsSerializer.load()) SettingsSerializer.save()
    }

    fun onImGuiRender() {
        if (!open.get()) return

        ImGui.setNextWindowPos(ImGui.getContentRegionMaxX() * 0.25f, ImGui.getContentRegionMaxY() * 0.167f, ImGuiCond.FirstUseEver)
        ImGui.setNextWindowSize(ImGui.getContentRegionMaxX() * 0.5f, ImGui.getContentRegionMaxY() * 0.667f, ImGuiCond.FirstUseEver)

        ImGui.begin("Settings", open, ImGuiWindowFlags.NoCollapse)

        val childFlags = ImGuiWindowFlags.NoCollapse or ImGuiWindowFlags.NoTitleBar or ImGuiWindowFlags.NoBackground or ImGuiWindowFlags.NoResize

        ImGui.columns(2)//ImGui.beginColumns("SettingsColumns", 2, OldColumnsFlag.NoResize.i)
        ImGui.setColumnWidth(-1, glm.min(250f, ImGui.getWindowWidth() / 3f))
        ImGui.beginChild("SettingsTree", ImGui.getColumnWidth(-1) - 15f, ImGui.getWindowHeight() - 77, false, childFlags)

        var currentSettings = "None"
        if (ImGui.treeNodeEx("Appearance", ImGuiTreeNodeFlags.Leaf)) {
            currentSettings = "Appearance"
            ImGui.treePop()
        }

        ImGui.endChild()
        ImGui.nextColumn()
        ImGui.beginChild("SettingsDisplay", 0f, ImGui.getWindowHeight() - 77, false, childFlags)

        when (currentSettings) {
            "None" -> ImGui.text("Open a context menu on the left to edit settings")
            "Appearance" -> {
                val imInt = ImInt(Theme.current)
                if (ImGui.combo("Theme", imInt, arrayOf("Dark", "Light"), 2)) {
                    Theme.current = imInt.get()
                    Theme[Theme.current].apply(ImGui.getStyle())
                }
            }
        }

        ImGui.endChild()
        //ImGui.nextColumn()//ImGui.endColumns()

        ImGui.spacing()
        if (ImGui.button("OK", 100f, 30f)) {
            SettingsSerializer.save(); open.set(false) }
        ImGui.sameLine()
        if (ImGui.button("Cancel", 100f, 30f)) {
            open.set(false); SettingsSerializer.load() }
        ImGui.sameLine()
        if (ImGui.button("Apply", 100f, 30f)) SettingsSerializer.save()

        ImGui.end()
    }

    fun setTheme(theme: Int) {
        Theme.current = theme
        //ImGui.currentContext?.style = Theme[theme].style
    }

    fun getTheme(): Int = Theme.current
}

object Theme {
    private val themes = mutableListOf(DarkTheme, LightTheme)
    private var currentTheme: EditorStyle = DarkTheme
    var current: Int = 0
        set(value) { field = value; currentTheme = themes[value] }

    operator fun get(i: Int): EditorStyle = themes[i]
}

interface EditorStyle {
    val name: String

    fun apply(style: ImGuiStyle)
}

object DarkTheme : EditorStyle {
    override val name: String = "Dark"

    override fun apply(style: ImGuiStyle) {
        style.windowRounding = 0f
        style.frameRounding = 0f

        style.setColor(ImGuiCol.Text, 1.00f, 1.00f, 1.00f, 1.00f)
        style.setColor(ImGuiCol.TextDisabled, 0.50f, 0.50f, 0.50f, 1.00f)
        style.setColor(ImGuiCol.WindowBg, 0.09f, 0.09f, 0.09f, 1.00f)
        style.setColor(ImGuiCol.ChildBg, 0.09f, 0.09f, 0.09f, 0.00f)
        style.setColor(ImGuiCol.PopupBg, 0.08f, 0.08f, 0.08f, 0.94f)
        style.setColor(ImGuiCol.Border, 0f, 0f, 0f, 0.20f)
        style.setColor(ImGuiCol.FrameBg, 0.16f, 0.16f, 0.16f, 0.54f)
        style.setColor(ImGuiCol.FrameBgHovered, 0.55f, 0.55f, 0.55f, 0.40f)
        style.setColor(ImGuiCol.FrameBgActive, 0.55f, 0.55f, 0.55f, 0.67f)
        style.setColor(ImGuiCol.TitleBg, 0.04f, 0.04f, 0.04f, 1.00f)
        style.setColor(ImGuiCol.TitleBgActive, 0.88f, 0.35f, 0.19f, 1.00f)
        style.setColor(ImGuiCol.TitleBgCollapsed, 0.00f, 0.00f, 0.00f, 0.51f)
        style.setColor(ImGuiCol.MenuBarBg, 0.14f, 0.14f, 0.14f, 1.00f)
        style.setColor(ImGuiCol.ScrollbarBg, 0.02f, 0.02f, 0.02f, 0.53f)
        style.setColor(ImGuiCol.ScrollbarGrab, 0.31f, 0.31f, 0.31f, 1.00f)
        style.setColor(ImGuiCol.ScrollbarGrabHovered, 0.41f, 0.41f, 0.41f, 1.00f)
        style.setColor(ImGuiCol.ScrollbarGrabActive, 0.51f, 0.51f, 0.51f, 1.00f)
        style.setColor(ImGuiCol.CheckMark, 0.98f, 0.43f, 0.26f, 1.00f)
        style.setColor(ImGuiCol.SliderGrab, 0.88f, 0.39f, 0.24f, 1.00f)
        style.setColor(ImGuiCol.SliderGrabActive, 0.98f, 0.43f, 0.26f, 1.00f)
        style.setColor(ImGuiCol.Button, 0.39f, 0.39f, 0.39f, 0.40f)
        style.setColor(ImGuiCol.ButtonHovered, 0.39f, 0.39f, 0.39f, 1.00f)
        style.setColor(ImGuiCol.ButtonActive, 0.39f, 0.39f, 0.39f, 1.00f)
        style.setColor(ImGuiCol.Header, 0.15f, 0.15f, 0.15f, 0.80f)
        style.setColor(ImGuiCol.HeaderHovered, 0.15f, 0.15f, 0.15f, 0.80f)
        style.setColor(ImGuiCol.HeaderActive, 0.15f, 0.15f, 0.15f, 1.00f)
        style.setColor(ImGuiCol.Separator, 0.50f, 0.46f, 0.43f, 0.50f)
        style.setColor(ImGuiCol.SeparatorHovered, 0.75f, 0.25f, 0.10f, 0.78f)
        style.setColor(ImGuiCol.SeparatorActive, 0.75f, 0.25f, 0.10f, 1.00f)
        style.setColor(ImGuiCol.ResizeGrip, 0.00f, 0.00f, 0.00f, 0.00f)
        style.setColor(ImGuiCol.ResizeGripHovered, 0.98f, 0.98f, 0.98f, 0.67f)
        style.setColor(ImGuiCol.ResizeGripActive, 0.98f, 0.98f, 0.98f, 0.95f)
        style.setColor(ImGuiCol.Tab, 0.16f, 0.16f, 0.16f, 0.86f)
        style.setColor(ImGuiCol.TabHovered, 0.98f, 0.43f, 0.26f, 0.80f)
        style.setColor(ImGuiCol.TabActive, 0.68f, 0.31f, 0.20f, 1.00f)
        style.setColor(ImGuiCol.TabUnfocused, 0.15f, 0.10f, 0.07f, 0.97f)
        style.setColor(ImGuiCol.TabUnfocusedActive, 0.42f, 0.22f, 0.14f, 1.00f)
        style.setColor(ImGuiCol.DockingPreview, 0.98f, 0.43f, 0.26f, 0.70f)
        style.setColor(ImGuiCol.DockingEmptyBg, 0.00f, 0.00f, 0.00f, 1.00f)
        style.setColor(ImGuiCol.PlotLines, 0.61f, 0.61f, 0.61f, 1.00f)
        style.setColor(ImGuiCol.PlotLinesHovered, 1.00f, 0.43f, 0.35f, 1.00f)
        style.setColor(ImGuiCol.TextSelectedBg, 0.26f, 0.59f, 0.98f, 0.35f)
        style.setColor(ImGuiCol.NavHighlight, 0.26f, 0.59f, 0.98f, 1.00f)
        style.setColor(ImGuiCol.ModalWindowDimBg, 0.80f, 0.80f, 0.80f, 0.35f)
        style.setColor(ImGuiCol.PopupBg, 0.07f, 0.07f, 0.07f, 1.00f)
    }
}

object LightTheme : EditorStyle {
    override val name: String = "Dark"

    override fun apply(style: ImGuiStyle) {
        style.windowRounding = 0f
        style.frameRounding = 0f
        style.frameBorderSize = 1f
        style.tabBorderSize = 1f

        style.setColor(ImGuiCol.Text, 0f, 0f, 0f, 1.00f)
        style.setColor(ImGuiCol.TextDisabled, 0.50f, 0.50f, 0.50f, 1.00f)
        style.setColor(ImGuiCol.WindowBg, 0.90f, 0.90f, 0.90f, 1.00f)
        style.setColor(ImGuiCol.ChildBg, 0.90f, 0.90f, 0.90f, 0.00f)
        style.setColor(ImGuiCol.PopupBg, 0.92f, 0.92f, 0.92f, 0.94f)
        style.setColor(ImGuiCol.Border, 0.1f, 0.1f, 0.1f, 0.25f)
        style.setColor(ImGuiCol.FrameBg, 0.86f, 0.86f, 0.86f, 0.54f)
        style.setColor(ImGuiCol.FrameBgHovered, 0.75f, 0.75f, 0.75f, 0.40f)
        style.setColor(ImGuiCol.FrameBgActive, 0.75f, 0.75f, 0.75f, 0.67f)
        style.setColor(ImGuiCol.TitleBg, 0.95f, 0.95f, 0.95f, 1.00f)
        style.setColor(ImGuiCol.TitleBgActive, 0.98f, 0.45f, 0.29f, 1.00f)
        style.setColor(ImGuiCol.TitleBgCollapsed, 1f, 1f, 1f, 0.51f)
        style.setColor(ImGuiCol.MenuBarBg, 0.84f, 0.84f, 0.84f, 1.00f)
        style.setColor(ImGuiCol.ScrollbarBg, 0.98f, 0.98f, 0.98f, 0.53f)
        style.setColor(ImGuiCol.ScrollbarGrab, 0.65f, 0.65f, 0.65f, 1.00f)
        style.setColor(ImGuiCol.ScrollbarGrabHovered, 0.51f, 0.51f, 0.51f, 1.00f)
        style.setColor(ImGuiCol.ScrollbarGrabActive, 0.61f, 0.61f, 0.61f, 1.00f)
        style.setColor(ImGuiCol.CheckMark, 0.98f, 0.43f, 0.26f, 1.00f)
        style.setColor(ImGuiCol.SliderGrab, 0.88f, 0.39f, 0.24f, 1.00f)
        style.setColor(ImGuiCol.SliderGrabActive, 0.98f, 0.43f, 0.26f, 1.00f)
        style.setColor(ImGuiCol.Button, 0.59f, 0.59f, 0.59f, 0.40f)
        style.setColor(ImGuiCol.ButtonHovered, 0.59f, 0.59f, 0.59f, 1.00f)
        style.setColor(ImGuiCol.ButtonActive, 0.69f, 0.69f, 0.69f, 1.00f)
        style.setColor(ImGuiCol.Header, 0.85f, 0.85f, 0.85f, 0.80f)
        style.setColor(ImGuiCol.HeaderHovered, 0.85f, 0.85f, 0.85f, 0.80f)
        style.setColor(ImGuiCol.HeaderActive, 0.85f, 0.85f, 0.85f, 1.00f)
        style.setColor(ImGuiCol.Separator, 0.80f, 0.76f, 0.73f, 0.50f)
        style.setColor(ImGuiCol.SeparatorHovered, 0.85f, 0.35f, 0.20f, 0.78f)
        style.setColor(ImGuiCol.SeparatorActive, 0.85f, 0.35f, 0.20f, 1.00f)
        style.setColor(ImGuiCol.ResizeGrip, 1f, 1f, 1f, 0.00f)
        style.setColor(ImGuiCol.ResizeGripHovered, 0.1f, 0.1f, 0.1f, 0.67f)
        style.setColor(ImGuiCol.ResizeGripActive, 0.1f, 0.1f, 0.1f, 0.95f)
        style.setColor(ImGuiCol.Tab, 0.86f, 0.86f, 0.86f, 0.86f)
        style.setColor(ImGuiCol.TabHovered, 0.98f, 0.43f, 0.26f, 0.80f)
        style.setColor(ImGuiCol.TabActive, 0.78f, 0.41f, 0.30f, 1.00f)
        style.setColor(ImGuiCol.TabUnfocused, 0.85f, 0.80f, 0.77f, 0.97f)
        style.setColor(ImGuiCol.TabUnfocusedActive, 0.62f, 0.42f, 0.34f, 1.00f)
        style.setColor(ImGuiCol.DockingPreview, 0.98f, 0.43f, 0.26f, 0.70f)
        style.setColor(ImGuiCol.DockingEmptyBg, 0.00f, 0.00f, 0.00f, 1.00f)
        style.setColor(ImGuiCol.PlotLines, 0.71f, 0.71f, 0.71f, 1.00f)
        style.setColor(ImGuiCol.PlotLinesHovered, 1.00f, 0.43f, 0.35f, 1.00f)
        style.setColor(ImGuiCol.TextSelectedBg, 0.26f, 0.59f, 0.98f, 0.35f)
        style.setColor(ImGuiCol.NavHighlight, 0.26f, 0.59f, 0.98f, 1.00f)
        style.setColor(ImGuiCol.ModalWindowDimBg, 0.30f, 0.30f, 0.30f, 0.35f)
        style.setColor(ImGuiCol.PopupBg, 0.87f, 0.87f, 0.87f, 1.00f)
    }
}
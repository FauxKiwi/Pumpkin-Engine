package com.pumpkin.core.settings

import glm_.vec2.Vec2
import glm_.vec4.Vec4
import imgui.*
import imgui.classes.Style
import imgui.internal.sections.OldColumnsFlag

object Settings {
    var editorCameraClearColor = Vec4(0.1f, 0.1f, 0.1f, 1f)
    var editorCameraFov = 45f; var uEditorCameraView = false

    private var open = false
    private var first = true

    fun open() {
        open = true
        first = true
        if (!SettingsSerializer.load()) SettingsSerializer.save()
    }

    fun onImGuiRender() {
        if (!open) return

        if (first) {
            ImGui.setNextWindowPos(ImGui.contentRegionMax * Vec2(0.25f, 0.167f))
            ImGui.setNextWindowSize(ImGui.contentRegionMax * Vec2(0.5f, 0.667f))
            first = false
        }
        ImGui.begin("Settings", Settings::open, WindowFlag.NoCollapse.i)

        val childFlags = WindowFlag.NoCollapse.i or WindowFlag.NoTitleBar.i or WindowFlag.NoBackground.i or WindowFlag.NoResize.i

        ImGui.beginColumns("SettingsColumns", 2, OldColumnsFlag.NoResize.i)
        ImGui.setColumnWidth(-1, 250f)
        ImGui.beginChild("SettingsTree", Vec2(ImGui.getColumnWidth(-1) - 15f, ImGui.windowHeight - 77), flags = childFlags)

        var currentSettings = "None"
        if (ImGui.treeNodeEx("Appearance", TreeNodeFlag.Leaf.i)) {
            currentSettings = "Appearance"
            ImGui.treePop()
        }

        ImGui.endChild()
        ImGui.nextColumn()
        ImGui.beginChild("SettingsDisplay", Vec2(0, ImGui.windowHeight - 77), flags = childFlags)

        when (currentSettings) {
            "None" -> ImGui.text("Open a context menu on the left to edit settings")
            "Appearance" -> {
                if (ImGui.combo("Theme", Theme::current, "Dark\u0000Light"))
                    ImGui.currentContext?.style = Theme[Theme.current].style
            }
        }

        ImGui.endChild()
        ImGui.endColumns()

        ImGui.spacing()
        if (ImGui.button("OK", Vec2(100, 30))) { SettingsSerializer.save(); open = false }
        ImGui.sameLine()
        if (ImGui.button("Cancel", Vec2(100, 30))) { open = false; SettingsSerializer.load() }
        ImGui.sameLine()
        if (ImGui.button("Apply", Vec2(100, 30))) SettingsSerializer.save()

        ImGui.end()
    }

    fun setTheme(theme: Int) {
        Theme.current = theme
        ImGui.currentContext?.style = Theme[theme].style
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
    val style: Style
}

object DarkTheme : EditorStyle {
    override val name: String = "Dark"
    override val style = Style()

    init {
        style.windowRounding = 0f
        style.frameRounding = 0f

        style.colors[Col.Text] = Vec4(1.00f, 1.00f, 1.00f, 1.00f)
        style.colors[Col.TextDisabled] = Vec4(0.50f, 0.50f, 0.50f, 1.00f)
        style.colors[Col.WindowBg] = Vec4(0.09f, 0.09f, 0.09f, 1.00f)
        style.colors[Col.ChildBg] = Vec4(0.09f, 0.09f, 0.09f, 0.00f)
        style.colors[Col.PopupBg] = Vec4(0.08f, 0.08f, 0.08f, 0.94f)
        style.colors[Col.Border] = Vec4(0f, 0f, 0f, 0.20f)
        style.colors[Col.FrameBg] = Vec4(0.16f, 0.16f, 0.16f, 0.54f)
        style.colors[Col.FrameBgHovered] = Vec4(0.55f, 0.55f, 0.55f, 0.40f)
        style.colors[Col.FrameBgActive] = Vec4(0.55f, 0.55f, 0.55f, 0.67f)
        style.colors[Col.TitleBg] = Vec4(0.04f, 0.04f, 0.04f, 1.00f)
        style.colors[Col.TitleBgActive] = Vec4(0.88f, 0.35f, 0.19f, 1.00f)
        style.colors[Col.TitleBgCollapsed] = Vec4(0.00f, 0.00f, 0.00f, 0.51f)
        style.colors[Col.MenuBarBg] = Vec4(0.14f, 0.14f, 0.14f, 1.00f)
        style.colors[Col.ScrollbarBg] = Vec4(0.02f, 0.02f, 0.02f, 0.53f)
        style.colors[Col.ScrollbarGrab] = Vec4(0.31f, 0.31f, 0.31f, 1.00f)
        style.colors[Col.ScrollbarGrabHovered] = Vec4(0.41f, 0.41f, 0.41f, 1.00f)
        style.colors[Col.ScrollbarGrabActive] = Vec4(0.51f, 0.51f, 0.51f, 1.00f)
        style.colors[Col.CheckMark] = Vec4(0.98f, 0.43f, 0.26f, 1.00f)
        style.colors[Col.SliderGrab] = Vec4(0.88f, 0.39f, 0.24f, 1.00f)
        style.colors[Col.SliderGrabActive] = Vec4(0.98f, 0.43f, 0.26f, 1.00f)
        style.colors[Col.Button] = Vec4(0.39f, 0.39f, 0.39f, 0.40f)
        style.colors[Col.ButtonHovered] = Vec4(0.39f, 0.39f, 0.39f, 1.00f)
        style.colors[Col.ButtonActive] = Vec4(0.39f, 0.39f, 0.39f, 1.00f)
        style.colors[Col.Header] = Vec4(0.15f, 0.15f, 0.15f, 0.80f)
        style.colors[Col.HeaderHovered] = Vec4(0.15f, 0.15f, 0.15f, 0.80f)
        style.colors[Col.HeaderActive] = Vec4(0.15f, 0.15f, 0.15f, 1.00f)
        style.colors[Col.Separator] = Vec4(0.50f, 0.46f, 0.43f, 0.50f)
        style.colors[Col.SeparatorHovered] = Vec4(0.75f, 0.25f, 0.10f, 0.78f)
        style.colors[Col.SeparatorActive] = Vec4(0.75f, 0.25f, 0.10f, 1.00f)
        style.colors[Col.ResizeGrip] = Vec4(0.00f, 0.00f, 0.00f, 0.00f)
        style.colors[Col.ResizeGripHovered] = Vec4(0.98f, 0.98f, 0.98f, 0.67f)
        style.colors[Col.ResizeGripActive] = Vec4(0.98f, 0.98f, 0.98f, 0.95f)
        style.colors[Col.Tab] = Vec4(0.16f, 0.16f, 0.16f, 0.86f)
        style.colors[Col.TabHovered] = Vec4(0.98f, 0.43f, 0.26f, 0.80f)
        style.colors[Col.TabActive] = Vec4(0.68f, 0.31f, 0.20f, 1.00f)
        style.colors[Col.TabUnfocused] = Vec4(0.15f, 0.10f, 0.07f, 0.97f)
        style.colors[Col.TabUnfocusedActive] = Vec4(0.42f, 0.22f, 0.14f, 1.00f)
        style.colors[Col.DockingPreview] = Vec4(0.98f, 0.43f, 0.26f, 0.70f)
        style.colors[Col.DockingEmptyBg] = Vec4(0.00f, 0.00f, 0.00f, 1.00f)
        style.colors[Col.PlotLines] = Vec4(0.61f, 0.61f, 0.61f, 1.00f)
        style.colors[Col.PlotLinesHovered] = Vec4(1.00f, 0.43f, 0.35f, 1.00f)
        style.colors[Col.TextSelectedBg] = Vec4(0.26f, 0.59f, 0.98f, 0.35f)
        style.colors[Col.NavHighlight] = Vec4(0.26f, 0.59f, 0.98f, 1.00f)
        style.colors[Col.ModalWindowDimBg] = Vec4(0.80f, 0.80f, 0.80f, 0.35f)
        style.colors[Col.PopupBg] = Vec4(0.07f, 0.07f, 0.07f, 1.00f)
    }
}

object LightTheme : EditorStyle {
    override val name: String = "Dark"
    override val style = Style()

    init {
        style.windowRounding = 0f
        style.frameRounding = 0f
        style.frameBorderSize = 1f
        style.tabBorderSize = 1f

        style.colors[Col.Text] = Vec4(0f, 0f, 0f, 1.00f)
        style.colors[Col.TextDisabled] = Vec4(0.50f, 0.50f, 0.50f, 1.00f)
        style.colors[Col.WindowBg] = Vec4(0.90f, 0.90f, 0.90f, 1.00f)
        style.colors[Col.ChildBg] = Vec4(0.90f, 0.90f, 0.90f, 0.00f)
        style.colors[Col.PopupBg] = Vec4(0.92f, 0.92f, 0.92f, 0.94f)
        style.colors[Col.Border] = Vec4(0.1f, 0.1f, 0.1f, 0.25f)
        style.colors[Col.FrameBg] = Vec4(0.86f, 0.86f, 0.86f, 0.54f)
        style.colors[Col.FrameBgHovered] = Vec4(0.75f, 0.75f, 0.75f, 0.40f)
        style.colors[Col.FrameBgActive] = Vec4(0.75f, 0.75f, 0.75f, 0.67f)
        style.colors[Col.TitleBg] = Vec4(0.95f, 0.95f, 0.95f, 1.00f)
        style.colors[Col.TitleBgActive] = Vec4(0.98f, 0.45f, 0.29f, 1.00f)
        style.colors[Col.TitleBgCollapsed] = Vec4(1f, 1f, 1f, 0.51f)
        style.colors[Col.MenuBarBg] = Vec4(0.84f, 0.84f, 0.84f, 1.00f)
        style.colors[Col.ScrollbarBg] = Vec4(0.98f, 0.98f, 0.98f, 0.53f)
        style.colors[Col.ScrollbarGrab] = Vec4(0.65f, 0.65f, 0.65f, 1.00f)
        style.colors[Col.ScrollbarGrabHovered] = Vec4(0.51f, 0.51f, 0.51f, 1.00f)
        style.colors[Col.ScrollbarGrabActive] = Vec4(0.61f, 0.61f, 0.61f, 1.00f)
        style.colors[Col.CheckMark] = Vec4(0.98f, 0.43f, 0.26f, 1.00f)
        style.colors[Col.SliderGrab] = Vec4(0.88f, 0.39f, 0.24f, 1.00f)
        style.colors[Col.SliderGrabActive] = Vec4(0.98f, 0.43f, 0.26f, 1.00f)
        style.colors[Col.Button] = Vec4(0.59f, 0.59f, 0.59f, 0.40f)
        style.colors[Col.ButtonHovered] = Vec4(0.59f, 0.59f, 0.59f, 1.00f)
        style.colors[Col.ButtonActive] = Vec4(0.69f, 0.69f, 0.69f, 1.00f)
        style.colors[Col.Header] = Vec4(0.85f, 0.85f, 0.85f, 0.80f)
        style.colors[Col.HeaderHovered] = Vec4(0.85f, 0.85f, 0.85f, 0.80f)
        style.colors[Col.HeaderActive] = Vec4(0.85f, 0.85f, 0.85f, 1.00f)
        style.colors[Col.Separator] = Vec4(0.80f, 0.76f, 0.73f, 0.50f)
        style.colors[Col.SeparatorHovered] = Vec4(0.85f, 0.35f, 0.20f, 0.78f)
        style.colors[Col.SeparatorActive] = Vec4(0.85f, 0.35f, 0.20f, 1.00f)
        style.colors[Col.ResizeGrip] = Vec4(1f, 1f, 1f, 0.00f)
        style.colors[Col.ResizeGripHovered] = Vec4(0.1f, 0.1f, 0.1f, 0.67f)
        style.colors[Col.ResizeGripActive] = Vec4(0.1f, 0.1f, 0.1f, 0.95f)
        style.colors[Col.Tab] = Vec4(0.86f, 0.86f, 0.86f, 0.86f)
        style.colors[Col.TabHovered] = Vec4(0.98f, 0.43f, 0.26f, 0.80f)
        style.colors[Col.TabActive] = Vec4(0.78f, 0.41f, 0.30f, 1.00f)
        style.colors[Col.TabUnfocused] = Vec4(0.85f, 0.80f, 0.77f, 0.97f)
        style.colors[Col.TabUnfocusedActive] = Vec4(0.62f, 0.42f, 0.34f, 1.00f)
        style.colors[Col.DockingPreview] = Vec4(0.98f, 0.43f, 0.26f, 0.70f)
        style.colors[Col.DockingEmptyBg] = Vec4(0.00f, 0.00f, 0.00f, 1.00f)
        style.colors[Col.PlotLines] = Vec4(0.71f, 0.71f, 0.71f, 1.00f)
        style.colors[Col.PlotLinesHovered] = Vec4(1.00f, 0.43f, 0.35f, 1.00f)
        style.colors[Col.TextSelectedBg] = Vec4(0.26f, 0.59f, 0.98f, 0.35f)
        style.colors[Col.NavHighlight] = Vec4(0.26f, 0.59f, 0.98f, 1.00f)
        style.colors[Col.ModalWindowDimBg] = Vec4(0.30f, 0.30f, 0.30f, 0.35f)
        style.colors[Col.PopupBg] = Vec4(0.87f, 0.87f, 0.87f, 1.00f)
    }
}
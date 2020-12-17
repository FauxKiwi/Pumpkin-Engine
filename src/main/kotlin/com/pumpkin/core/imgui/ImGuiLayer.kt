package com.pumpkin.core.imgui

import com.pumpkin.core.event.Event
import com.pumpkin.core.event.EventCategory
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.stack
import com.pumpkin.core.window.Window
import glm_.vec4.Vec4
import imgui.*
import imgui.classes.Context
import imgui.classes.Style
import imgui.font.Font
import imgui.impl.gl.ImplGL3
import imgui.impl.glfw.ImplGlfw
import org.lwjgl.glfw.GLFW
import uno.glfw.glfw

class ImGuiLayer : Layer("ImGui") {
    private var showDemoWindow = false
    private var lightMode = false
    //set(value) {if (value) ImGui.styleColorsLight(style) else ImGui.styleColorsDark(style); field = value}

    private lateinit var context: Context
    private lateinit var implGlfw: ImplGlfw
    private lateinit var implGL3: ImplGL3

    private var font: Font? = null
    private lateinit var style: Style

    var blockEvents = true

    override fun onAttach() {
        context = Context()

        stack {
            ImGui.io.configFlags = ImGui.io.configFlags or ConfigFlag.NavEnableKeyboard     // Enable Keyboard Controls
            ImGui.io.configFlags = ImGui.io.configFlags or ConfigFlag.NavEnableGamepad    // Enable Gamepad Controls
            ImGui.io.configFlags = ImGui.io.configFlags or ConfigFlag.DockingEnable         // Enable Docking
            //ImGui.io.configFlags = ImGui.io.configFlags or ConfigFlag.ViewportsEnable

            if (lightMode) ImGui.styleColorsLight()
            else ImGui.styleColorsDark()

            style = context.style
            style.windowRounding = 0f
            style.colors[Col.WindowBg].w = 1f
            style.frameRounding = 3.33f

            run {
            /*val colors = ImGui.style.colors
            colors[Col.Text]                    = Vec4(1.00f, 1.00f, 1.00f, 1.00f)
            colors[Col.TextDisabled]            = Vec4(0.50f, 0.50f, 0.50f, 1.00f)
            colors[Col.WindowBg]                = Vec4(0.09f, 0.09f, 0.09f, 1.00f)
            colors[Col.ChildBg]                 = Vec4(0.09f, 0.09f, 0.09f, 0.00f)
            colors[Col.PopupBg]                 = Vec4(0.08f, 0.08f, 0.08f, 0.94f)
            colors[Col.Border]                  = Vec4(0.43f, 0.43f, 0.50f, 0.50f)
            colors[Col.FrameBg]                 = Vec4(0.16f, 0.16f, 0.16f, 0.54f)
            colors[Col.FrameBgHovered]          = Vec4(0.55f, 0.55f, 0.55f, 0.40f)
            colors[Col.FrameBgActive]           = Vec4(0.55f, 0.55f, 0.55f, 0.67f)
            colors[Col.TitleBg]                 = Vec4(0.04f, 0.04f, 0.04f, 1.00f)
            colors[Col.TitleBgActive]           = Vec4(0.31f, 0.31f, 0.31f, 1.00f)
            colors[Col.TitleBgCollapsed]        = Vec4(0.00f, 0.00f, 0.00f, 0.51f)
            colors[Col.MenuBarBg]               = Vec4(0.14f, 0.14f, 0.14f, 1.00f)
            colors[Col.ScrollbarBg]             = Vec4(0.02f, 0.02f, 0.02f, 0.53f)
            colors[Col.ScrollbarGrab]           = Vec4(0.31f, 0.31f, 0.31f, 1.00f)
            colors[Col.ScrollbarGrabHovered]    = Vec4(0.41f, 0.41f, 0.41f, 1.00f)
            colors[Col.ScrollbarGrabActive]     = Vec4(0.51f, 0.51f, 0.51f, 1.00f)
            colors[Col.CheckMark]               = Vec4(0.26f, 0.59f, 0.98f, 1.00f)
            colors[Col.SliderGrab]              = Vec4(0.24f, 0.52f, 0.88f, 1.00f)
            colors[Col.SliderGrabActive]        = Vec4(0.26f, 0.59f, 0.98f, 1.00f)
            colors[Col.Button]                  = Vec4(0.39f, 0.39f, 0.39f, 0.40f)
            colors[Col.ButtonHovered]           = Vec4(0.39f, 0.39f, 0.39f, 1.00f)
            colors[Col.ButtonActive]            = Vec4(0.39f, 0.39f, 0.39f, 1.00f)
            colors[Col.Header]                  = Vec4(0.15f, 0.15f, 0.15f, 0.31f)
            colors[Col.HeaderHovered]           = Vec4(0.15f, 0.15f, 0.15f, 0.80f)
            colors[Col.HeaderActive]            = Vec4(0.15f, 0.15f, 0.15f, 1.00f)
            colors[Col.Separator]               = Vec4(0.43f, 0.43f, 0.50f, 0.50f)
            colors[Col.SeparatorHovered]        = Vec4(0.10f, 0.40f, 0.75f, 0.78f)
            colors[Col.SeparatorActive]         = Vec4(0.10f, 0.40f, 0.75f, 1.00f)
            colors[Col.ResizeGrip]              = Vec4(0.00f, 0.00f, 0.00f, 0.00f)
            colors[Col.ResizeGripHovered]       = Vec4(0.98f, 0.98f, 0.98f, 0.67f)
            colors[Col.ResizeGripActive]        = Vec4(0.98f, 0.98f, 0.98f, 0.95f)
            colors[Col.Tab]                     = Vec4(0.16f, 0.16f, 0.16f, 0.86f)
            colors[Col.TabHovered]              = Vec4(0.26f, 0.59f, 0.98f, 0.80f)
            colors[Col.TabActive]               = Vec4(0.20f, 0.41f, 0.68f, 1.00f)
            colors[Col.TabUnfocused]            = Vec4(0.07f, 0.10f, 0.15f, 0.97f)
            colors[Col.TabUnfocusedActive]      = Vec4(0.14f, 0.26f, 0.42f, 1.00f)
            colors[Col.DockingPreview]          = Vec4(0.26f, 0.59f, 0.98f, 0.70f)
            colors[Col.DockingEmptyBg]          = Vec4(0.00f, 0.00f, 0.00f, 1.00f)
            colors[Col.PlotLines]               = Vec4(0.61f, 0.61f, 0.61f, 1.00f)
            colors[Col.PlotLinesHovered]        = Vec4(1.00f, 0.43f, 0.35f, 1.00f)
            colors[Col.TextSelectedBg]          = Vec4(0.26f, 0.59f, 0.98f, 0.35f)
            colors[Col.NavHighlight]            = Vec4(0.26f, 0.59f, 0.98f, 1.00f)
            colors[Col.ModalWindowDimBg]        = Vec4(0.80f, 0.80f, 0.80f, 0.35f)
            }*/
            val colors = ImGui.style.colors
            colors[Col.Text]                    = Vec4(1.00, 1.00, 1.00, 1.00)
            colors[Col.TextDisabled]            = Vec4(0.50, 0.50, 0.50, 1.00)
            colors[Col.WindowBg]                = Vec4(0.09, 0.09, 0.09, 1.00)
            colors[Col.ChildBg]                 = Vec4(0.09, 0.09, 0.09, 0.00)
            colors[Col.PopupBg]                 = Vec4(0.08, 0.08, 0.08, 0.94)
            colors[Col.Border]                  = Vec4(0.43, 0.43, 0.50, 0.50)
            colors[Col.FrameBg]                 = Vec4(0.16, 0.16, 0.16, 0.54)
            colors[Col.FrameBgHovered]          = Vec4(0.55, 0.55, 0.55, 0.40)
            colors[Col.FrameBgActive]           = Vec4(0.55, 0.55, 0.55, 0.67)
            colors[Col.TitleBg]                 = Vec4(0.04, 0.04, 0.04, 1.00)
            colors[Col.TitleBgActive]           = Vec4(0.88, 0.35, 0.19, 1.00)
            colors[Col.TitleBgCollapsed]        = Vec4(0.00, 0.00, 0.00, 0.51)
            colors[Col.MenuBarBg]               = Vec4(0.14, 0.14, 0.14, 1.00)
            colors[Col.ScrollbarBg]             = Vec4(0.02, 0.02, 0.02, 0.53)
            colors[Col.ScrollbarGrab]           = Vec4(0.31, 0.31, 0.31, 1.00)
            colors[Col.ScrollbarGrabHovered]    = Vec4(0.41, 0.41, 0.41, 1.00)
            colors[Col.ScrollbarGrabActive]     = Vec4(0.51, 0.51, 0.51, 1.00)
            colors[Col.CheckMark]               = Vec4(0.98, 0.43, 0.26, 1.00)
            colors[Col.SliderGrab]              = Vec4(0.88, 0.39, 0.24, 1.00)
            colors[Col.SliderGrabActive]        = Vec4(0.98, 0.43, 0.26, 1.00)
            colors[Col.Button]                  = Vec4(0.39, 0.39, 0.39, 0.40)
            colors[Col.ButtonHovered]           = Vec4(0.39, 0.39, 0.39, 1.00)
            colors[Col.ButtonActive]            = Vec4(0.39, 0.39, 0.39, 1.00)
            colors[Col.Header]                  = Vec4(0.15, 0.15, 0.15, 0.31)
            colors[Col.HeaderHovered]           = Vec4(0.15, 0.15, 0.15, 0.80)
            colors[Col.HeaderActive]            = Vec4(0.15, 0.15, 0.15, 1.00)
            colors[Col.Separator]               = Vec4(0.50, 0.46, 0.43, 0.50)
            colors[Col.SeparatorHovered]        = Vec4(0.75, 0.25, 0.10, 0.78)
            colors[Col.SeparatorActive]         = Vec4(0.75, 0.25, 0.10, 1.00)
            colors[Col.ResizeGrip]              = Vec4(0.00, 0.00, 0.00, 0.00)
            colors[Col.ResizeGripHovered]       = Vec4(0.98, 0.98, 0.98, 0.67)
            colors[Col.ResizeGripActive]        = Vec4(0.98, 0.98, 0.98, 0.95)
            colors[Col.Tab]                     = Vec4(0.16, 0.16, 0.16, 0.86)
            colors[Col.TabHovered]              = Vec4(0.98, 0.43, 0.26, 0.80)
            colors[Col.TabActive]               = Vec4(0.68, 0.31, 0.20, 1.00)
            colors[Col.TabUnfocused]            = Vec4(0.15, 0.10, 0.07, 0.97)
            colors[Col.TabUnfocusedActive]      = Vec4(0.42, 0.22, 0.14, 1.00)
            colors[Col.DockingPreview]          = Vec4(0.98, 0.43, 0.26, 0.70)
            colors[Col.DockingEmptyBg]          = Vec4(0.00, 0.00, 0.00, 1.00)
            colors[Col.PlotLines]               = Vec4(0.61, 0.61, 0.61, 1.00)
            colors[Col.PlotLinesHovered]        = Vec4(1.00, 0.43, 0.35, 1.00)
            colors[Col.TextSelectedBg]          = Vec4(0.26, 0.59, 0.98, 0.35)
            colors[Col.NavHighlight]            = Vec4(0.26, 0.59, 0.98, 1.00)
            colors[Col.ModalWindowDimBg]        = Vec4(0.80, 0.80, 0.80, 0.35)
            }
        }

        implGlfw = ImplGlfw.initForOpenGL(Window.getWindow().window, true)/*(Window.getWindow().window)*/
        implGL3 = ImplGL3()

        font = ImGui.io.fonts.addFontFromFileTTF("fonts/Roboto-Medium.ttf", 16.0f)
    }

    override fun onDetach() {
        implGlfw.shutdown()
        implGL3.shutdown()
        context.destroy()
    }

    override fun onImGuiRender() {
        if (showDemoWindow) {
            ImGui.showDemoWindow(::showDemoWindow)
        }
    }

    override fun onEvent(event: Event) {
        if (blockEvents) {
            event.handled = event.handled or event.isInCategory(EventCategory.Mouse) and ImGui.io.wantCaptureMouse
            event.handled = event.handled or event.isInCategory(EventCategory.Keyboard) and ImGui.io.wantCaptureKeyboard
        }
    }

    fun begin() {
        implGL3.newFrame()
        implGlfw.newFrame()
        ImGui.newFrame()
    }

    fun end() {
        ImGui.render()

        ImGui.drawData?.let { implGL3.renderDrawData(it) }

        if (ImGui.io.configFlags has ConfigFlag.ViewportsEnable) {
            val backupCurrentContext = glfw.currentContext
            ImGui.updatePlatformWindows()
            ImGui.renderPlatformWindowsDefault()
            GLFW.glfwMakeContextCurrent(backupCurrentContext.value)
        }
    }
}
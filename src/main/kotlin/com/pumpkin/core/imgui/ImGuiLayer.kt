package com.pumpkin.core.imgui

import com.pumpkin.core.event.Event
import com.pumpkin.core.event.EventCategory
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.stack
import com.pumpkin.core.window.Window
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
        event.handled = event.handled or event.isInCategory(EventCategory.Mouse) and ImGui.io.wantCaptureMouse
        event.handled = event.handled or event.isInCategory(EventCategory.Keyboard) and ImGui.io.wantCaptureKeyboard
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
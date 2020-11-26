package com.pumpkin.imgui

import com.pumpkin.layer.Layer
import com.pumpkin.window.window
import imgui.*
import imgui.classes.Context
import imgui.font.Font
import imgui.impl.gl.ImplGL3
import imgui.impl.glfw.ImplGlfw
import org.lwjgl.glfw.GLFW
import uno.glfw.glfw

class ImGuiLayer : Layer("ImGui") {
    private var showDemoWindow = true

    private lateinit var context: Context
    private lateinit var implGlfw: ImplGlfw
    private lateinit var implGL3: ImplGL3

    private var font: Font? = null

    override fun onAttach() {
        context = Context()

        ImGui.io.configFlags = ImGui.io.configFlags or ConfigFlag.NavEnableKeyboard     // Enable Keyboard Controls
//        ImGui.io.configFlags = io.configFlags or ConfigFlag.NavEnableGamepad    // Enable Gamepad Controls
        ImGui.io.configFlags = ImGui.io.configFlags or ConfigFlag.DockingEnable         // Enable Docking
        ImGui.io.configFlags = ImGui.io.configFlags or ConfigFlag.ViewportsEnable

        ImGui.styleColorsDark()

        val style = context.style
        if (ImGui.io.configFlags has ConfigFlag.ViewportsEnable) {
            style.windowRounding = 0f
            style.colors[Col.WindowBg].w = 1f
        }

        implGlfw = ImplGlfw.initForOpenGL(window.window, true)
        implGL3 = ImplGL3()

        font = ImGui.io.fonts.addFontFromFileTTF("fonts/DroidSans.ttf", 16.0f);
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
        font?.let { ImGui.pushFont(it) }
        font?.let { ImGui.setCurrentFont(it) }
        ImGui.begin("Test")
        ImGui.text("Hello world!")
        ImGui.end()
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
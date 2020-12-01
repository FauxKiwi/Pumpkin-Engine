package com.pumpkin.core.imgui

import com.pumpkin.core.Timestep
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.logDebug
import com.pumpkin.core.window.Window
import glm_.vec2.Vec2
import imgui.*
import imgui.classes.Context
import imgui.classes.Style
import imgui.demo.ShowDemoWindowWidgets
import imgui.font.Font
import imgui.impl.gl.ImplGL3
import imgui.impl.glfw.ImplGlfw
import kotlin.math.cos
import kotlin.math.sin

class ImGuiLayer : Layer("ImGui") {
    private var showDemoWindow = false

    private lateinit var context: Context
    private lateinit var implGlfw: ImplGlfw
    private lateinit var implGL3: ImplGL3

    private var font: Font? = null
    private lateinit var style: Style

    private var values0 = FloatArray(200) {0f}
    private var refreshTime = 0.0
    private var valuesOffset = 0

    override fun onAttach() {
        context = Context()

        ImGui.io.configFlags = ImGui.io.configFlags or ConfigFlag.NavEnableKeyboard     // Enable Keyboard Controls
        ImGui.io.configFlags = ImGui.io.configFlags or ConfigFlag.NavEnableGamepad    // Enable Gamepad Controls
        //ImGui.io.configFlags = ImGui.io.configFlags or ConfigFlag.DockingEnable         // Enable Docking
        //ImGui.io.configFlags = ImGui.io.configFlags or ConfigFlag.ViewportsEnable

        ImGui.styleColorsDark()

        style = context.style
        style.windowRounding = 0f
        style.colors[Col.WindowBg].w = 1f
        style.frameRounding = 3.33f

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
        with(ImGui) {
            begin("Framerate")
            text("Your Framerate is: ${ImGui.io.framerate}")
            text("Update time: ${1000 / ImGui.io.framerate}")
            val overlay = "avg ${String.format("%.3f", values0.average())}"
            plotLines("Frametime", values0, valuesOffset, overlay, 0f, 60f, Vec2(0f, 80f))
            checkbox("Demo", ::showDemoWindow)
            end()
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

        /*if (ImGui.io.configFlags has ConfigFlag.ViewportsEnable) {
            val backupCurrentContext = glfw.currentContext
            ImGui.updatePlatformWindows()
            ImGui.renderPlatformWindowsDefault()
            GLFW.glfwMakeContextCurrent(backupCurrentContext.value)
        }*/
    }

    override fun onUpdate(ts: Timestep) {
        if (refreshTime == 0.0) refreshTime =
            ImGui.time
        while (refreshTime < ImGui.time) {
            values0[valuesOffset] = 1000 / ImGui.io.framerate
            valuesOffset = (valuesOffset + 1) % values0.size
            refreshTime += 1f / 60f
        }
    }
}
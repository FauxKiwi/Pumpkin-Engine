package com.pumpkin.core.imgui

import com.pumpkin.core.event.Event
import com.pumpkin.core.event.EventCategory
import com.pumpkin.core.event.KeyPressedEvent
import com.pumpkin.core.input.KeyCode
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.settings.Settings
import com.pumpkin.core.window.Window
import glm_.vec2.Vec2
import imgui.ConfigFlag
import imgui.ImGui
import imgui.classes.Context
import imgui.has
import imgui.impl.gl.ImplGL3
import imgui.impl.glfw.ImplGlfw
import imgui.or
import org.lwjgl.glfw.GLFW.glfwGetCurrentContext
import org.lwjgl.glfw.GLFW.glfwMakeContextCurrent
import uno.glfw.GlfwWindow

class ImGuiLayer : Layer("ImGui") {
    private var showDemoWindow = false
    private var lightMode = false
    //set(value) {if (value) ImGui.styleColorsLight(style) else ImGui.styleColorsDark(style); field = value}

    private lateinit var context: Context
    private lateinit var implGlfw: ImplGlfw
    private lateinit var implGL3: ImplGL3

    var blockEvents = true

    override fun onAttach() {
        context = Context()

        ImGui.io.configFlags = ImGui.io.configFlags or ConfigFlag.NavEnableKeyboard     // Enable Keyboard Controls
        ImGui.io.configFlags = ImGui.io.configFlags or ConfigFlag.NavEnableGamepad    // Enable Gamepad Controls
        ImGui.io.configFlags = ImGui.io.configFlags or ConfigFlag.DockingEnable         // Enable Docking
        //ImGui.io.configFlags = ImGui.io.configFlags or ConfigFlag.ViewportsEnable

        if (lightMode) ImGui.styleColorsLight()
        else ImGui.styleColorsDark()

        Settings.setTheme(0)
        ImGui.style.windowTitleAlign = Vec2(0.02f, 0.5f)

        implGlfw = ImplGlfw.initForOpenGL(GlfwWindow.from(Window.getWindow().window), true)/*(Window.getWindow().window)*/
        implGL3 = ImplGL3()

        ImGui.io.fonts.addFontFromFileTTF("fonts/Roboto-Black.ttf", 16f) // Bold
        ImGui.io.fonts.addFontFromFileTTF("fonts/FontAwesome.ttf", 16f, glyphRanges = arrayOf(0xf368..0xf375)) // Icons
        ImGui.io.fontDefault = ImGui.io.fonts.addFontFromFileTTF("fonts/Roboto-Regular.ttf", 16f) // Default
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
        if (event is KeyPressedEvent && (event.keyCode == KeyCode.LEFT_CONTROL || event.keyCode == KeyCode.RIGHT_CONTROL))
        if (blockEvents) {
            event.handled = event.handled or (event.isInCategory(EventCategory.Mouse) and ImGui.io.wantCaptureMouse)
            event.handled = event.handled or (event.isInCategory(EventCategory.Keyboard) and ImGui.io.wantCaptureKeyboard)
        }
    }

    fun begin() {
        implGL3.newFrame()
        implGlfw.newFrame()
        ImGui.newFrame()
        ImGuizmo.newFrame()
    }

    fun end() {
        ImGui.render()

        ImGui.drawData?.let { implGL3.renderDrawData(it) }

        if (ImGui.io.configFlags has ConfigFlag.ViewportsEnable) {
            val backupCurrentContext = glfwGetCurrentContext() //glfw.currentContext
            ImGui.updatePlatformWindows()
            ImGui.renderPlatformWindowsDefault()
            glfwMakeContextCurrent(backupCurrentContext)
        }
    }
}
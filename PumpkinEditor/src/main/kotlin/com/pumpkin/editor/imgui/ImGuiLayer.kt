package com.pumpkin.editor.imgui

import com.pumpkin.core.event.Event
import com.pumpkin.core.event.EventCategory
import com.pumpkin.core.event.KeyPressedEvent
import com.pumpkin.core.event.MouseScrolledEvent
import com.pumpkin.core.input.KeyCode
import com.pumpkin.core.layer.Layer
import com.pumpkin.editor.settings.Settings
import com.pumpkin.core.window.Window
import imgui.ImFont
import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import imgui.type.ImBoolean
import org.lwjgl.glfw.GLFW.glfwGetCurrentContext
import org.lwjgl.glfw.GLFW.glfwMakeContextCurrent

class ImGuiLayer : Layer("ImGui") {
    private var showDemoWindow = ImBoolean(false)
    private var lightMode = false
    //set(value) {if (value) ImGui.styleColorsLight(style) else ImGui.styleColorsDark(style); field = value}

    //private lateinit var context: Context
    private lateinit var implGlfw: ImGuiImplGlfw
    private lateinit var implGL3: ImGuiImplGl3

    var blockEvents = true

    val fonts = mutableListOf<ImFont>()

    override fun onAttach() {
        /*context =*/ ImGui.createContext()

        val io = ImGui.getIO()
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard     // Enable Keyboard Controls
        /*io.configFlags = ImGui.io.configFlags*/ //or ImGuiConfigFlags.NavEnableGamepad    // Enable Gamepad Controls
        /*io.configFlags = ImGui.io.configFlags*/ or ImGuiConfigFlags.DockingEnable         // Enable Docking
        /*ImGui.io.configFlags = ImGui.io.configFlags*/ or ImGuiConfigFlags.ViewportsEnable)

        if (lightMode) ImGui.styleColorsLight()
        else ImGui.styleColorsDark()

        Settings.setTheme(0)
        ImGui.getStyle().setWindowTitleAlign(0.02f, 0.5f)

        implGlfw = ImGuiImplGlfw()
        implGlfw.init(Window.getWindow().getWindow(), true)
        implGL3 = ImGuiImplGl3()

        io.fonts.addFontFromMemoryTTF(loadFromResources("fonts/Roboto-Black.ttf"), 16f).also { fonts.add(it) } // Bold
        /*1*/io.fonts.addFontFromMemoryTTF(loadFromResources("fonts/FontAwesome-Solid.ttf"), 14f,
            shortArrayOf(0x001e, 0x007a, 0xe048.toShort(), 0xf8e8.toShort())).also { fonts.add(it) }
        /*2*/io.fonts.addFontFromMemoryTTF(loadFromResources("fonts/FontAwesome-Regular.ttf"), 14f,
            shortArrayOf(0x001e, 0x007a, 0xe048.toShort(), 0xf8e8.toShort())).also { fonts.add(it) } // Icons
        /*3*/io.fonts.addFontFromMemoryTTF(loadFromResources("fonts/FontAwesome-Brands.ttf"), 14f,
            shortArrayOf(0x001e, 0x007a, 0xe048.toShort(), 0xf8e8.toShort())).also { fonts.add(it) }
        /*4*/io.fonts.addFontFromMemoryTTF(loadFromResources("fonts/FontAwesome-Brands.ttf"), 40f,
            shortArrayOf(0x001e, 0x007a, 0xe048.toShort(), 0xf8e8.toShort())).also { fonts.add(it) }
        io.setFontDefault(io.fonts.addFontFromMemoryTTF(loadFromResources("fonts/Roboto-Regular.ttf"), 16f).also { fonts.add(it) }) // Default

        implGL3.init()
    }

    override fun onDetach() {
        implGlfw.dispose() //shutdown()
        implGL3.dispose() //shutdown()
        ImGui.destroyContext() //context.destroy()
    }

    override fun onImGuiRender() {
        if (showDemoWindow.get()) {
            ImGui.showDemoWindow(showDemoWindow)
        }
    }

    override fun onEvent(event: Event) {
        if (event is KeyPressedEvent && (event.keyCode == KeyCode.LEFT_CONTROL || event.keyCode == KeyCode.RIGHT_CONTROL))
        if (blockEvents) {
            event.handled = event.handled or (event.isInCategory(EventCategory.Mouse) and ImGui.getIO().wantCaptureMouse)
            event.handled = event.handled or (event.isInCategory(EventCategory.Keyboard) and ImGui.getIO().wantCaptureKeyboard)
        }
        event.handled = event.handled || (event is MouseScrolledEvent && blockEvents)
    }

    fun begin() {
        //implGL3.newFrame()
        implGlfw.newFrame()
        ImGui.newFrame()
        ImGuizmo.newFrame()
    }

    fun end() {
        ImGui.render()

        implGL3.renderDrawData(ImGui.getDrawData()) //ImGui.drawData?.let { implGL3.renderDrawData(it) }

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            val backupCurrentContext = glfwGetCurrentContext() //glfw.currentContext
            ImGui.updatePlatformWindows()
            ImGui.renderPlatformWindowsDefault()
            glfwMakeContextCurrent(backupCurrentContext)
        }
    }
}
package com.pumpkin.editor

import com.pumpkin.core.*
import com.pumpkin.core.event.Event
import com.pumpkin.core.event.KeyPressedEvent
import com.pumpkin.editor.imgui.ImGuiProfiler
import com.pumpkin.editor.imgui.ImGuizmo
import com.pumpkin.core.input.Input
import com.pumpkin.core.input.KeyCode
import com.pumpkin.core.layer.Layer
import com.pumpkin.editor.panels.SceneHierarchyPanel
import com.pumpkin.core.renderer.Framebuffer
import com.pumpkin.core.renderer.FramebufferSpecification
import com.pumpkin.core.renderer.ProjectionType
import com.pumpkin.core.scene.*
import com.pumpkin.editor.settings.Settings
import glm.Mat4
import glm.Vec2
import imgui.ImGui
import imgui.ImVec4
import imgui.flag.*
import imgui.type.ImBoolean

class EditorLayer : Layer("Editor") {
    private var dockspaceOpen = true
    private var optFullscreenPersistent = true
    private var dockspaceFlags: Int = ImGuiDockNodeFlags.None

    private val framebuffer = Framebuffer.create(FramebufferSpecification(1280, 720))
    private var viewportSize = Vec2()
    private var viewportFocused = false
    private var viewportHovered = false

    private var activeScene by Reference<Scene>()
    private lateinit var sceneHierarchyPanel: SceneHierarchyPanel
    private lateinit var sceneSerializer: SceneSerializer
    private val editorCamera = EditorCamera()

    private var gizmoType = -1

    override fun onAttach() {
        activeScene = Scene()
        sceneHierarchyPanel = SceneHierarchyPanel(activeScene)
        sceneSerializer = SceneSerializer(activeScene)

        ImGuiProfiler.onAttach()
    }

    override fun onDetach() {
        ImGuiProfiler.onDetach()
    }

    override fun onUpdate(ts: Timestep) {
        ImGuiProfiler.onUpdate(ts)
        if (viewportSize.x > 0f && viewportSize.y > 0f &&
            (framebuffer.specification.width != viewportSize.x.toInt() || framebuffer.specification.height != viewportSize.y.toInt())
        ) {
            framebuffer.resize(viewportSize.x.toInt(), viewportSize.y.toInt())
            activeScene.onViewportResize(viewportSize.x.toInt(), viewportSize.y.toInt())
            editorCamera.setViewportSize(viewportSize.x, viewportSize.y)
        }
        editorCamera.onUpdate(ts)

        framebuffer.bind()

        activeScene.onUpdateEditor(ts, editorCamera)
        //activeScene.onUpdateRuntime(ts)

        framebuffer.unbind()

        Application.get().getImGuiLayer().begin()
        onImGuiRender()
        Application.get().getImGuiLayer().end()
    }

    override fun onImGuiRender()  {
        ImGui.setNextWindowPos(0f, 22f)
        ImGui.setNextWindowSize(ImGui.getMainViewport().sizeX, 38f)
        ImGui.begin("Toolbar", ImBoolean(true), ImGuiWindowFlags.NoCollapse or ImGuiWindowFlags.NoTitleBar or ImGuiWindowFlags.NoScrollbar
                or ImGuiWindowFlags.NoMove or ImGuiWindowFlags.NoResize or ImGuiWindowFlags.NoDocking or ImGuiWindowFlags.NoSavedSettings)

        //ImGui.pushMultiItemsWidths(3, calcItemWidth())
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0f, 0f)

        val lineHeight = ImGui.getFont().fontSize + ImGui.getStyle().framePaddingY * 2f
        val buttonSize = Vec2(lineHeight + 3.0f, lineHeight)

        //val iconFont = ImGui.io.fonts.fonts[1]
        //pushFont(iconFont)
        /*val gizmoType0 = gizmoType
        if (gizmoType0 == -1) ImGui.pushStyleColor(ImGuiCol.Button, ImGui.getStyleColorVec4(ImGuiCol.ButtonActive))
        if (ImGui.button("Q", buttonSize)) gizmoType = -1; if (gizmoType0 == -1) ImGui.popStyleColor()
        if (gizmoType0 == 1) ImGui.pushStyleColor(ImGuiCol.Button, ImGui.getStyleColorVec4(Col.ButtonActive))
        ImGui.sameLine(); if (ImGui.button("W", buttonSize)) gizmoType = 1; if (gizmoType0 == 1) ImGui.popStyleColor()
        if (gizmoType0 == 2) ImGui.pushStyleColor(ImGuiCol.Button, ImGui.getStyleColorVec4(Col.ButtonActive))
        ImGui.sameLine(); if (ImGui.button("E", buttonSize)) gizmoType = 2; if (gizmoType0 == 2) ImGui.popStyleColor()
        if (gizmoType0 == 3) ImGui.pushStyleColor(ImGuiCol.Button, ImGui.getStyleColorVec4(Col.ButtonActive))
        ImGui.sameLine(); if (ImGui.button("R", buttonSize)) gizmoType = 3; if (gizmoType0 == 3) ImGui.popStyleColor()*/
        //popFont()

        //ImGui.popItemWidth()
        ImGui.popStyleVar()

        ImGui.end()


        val optFullscreen = optFullscreenPersistent

        var ImGuiWindowFlagss = ImGuiWindowFlags.MenuBar or ImGuiWindowFlags.NoDocking
        if (optFullscreen) {
            val viewport = ImGui.getMainViewport()
            ImGui.setNextWindowPos(viewport.posX, viewport.posY + 60f)
            ImGui.setNextWindowSize(viewport.sizeX, viewport.sizeY)
            ImGui.setNextWindowViewport(viewport.id)
            ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0f)
            ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0f)
            ImGuiWindowFlagss =
                ImGuiWindowFlagss or ImGuiWindowFlags.NoTitleBar or ImGuiWindowFlags.NoCollapse or ImGuiWindowFlags.NoResize or ImGuiWindowFlags.NoMove
            ImGuiWindowFlagss = ImGuiWindowFlagss or ImGuiWindowFlags.NoBringToFrontOnFocus or ImGuiWindowFlags.NoNavFocus
        }
        if (dockspaceFlags and ImGuiDockNodeFlags.PassthruCentralNode != 0)
            ImGuiWindowFlagss = ImGuiWindowFlagss or ImGuiWindowFlags.NoBackground

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f)
        ImGui.begin("DockSpace Demo", ImBoolean(dockspaceOpen), ImGuiWindowFlagss)
        ImGui.popStyleVar()

        if (optFullscreen)
            ImGui.popStyleVar(2)

        menuBar()

        val minWinSizeX = ImGui.getStyle().windowMinSizeX
        ImGui.getStyle().setWindowMinSize(370f, ImGui.getStyle().windowMinSizeY)
        if (ImGui.getIO().configFlags and ImGuiConfigFlags.DockingEnable != 0) {
            val dockspaceID = ImGui.getID("MyDockSpace")
            ImGui.dockSpace(dockspaceID, 0f, 0f, dockspaceFlags)
        }
        ImGui.getStyle().setWindowMinSize(minWinSizeX, ImGui.getStyle().windowMinSizeY)

        //val cr = Vec2(ImGui.getContentRegionMaxX(), ImGui.getContentRegionMaxY() - 60f)

        //var pos = cr * Vec2(0f, 0.75f) + Vec2(0f, 59f)
        //ImGui.setNextWindowPos(pos.x, pos.y)
        //var size = cr * Vec2(0.2f, 0.25f)
        //ImGui.setNextWindowSize(size.x, size.y)
        ImGuiProfiler.onImGuiRender()

        sceneHierarchyPanel.onImGuiRender()

        Settings.onImGuiRender()
        if (Settings.uEditorCameraView) { editorCamera.fov = Settings.editorCameraFov; editorCamera.updateProjection(); Settings.uEditorCameraView = false; }

        //pos = cr * Vec2(0.2f, 0f) + Vec2(0f, 60f)
        //ImGui.setNextWindowPos(pos.x, pos.y)
        //size = cr * Vec2(0.8f, 1f)
        //ImGui.setNextWindowSize(size.x, size.y)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f)
        //ImGui.pushStyleColor(ImGuiCol.MenuBarBg, ImGui.getStyleColorVec4(ImGuiStyleVar.TitleBg, ImVec4()))
        ImGui.begin("Viewport", /**/ImBoolean(true), ImGuiWindowFlags.NoMove or ImGuiWindowFlags.NoTitleBar or ImGuiWindowFlags.MenuBar or ImGuiWindowFlags.NoCollapse)
        //ImGui.popStyleColor()

        viewportMenuBar()

        viewportSize = Vec2(ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY())
        viewportFocused = ImGui.isWindowFocused()
        viewportHovered = ImGui.isWindowHovered()
        Application.get().getImGuiLayer().blockEvents = !viewportHovered && !viewportFocused
        ImGui.image(framebuffer.colorAttachmentID, viewportSize.x, viewportSize.y, 0f, 1f, 1f, 0f)

        // GIZMOS
        val selectedEntity = sceneHierarchyPanel.selectionContext
        if (selectedEntity != null && gizmoType != -1) {
            ImGuizmo.setOrthographic(false)
            ImGuizmo.setDrawlist()

            ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY() + 22, ImGui.getWindowWidth(), ImGui.getWindowHeight() - 22)

            // Entity transform
            val tc = activeScene.registry.get<TransformComponent>(selectedEntity)

            // Snapping
            val snap = Input.isKeyPressed(KeyCode.LEFT_CONTROL)
            var snapValue = 0.5f // Snap to 0.5m for translation/scale
            // Snap to 45 degrees for rotation
            if (gizmoType == ImGuizmo.OPERATION.ROTATE.ordinal)
                snapValue = 45.0f

            val snapValues = floatArrayOf(snapValue, snapValue, snapValue)

            ImGuizmo.manipulate(
                Mat4(editorCamera.viewProjection.array),
                ImGuizmo.OPERATION.values()[gizmoType], ImGuizmo.MODE.LOCAL, tc,
                null, if (snap) snapValues else null
            )
        }
        ImGui.end()
        ImGui.popStyleVar()

        ImGui.end()

    }

    override fun onEvent(event: Event) {
        editorCamera.onEvent(event)
        if (event is KeyPressedEvent) {
            keybinds(event)
            when (event.keyCode) {
                KeyCode.Q -> gizmoType = -1
                KeyCode.W -> gizmoType = ImGuizmo.OPERATION.TRANSLATE.ordinal
                KeyCode.E -> gizmoType = ImGuizmo.OPERATION.ROTATE.ordinal
                KeyCode.R -> gizmoType = ImGuizmo.OPERATION.SCALE.ordinal
            }
        }
    }

private fun menuBar() {
    if (ImGui.beginMainMenuBar()) {
        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("New", "Ctrl+N")) { newScene() }
            if (ImGui.menuItem("Open...", "Ctrl+O")) { openScene() }
            if (ImGui.menuItem("Open Recent")) {}
            if (ImGui.menuItem("Close Project", "Ctrl+W")) {}
            ImGui.separator()
            if (ImGui.menuItem("Settings", "Ctrl+Alt+S")) { Settings.open() }
            if (ImGui.menuItem("Build Settings", "Ctrl+Alt+B")) {}
            ImGui.separator()
            if (ImGui.menuItem("Save Scene", "Ctrl+S")) { saveSceneAs() }
            if (ImGui.menuItem("Save As...", "Ctrl+Shift+S")) { saveSceneAs() }
            if (ImGui.menuItem("Reload")) {}
            ImGui.separator()
            if (ImGui.menuItem("Exit")) { Application.get().close() }
            ImGui.endMenu()
        }

        if (ImGui.beginMenu("Edit")) {
            if (ImGui.menuItem("Undo", "Ctrl+Z")) {}
            if (ImGui.menuItem("Redo", "Ctrl+Y")) {}
            ImGui.separator()
            if (ImGui.menuItem("Cut", "Ctrl+X")) {}
            if (ImGui.menuItem("Copy", "Ctrl+C")) {}
            if (ImGui.menuItem("Paste", "Ctrl+V")) {}
            if (ImGui.menuItem("Delete", "Del")) {}
            ImGui.separator()
            if (ImGui.menuItem("Find", "Ctrl+F")) {}
            ImGui.endMenu()
        }

        if (ImGui.beginMenu("View")) {
            if (ImGui.menuItem("Windows")) {}
            if (ImGui.menuItem("Appearance")) {}
            ImGui.endMenu()
        }

        if (ImGui.beginMenu("Build & Run")) {
            if (ImGui.menuItem("Run")) {}
            if (ImGui.menuItem("Build")) {}
            ImGui.endMenu()
        }

        if (ImGui.beginMenu("Tools")) {
            if (ImGui.menuItem("Tasks")) {}
            ImGui.separator()
            //Plugins
            ImGui.endMenu()
        }

        if (ImGui.beginMenu("Help")) {
            if (ImGui.menuItem("Help")) {}
            ImGui.endMenu()
        }

        ImGui.endMainMenuBar()
    }
}

    private fun viewportMenuBar() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 4f, 4f)
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 4f, 8f)
        ImGui.beginMenuBar()

        if (ImGui.beginMenu("${editorCamera.sceneProjection}D")) {
            if (ImGui.menuItem("2D")) editorCamera.sceneProjection = 2
            if (ImGui.menuItem("3D")) editorCamera.sceneProjection = 3
            ImGui.endMenu()
        }

        if (ImGui.beginMenu(ProjectionType.projectionTypes[editorCamera.projectionType.ordinal])) {
            for (pt in ProjectionType.values()) {
                if (ImGui.menuItem(ProjectionType.projectionTypes[pt.ordinal])) editorCamera.projectionType = pt
            }
            ImGui.endMenu()
        }

        ImGui.endMenuBar()
        ImGui.popStyleVar(2)
    }

private fun keybinds(event: KeyPressedEvent) {
    if (event.repeatCount > 0) return

    val ctrl = Input.isKeyPressed(KeyCode.LEFT_CONTROL) || Input.isKeyPressed(KeyCode.RIGHT_CONTROL)
    if (!ctrl) return
    val shift = Input.isKeyPressed(KeyCode.LEFT_SHIFT) || Input.isKeyPressed(KeyCode.RIGHT_SHIFT)
    val alt = Input.isKeyPressed(KeyCode.LEFT_ALT) || Input.isKeyPressed(KeyCode.RIGHT_ALT)

    when (event.keyCode) {
        KeyCode.S ->
            if (!shift) {
                if (alt)
                    Settings.open()
                else
                    saveSceneAs()
            } else saveSceneAs()
        KeyCode.O ->
            if (!shift && !alt) openScene()
        KeyCode.N ->
            if (!shift && !alt) newScene()
    }
}

    fun newScene() {
        activeScene = Scene()
        sceneHierarchyPanel.context = activeScene
        sceneHierarchyPanel.selectionContext = null
        sceneSerializer.scene = activeScene
        activeScene.onViewportResize(viewportSize.x.toInt(), viewportSize.y.toInt())
    }

    fun openScene() {
        FileDialog.open("Open Scene", fileSelectorFilter)?.let {
            newScene()
            sceneSerializer.deserialize(it)
        }
    }

    fun saveSceneAs() {
        FileDialog.save("Save Scene", fileSelectorFilter)?.let {
            val name = if (!it.endsWith(".pumpkin")) "$it.pumpkin" else it
            sceneSerializer.serialize(name)
        }
    }
}
private val fileSelectorFilter = FileDialog.FileFilter("Scene (*.pumpkin)", "pumpkin")

class CameraController : ScriptableEntity() {
    var speed = 5f

    override fun onUpdate(ts: Timestep) {
        val transform = getComponent<TransformComponent>()
        val position = transform.position
        if (Input.isKeyPressed(KeyCode.A))
            position.x -= speed * ts
        if (Input.isKeyPressed(KeyCode.D))
            position.x += speed * ts
        if (Input.isKeyPressed(KeyCode.S))
            position.y -= speed * ts
        if (Input.isKeyPressed(KeyCode.W))
            position.y += speed * ts
        transform.position = position
    }
}
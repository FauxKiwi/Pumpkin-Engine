package com.pumpkin.editor

import com.pumpkin.core.*
import com.pumpkin.core.event.Event
import com.pumpkin.core.event.KeyPressedEvent
import com.pumpkin.editor.imgui.ImGuizmo
import com.pumpkin.core.input.Input
import com.pumpkin.core.input.KeyCode
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.renderer.Framebuffer
import com.pumpkin.core.renderer.FramebufferSpecification
import com.pumpkin.core.renderer.ProjectionType
import com.pumpkin.core.scene.*
import com.pumpkin.editor.imgui.ImGuiWindow
import com.pumpkin.editor.panels.*
import com.pumpkin.editor.project.Project
import com.pumpkin.editor.settings.Settings
import glm.Mat4
import glm.Vec2
import imgui.ImGui
import imgui.flag.*
import imgui.type.ImBoolean

// Project
internal var activeProject: Project? = Project("TestProject", ""/*TODO*/)//null

class EditorLayer : Layer("Editor") {
    private var dockspaceOpen = true
    private var optFullscreenPersistent = true
    private var dockspaceFlags: Int = ImGuiDockNodeFlags.None

    private val framebuffer = Framebuffer.create(FramebufferSpecification(1280, 720))
    private var viewportSize = Vec2()
    private var viewportFocused = false
    private var viewportHovered = false

    // Scene
    private var activeScene by Reference<Scene>()
    private lateinit var sceneSerializer: SceneSerializer
    private val editorCamera = EditorCamera()
    internal val showSceneView = ImBoolean(true)

    private var gizmoType = -1

    internal lateinit var panels: Panels
    private val menuBar = MenuBar(this)

    internal val imGuiDemo = ImBoolean(false)

    override fun onAttach() {
        activeScene = Scene()

        panels = Panels(activeScene)

        sceneSerializer = SceneSerializer(activeScene)
    }

    override fun onDetach() {
    }

    override fun onUpdate(ts: Timestep) {
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
        val viewport = ImGui.getMainViewport()

        val optFullscreen = optFullscreenPersistent

        var dockspaceWfs = ImGuiWindowFlags.NoDocking or ImGuiWindowFlags.MenuBar
        if (optFullscreen) {
            ImGui.setNextWindowPos(viewport.posX, viewport.posY)
            ImGui.setNextWindowSize(viewport.sizeX, viewport.sizeY)
            ImGui.setNextWindowViewport(viewport.id)
            ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0f)
            ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0f)
            dockspaceWfs =
                dockspaceWfs or ImGuiWindowFlags.NoTitleBar or ImGuiWindowFlags.NoCollapse or ImGuiWindowFlags.NoResize or ImGuiWindowFlags.NoMove
            dockspaceWfs = dockspaceWfs or ImGuiWindowFlags.NoBringToFrontOnFocus or ImGuiWindowFlags.NoNavFocus
        }
        if (dockspaceFlags and ImGuiDockNodeFlags.PassthruCentralNode != 0)
            dockspaceWfs = dockspaceWfs or ImGuiWindowFlags.NoBackground

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f)
        ImGui.begin("Dockspace", ImBoolean(dockspaceOpen), dockspaceWfs)
        ImGui.popStyleVar()

        if (optFullscreen)
            ImGui.popStyleVar(2)

        menuBar.showMenuBar()

        val minWinSizeX = ImGui.getStyle().windowMinSizeX
        ImGui.getStyle().setWindowMinSize(370f, ImGui.getStyle().windowMinSizeY)
        if (ImGui.getIO().configFlags and ImGuiConfigFlags.DockingEnable != 0) {
            val dockspaceID = ImGui.getID("MyDockSpace")
            ImGui.dockSpace(dockspaceID, 0f, 0f, dockspaceFlags)
        }
        ImGui.getStyle().setWindowMinSize(minWinSizeX, ImGui.getStyle().windowMinSizeY)

        // ImGui

        panels.onImGuiRender()

        Settings.onImGuiRender()
        if (Settings.uEditorCameraView) { editorCamera.fov = Settings.editorCameraFov; editorCamera.updateProjection(); Settings.uEditorCameraView = false; }

        activeProject?.buildSettings?.onImGuiRender()


        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f)
        ImGuiWindow("Scene", showSceneView, ImGuiWindowFlags.MenuBar or ImGuiWindowFlags.NoCollapse) {

            viewportMenuBar()

            //val viewportPos = Vec2(ImGui.getWindowPosX(), ImGui.getWindowPosY())
            viewportSize = Vec2(ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY())
            viewportFocused = ImGui.isWindowFocused()
            viewportHovered = ImGui.isWindowHovered()
            Application.get().getImGuiLayer().blockEvents = !viewportHovered && !viewportFocused
            ImGui.image(framebuffer.colorAttachmentID, viewportSize.x, viewportSize.y, 0f, 1f, 1f, 0f)

            imGuiLayer.blockEvents = !ImGui.isWindowHovered()

            // Camera Preview
            /*if (hierarchyPanel.selectionContext?.let { hierarchyPanel.context.registry.has<CameraComponent>(it) } == true) run {
            val cc = hierarchyPanel.context.registry.get<CameraComponent>(hierarchyPanel.selectionContext!!)
            val tc = if (hierarchyPanel.context.registry.has<TransformComponent>(hierarchyPanel.selectionContext!!))
                hierarchyPanel.context.registry.get<TransformComponent>(hierarchyPanel.selectionContext!!) else null
            if (tc == null) return@run

            val fbWidth = 100 * cc.camera.aspectRatio
            val previewFb = Framebuffer.create(FramebufferSpecification(fbWidth.toInt(), 100))

            previewFb.bind()
            hierarchyPanel.context.onPreviewCamera(cc, tc)
            previewFb.unbind()

            ImGui.setNextWindowPos(viewportPos.x + viewportSize.x - fbWidth - 20f, viewportPos.y + viewportSize.y - 80f)
            ImGui.setNextWindowSize(fbWidth, 100f)
            ImGuiWindow("Preview", windowFlags = ImGuiWindowFlags.NoTitleBar or ImGuiWindowFlags.NoMove or ImGuiWindowFlags.NoNavFocus or ImGuiWindowFlags.NoResize) {
                ImGui.image(previewFb.colorAttachmentID, fbWidth, 100f, 0f, 1f, 1f, 0f)
            }
        }*/

            // GIZMOS
            /*val selectedEntity = hierarchyPanel.selectionContext
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
        }*/
        }
        ImGui.popStyleVar()

        if(imGuiDemo.get()) {
            ImGui.showDemoWindow(imGuiDemo)
        }

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
            KeyCode.B ->
                if (!shift && alt) activeProject?.buildSettings?.open()
        }
    }

    fun newScene() {
        activeScene = Scene()
        panels.hierarchyPanel.context = activeScene
        panels.hierarchyPanel.selectionContext = null
        sceneSerializer.scene = activeScene
        activeScene.onViewportResize(viewportSize.x.toInt(), viewportSize.y.toInt())
    }

    fun openScene() {
        FileDialog.open("Open Scene", fileSelectorFilter)?.let {
            newScene()
            deserialize(it)
        }
    }
    fun deserialize(name: String) = sceneSerializer.deserialize(name)

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
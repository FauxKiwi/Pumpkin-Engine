package editor

import com.pumpkin.core.*
import com.pumpkin.core.event.Event
import com.pumpkin.core.event.EventDispatcher
import com.pumpkin.core.event.KeyPressedEvent
import com.pumpkin.core.imgui.ImGuiProfiler
import com.pumpkin.core.imgui.ImGuizmo
import com.pumpkin.core.input.Input
import com.pumpkin.core.input.KeyCode
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.panels.SceneHierarchyPanel
import com.pumpkin.core.renderer.Framebuffer
import com.pumpkin.core.renderer.FramebufferSpecification
import com.pumpkin.core.renderer.ProjectionType
import com.pumpkin.core.renderer.RendererCommand
import com.pumpkin.core.scene.*
import com.pumpkin.core.settings.Settings
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import imgui.*

class EditorLayer : Layer("Editor") {
    private var dockspaceOpen = true
    private var optFullscreenPersistent = true
    private var dockspaceFlags: DockNodeFlags = DockNodeFlag.None.i

    private val framebuffer = Framebuffer.create(FramebufferSpecification(1280, 720))
    private var viewportSize = Vec2()
    private var viewportFocused = false
    private var viewportHovered = false

    private var activeScene by Reference<Scene>()
    private lateinit var sceneHierarchyPanel: SceneHierarchyPanel
    private lateinit var sceneSerializer: SceneSerializer
    private val editorCamera = EditorCamera()
    private lateinit var runtimeCamera: Entity

    private var gizmoType = -1

    override fun onAttach() {
        activeScene = Scene()
        sceneHierarchyPanel = SceneHierarchyPanel(activeScene)
        sceneSerializer = SceneSerializer(activeScene)

        /*runtimeCamera = activeScene.createEntity("Runtime Camera")
        runtimeCamera.addComponent<CameraComponent>(SceneCamera())
        runtimeCamera.getComponent<CameraComponent>().camera.projectionType = ProjectionType.Perspective
        runtimeCamera.getComponent<TransformComponent>().position = Vec3(0f, 0f, 10f)*/

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
    }

    override fun onImGuiRender() = with(ImGui) {

        val optFullscreen = optFullscreenPersistent

        var windowFlags: WindowFlags = WindowFlag.MenuBar.i or WindowFlag.NoDocking.i
        if (optFullscreen) {
            val viewport = mainViewport
            setNextWindowPos(viewport.pos)
            setNextWindowSize(viewport.size)
            setNextWindowViewport(viewport.id)
            pushStyleVar(StyleVar.WindowRounding, 0f)
            pushStyleVar(StyleVar.WindowBorderSize, 0f)
            windowFlags =
                windowFlags or WindowFlag.NoTitleBar.i or WindowFlag.NoCollapse.i or WindowFlag.NoResize.i or WindowFlag.NoMove.i
            windowFlags = windowFlags or WindowFlag.NoBringToFrontOnFocus.i or WindowFlag.NoNavFocus.i
        }
        if (dockspaceFlags and DockNodeFlag.PassthruCentralNode.i != 0)
            windowFlags = windowFlags or WindowFlag.NoBackground.i

        pushStyleVar(StyleVar.WindowPadding, Vec2(0f, 0f))
        begin("DockSpace Demo", ::dockspaceOpen, windowFlags)
        popStyleVar()

        if (optFullscreen)
            popStyleVar(2)

        menuBar()

        val minWinSizeX = style.windowMinSize.x
        style.windowMinSize.x = 370
        if (io.configFlags and ConfigFlag.DockingEnable.i != 0) {
            val dockspaceID = getID("MyDockSpace")
            dockSpace(dockspaceID, Vec2(0f, 0f), dockspaceFlags)
        }
        style.windowMinSize.x = minWinSizeX

        /**/val avail = contentRegionMax - Vec2(0f, 22f)
        /**/setNextWindowPos(avail * Vec2(0f, 0.75f) + Vec2(0f, 21f))
        /**/setNextWindowSize(avail * Vec2(0.2f, 0.25f))

        ImGuiProfiler.onImGuiRender()

        sceneHierarchyPanel.onImGuiRender()

        Settings.onImGuiRender()
        if (Settings.uEditorCameraView) { editorCamera.fov = Settings.editorCameraFov; editorCamera.updateProjection(); Settings.uEditorCameraView = false; }

        /**/setNextWindowPos(avail * Vec2(0.2f, 0f) + Vec2(0f, 22f))
        /**/setNextWindowSize(avail * Vec2(0.8f, 1f))

        pushStyleVar(StyleVar.WindowPadding, Vec2())
        begin("Viewport", /**/null, WindowFlag.NoMove.i)
        viewportFocused = isWindowFocused()
        viewportHovered = isWindowHovered()
        Application.get().getImGuiLayer().blockEvents = !viewportHovered && !viewportFocused
        //if (viewportSize != contentRegionAvail) {
        //    framebuffer.resize(contentRegionAvail.x.toInt(), contentRegionAvail.y.toInt())
        viewportSize = Vec2(contentRegionAvail.x, contentRegionAvail.y)
        //    cameraController.onResize(contentRegionAvail.x, contentRegionAvail.y)
        //}
        image(framebuffer.colorAttachmentID, viewportSize, Vec2(0, 1), Vec2(1, 0))

        // GIZMOS
        val selectedEntity = sceneHierarchyPanel.selectionContext
        if (selectedEntity != null && gizmoType != -1) {
            ImGuizmo.setOrthographic(false)
            ImGuizmo.setDrawlist()

            ImGuizmo.setRect(ImGui.windowPos.x, ImGui.windowPos.y, ImGui.windowWidth, ImGui.windowHeight)

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
                /*runtimeCamera.getComponent<TransformComponent>().transform, runtimeCamera.getComponent<CameraComponent>().camera.projection,*/editorCamera.view, editorCamera.projection,
                ImGuizmo.OPERATION.values()[gizmoType], ImGuizmo.MODE.LOCAL, tc,
                null, if (snap) snapValues else null
            )

           /* if (ImGuizmo.isUsing()) {
                val translation = Vec3()
                val rotation = Vec3()
                val scale = Vec3()
                ImGuizmo.decomposeFromMatrix(transform, translation, rotation, scale)

                val deltaRotation = rotation - tc.rotation
                tc.position = translation
                tc.rotation = tc.rotation + deltaRotation
                tc.scale = scale
            }*/
        }
        end()
        popStyleVar()

        end()

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

fun menuBar() {
    if (ImGui.beginMenuBar()) {
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

        ImGui.endMenuBar()
    }
}

fun keybinds(event: KeyPressedEvent) {
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
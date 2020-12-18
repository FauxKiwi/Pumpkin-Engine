package editor

import com.pumpkin.core.*
import com.pumpkin.core.Debug
import com.pumpkin.core.event.Event
import com.pumpkin.core.imgui.ImGuiProfiler
import com.pumpkin.core.input.*
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.panels.SceneHierarchyPanel
import com.pumpkin.core.renderer.*
import com.pumpkin.core.scene.*
import glm_.glm
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import imgui.*

class EditorLayer : Layer("Editor") {
    private val cameraController = OrthographicCameraController(16f / 9f, true)
    private lateinit var cameraEntity: Entity
    private lateinit var sceneHierarchyPanel: SceneHierarchyPanel

    private var texture by Reference<Ref<Texture2D>>()
    private val particleSystem = ParticleSystem()
    private val particleProps = ParticleProps(
        Vec2(), Vec2(0f, 1f), Vec2(0.7f, 0.7f),
        Vec4(1f, 0.8f, 0f, 1f), Vec4(0.8f, 0f, 0f, 0.5f), 0.1f, 0.001f, 0.05f, 0.1f
    )

    private var dockspaceOpen = true
    private var optFullscreenPersistent = true
    private var dockspaceFlags: DockNodeFlags = DockNodeFlag.None.i

    private val framebuffer = Framebuffer.create(FramebufferSpecification(1280, 720))
    private var viewportSize = Vec2()
    private var viewportFocused = false
    private var viewportHovered = false

    private var activeScene by Reference<Scene>()
    private lateinit var squareEntity: Entity
    private lateinit var entity2: Entity
    private lateinit var secondCamera: Entity
    private var primary = true

    override fun onAttach() {
        texture = Texture2D.create("textures/Checkerboard.png")
        activeScene = Scene()

        cameraEntity = activeScene.createEntity("Camera Entity")
        cameraEntity.addComponent<CameraComponent>(SceneCamera())
        cameraEntity.addComponent<NativeScriptComponent>().bind<CameraController>()

        sceneHierarchyPanel = SceneHierarchyPanel(activeScene)

        secondCamera = activeScene.createEntity("Clip-Space Entity")
        val cc = secondCamera.addComponent<CameraComponent>(SceneCamera())
        cc.primary = false

        squareEntity = activeScene.createEntity("Square")
        squareEntity.addComponent<SpriteRendererComponent>(floatArrayOf(0f, 1f, 0f, 1f))

        entity2 = activeScene.createEntity("2")
        entity2.addComponent<SpriteRendererComponent>(floatArrayOf(1f, 0f, 0f, 1f))
        entity2.getComponent<TransformComponent>().position = Vec3(0.5f, 0.5f, 0.5f)

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
            cameraController.onResize(viewportSize.x, viewportSize.y)
            activeScene.onViewportResize(viewportSize.x.toInt(), viewportSize.y.toInt())
        }

        framebuffer.bind()

        if (viewportFocused) {
            cameraController.onUpdate(ts)
        }
        /*val x = (2 * Input.getMouseX() / Window.getWindow().width - 1) * cameraController.zoomLevel * cameraController.aspectRatio + cameraController.cameraPosition.x
        val y = (-2 * Input.getMouseY() / Window.getWindow().height + 1) * cameraController.zoomLevel + cameraController.cameraPosition.y
        particleProps.position = Vec2(x, y)
        particleSystem.emit(particleProps)*/

        //Renderer2D.beginScene(cameraController.camera)

        //Renderer2D.drawQuad(texture = texture())
        //particleSystem.onUpdate(ts)
        activeScene.onUpdate(ts)

        //Renderer2D.endScene()
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
            windowFlags = windowFlags or WindowFlag.NoTitleBar.i or WindowFlag.NoCollapse.i or WindowFlag.NoResize.i or WindowFlag.NoMove.i
            windowFlags = windowFlags or WindowFlag.NoBringToFrontOnFocus.i or WindowFlag.NoNavFocus.i
        }
        if (dockspaceFlags and DockNodeFlag.PassthruCentralNode.i != 0)
            windowFlags = windowFlags or WindowFlag.NoBackground.i

        pushStyleVar(StyleVar.WindowPadding, Vec2(0f, 0f))
        begin("DockSpace Demo", ::dockspaceOpen, windowFlags)
        popStyleVar()

        if (optFullscreen)
            popStyleVar(2)

        if (io.configFlags and ConfigFlag.DockingEnable.i != 0) {
            val dockspaceID = getID("MyDockSpace")
            dockSpace(dockspaceID, Vec2(0f, 0f), dockspaceFlags)
        }


        if (beginMenuBar()) {
            if (beginMenu("File")) {
                if (menuItem("New", "Ctrl+N")) {}
                if (menuItem("Open", "Ctrl+O")) {}
                if (menuItem("Open Recent")) {}
                if (menuItem("Close Project", "Ctrl+W")) {}
                separator()
                if (menuItem("Settings", "Ctrl+Alt+S")) {}
                if (menuItem("Build Settings", "Ctrl+Alt+B")) {}
                separator()
                if (menuItem("Save Scene", "Ctrl+S")) {}
                if (menuItem("Save All", "Ctrl+Shift+S"))
                if (menuItem("Reload")) {}
                separator()
                if (menuItem("Exit")) { Application.get().close() }
                endMenu()
            }

            if (beginMenu("Edit")) {
                if (menuItem("Undo", "Ctrl+Z")) {}
                if (menuItem("Redo", "Ctrl+Y")) {}
                separator()
                if (menuItem("Cut", "Ctrl+X")) {}
                if (menuItem("Copy", "Ctrl+C")) {}
                if (menuItem("Paste", "Ctrl+V")) {}
                if (menuItem("Delete", "Del")) {}
                separator()
                if (menuItem("Find", "Ctrl+F")) {}
                endMenu()
            }

            if (beginMenu("View")) {
                if (menuItem("Windows")) {}
                if (menuItem("Appearance")) {}
                endMenu()
            }

            if (beginMenu("Build & Run")) {
                if (menuItem("Run")) {}
                if (menuItem("Build")) {}
                endMenu()
            }

            if (beginMenu("Tools")) {
                if (menuItem("Tasks")) {}
                separator()
                //Plugins
                endMenu()
            }

            if (beginMenu("Help")) {
                if (menuItem("Help")) {}
                endMenu()
            }

            endMenuBar()
        }

        ImGuiProfiler.onImGuiRender()

        sceneHierarchyPanel.onImGuiRender()

        pushStyleVar(StyleVar.WindowPadding, Vec2())
        begin("Viewport")
        viewportFocused = isWindowFocused()
        viewportHovered = isWindowHovered()
        Application.get().getImGuiLayer().blockEvents = !viewportHovered || !viewportFocused
        //if (viewportSize != contentRegionAvail) {
        //    framebuffer.resize(contentRegionAvail.x.toInt(), contentRegionAvail.y.toInt())
            viewportSize = Vec2(contentRegionAvail.x, contentRegionAvail.y)
        //    cameraController.onResize(contentRegionAvail.x, contentRegionAvail.y)
        //}
        image(framebuffer.colorAttachmentID, viewportSize, Vec2(0, 1), Vec2(1, 0))
        end()
        popStyleVar()

        end()

    }

    override fun onEvent(event: Event) {
        cameraController.onEvent(event)
    }
}

class CameraController : ScriptableEntity() {
    private val speed = 5f

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
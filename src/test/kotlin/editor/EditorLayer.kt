package editor

import com.pumpkin.core.*
import com.pumpkin.core.Debug
import com.pumpkin.core.event.Event
import com.pumpkin.core.imgui.ImGuiProfiler
import com.pumpkin.core.input.*
import com.pumpkin.core.layer.Layer
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
        RendererCommand.setClearColor(Vec4(0.1f, 0.1f, 0.1f, 1.0f))
        RendererCommand.clear()

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
                if (menuItem("Exit"))
                    Application.get().close()
                endMenu()
            }
            endMenuBar()
        }

        ImGuiProfiler.onImGuiRender()

        begin("Inspector")
        if (collapsingHeader("Transform", TreeNodeFlag.DefaultOpen.i)) {
            val transform = squareEntity.getComponent<TransformComponent>()
            val position = transform.position
            dragVec3("Position", position, 0.01f, format = "%.2f")
            transform.position = position
            val scale = transform.scale
            dragVec2("Scale", scale, 0.1f, format = "%.1f")
            transform.scale = scale
            val rotation = intArrayOf(glm.degrees(transform.rotation).toInt())
            dragInt("Rotation", rotation, 0)
            transform.rotation = glm.radians(rotation[0].toFloat())
        }
        if (collapsingHeader("SpriteRenderer", TreeNodeFlag.DefaultOpen.i)) {
            val spriteRenderer = squareEntity.getComponent<SpriteRendererComponent>()
            val color = spriteRenderer.color
            colorEdit4("Color", color)
            spriteRenderer.color = color
        }
        end()

        begin("Camera")
        val secondCameraComponent = secondCamera.getComponent<CameraComponent>()
        if (checkbox("Primary", ::primary)) {
            cameraEntity.getComponent<CameraComponent>().primary = primary
            secondCameraComponent.primary = !primary
        }
        val cTransform = cameraEntity.getComponent<TransformComponent>()
        val cPosition = cTransform.position
        dragVec3("Position", cPosition, 0.01f, format = "%.2f")
        cTransform.position = cPosition
        val cScale = cTransform.scale
        dragVec2("Scale", cScale, 0.1f, format = "%.1f")
        cTransform.scale = cScale
        val rotation = intArrayOf(glm.degrees(cTransform.rotation).toInt())
        dragInt("Rotation", rotation, 0)
        cTransform.rotation = glm.radians(rotation[0].toFloat())
        ImGui.dragFloat("Second Camera Ortho Size", secondCameraComponent.camera::othographicSize)

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
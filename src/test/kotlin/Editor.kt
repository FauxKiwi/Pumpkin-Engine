import com.pumpkin.core.*
import com.pumpkin.core.event.Event
import com.pumpkin.core.imgui.ImGuiProfiler
import com.pumpkin.core.input.Input
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.renderer.*
import com.pumpkin.core.window.Window
import glm_.vec2.Vec2
import glm_.vec4.Vec4
import imgui.*

class EditorLayer : Layer("Editor") {
    private val cameraController = OrthographicCameraController(16f / 9f, true)
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

    override fun onAttach() {
        ImGuiProfiler.onAttach()
        texture = Texture2D.create("textures/Checkerboard.png")
    }

    override fun onDetach() {
        ImGuiProfiler.onDetach()
    }

    override fun onUpdate(ts: Timestep) {
        framebuffer.bind()
        RendererCommand.setClearColor(Vec4(0.1f, 0.1f, 0.1f, 1.0f))
        RendererCommand.clear()

        cameraController.onUpdate(ts)
        ImGuiProfiler.onUpdate(ts)
        val x = (2 * Input.getMouseX() / Window.getWindow().width - 1) * cameraController.zoomLevel * cameraController.aspectRatio + cameraController.cameraPosition.x
        val y = (-2 * Input.getMouseY() / Window.getWindow().height + 1) * cameraController.zoomLevel + cameraController.cameraPosition.y
        particleProps.position = Vec2(x, y)
        particleSystem.emit(particleProps)

        Renderer2D.beginScene(cameraController.camera)

        Renderer2D.drawQuad(texture = texture!!())
        particleSystem.onUpdate(ts)

        Renderer2D.endScene()
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

        pushStyleVar(StyleVar.WindowPadding, Vec2())
        begin("Viewport")
        if (viewportSize != contentRegionAvail) {
            framebuffer.resize(contentRegionAvail.x.toInt(), contentRegionAvail.y.toInt())
            viewportSize = Vec2(contentRegionAvail.x, contentRegionAvail.y)
            cameraController.onResize(contentRegionAvail.x, contentRegionAvail.y)
        }
        image(framebuffer.colorAttachmentID, viewportSize, Vec2(0, 1), Vec2(1, 0))
        end()
        popStyleVar()

        end()

    }

    override fun onEvent(event: Event) {
        cameraController.onEvent(event)
    }
}

class EditorApp : Application() {

    override fun init() {
        pushLayer(EditorLayer())
    }
}

fun main() {
    Application.set(EditorApp())
}
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
    private val particleProps = ParticleProps(Vec2(), Vec2(0f, 1f), Vec2(0.7f, 0.7f),
        Vec4(1f, 0.8f, 0f, 1f), Vec4(0.8f, 0f, 0f, 0.5f), 0.1f, 0.001f, 0.05f, 0.1f)

    private var dockingEnabled = true
    private var dockspaceOpen = dockingEnabled
    private var optFullscreenPersistent = dockingEnabled
    private var dockspaceFlags: DockNodeFlags = DockNodeFlag.None.i

    private val framebuffer = Framebuffer.create(FramebufferSpecification(1280, 720))

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
        /*val x = (2 * Input.getMouseX() / Window.getWindow().width - 1) * cameraController.zoomLevel * cameraController.aspectRatio + cameraController.cameraPosition.x
        val y = (-2 * Input.getMouseY() / Window.getWindow().height + 1) * cameraController.zoomLevel + cameraController.cameraPosition.y
        particleProps.position = Vec2(x, y)
        particleSystem.emit(particleProps)*/

        Renderer2D.beginScene(cameraController.camera)

        Renderer2D.drawQuad(texture = texture!!())
        //particleSystem.onUpdate(ts)

        Renderer2D.endScene()
        framebuffer.unbind()
    }

    override fun onImGuiRender() {
        if (dockingEnabled) {
            val optFullscreen = optFullscreenPersistent

            var windowFlags: WindowFlags = WindowFlag.MenuBar.i or WindowFlag.NoDocking.i
            if (optFullscreen) {
                val viewport = ImGui.mainViewport
                ImGui.setNextWindowPos(viewport.pos)
                ImGui.setNextWindowSize(viewport.size)
                ImGui.setNextWindowViewport(viewport.id)
                ImGui.pushStyleVar(StyleVar.WindowRounding, 0f)
                ImGui.pushStyleVar(StyleVar.WindowBorderSize, 0f)
                windowFlags = windowFlags or WindowFlag.NoTitleBar.i or WindowFlag.NoCollapse.i or WindowFlag.NoResize.i or WindowFlag.NoMove.i
                windowFlags = windowFlags or WindowFlag.NoBringToFrontOnFocus.i or WindowFlag.NoNavFocus.i
            }
            if (dockspaceFlags and DockNodeFlag.PassthruCentralNode.i != 0)
                windowFlags = windowFlags or WindowFlag.NoBackground.i

            ImGui.pushStyleVar(StyleVar.WindowPadding, Vec2(0f, 0f))
            ImGui.begin("DockSpace Demo", ::dockspaceOpen, windowFlags)
            ImGui.popStyleVar()

            if (optFullscreen)
                ImGui.popStyleVar(2)

            val io = ImGui.io
            if (io.configFlags and ConfigFlag.DockingEnable.i != 0) {
                val dockspaceID = ImGui.getID("MyDockSpace")
                ImGui.dockSpace(dockspaceID, Vec2(0f, 0f), dockspaceFlags)
            }
            if (ImGui.beginMenuBar()) {
                if (ImGui.beginMenu("File")) {
                    if (ImGui.menuItem("Exit"))
                        Application.get().close()
                    ImGui.endMenu()
                }
                ImGui.endMenuBar()
            }

            ImGuiProfiler.onImGuiRender()

            ImGui.begin("Framebuffer")
            ImGui.image(framebuffer.colorAttachmentID, Vec2(1280, 720))
            ImGui.end()

            ImGui.end()
        } else {
            ImGuiProfiler.onImGuiRender()

            ImGui.begin("Framebuffer")
            ImGui.image(framebuffer.colorAttachmentID, Vec2(1280, 720))
            ImGui.end()
        }
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
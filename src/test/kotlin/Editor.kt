import com.pumpkin.core.Application
import com.pumpkin.core.OrthographicCameraController
import com.pumpkin.core.Timestep
import com.pumpkin.core.event.Event
import com.pumpkin.core.imgui.ImGuiProfiler
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.renderer.*
import glm_.vec2.Vec2
import glm_.vec4.Vec4
import imgui.*

class EditorLayer : Layer("Editor") {
    private val cameraController = OrthographicCameraController(16f / 9f, true)
    private val texture = Texture2D.create("textures/Checkerboard.png")
    private var dockingEnabled = true
    private var dockspaceOpen = dockingEnabled
    private var optFullscreenPersistent = dockingEnabled
    private var dockspaceFlags: DockNodeFlags = DockNodeFlag.None.i

    private val framebuffer = Framebuffer.create(FramebufferSpecification(1280, 720))

    override fun onAttach() {
        ImGuiProfiler.onAttach()
    }

    override fun onDetach() {
        ImGuiProfiler.onDetach()
        texture.release()
    }

    override fun onUpdate(ts: Timestep) {
        framebuffer.bind()
        RendererCommand.setClearColor(Vec4(0.1f, 0.1f, 0.1f, 1.0f))
        RendererCommand.clear()

        cameraController.onUpdate(ts)
        ImGuiProfiler.onUpdate(ts)

        Renderer2D.beginScene(cameraController.camera)

        Renderer2D.drawQuad(texture = texture())

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
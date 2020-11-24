import com.pumpkin.Application
import com.pumpkin.application
import com.pumpkin.event.Event
import com.pumpkin.imgui.ImGuiLayer
import com.pumpkin.layer.Layer
import com.pumpkin.logDebug

class LogLayer : Layer() {

    override fun onAttach() {
        logDebug("Attached layer")
    }

    override fun onDetach() {
        logDebug("Detached layer")
    }

    override fun onUpdate() {
        logDebug("Updated layer")
    }

    override fun onEvent(event: Event) {
        logDebug("Received event: ${event.getName()}")
    }
}

class TestApplication : Application() {

    override fun init() {
        pushLayer(LogLayer())
        pushOverlay(ImGuiLayer())
    }
}

fun main() {
    application = TestApplication()
}
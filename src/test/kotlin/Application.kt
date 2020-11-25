import com.pumpkin.Application
import com.pumpkin.application
import com.pumpkin.event.Event
import com.pumpkin.event.EventDispatcher
import com.pumpkin.event.EventType
import com.pumpkin.event.KeyPressedEvent
import com.pumpkin.imgui.ImGuiLayer
import com.pumpkin.input.PK_KEY_TAB
import com.pumpkin.input.isKeyPressed
import com.pumpkin.layer.Layer
import com.pumpkin.logDebug

class LogLayer : Layer() {

    override fun onAttach() {
        logDebug("Layer attached")
    }

    override fun onDetach() {
        logDebug("Layer detached")
    }

    override fun onUpdate() {
        if (isKeyPressed(PK_KEY_TAB)) {
            logDebug("Tab is pressed (poll)")
        }
    }

    override fun onEvent(event: Event) {
        val dispatcher = EventDispatcher(event)
        dispatcher.dispatch(EventType.KeyPressed) {
            logDebug("Tab is pressed (event)")
            false
        }
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
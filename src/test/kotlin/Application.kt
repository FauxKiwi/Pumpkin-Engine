import com.pumpkin.Application
import com.pumpkin.application
import com.pumpkin.event.Event
import com.pumpkin.event.EventDispatcher
import com.pumpkin.event.KeyPressedEvent
import com.pumpkin.imgui.ImGuiLayer
import com.pumpkin.input.PK_KEY_TAB
import com.pumpkin.input.isKeyPressed
import com.pumpkin.layer.Layer
import com.pumpkin.logDebug

class LogLayer : Layer() {

    override fun onAttach() {

    }

    override fun onDetach() {

    }

    override fun onUpdate() {
        if (isKeyPressed(PK_KEY_TAB)) {
            logDebug("Tab is pressed (poll)")
        }
    }

    override fun onEvent(event: Event) {
        /*val dispatcher = EventDispatcher(event)
        dispatcher.dispatch<KeyPressedEvent> {
            logDebug("Tab is pressed (event)")
            false
        }*/
        if (event is KeyPressedEvent) {
            if (event.keyCode == PK_KEY_TAB) {
                logDebug("Tab is pressed (event)")
            }
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
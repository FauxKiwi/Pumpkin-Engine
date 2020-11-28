import com.pumpkin.core.Application
import com.pumpkin.core.event.Event
import com.pumpkin.core.event.EventDispatcher
import com.pumpkin.core.event.EventType
import com.pumpkin.core.event.KeyPressedEvent
import com.pumpkin.core.input.PK_KEY_TAB
import com.pumpkin.core.input.isKeyPressed
import com.pumpkin.core.layer.Layer
import com.pumpkin.core.logDebug

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
        dispatcher.dispatch<KeyPressedEvent> {
            logDebug("Tab is pressed (event)")
            false
        }
    }
}

class TestApplication : Application() {

    override fun init() {
        pushLayer(LogLayer())
    }
}

fun main() {
    Application set TestApplication()
}
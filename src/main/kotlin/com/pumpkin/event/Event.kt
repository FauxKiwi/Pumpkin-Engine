package com.pumpkin.event

enum class EventType {
    None,
    WindowClose, WindowResize, WindowFocus, WindowLostFocus, WindowMoved,
    AppTick, AppUpdate, AppRender,
    KeyPressed, KeyReleased,
    MouseButtonPressed, MouseButtonReleased, MouseMoved, MouseScrolled
}

enum class EventCategory(val id: Int) {
    None(0),
    Application(1 shl 0),
    Input(1 shl 1),
    Keyboard(1 shl 2),
    Mouse(1 shl 3),
    MouseButton(1 shl 4)
}

abstract class Event {
    var handled = false

    abstract fun getEventType(): EventType

    abstract fun getName(): String

    abstract fun getCategoryFlags(): Int

    override fun toString(): String {
        return getName()
    }

    fun isInCategory(category: EventCategory): Boolean {
        return (getCategoryFlags() and category.id) > 0
    }
}

class EventDispatcher(var event: Event) {

    fun dispatch(eventType: EventType, function: EventFunction) {
        if (event.getEventType() == eventType) {
            event.handled = function(event)
        }
    }
}

typealias EventFunction = (Event) -> Boolean
package com.pumpkin.core.event

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

interface Event {
    var handled: Boolean

    fun getEventType(): EventType

    fun getName(): String

    fun getCategoryFlags(): Int

    override fun toString(): String

    fun isInCategory(category: EventCategory) = (getCategoryFlags() and category.id) > 0
}

class EventDispatcher(var event: Event) {
    inline fun <reified T: Event> dispatch(noinline function: EventFunction<T>) {
        if (event is T) {
            event.handled = event.handled || function(event as T)
        }
    }
}

typealias EventFunction<T> = T.() -> Boolean
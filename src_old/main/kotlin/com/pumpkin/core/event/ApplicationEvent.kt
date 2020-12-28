package com.pumpkin.core.event

class WindowResizeEvent(val width: Int, val height: Int) : Event {
    override var handled = false

    override fun getEventType() = EventType.WindowResize

    override fun getName()= "WindowResize"

    override fun getCategoryFlags() = EventCategory.Application.id

    override fun toString() = "WindowResizeEvent: $width, $height"
}

class WindowCloseEvent : Event {
    override var handled = false

    override fun getEventType() = EventType.WindowClose

    override fun getName() = "WindowClose"

    override fun getCategoryFlags() = EventCategory.Application.id

    override fun toString() = "WindowCloseEvent"
}

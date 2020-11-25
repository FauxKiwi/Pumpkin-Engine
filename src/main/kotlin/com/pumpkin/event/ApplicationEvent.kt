package com.pumpkin.event

class WindowResizeEvent(val width: Int, val height: Int) : Event() {

    override fun getEventType(): EventType {
        return EventType.WindowResize
    }

    override fun getName(): String {
        return "WindowResize"
    }

    override fun getCategoryFlags(): Int {
        return EventCategory.Application.id
    }

    override fun toString(): String {
        return "WindowResizeEvent: $width, $height"
    }
}

class WindowCloseEvent : Event() {

    override fun getEventType(): EventType {
        return EventType.WindowClose
    }

    override fun getName(): String {
        return "WindowClose"
    }

    override fun getCategoryFlags(): Int {
        return EventCategory.Application.id
    }

    override fun toString(): String {
        return "WindowCloseEvent"
    }
}

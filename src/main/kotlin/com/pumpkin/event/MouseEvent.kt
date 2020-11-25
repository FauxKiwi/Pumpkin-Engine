package com.pumpkin.event

class MouseMoveEvent(val x: Float, val y: Float) : Event() {

    override fun getEventType(): EventType {
        return EventType.MouseMoved
    }

    override fun getName(): String {
        return "MouseMoved"
    }

    override fun getCategoryFlags(): Int {
        return EventCategory.Input.id or EventCategory.Mouse.id
    }

    override fun toString(): String {
        return "MouseMoveEvent: $x, $y"
    }
}

class MouseScrolledEvent(val xOffset: Float, val yOffset: Float) : Event() {

    override fun getEventType(): EventType {
        return EventType.MouseScrolled
    }

    override fun getName(): String {
        return "MouseScrolled"
    }

    override fun getCategoryFlags(): Int {
        return EventCategory.Input.id or EventCategory.Mouse.id
    }

    override fun toString(): String {
        return "MouseScrolledEvent: $xOffset, $yOffset"
    }
}

abstract class MouseButtonEvent protected constructor(val button: Int) : Event()

class MouseButtonPressedEvent(button: Int) : MouseButtonEvent(button) {

    override fun getEventType(): EventType {
        return EventType.MouseButtonPressed
    }

    override fun getName(): String {
        return "MouseButtonPressed"
    }

    override fun getCategoryFlags(): Int {
        return EventCategory.Input.id or EventCategory.Mouse.id or EventCategory.MouseButton.id
    }

    override fun toString(): String {
        return "MouseButtonPressedEvent: $button"
    }
}

class MouseButtonReleasedEvent(button: Int) : MouseButtonEvent(button) {

    override fun getEventType(): EventType {
        return EventType.MouseButtonReleased
    }

    override fun getName(): String {
        return "MouseButtonReleased"
    }

    override fun getCategoryFlags(): Int {
        return EventCategory.Input.id or EventCategory.Mouse.id or EventCategory.MouseButton.id
    }

    override fun toString(): String {
        return "MouseButtonReleasedEvent: $button"
    }
}
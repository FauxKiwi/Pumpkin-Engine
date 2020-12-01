package com.pumpkin.core.event

class MouseMoveEvent(val x: Float, val y: Float) : Event {
    override var handled = false

    override fun getEventType() = EventType.MouseMoved

    override fun getName() = "MouseMoved"

    override fun getCategoryFlags() = EventCategory.Input.id or EventCategory.Mouse.id

    override fun toString() = "MouseMoveEvent: $x, $y"
}

class MouseScrolledEvent(val xOffset: Float, val yOffset: Float) : Event {
    override var handled = false

    override fun getEventType() = EventType.MouseScrolled

    override fun getName() = "MouseScrolled"

    override fun getCategoryFlags() = EventCategory.Input.id or EventCategory.Mouse.id

    override fun toString() = "MouseScrolledEvent: $xOffset, $yOffset"
}

abstract class MouseButtonEvent protected constructor(val button: Int) : Event

class MouseButtonPressedEvent(button: Int) : MouseButtonEvent(button) {
    override var handled = false

    override fun getEventType() = EventType.MouseButtonPressed

    override fun getName() = "MouseButtonPressed"

    override fun getCategoryFlags() = EventCategory.Input.id or EventCategory.Mouse.id or EventCategory.MouseButton.id

    override fun toString() = "MouseButtonPressedEvent: $button"
}

class MouseButtonReleasedEvent(button: Int) : MouseButtonEvent(button) {
    override var handled = false

    override fun getEventType() = EventType.MouseButtonReleased

    override fun getName() = "MouseButtonReleased"

    override fun getCategoryFlags() = EventCategory.Input.id or EventCategory.Mouse.id or EventCategory.MouseButton.id

    override fun toString() = "MouseButtonReleasedEvent: $button"
}
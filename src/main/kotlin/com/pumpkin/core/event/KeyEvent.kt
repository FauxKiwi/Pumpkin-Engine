package com.pumpkin.core.event

abstract class KeyEvent protected constructor(val keyCode: Int) : Event

class KeyPressedEvent(keyCode: Int, val repeatCount: Int) : KeyEvent(keyCode) {
    override var handled = false

    override fun getEventType(): EventType {
        return EventType.KeyPressed
    }

    override fun getName(): String {
        return "KeyPressed"
    }

    override fun getCategoryFlags(): Int {
        return EventCategory.Input.id or EventCategory.Keyboard.id
    }

    override fun toString(): String {
        return "KeyPressedEvent: $keyCode (*$repeatCount)"
    }
}

class KeyReleasedEvent(keyCode: Int) : KeyEvent(keyCode) {
    override var handled = false

    override fun getEventType(): EventType {
        return EventType.KeyReleased
    }

    override fun getName(): String {
        return "KeyReleased"
    }

    override fun getCategoryFlags(): Int {
        return EventCategory.Input.id or EventCategory.Keyboard.id
    }

    override fun toString(): String {
        return "KeyReleasedEvent: $keyCode"
    }
}
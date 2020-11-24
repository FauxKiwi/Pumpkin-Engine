package com.pumpkin.event

abstract class KeyEvent protected constructor(val keyCode: Int) : Event()

class KeyPressedEvent(keyCode: Int, val repeatCount: Int) : KeyEvent(keyCode) {

    override fun getEventType(): EventType {
        return EventType.KeyPressed
    }

    override fun getName(): String {
        return "KeyPressed"
    }

    override fun getCategoryFlags(): Int {
        return EventCategory.Input.cid or EventCategory.Keyboard.cid
    }

    override fun toString(): String {
        return "KeyPressedEvent: $keyCode (*$repeatCount)"
    }
}

class KeyReleasedEvent(keyCode: Int) : KeyEvent(keyCode) {

    override fun getEventType(): EventType {
        return EventType.KeyReleased
    }

    override fun getName(): String {
        return "KeyReleased"
    }

    override fun getCategoryFlags(): Int {
        return EventCategory.Input.cid or EventCategory.Keyboard.cid
    }

    override fun toString(): String {
        return "KeyReleasedEvent: $keyCode"
    }
}
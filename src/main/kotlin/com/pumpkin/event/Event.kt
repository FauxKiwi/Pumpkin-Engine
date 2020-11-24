package com.pumpkin.event

import com.pumpkin.logWarn

enum class EventType {
    None,
    WindowClose, WindowResize, WindowFocus, WindowLostFocus, WindowMoved,
    AppTick, AppUpdate, AppRender,
    KeyPressed, KeyReleased,
    MouseButtonPressed, MouseButtonReleased, MouseMoved, MouseScrolled
}

enum class EventCategory(val cid: Int) {
    None(0),
    Application(1 shl 0),
    Input(1 shl 1),
    Keyboard(1 shl 2),
    Mouse(1 shl 3),
    MouseButton(1 shl 4)
}

/*
#define EVENT_CLASS_TYPE(type)
static EventType GetStaticType() { return EventType.##type }
open EventType GetEventType() const override { return GetStaticType() }
open const String GetName() const override { return #type }

#define EVENT_CLASS_CATEGORY(category) virtual int GetCategoryFlags() const override {return category }
*/

abstract class Event {
    var handled = false

    abstract fun getEventType(): EventType

    abstract fun getName(): String

    abstract fun getCategoryFlags(): Int

    override fun toString(): String {
        return getName()
    }

    fun isInCategory(category: EventCategory): Boolean {
        return (getCategoryFlags() and category.cid) > 0
    }
}

class EventDispatcher(var event: Event) {

    inline fun <reified T> dispatch(function: EventFunction<T>) {
        if (event is T) {
            event.handled = function(event as T)
        }
    }
}

typealias EventFunction<T> = (T) -> Boolean
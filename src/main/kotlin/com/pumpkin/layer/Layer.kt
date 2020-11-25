package com.pumpkin.layer

import com.pumpkin.event.Event

open class Layer(val debugName: String = "Layer") {

    open fun onAttach() {}

    open fun onDetach() {}

    open fun onUpdate() {}

    open fun onImGuiRender() {}

    open fun onEvent(event: Event) {}
}
package com.pumpkin.core.layer

import com.pumpkin.core.event.Event

open class Layer(val debugName: String = "Layer") {

    open fun onAttach() {}

    open fun onDetach() {}

    open fun onUpdate() {}

    open fun onImGuiRender() {}

    open fun onEvent(event: Event) {}
}
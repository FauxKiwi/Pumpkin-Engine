package com.pumpkin.core.layer

import com.pumpkin.core.Timestep
import com.pumpkin.core.event.Event

open class Layer(val debugName: String = "Layer") {

    open fun onAttach() {}

    open fun onDetach() {}

    open fun onUpdate(ts: Timestep) {}

    open fun onImGuiRender() {}

    open fun onEvent(event: Event) {}
}
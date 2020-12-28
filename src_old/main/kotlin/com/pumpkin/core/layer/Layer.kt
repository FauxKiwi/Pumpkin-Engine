package com.pumpkin.core.layer

import com.pumpkin.core.Referencable
import com.pumpkin.core.Timestep
import com.pumpkin.core.event.Event

open class Layer(val debugName: String = "Layer") : Referencable() {

    open fun onAttach() {}

    override fun destruct() = onDetach()

    open fun onDetach() {}

    open fun onUpdate(ts: Timestep) {}

    open fun onImGuiRender() {}

    open fun onEvent(event: Event) {}
}
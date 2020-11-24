package com.pumpkin.layer

import com.pumpkin.event.Event

abstract class Layer(val debugName: String = "Layer") {

    abstract fun onAttach()

    abstract fun onDetach()

    abstract fun onUpdate()

    abstract fun onEvent(event: Event)
}
package com.pumpkin.core.layer

import com.pumpkin.core.logWarnCore

class LayerStack {
    internal val layers: MutableList<Layer> = mutableListOf()
    internal val layersReversed = layers.asReversed()
    private var insert = 0

    fun pushLayer(layer: Layer) {
        layers.add(insert, layer)
        layer.onAttach()
        insert++
    }

    fun pushOverlay(layer: Layer) {
        layers.add(layer)
        layer.onAttach()
    }

    fun popLayer(layer: Layer) {
        val index = layers.indexOf(layer)
        if (index >= insert) {
            logWarnCore("Layer is an overlay, please use popOverlay() instead.")
            return
        }
        layers.removeAt(index)
        layer.onDetach()
        insert--
    }

    fun popOverlay(layer: Layer) {
        val index = layers.indexOf(layer)
        if (index < insert) {
            logWarnCore("Layer is not an overlay, please use popLayer() instead.")
            return
        }
        layers.removeAt(index)
        layer.onDetach()
    }

    internal fun getInsert(): Int {
        return insert
    }
}
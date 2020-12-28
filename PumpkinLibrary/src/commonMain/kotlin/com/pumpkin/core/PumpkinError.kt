package com.pumpkin.core

internal class PumpkinError(message: String? = null) : Throwable(message) {

    init {
        if (message != null) {
            Debug.logErrorCore(message)
        }
    }
}
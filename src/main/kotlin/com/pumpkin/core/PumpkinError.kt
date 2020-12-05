package com.pumpkin.core

class PumpkinError(message: String? = null) : Throwable(message) {

    init {
        if (message != null) {
            logErrorCore(message)
        }
    }
}
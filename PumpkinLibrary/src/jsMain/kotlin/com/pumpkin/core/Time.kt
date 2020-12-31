package com.pumpkin.core

var frameTime: Double = 0.0

actual val Time.current get() = frameTime.toFloat()
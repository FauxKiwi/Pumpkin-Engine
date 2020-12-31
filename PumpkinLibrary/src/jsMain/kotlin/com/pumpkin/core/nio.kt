package com.pumpkin.core

import org.khronos.webgl.Float32Array

fun glm.Mat3.toFloat32Array() = Float32Array(arrayOf(
    m0, m1, m2, m3, m4, m5, m6, m7, m8
))

fun glm.Mat4.toFloat32Array() = Float32Array(arrayOf(
    m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12, m13, m14, m15
))
package com.pumpkin.core

import org.lwjgl.system.MemoryStack

fun glm.Mat3.toFloatBuffer(stack: MemoryStack) = stack.mallocFloat(9).also {
    it.put(0, m0); it.put(1, m1); it.put(2, m2)
    it.put(3, m3); it.put(4, m4); it.put(5, m5)
    it.put(6, m6); it.put(7, m7); it.put(8, m8)
}

fun glm.Mat4.toFloatBuffer(stack: MemoryStack) = stack.mallocFloat(16).also {
    it.put(0, m0); it.put(1, m1); it.put(2, m2); it.put(3, m3)
    it.put(4, m4); it.put(5, m5); it.put(6, m6); it.put(7, m7)
    it.put(8, m8); it.put(9, m9); it.put(10, m10); it.put(11, m11)
    it.put(12, m12); it.put(13, m13); it.put(14, m14); it.put(15, m15)
}
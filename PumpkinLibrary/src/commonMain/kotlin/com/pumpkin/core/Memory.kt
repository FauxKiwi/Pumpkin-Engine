package com.pumpkin.core

expect class MemoryStack : AutoCloseable {
    companion object {
        fun stackPush(): MemoryStack
    }

    fun malloc(count: Int): ByteBuffer
}

expect object MemoryUtil {
    fun memAllocFloat(count: Int): FloatBuffer
}

interface Buffer<T, A> {
    fun put(value: T): Buffer<T, A>

    fun put(index: Int, value: T): Buffer<T, A>

    fun position(): Int

    fun position(newPosition: Int): Buffer<T, A>

    fun flip(): Buffer<T, A>

    operator fun get(array: A, pos: Int, size: Int)
}

expect class ByteBuffer : Buffer<Byte, ByteArray>

expect class FloatBuffer : Buffer<Float, FloatArray>
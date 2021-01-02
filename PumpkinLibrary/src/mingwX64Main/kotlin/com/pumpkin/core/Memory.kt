package com.pumpkin.core

import kotlinx.cinterop.*

actual class MemoryStack : AutoCloseable {
    actual companion object {
        actual fun stackPush(): MemoryStack = MemoryStack()
    }

    actual fun malloc(count: Int): ByteBuffer = ByteBuffer()

    override fun close() {}
}

actual object MemoryUtil {
    actual fun memAllocFloat(count: Int): FloatBuffer = FloatBuffer(nativeHeap.alloc())
}

actual class ByteBuffer(val pointer: CPointer<ByteVarOf<Byte>>) : Buffer<Byte, ByteArray> {
    private var position = 0

    override fun put(value: Byte): Buffer<Byte, ByteArray> = run {
        pointer[position] = value
        this
    }

    override fun put(index: Int, value: Byte): Buffer<Byte, ByteArray> = run {
        pointer[index] = value
        this
    }

    override fun position(): Int = position

    override fun position(newPosition: Int): Buffer<Byte, ByteArray> = run {
        position = newPosition
        this
    }

    override fun flip(): Buffer<Byte, ByteArray> = this

    override fun get(array: ByteArray, pos: Int, size: Int) {
        //TODO
    }
}

actual class FloatBuffer(val pointer: CPointer<FloatVarOf<Float>>) : Buffer<Float, FloatArray> {
    private var position = 0

    override fun put(value: Float): Buffer<Float, FloatArray> = run {
        pointer[position] = value
        this
    }

    override fun put(index: Int, value: Float): Buffer<Float, FloatArray> = run {
        pointer[index] = value
        this
    }

    override fun position(): Int = position

    override fun position(newPosition: Int): Buffer<Float, FloatArray> = run {
        position = newPosition
        this
    }

    override fun flip(): Buffer<Float, FloatArray> = this

    override fun get(array: FloatArray, pos: Int, size: Int) {
        //TODO
    }
}
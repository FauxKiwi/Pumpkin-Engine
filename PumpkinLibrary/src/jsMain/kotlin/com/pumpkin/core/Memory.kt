package com.pumpkin.core

import org.khronos.webgl.Float32Array
import org.khronos.webgl.Int8Array

actual class MemoryStack : AutoCloseable {
    actual companion object {
        actual fun stackPush() = MemoryStack()
    }

    actual fun malloc(count: Int) = ByteBuffer(Int8Array(count))

    override fun close() {

    }
}

actual object MemoryUtil {
    actual fun memAllocFloat(count: Int) = FloatBuffer(Float32Array(count))
}

actual class ByteBuffer(val array: Int8Array) : Buffer<Byte, ByteArray> {
    override fun put(value: Byte): Buffer<Byte, ByteArray> = run {
        //TODO
        this
    }

    override fun put(index: Int, value: Byte): Buffer<Byte, ByteArray> = run {
        //TODO
        this
    }

    override fun position(): Int = 0 //TODO

    override fun position(newPosition: Int): Buffer<Byte, ByteArray> = run {
        //TODO
        this
    }

    override fun flip(): Buffer<Byte, ByteArray> = run {
        //TODO
        this
    }

    override fun get(array: ByteArray, pos: Int, size: Int) {
        //TODO
    }
}

actual class FloatBuffer(val array: Float32Array) : Buffer<Float, FloatArray> {
    override fun put(value: Float): Buffer<Float, FloatArray> = run {
        //TODO
        this
    }

    override fun put(index: Int, value: Float): Buffer<Float, FloatArray> = run {
        //TODO
        this
    }

    override fun position(): Int = 0 //TODO

    override fun position(newPosition: Int): Buffer<Float, FloatArray> = run {
        //TODO
        this
    }

    override fun flip(): Buffer<Float, FloatArray> = run {
        //TODO
        this
    }

    override fun get(array: FloatArray, pos: Int, size: Int) {
        //TODO
    }
}
package com.pumpkin.core

actual class MemoryStack(val stack: org.lwjgl.system.MemoryStack) : AutoCloseable {
    actual companion object {
        actual fun stackPush(): MemoryStack = MemoryStack(org.lwjgl.system.MemoryStack.stackPush())
    }

    actual fun malloc(count: Int): ByteBuffer = ByteBuffer(stack.malloc(count))

    override fun close() {
        stack.close()
    }
}

actual object MemoryUtil {
    actual fun memAllocFloat(count: Int): FloatBuffer = FloatBuffer(org.lwjgl.system.MemoryUtil.memAllocFloat(count))
}

actual class ByteBuffer(val buffer: java.nio.ByteBuffer) : Buffer<Byte, ByteArray> {
    override fun put(value: Byte): Buffer<Byte, ByteArray> = run {
        buffer.put(value)
        this
    }

    override fun put(index: Int, value: Byte): Buffer<Byte, ByteArray> = run {
        buffer.put(index, value)
        this
    }

    override fun position(): Int = buffer.position()

    override fun position(newPosition: Int): Buffer<Byte, ByteArray> = run {
        buffer.position(newPosition)
        this
    }

    override fun flip(): Buffer<Byte, ByteArray> = run {
        buffer.flip()
        this
    }

    override fun get(array: ByteArray, pos: Int, size: Int) {
        buffer.get(array, pos, size)
    }
}

actual class FloatBuffer(val buffer: java.nio.FloatBuffer) : Buffer<Float, FloatArray> {
    override fun put(value: Float): Buffer<Float, FloatArray> = run {
        buffer.put(value)
        this
    }

    override fun put(index: Int, value: Float): Buffer<Float, FloatArray> = run {
        buffer.put(index, value)
        this
    }

    override fun position(): Int = buffer.position()

    override fun position(newPosition: Int): Buffer<Float, FloatArray> = run {
        buffer.position(newPosition)
        this
    }

    override fun flip(): Buffer<Float, FloatArray> = run {
        buffer.flip()
        this
    }

    override fun get(array: FloatArray, pos: Int, size: Int) {
        buffer.get(array, pos, size)
    }
}
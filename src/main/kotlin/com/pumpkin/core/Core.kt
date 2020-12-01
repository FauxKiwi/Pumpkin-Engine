package com.pumpkin.core

typealias Scope<T> = () -> T

class Ref<T: AutoCloseable>(private val value: T) : AutoCloseable {
    @Volatile private var refCount = 0

    init {
        take(this)
    }

    override fun close() {
        release()
    }

    operator fun invoke() = value

    operator fun not() = refCount <= 0

    fun Any.take(ref: Ref<T>) {
        ref.refCount++
    }

    fun release() {
        if (--refCount <= 0) {
            value.close()
        }
    }
}
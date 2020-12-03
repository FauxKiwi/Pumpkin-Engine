package com.pumpkin.core

import org.lwjgl.system.MemoryStack

class Ref<out T : AutoCloseable>(private val value: T) : AutoCloseable {
    @Volatile
    private var refCount = 0

    init {
        refCount++
    }

    override fun close() {
        release()
    }

    operator fun invoke() = value

    operator fun not() = refCount <= 0

    fun take() = this.also { refCount++ }

    fun release() {
        if (--refCount <= 0) {
            value.close()
            logTraceCore("Released $value")
        }
    }
}

typealias Timestep = Float

inline fun stack(block: (memoryStack: MemoryStack) -> Unit) {
    MemoryStack.stackPush().apply { use (block) }
}

fun lifetimeScope(vararg scoped: AutoCloseable, block: () -> Unit) = stack {
    block().also {
        scoped.forEach {
            it.close()
        }
    }
}
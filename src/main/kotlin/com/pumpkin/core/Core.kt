package com.pumpkin.core

import org.lwjgl.system.MemoryStack

data class Scope<out T : AutoCloseable>(private val value: T) : Referencable() {

    override fun destruct() {
        value.close()
    }

    operator fun invoke() = value
}

data class Ref<out T : AutoCloseable>(private val value: T) : Referencable() {
    @Volatile
    private var refCount = 0

    init {
        refCount++
    }

    override fun destruct() {
        release()
    }

    operator fun invoke() = value

    operator fun not() = refCount <= 0

    fun take() = this.also { refCount++ }

    fun release() {
        if (--refCount <= 0) {
            value.close()
            Debug.logTraceCore("Released $value")
        }
    }
}

typealias Timestep = Float

inline fun <R> stack(block: (memoryStack: MemoryStack) -> R): R = MemoryStack.stackPush().use(block)

fun lifetimeScope(vararg scoped: AutoCloseable, block: () -> Unit) = stack {
    block().also {
        scoped.forEach {
            it.close()
        }
    }
}
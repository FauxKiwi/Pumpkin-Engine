package com.pumpkin.core

import kotlinx.serialization.json.Json
//import org.lwjgl.system.MemoryStack
import kotlin.jvm.Volatile

interface AutoCloseable {
    fun close()
}

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

val jsonFormat = Json { prettyPrint = true }

typealias Timestep = Float

inline fun <A : AutoCloseable, R> A.use(block: (A) -> R): R = try {
    block(this)
} finally {
    close()
}

inline fun <R> stack(block: (memoryStack: MemoryStack) -> R): R = MemoryStack.stackPush().use(block)

fun lifetimeScope(vararg scoped: AutoCloseable, block: () -> Unit) =
    block().also {
        scoped.forEach {
            it.close()
        }
    }

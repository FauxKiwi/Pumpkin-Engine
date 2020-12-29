package glm

import kotlin.jvm.JvmName

@Suppress("NOTHING_TO_INLINE")
inline fun min(a: Float, b: Float) = kotlin.math.min(a, b)

@JvmName("infixMax")
@Suppress("NOTHING_TO_INLINE")
inline infix fun Float.max(x: Float) = max(this, x)

@Suppress("NOTHING_TO_INLINE")
inline fun max(a: Float, b: Float) = kotlin.math.max(a, b)

@Suppress("NOTHING_TO_INLINE")
inline fun sqrt(x: Float) = kotlin.math.sqrt(x)

@Suppress("NOTHING_TO_INLINE")
inline fun inverseSqrt(x: Float) = 1f / sqrt(x)

@Suppress("NOTHING_TO_INLINE")
inline fun floor(x: Float) = kotlin.math.floor(x)
package glm

@Suppress("NOTHING_TO_INLINE")
inline fun min(a: Float, b: Float) = kotlin.math.min(a, b)

@Suppress("NOTHING_TO_INLINE")
inline fun max(a: Float, b: Float) = kotlin.math.max(a, b)

@Suppress("NOTHING_TO_INLINE")
inline fun sqrt(x: Float) = kotlin.math.sqrt(x)

@Suppress("NOTHING_TO_INLINE")
inline fun inverseSqrt(x: Float) = 1f / sqrt(x)
package glm

@Suppress("NOTHING_TO_INLINE")
inline fun radians(degrees: Float) = degrees * DEG2RAD

@Suppress("NOTHING_TO_INLINE")
inline fun degrees(radians: Float) = radians * RAD2DEG

@Suppress("NOTHING_TO_INLINE")
inline fun sin(x: Float) = kotlin.math.sin(x)

@Suppress("NOTHING_TO_INLINE")
inline fun cos(x: Float) = kotlin.math.cos(x)

@Suppress("NOTHING_TO_INLINE")
inline fun tan(x: Float) = kotlin.math.tan(x)
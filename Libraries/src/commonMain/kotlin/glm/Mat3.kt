package glm

open class Mat3T<T>(
    var m0: Float, var m1: Float, var m2: Float,
    var m3: Float, var m4: Float, var m5: Float,
    var m6: Float, var m7: Float, var m8: Float
)

class Mat3(
    m0: Float = 1f, m1: Float = 0f, m2: Float = 0f,
    m3: Float = 0f, m4: Float = 1f, m5: Float = 0f,
    m6: Float = 0f, m7: Float = 0f, m8: Float = 1f
) : Mat3T<Float>(m0, m1, m2, m3, m4, m5, m6, m7, m8)
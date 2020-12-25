package glm

open class Mat3T<T>(
    var m0: Float, var m1: Float, var m2: Float,
    var m3: Float, var m4: Float, var m5: Float,
    var m6: Float, var m7: Float, var m8: Float
)

class Mat3(
    m0: Float, m1: Float, m2: Float,
    m3: Float, m4: Float, m5: Float,
    m6: Float, m7: Float, m8: Float
) : Mat3T<Float>(m0, m1, m2, m3, m4, m5, m6, m7, m8)
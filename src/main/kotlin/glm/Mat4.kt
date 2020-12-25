package glm

open class Mat4T<T>(
    var m0: Float, var m1: Float, var m2: Float, var m3: Float,
    var m4: Float, var m5: Float, var m6: Float, var m7: Float,
    var m8: Float, var m9: Float, var m10: Float, var m11: Float,
    var m12: Float, var m13: Float, var m14: Float, var m15: Float
)

class Mat4(
    m0: Float, m1: Float, m2: Float, m3: Float,
    m4: Float, m5: Float, m6: Float, m7: Float,
    m8: Float, m9: Float, m10: Float, m11: Float,
    m12: Float, m13: Float, m14: Float, m15: Float
) : Mat4T<Float>(m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12, m13, m14, m15) {
    inline var right get() = Vec4(m0, m1, m2, m3); set(v) { m0 = v.x; m1 = v.y; m2 = v.z; m3 = v.w }
    inline var up get() = Vec4(m4, m5, m6, m7); set(v) { m4 = v.x; m5 = v.y; m6 = v.z; m7 = v.w }
    inline var dir get() = Vec4(m8, m9, m10, m11); set(v) { m8 = v.x; m9 = v.y; m10 = v.z; m11 = v.w }
    inline var position get() = Vec4(m12, m13, m14, m15); set(v) { m12 = v.x; m13 = v.y; m14 = v.z; m15 = v.w }
    val array = floatArrayOf(m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12, m13, m14, m15)

    constructor() : this(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)
    constructor(f: Float) : this(f, 0f, 0f, 0f, 0f, f, 0f, 0f, 0f, 0f, f, 0f, 0f, 0f, 0f, f)
    constructor(a: FloatArray) : this(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15])

    operator fun get(m: Int) = array[m]
    @Suppress("NOTHING_TO_INLINE") inline operator fun get(v: Int, i: Int) = get(v * 4 + i)

    infix operator fun times(v: Vec4) = Vec4(
        m0*v.x + m4*v.y + m8*v.z + m12*v.w,
        m1*v.x + m5*v.y + m9*v.z + m13*v.w,
        m2*v.x + m6*v.y + m10*v.z + m14*v.w,
        m3*v.x + m7*v.y + m11*v.z + m15*v.w
    )

    infix operator fun times(m: Mat4) = Mat4(
        m0*m.m0+m4*m.m1+m8*m.m2+m12*m.m3, m1*m.m0+m5*m.m1+m9*m.m2+m13*m.m3, m2*m.m0+m6*m.m1+m10*m.m2+m14*m.m3, m3*m.m0+m7*m.m1+m11*m.m2+m15*m.m3,
        m0*m.m4+m4*m.m5+m8*m.m6+m12*m.m7, m1*m.m4+m5*m.m5+m9*m.m6+m13*m.m7, m2*m.m4+m6*m.m5+m10*m.m6+m14*m.m7, m3*m.m4+m7*m.m5+m11*m.m6+m15*m.m7,
        m0*m.m8+m4*m.m9+m8*m.m10+m12*m.m11, m1*m.m8+m5*m.m9+m9*m.m10+m13*m.m11, m2*m.m8+m6*m.m9+m10*m.m10+m14*m.m11, m3*m.m8+m7*m.m9+m11*m.m10+m15*m.m11,
        m0*m.m12+m4*m.m13+m8*m.m14+m12*m.m15, m1*m.m12+m5*m.m13+m9*m.m14+m13*m.m15, m2*m.m12+m6*m.m13+m10*m.m14+m14*m.m15, m3*m.m12+m7*m.m13+m11*m.m14+m15*m.m15
    )

    fun inverse(): Mat4 { //TODO
        val inv = glm_.glm.inverse(glm_.mat4x4.Mat4(floatArrayOf(m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12, m13, m14, m15)))
        return Mat4(inv.array)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun inverse(m: Mat4) = m.inverse()
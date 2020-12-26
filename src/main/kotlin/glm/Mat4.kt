package glm

open class Mat4T<T>(
    var m0: Float, var m1: Float, var m2: Float, var m3: Float,
    var m4: Float, var m5: Float, var m6: Float, var m7: Float,
    var m8: Float, var m9: Float, var m10: Float, var m11: Float,
    var m12: Float, var m13: Float, var m14: Float, var m15: Float
)

class Mat4(
    m0: Float = 1f, m1: Float = 0f, m2: Float = 0f, m3: Float = 0f,
    m4: Float = 0f, m5: Float = 1f, m6: Float = 0f, m7: Float = 0f,
    m8: Float = 0f, m9: Float = 0f, m10: Float = 1f, m11: Float = 0f,
    m12: Float = 0f, m13: Float = 0f, m14: Float = 0f, m15: Float = 1f
) : Mat4T<Float>(m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12, m13, m14, m15) {
    inline var right get() = Vec4(m0, m1, m2, m3); set(v) { m0 = v.x; m1 = v.y; m2 = v.z; m3 = v.w }
    inline var up get() = Vec4(m4, m5, m6, m7); set(v) { m4 = v.x; m5 = v.y; m6 = v.z; m7 = v.w }
    inline var dir get() = Vec4(m8, m9, m10, m11); set(v) { m8 = v.x; m9 = v.y; m10 = v.z; m11 = v.w }
    inline var position get() = Vec4(m12, m13, m14, m15); set(v) { m12 = v.x; m13 = v.y; m14 = v.z; m15 = v.w }
    val array = floatArrayOf(m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12, m13, m14, m15)

    constructor(f: Float) : this(f, 0f, 0f, 0f, 0f, f, 0f, 0f, 0f, 0f, f, 0f, 0f, 0f, 0f, f)
    constructor(a: FloatArray) : this(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15])

    operator fun get(i: Int) = array[i]
    operator fun set(i: Int, v: Float) { array[i] = v }
    @Suppress("NOTHING_TO_INLINE") inline operator fun get(i: Int, j: Int) = get(i * 4 + j)
    @Suppress("NOTHING_TO_INLINE") inline operator fun set(i: Int, j: Int, v: Float) = set(i * 4 + j, v)

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

    fun inverse(): Mat4 {
        val c00 = m10 * m15 - m14 * m11
        val c02 = m6 * m15 - m14 * m7
        val c03 = m6 * m11 - m10 * m7

        val c04 = m9 * m15 - m13 * m11
        val c06 = m5 * m15 - m13 * m7
        val c07 = m5 * m11 - m9 * m7

        val c08 = m9 * m14 - m13 * m10
        val c10 = m5 * m14 - m13 * m6
        val c11 = m5 * m10 - m9 * m6

        val c12 = m8 * m15 - m12 * m11
        val c14 = m4 * m15 - m12 * m7
        val c15 = m4 * m11 - m8 * m7

        val c16 = m8 * m14 - m12 * m10
        val c18 = m4 * m14 - m12 * m6
        val c19 = m4 * m10 - m8 * m6

        val c20 = m8 * m13 - m12 * m9
        val c22 = m4 * m13 - m12 * m5
        val c23 = m4 * m9 - m8 * m5

        val i00 = +(m5 * c00 - m6 * c04 + m7 * c08)
        val i01 = -(m1 * c00 - m2 * c04 + m3 * c08)
        val i02 = +(m1 * c02 - m2 * c06 + m3 * c10)
        val i03 = -(m1 * c03 - m2 * c07 + m3 * c11)

        val i10 = -(m4 * c00 - m6 * c12 + m7 * c16)
        val i11 = +(m0 * c00 - m2 * c12 + m3 * c16)
        val i12 = -(m0 * c02 - m2 * c14 + m3 * c18)
        val i13 = +(m0 * c03 - m2 * c15 + m3 * c19)

        val i20 = +(m4 * c04 - m5 * c12 + m7 * c20)
        val i21 = -(m0 * c04 - m1 * c12 + m3 * c20)
        val i22 = +(m0 * c06 - m1 * c14 + m3 * c22)
        val i23 = -(m0 * c07 - m1 * c15 + m3 * c23)

        val i30 = -(m10 * c08 - m5 * c16 + m6 * c20)
        val i31 = +(m0 * c08 - m1 * c16 + m2 * c20)
        val i32 = -(m0 * c10 - m1 * c18 + m2 * c22)
        val i33 = +(m0 * c11 - m1 * c19 + m2 * c23)

        val oneOverDet = 1 / (m0 * i00 + m1 * i10 + m2 * i20 + m3 * i30)

        return Mat4(
            i00 * oneOverDet,
            i01 * oneOverDet,
            i02 * oneOverDet,
            i03 * oneOverDet,

            i10 * oneOverDet,
            i11 * oneOverDet,
            i12 * oneOverDet,
            i13 * oneOverDet,

            i20 * oneOverDet,
            i21 * oneOverDet,
            i22 * oneOverDet,
            i23 * oneOverDet,

            i30 * oneOverDet,
            i31 * oneOverDet,
            i32 * oneOverDet,
            i33 * oneOverDet
        )
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun inverse(m: Mat4) = m.inverse()
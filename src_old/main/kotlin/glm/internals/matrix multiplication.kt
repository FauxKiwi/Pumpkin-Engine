package glm.internals

internal fun __Mat4_times_Mat4__(m0: glm.Mat4, m1: glm.Mat4): glm.Mat4 {
    val m0a = m0.array
    val m1a = m1.array

    val m1c = floatArrayOf(
        m1a[0], m1a[0], m1a[0], m1a[0],
        m1a[1], m1a[1], m1a[1], m1a[1],
        m1a[2], m1a[2], m1a[2], m1a[2],
        m1a[3], m1a[3], m1a[3], m1a[3],
        m1a[4], m1a[4], m1a[4], m1a[4],
        m1a[5], m1a[5], m1a[5], m1a[5],
        m1a[6], m1a[6], m1a[6], m1a[6],
        m1a[7], m1a[7], m1a[7], m1a[7],
        m1a[8], m1a[8], m1a[8], m1a[8],
        m1a[9], m1a[9], m1a[9], m1a[9],
        m1a[10], m1a[10], m1a[10], m1a[10],
        m1a[11], m1a[11], m1a[11], m1a[11],
        m1a[12], m1a[12], m1a[12], m1a[12],
        m1a[13], m1a[13], m1a[13], m1a[13],
        m1a[14], m1a[14], m1a[14], m1a[14],
        m1a[15], m1a[15], m1a[15], m1a[15],
    )

    val m0c = floatArrayOf(
        m0a[0], m0a[1], m0a[2], m0a[3],
        m0a[4], m0a[5], m0a[6], m0a[7],
        m0a[8], m0a[9], m0a[10], m0a[11],
        m0a[12], m0a[13], m0a[14], m0a[15],
        m0a[0], m0a[1], m0a[2], m0a[3],
        m0a[4], m0a[5], m0a[6], m0a[7],
        m0a[8], m0a[9], m0a[10], m0a[11],
        m0a[12], m0a[13], m0a[14], m0a[15],
        m0a[0], m0a[1], m0a[2], m0a[3],
        m0a[4], m0a[5], m0a[6], m0a[7],
        m0a[8], m0a[9], m0a[10], m0a[11],
        m0a[12], m0a[13], m0a[14], m0a[15],
        m0a[0], m0a[1], m0a[2], m0a[3],
        m0a[4], m0a[5], m0a[6], m0a[7],
        m0a[8], m0a[9], m0a[10], m0a[11],
        m0a[12], m0a[13], m0a[14], m0a[15],
    )

    n_f32_timesAssign(m1c, 0, m0c, 0, 64)

    n_f32_plusAssign(m1c, 0, m1c, 16, 16)
    n_f32_plusAssign(m1c, 0, m1c, 32, 16)
    n_f32_plusAssign(m1c, 0, m1c, 48, 16)

    return glm.Mat4(m1c)
}
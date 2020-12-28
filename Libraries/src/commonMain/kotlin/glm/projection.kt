package glm

fun perspective(fovY: Float, aspectRatio: Float, zNear: Float, zFar: Float) =
    if (aspectRatio == 0f) throw ArithmeticException()
    else tan(fovY / 2f).let { tanHalfFovy ->
        Mat4(
            m0 = 1f / (aspectRatio * tanHalfFovy),
            m5 = 1f / (tanHalfFovy),
            m10 = -(zFar + zNear) / (zFar - zNear),
            m11 = -1f,
            m14 = -(2f * zFar * zNear) / (zFar - zNear),
            m15 = 0f
        )
    }

fun ortho(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float) = Mat4(
    m0 = 2f / (right - left),
    m5 = 2f / (top - bottom),
    m10 = -2f / (zFar - zNear),
    m12 = -(right + left) / (right - left),
    m13 = -(top + bottom) / (top - bottom),
    m14 = -(zFar + zNear) / (zFar - zNear),
)
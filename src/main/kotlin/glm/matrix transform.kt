package glm

fun translate(v: Vec3) = Mat4(
    m12 = v.x,
    m13 = v.y,
    m14 = v.z
)

fun translate(x: Float, y: Float, z: Float) = Mat4(
    m12 = x,
    m13 = y,
    m14 = z
)

fun rotate(angle: Float, axis: Vec3): Mat4 {
    val c = cos(angle)
    val s = sin(angle)

    val dot = axis.x * axis.x + axis.y * axis.y + axis.z * axis.z
    val inv = inverseSqrt(dot)

    val aX = axis.x * inv
    val aY = axis.y * inv
    val aZ = axis.z * inv

    val tempX = (1f - c) * aX
    val tempY = (1f - c) * aY
    val tempZ = (1f - c) * aZ

    return Mat4(
        c + tempX * aX,
        tempX * aY + s * aZ,
        tempX * aZ - s * aY,
        0f,

        tempY * aX - s * aZ,
        c + tempY * aY,
        tempY * aZ + s * aX,
        0f,

        tempZ * aX + s * aY,
        tempZ * aY - s * aX,
        c + tempZ * aZ
    )
}

fun rotateXYZ(v: Vec3) = rotateXYZ(v.x, v.y, v.z)

fun rotateXYZ(x: Float, y: Float, z: Float): Mat4 {
    val sinX = sin(x)
    val cosX = cos(x)
    val sinY = sin(y)
    val cosY = cos(y)
    val sinZ = sin(z)
    val cosZ = cos(z)
    val m_sinX = -sinX
    val m_sinY = -sinY
    val m_sinZ = -sinZ

    val nm01 = m_sinX * m_sinY
    val nm02 = cosX * m_sinY
    return Mat4(
        m8 = 0f,
        m9 = m_sinX * cosY,
        m10 = cosX * cosY,

        m0 = cosY * cosZ,
        m1 = nm01 * cosZ + cosX * sinZ,
        m2 = nm02 * cosZ + sinX * sinZ,
        m4 = cosY * m_sinZ,
        m5 = nm01 * m_sinZ + cosX * cosZ,
        m6 = nm02 * m_sinZ + sinX * cosZ,
    )
}

fun scale(v: Vec3) = Mat4(
    m0 = v.x,
    m5 = v.y,
    m10 = v.z
)

fun scale(x: Float, y: Float, z: Float) = Mat4(
    m0 = x,
    m5 = y,
    m10 = z
)
package glm

open class QuatT<T>(var re: T, var i: T, var j: T, var k: T) {
    inline var w get() = re; set(v) { re = v }
    inline var x get() = i; set(v) { i = v }
    inline var y get() = j; set(v) { j = v }
    inline var z get() = k; set(v) { k = v }
}

class Quat(re: Float, i: Float, j: Float, k: Float) : QuatT<Float>(re, i, j, k) {

    constructor() : this(1f, 0f, 0f, 0f)
    constructor(v: Vec3) : this() {
        val eX = v.x * 0.5f
        val eY = v.y * 0.5f
        val eZ = v.z * 0.5f
        val cX = cos(eX)
        val cY = cos(eY)
        val cZ = cos(eZ)
        val sX = sin(eX)
        val sY = sin(eY)
        val sZ = sin(eZ)
        re = cX * cY * cZ + sX * sY * sZ
        i = sX * cY * cZ - cX * sY * sZ
        j = cX * sY * cZ + sX * cY * sZ
        k = cX * cY * sZ - sX * sY * cZ
    }

    operator fun times(v: Vec3): Vec3 {
        val uvX = y * v.z - v.y * z
        val uvY = z * v.x - v.z * x
        val uvZ = x * v.y - v.x * y
        val uuvX = y * uvZ - uvY * z
        val uuvY = z * uvX - uvZ * x
        val uuvZ = x * uvY - uvX * y
        return Vec3(
            v.x + (uvX * w + uuvX) * 2f,
            v.y + (uvY * w + uuvY) * 2f,
            v.z + (uvZ * w + uuvZ) * 2f
        )
    }

    fun toMat4(): Mat4 {
        val qxx = i * i
        val qyy = j * j
        val qzz = k * k
        val qxz = i * k
        val qxy = i * j
        val qyz = j * k
        val qwx = re * i
        val qwy = re * j
        val qwz = re * k

        return Mat4(
            m0 = 1f - 2f * (qyy + qzz),
            m1 = 2f * (qxy + qwz),
            m2 = 2f * (qxz - qwy),

            m4 = 2f * (qxy - qwz),
            m5 = 1f - 2f * (qxx + qzz),
            m6 = 2f * (qyz + qwx),

            m8 = 2f * (qxz + qwy),
            m9 = 2f * (qyz - qwx),
            m10 = 1f - 2f * (qxx + qyy)
        )
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun toMat4(q: Quat) = q.toMat4()
package glm

class Quat(var re: Float, var i: Float, var j: Float, var k: Float) { //TODO
    inline var w get() = re; set(v) { re = v }
    inline var x get() = i; set(v) { i = v }
    inline var y get() = j; set(v) { j = v }
    inline var z get() = k; set(v) { k = v }

    constructor() : this(1f, 0f, 0f, 0f)
    constructor(v: Vec3) : this() {
        val q = glm_.quat.Quat(glm_.vec3.Vec3(v.x, v.y, v.z))
        re = q.w; i = q.x; j = q.y; k = q.z
    } //TODO
    //constructor(a: FloatArray) : this(a[0], a[1], a[2], a[3])

    fun toMat4() = Mat4(
        glm_.glm.toMat4(glm_.quat.Quat(w, x, y, z)).array
    ) //TODO
}

@Suppress("NOTHING_TO_INLINE")
inline fun toMat4(q: Quat) = q.toMat4()
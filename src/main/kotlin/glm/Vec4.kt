package glm

class Vec4(var x: Float, var y: Float, var z: Float, var w: Float) {
    inline var r get() = x; set(v) { x = v }
    inline var g get() = y; set(v) { y = v }
    inline var b get() = z; set(v) { z = v }
    inline var a get() = w; set(v) { w = v }

    constructor() : this(0f, 0f, 0f, 0f)
    constructor(v: Vec4) : this(v.x, v.y, v.z, v.w)
    constructor(xyzw: Float) : this(xyzw, xyzw, xyzw, xyzw)

    operator fun get(i: Int) = when (i) { 0 -> x; 1 -> y; 2 -> z; 3 -> w; else -> throw IndexOutOfBoundsException() }
}
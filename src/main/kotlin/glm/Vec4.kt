package glm

open class Vec4T<T>(var x: T, var y: T, var z: T, var w: T) {
    inline var r get() = x; set(v) { x = v }
    inline var g get() = y; set(v) { y = v }
    inline var b get() = z; set(v) { z = v }
    inline var a get() = w; set(v) { w = v }

    operator fun get(i: Int) = when (i) { 0 -> x; 1 -> y; 2 -> z; 3 -> w; else -> throw IndexOutOfBoundsException() }
}

class Vec4(x: Float, y: Float, z: Float, w: Float) : Vec4T<Float>(x, y, z, w) {
    constructor() : this(0f, 0f, 0f, 0f)
    constructor(v: Vec4) : this(v.x, v.y, v.z, v.w)
    constructor(xyzw: Float) : this(xyzw, xyzw, xyzw, xyzw)
}

class Vec4i(x: Int, y: Int, z: Int, w: Int) : Vec4T<Int>(x, y, z, w)

class Vec4d(x: Double, y: Double, z: Double, w: Double) : Vec4T<Double>(x, y, z, w)

class Vec4b(x: Boolean, y: Boolean, z: Boolean, w: Boolean) : Vec4T<Boolean>(x, y, z, w)
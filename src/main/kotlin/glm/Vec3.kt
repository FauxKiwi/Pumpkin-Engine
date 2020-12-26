package glm

open class Vec3T<T>(var x: T, var y: T, var z: T) {
    inline var r get() = x; set(v) { x = v }
    inline var g get() = y; set(v) { y = v }
    inline var b get() = z; set(v) { z = v }

    operator fun get(i: Int) = when (i) { 0 -> x; 1 -> y; 2 -> z; else -> throw IndexOutOfBoundsException() }
    operator fun set(i: Int, v: T) = when (i) { 0 -> x = v; 1 -> y = v; 2 -> z = v; else -> throw IndexOutOfBoundsException() }
}

class Vec3(x: Float, y: Float, z: Float) : Vec3T<Float>(x, y, z) {

    constructor() : this(0f, 0f, 0f)
    constructor(v: Vec3) : this(v.x, v.y, v.z)

    constructor(xyz: Float) : this(xyz, xyz, xyz)
    constructor(xy: Vec2, z: Float) : this(xy.x, xy.y, z)

    operator fun unaryMinus() = Vec3(-x, -y, -z)
    infix operator fun plus(v: Vec3) = Vec3(x + v.x, y + v.y, z + v.z)
    infix operator fun minus(v: Vec3) = Vec3(x - v.x, y - v.y, z - v.z)
    infix operator fun times(v: Vec3) = Vec3(x * v.x, y * v.y, z * v.z)
    infix operator fun times(s: Float) = Vec3(x * s, y * s, z * s)

    infix operator fun plusAssign(v: Vec3) {
        x += v.x
        y += v.y
        z += v.z
    }

    infix operator fun minusAssign(v: Vec3) {
        x -= v.x
        y -= v.y
        z -= v.z
    }
}

class Vec3i(x: Int, y: Int, z: Int) : Vec3T<Int>(x, y, z)

class Vec3ui(x: UInt, y: UInt, z: UInt) : Vec3T<UInt>(x, y, z)

class Vec3d(x: Double, y: Double, z: Double) : Vec3T<Double>(x, y, z)

class Vec3b(x: Boolean, y: Boolean, z: Boolean) : Vec3T<Boolean>(x, y, z)
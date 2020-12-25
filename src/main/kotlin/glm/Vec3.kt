package glm

class Vec3(var x: Float, var y: Float, var z: Float) {

    constructor() : this(0f, 0f, 0f)
    constructor(v: Vec3) : this(v.x, v.y, v.z)

    constructor(xyz: Float) : this(xyz, xyz, xyz)
    constructor(xy: Vec2, z: Float) : this(xy.x, xy.y, z)

    operator fun get(i: Int) = when (i) { 0 -> x; 1 -> y; 2 -> z; else -> throw IndexOutOfBoundsException() }

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
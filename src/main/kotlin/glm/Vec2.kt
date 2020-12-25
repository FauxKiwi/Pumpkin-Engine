package glm

open class Vec2T<T>(var x: T, var y: T) {
    operator fun get(i: Int): T = when (i) { 0 -> x; 1 -> y; else -> throw IndexOutOfBoundsException() }
}

class Vec2(x: Float, y: Float) : Vec2T<Float>(x, y) {

    constructor() : this(0f, 0f)
    constructor(v: Vec2) : this(v.x, v.y)

    constructor(xy: Float) : this(xy, xy)

    infix operator fun plus(v: Vec2) = Vec2(x + v.x, y + v.y)
    infix operator fun minus(v: Vec2) = Vec2(x - v.x, y - v.y)

    infix operator fun times(s: Float) = Vec2(x * s, y * s)
    infix operator fun times(xy: Vec2) = Vec2(xy.x, xy.y)
}

class Vec2i(x: Int, y: Int) : Vec2T<Int>(x, y)

class Vec2d(x: Double, y: Double) : Vec2T<Double>(x, y)

class Vec2b(x: Boolean, y: Boolean) : Vec2T<Boolean>(x, y)
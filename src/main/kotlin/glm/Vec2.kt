package glm

class Vec2(var x: Float, var y: Float) {

    constructor() : this(0f, 0f)
    constructor(v: Vec2) : this(v.x, v.y)

    constructor(xy: Float) : this(xy, xy)

    operator fun get(i: Int) = when (i) { 0 -> x; 1 -> y; else -> throw IndexOutOfBoundsException() }

    infix operator fun plus(v: Vec2) = Vec2(x + v.x, y + v.y)
    infix operator fun minus(v: Vec2) = Vec2(x - v.x, y - v.y)

    infix operator fun times(s: Float) = Vec2(x * s, y * s)
    infix operator fun times(xy: Vec2) = Vec2(xy.x, xy.y)
}

class Vec2i(val x: Int, val y: Int) {

    operator fun get(i: Int) = when (i) { 0 -> x; 1 -> y; else -> throw IndexOutOfBoundsException() }
}

class Vec2d(val x: Double, val y: Double) {

    operator fun get(i: Int) = when (i) { 0 -> x; 1 -> y; else -> throw IndexOutOfBoundsException() }
}
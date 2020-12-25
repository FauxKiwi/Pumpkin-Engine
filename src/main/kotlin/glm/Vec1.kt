package glm

open class Vec1T<T>(var x: T) {
    operator fun get(i: Int) = if (i == 0) x else throw IndexOutOfBoundsException()
}

class Vec1(x: Float) : Vec1T<Float>(x)

class Vec1i(x: Int) : Vec1T<Int>(x)

class Vec1d(x: Double) : Vec1T<Double>(x)

class Vec1b(x: Boolean) : Vec1T<Boolean>(x)
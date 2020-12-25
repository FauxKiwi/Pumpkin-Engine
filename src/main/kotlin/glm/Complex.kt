package glm

open class ComplexT<T>(re: T, im: T)

class Complex(re: Float, im: Float) : ComplexT<Float>(re, im)

inline val Float.i get() = Complex(0f, this)
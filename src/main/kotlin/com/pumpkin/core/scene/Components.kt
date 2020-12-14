package com.pumpkin.core.scene

import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4

inline class TransformComponent(@ComponentSize(6) val t: FloatArray) {
    var position: Vec3
        get() = Vec3(t[0], t[1], t[2])
        set(value) { t[0] = value[0]; t[1] = value[1]; t[2] = value[2] }
    var scale: Vec2
        get() = Vec2(t[3], t[4])
        set(value) { t[3] = value[0]; t[4] = value[1] }
    var rotation: Float
        get() = t[5]
        set(value) { t[5] = value }
}

inline class SpriteRendererComponent(@ComponentSize(4) val c: FloatArray) {
    var color: Vec4
        get() = Vec4(c[0], c[1], c[2], c[3])
        set(value) { c[0] = value[0]; c[1] = value[1]; c[2] = value[2]; c[3] = value[3] }
}

inline class MeshComponent(val mesh: Float) {

}

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class ComponentSize(val size: Int)
package com.pumpkin.core.scene

import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import imgui.toByteArray
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class TagComponent(str: String) {
    var byteArray = str.toByteArray(64)
    var tag: String
        get() = byteArray.decodeToString()
        set(value) { byteArray = value.toByteArray(64) }
    val trimTag: String
        get() = tag.trim('\u0000')
}

inline class TransformComponent(@ComponentSize(8) val t: FloatArray) {
    var position: Vec3
        get() = Vec3(t[0], t[1], t[2])
        set(value) { t[0] = value[0]; t[1] = value[1]; t[2] = value[2] }
    var scale: Vec2
        get() = Vec2(t[3], t[4])
        set(value) { t[3] = value[0]; t[4] = value[1] }
    var rotation: Float
        get() = glm.degrees(t[5])
        set(value) { t[5] = glm.radians(value) }
    val transform: Mat4
        get() = glm.translate(glm.rotate(glm.scale(Mat4.identity,
            Vec3(scale, 1f)),
            rotation, Vec3(0, 0, 1)),
            position)
}

class CameraComponent(@ComponentSize(2) val camera: SceneCamera) {
    var primary = true
    var fixedAspectRatio = false
}

inline class SpriteRendererComponent(@ComponentSize(4) val c: FloatArray) {
    var color: Vec4
        get() = Vec4(c[0], c[1], c[2], c[3])
        set(value) { c[0] = value[0]; c[1] = value[1]; c[2] = value[2]; c[3] = value[3] }
}

class NativeScriptComponent {
    @PublishedApi internal var i: ScriptableEntity? = null
    inline val instance: ScriptableEntity get() = i!!
    internal var instantiateScript: (Entity) -> Unit = {  }
    internal var destroyScript: () -> Unit = {  }

    inline fun <reified T : ScriptableEntity> bind() = bind(T::class)
    fun <C : ScriptableEntity> bind(clazz: KClass<C>) {
        instantiateScript = { entity -> i = clazz.primaryConstructor!!.call().instantiate(entity) }
        destroyScript = { i = null }
    }
}

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class ComponentSize(val size: Int)
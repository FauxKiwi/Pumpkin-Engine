package com.pumpkin.core.scene

import com.pumpkin.core.math.inverseTransform
import glm.Mat4
import glm.Vec3
import glm.Vec4
//import imgui.toByteArray
import kotlin.reflect.KClass
//import kotlin.reflect.full.primaryConstructor

class TagComponent(var str: String) {
    /*var byteArray = str.toByteArray(64)
    var tag: String
        get() = byteArray.decodeToString()
        set(value) { byteArray = value.toByteArray(64) }
    val trimTag: String
        get() = tag.trim('\u0000')*/
}

inline class TransformComponent(@ComponentSize(9) val t: FloatArray) {
    var position: Vec3
        get() = Vec3(t[0], t[1], t[2])
        set(value) { t[0] = value[0]; t[1] = value[1]; t[2] = value[2] }
    var rotation: Vec3
        get() = Vec3(t[3], t[4], t[5])
        set(value) { t[3] = value[0]; t[4] = value[1]; t[5] = value[2] }
    inline var rx get() = t[3]; set(x) { t[3] = x }
    inline var ry get() = t[4]; set(y) { t[4] = y }
    inline var rz get() = t[5]; set(z) { t[5] = z }
    var scale: Vec3
        get() = Vec3(t[6], t[7], t[8])
        set(value) { t[6] = value[0]; t[7] = value[1]; t[8] = value[2] }
    val scaleInv: Vec3
        get() = Vec3(1f / t[6], 1f / t[7], 1f / t[8])

    val transform: Mat4
        get() = com.pumpkin.core.math.transform(t)
    val inverse: Mat4
        get() = inverseTransform(t)
}

inline class ChildComponent(val child: entt.Entity)

inline class ParentComponent(val parent: entt.Entity)

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
        //instantiateScript = { entity -> i = clazz.primaryConstructor!!.call().instantiate(entity) }
        destroyScript = { i = null }
    }
}

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class ComponentSize(val size: Int)
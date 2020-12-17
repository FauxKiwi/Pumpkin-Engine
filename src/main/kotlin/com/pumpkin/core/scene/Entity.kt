package com.pumpkin.core.scene

import com.pumpkin.ecs.Entity as EntityHandle

class Entity(val entityHandle: EntityHandle, val scene: Scene) {

    inline fun <reified T : Any> addComponent(vararg args: Any?) = scene.registry.emplace(T::class, entityHandle, args)

    inline fun <reified T : Any> addComponent(component: T): T = scene.registry.insert(entityHandle, component)

    inline fun <reified T : Any> hasComponent(): Boolean = scene.registry.has(T::class, entityHandle)

    inline fun <reified T : Any> getComponent(): T = scene.registry.get(entityHandle)

    inline fun <reified T : Any> removeComponent() = scene.registry.remove(T::class, entityHandle)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Entity

        if (entityHandle != other.entityHandle) return false
        if (scene != other.scene) return false

        return true
    }

    override fun hashCode(): Int {
        var result = entityHandle.hashCode()
        result = 31 * result + scene.hashCode()
        return result
    }
}
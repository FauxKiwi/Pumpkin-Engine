package com.pumpkin.core.scene

import com.pumpkin.ecs.Entity as EntityHandle

class Entity(val entityHandle: EntityHandle, val scene: Scene) {

    inline fun <reified T : Any> addComponent(component: T): T = scene.registry.emplace(entityHandle, component)

    inline fun <reified T : Any> hasComponent(): Boolean = scene.registry.has(T::class, entityHandle)

    inline fun <reified T : Any> getComponent(): T = scene.registry.get(entityHandle)

    inline fun <reified T : Any> removeComponent() = scene.registry.remove(T::class, entityHandle)
}
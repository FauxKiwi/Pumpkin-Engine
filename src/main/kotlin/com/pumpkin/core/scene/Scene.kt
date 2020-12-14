package com.pumpkin.core.scene

import com.pumpkin.core.Referencable
import com.pumpkin.core.Timestep
import com.pumpkin.core.renderer.Renderer2D
import com.pumpkin.ecs.Entity
import com.pumpkin.ecs.Registry

class Scene : Referencable() {
    val registry: Registry = Registry()

    init {
        val entity = registry.create()

        registry.onConstruct<TransformComponent>().connect(::onTransformConstruct)
        registry.emplace(entity, TransformComponent(floatArrayOf(0f, 0f, 0f, 0f, 1f, 1f, 0f)))

        if (registry.has<TransformComponent>(entity)) {
            val transform = registry.get<TransformComponent>(entity)

            val view = registry.view<TransformComponent>()
            for (e in view) {
                val tr = view.get(e)
            }

            val group = registry.group<TransformComponent, MeshComponent>()
        }
    }

    override fun destruct() {

    }

    fun createEntity(): Entity = registry.create()

    fun onUpdate(ts: Timestep) {
        val group = registry.group<TransformComponent, SpriteRendererComponent>()
        for (entity in group) {
            val (transform, sprite) = group.get(entity)
            Renderer2D.drawQuad(transform.position, transform.scale, transform.rotation, sprite.color)
        }
    }
}

fun onTransformConstruct(registry: Registry, entity: Entity) {
    println("Constructed")
}
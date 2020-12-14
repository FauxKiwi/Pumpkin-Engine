package com.pumpkin.ecs

import kotlin.reflect.KClass

inline class Entity(val id: Int) {

}

class Registry {
    private val entities = mutableListOf<Entity>()
    private var entityID: Int = 0
    val classes = hashMapOf<Entity, MutableList<KClass<*>>>()
    val components = hashMapOf<Entity, HashMap<KClass<*>, Any>>()
    val constructionHandlers = hashMapOf<KClass<*>, ConstructionHandler>()

    fun create(): Entity {
        return Entity(entityID++).also {
            entities.add(it)
            classes[it] = mutableListOf()
            components[it] = hashMapOf()
        }
    }

    fun emplace(entity: Entity, element: Any) {
        classes[entity]?.add(element::class)
        components[entity]?.put(element::class, element)
        constructionHandlers[element::class]?.handles?.forEach { it(this, entity) }
    }

    inline fun <reified T> onConstruct(): ConstructionHandler {
        return ConstructionHandler(mutableListOf()).also {
            constructionHandlers[T::class] = it
        }
    }

    inline fun <reified T> has(entity: Entity): Boolean = classes[entity]?.contains(T::class) ?: false

    inline fun <reified T> get(entity: Entity): T = components[entity]?.get(T::class) as T

    inline fun <reified T> view(): RegistryView<T> {
        val entities = mutableListOf<Entity>()
        val components = hashMapOf<Entity, T>()
        for ((e, l) in classes) for (c in l) {
            if (c == T::class) {
                entities.add(e)
                components[e] = get(e)
            }
        }
        return RegistryView(entities, components)
    }

    inline fun <reified A, reified B> group(): RegistryGroup<A, B> {
        val entities = mutableListOf<Entity>()
        val components = hashMapOf<Entity, Pair<A, B>>()
        for ((e, l) in classes) {
            var a = false
            var b = false
            inn@for (c in l) {
                if (c == A::class) {
                    if (b) {
                        entities.add(e)
                        components[e] = Pair(get(e), get(e))
                        break@inn
                    } else a = true
                } else if (c == B::class) {
                    if (a) {
                        entities.add(e)
                        components[e] = Pair(get(e), get(e))
                        break@inn
                    } else b = true
                }
            }
        }
        return RegistryGroup(entities, components)
    }
}

class RegistryView<T>(private val entities: MutableList<Entity>, private val components: HashMap<Entity, T>) {

    fun get(entity: Entity): T = components[entity]!!

    operator fun iterator(): Iterator<Entity> = entities.iterator()
}

class RegistryGroup<A, B>(private val entities: MutableList<Entity>, private val components: HashMap<Entity, Pair<A, B>>) {

    fun get(entity: Entity): Pair<A, B> = components[entity]!!

    operator fun iterator(): Iterator<Entity> = entities.iterator()
}

inline class ConstructionHandler(val handles: MutableList<ConstructionHandle>) {

    fun connect(handle: ConstructionHandle) {
        handles.add(handle)
    }
}

typealias ConstructionHandle = (Registry, Entity) -> Unit
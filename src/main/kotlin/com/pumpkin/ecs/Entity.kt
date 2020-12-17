package com.pumpkin.ecs

import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.full.primaryConstructor

inline class Entity(val id: Int) {

}

class Registry {
    private val entities = mutableListOf<Entity>()
    private var entityID: Int = 0
    val classes = mutableListOf<MutableList<KClass<*>>>()
    val classEntities = hashMapOf<KClass<*>, MutableList<Entity>>()
    val components = mutableListOf<HashMap<KClass<*>, Any>>()
    val constructionHandlers = hashMapOf<KClass<*>, ConstructionHandler>()

    fun create(): Entity {
        return Entity(entityID++).also {
            entities.add(it.id, it)
            classes.add(it.id, mutableListOf())
            components.add(it.id, hashMapOf())
        }
    }

    inline fun <reified T : Any> emplace(entity: Entity, vararg args: Any?): T = emplace(T::class, entity, args)
    fun <T : Any> emplace(clazz: KClass<T>, entity: Entity, args: Array<out Any?>): T {
        classes[entity.id].add(clazz)
        if (!classEntities.containsKey(clazz))
            classEntities[clazz] = mutableListOf()
        classEntities[clazz]!!.add(entity)
        val element = clazz.primaryConstructor!!.call(*args).also { components[entity.id][clazz] = it }
        constructionHandlers[clazz]?.handles?.forEach { it(this, entity) }
        return element
    }

    fun <T : Any> insert(entity: Entity, element: T): T {
        classes[entity.id].add(element::class)
        if (!classEntities.containsKey(element::class))
            classEntities[element::class] = mutableListOf()
        classEntities[element::class]!!.add(entity)
        components[entity.id][element::class] = element
        constructionHandlers[element::class]?.handles?.forEach { it(this, entity) }
        return element
    }

    inline fun <reified T : Any> onConstruct(): ConstructionHandler = onConstruct(T::class)
    fun onConstruct(clazz: KClass<*>): ConstructionHandler = ConstructionHandler(mutableListOf()).also {
        constructionHandlers[clazz] = it
    }

    inline fun <reified T : Any> has(entity: Entity): Boolean = has(T::class, entity)
    fun has(clazz: KClass<*>, entity: Entity): Boolean = classes[entity.id].contains(clazz)

    inline fun <reified T : Any> get(entity: Entity): T = components[entity.id][T::class] as T
    fun <T : Any> get(clazz: KClass<T>, entity: Entity): T = clazz.cast(components[entity.id][clazz])

    inline fun <reified T : Any> remove(entity: Entity) = remove(T::class, entity)
    fun remove(clazz: KClass<*>, entity: Entity) {
        classes[entityID].remove(clazz)
        classEntities[clazz]?.remove(entity)
        components[entityID].remove(clazz)
    }

    inline fun <reified T : Any> view(): RegistryView<T> = view(T::class)
    fun <T : Any> view(clazz: KClass<T>): RegistryView<T> {
        val components = hashMapOf<Entity, T>()
        val entities = classEntities[clazz]!!
        entities.forEach {
            components[it] = get(clazz, it)
        }
        return RegistryView(components)
    }

    inline fun <reified A : Any, reified B : Any> group(): RegistryGroup<A, B> = group(A::class, B::class)
    fun <A : Any, B : Any> group(clazzA: KClass<A>, clazzB: KClass<B>): RegistryGroup<A, B> {
        val entities: List<Entity>
        val components = hashMapOf<Entity, Pair<A, B>>()
        val aEntities = classEntities[clazzA]!!
        val bEntities = classEntities[clazzB]!!.filter { aEntities.contains(it) }
        entities = aEntities.filter { bEntities.contains(it) }
        entities.forEach {
            components[it] = Pair(get(clazzA, it), get(clazzB, it))
        }
        return RegistryGroup(components)
    }
}

class RegistryView<T>(private val components: HashMap<Entity, T>) {

    fun get(entity: Entity): T = components[entity]!!

    operator fun iterator(): Iterator<Entity> = components.keys.iterator()
}

class RegistryGroup<A, B>(private val components: HashMap<Entity, Pair<A, B>>) {

    fun get(entity: Entity): Pair<A, B> = components[entity]!!

    operator fun iterator(): Iterator<Entity> = components.keys.iterator()
}

inline class ConstructionHandler(val handles: MutableList<ConstructionHandle>) {

    fun connect(handle: ConstructionHandle) {
        handles.add(handle)
    }
}

typealias ConstructionHandle = (Registry, Entity) -> Unit
package entt

import kotlin.collections.HashMap
import kotlin.reflect.KClass
import kotlin.reflect.cast

inline class Entity(val fullID: Int) {
    inline val id: Int get() = fullID and 0x00ffffff
    inline val version: Byte get() = ((fullID shr 24) and 0xff).toByte()
}

class Registry {
    val entities = mutableListOf<Entity?>()
    private val freeSlots = Stack<Int>()
    private var currentID: Int = 0
    internal val classes = mutableListOf<MutableList<KClass<*>>?>()
    internal val classEntities = hashMapOf<KClass<*>, MutableList<Entity>>()
    val components = mutableListOf<HashMap<KClass<*>, Any>?>()
    internal val constructionHandlers = hashMapOf<KClass<*>, ConstructionHandler>()

    fun create(): Entity {
        if (!freeSlots.empty()) {
            val fid = freeSlots.pop() + 0x01000000
            return Entity(fid).also {
                entities[it.id] = it
                classes[it.id] = mutableListOf()
                components[it.id] = hashMapOf()
            }
        }
        return Entity(currentID++).also {
            entities.add(it.id, it)
            classes.add(it.id, mutableListOf())
            components.add(it.id, hashMapOf())
        }
    }

    fun destroy(entity: Entity) {
        entities[entity.id] = null
        freeSlots.push(entity.fullID)
        for (clazz in classes[entity.id]!!) {
            classEntities[clazz]?.remove(entity)
        }
        classes[entity.id] = null
        components[entity.id] = null
    }

    fun <T : Any> insert(entity: Entity, element: T): T {
        classes[entity.id]!!.add(element::class)
        if (!classEntities.containsKey(element::class))
            classEntities[element::class] = mutableListOf()
        classEntities[element::class]!!.add(entity)
        components[entity.id]!![element::class] = element
        constructionHandlers[element::class]?.handles?.forEach { it(this, entity) }
        return element
    }

    inline fun <reified T : Any> has(entity: Entity): Boolean = has(T::class, entity)
    fun has(clazz: KClass<*>, entity: Entity): Boolean = if (classes.size > entity.id) classes[entity.id]!!.contains(clazz) else false

    inline fun <reified T : Any> get(entity: Entity): T = components[entity.id]!![T::class] as T
    fun <T : Any> get(clazz: KClass<T>, entity: Entity): T = clazz.cast(components[entity.id]!![clazz])

    inline fun <reified T : Any> remove(entity: Entity) = remove(T::class, entity)
    fun remove(clazz: KClass<*>, entity: Entity) {
        classes[entity.id]!!.remove(clazz)
        classEntities[clazz]?.remove(entity)
        components[entity.id]!!.remove(clazz)
    }

    inline fun <reified T : Any> view(): RegistryView<T> = view(T::class)
    fun <T : Any> view(clazz: KClass<T>): RegistryView<T> {
        val components = hashMapOf<Entity, T>()
        val entities = classEntities[clazz] ?: return RegistryView(hashMapOf())
        entities.forEach {
            components[it] = get(clazz, it)
        }
        return RegistryView(components)
    }

    inline fun <reified A : Any, reified B : Any> group(): RegistryGroup<A, B> = group(A::class, B::class)
    fun <A : Any, B : Any> group(clazzA: KClass<A>, clazzB: KClass<B>): RegistryGroup<A, B> {
        val entities: List<Entity>
        val components = hashMapOf<Entity, Pair<A, B>>()
        val aEntities = classEntities[clazzA] ?: return RegistryGroup(hashMapOf())
        val bEntities = classEntities[clazzB] ?: return RegistryGroup(hashMapOf())
        bEntities.filter { aEntities.contains(it) }
        entities = aEntities.filter { bEntities.contains(it) }
        entities.forEach {
            components[it] = Pair(get(clazzA, it), get(clazzB, it))
        }
        return RegistryGroup(components)
    }

    inline fun each(operation: (Entity) -> Unit) = entities.forEach { it?.let(operation) }


    inline fun <reified T : Any> onConstruct(): ConstructionHandler = onConstruct(T::class)
    fun onConstruct(clazz: KClass<*>): ConstructionHandler = ConstructionHandler(mutableListOf()).also {
        constructionHandlers[clazz] = it
    }
}

class RegistryView<T>(@PublishedApi internal val components: HashMap<Entity, T>) {

    fun get(entity: Entity): T = components[entity]!!

    @Suppress("NOTHING_TO_INLINE")
    inline operator fun iterator(): Iterator<Entity> = components.keys.iterator()

    inline fun each(operation: (MutableMap.MutableEntry<Entity, T>) -> Unit) = components.iterator().forEach(operation)
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

expect class Stack<E>() {
    fun push(item: E): E
    fun pop(): E
    fun empty(): Boolean
}
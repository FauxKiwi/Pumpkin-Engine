package entt

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

inline fun <reified T : Any> Registry.emplace(entity: Entity, vararg args: Any?): T = emplace(T::class, entity, args)
fun <T : Any> Registry.emplace(clazz: KClass<T>, entity: Entity, args: Array<out Any?>): T {
    classes[entity.id]!!.add(clazz)
    if (!classEntities.containsKey(clazz))
        classEntities[clazz] = mutableListOf()
    classEntities[clazz]!!.add(entity)
    val element = clazz.primaryConstructor!!.call(*args).also { components[entity.id]!![clazz] = it }
    constructionHandlers[clazz]?.handles?.forEach { it(this, entity) }
    return element
}
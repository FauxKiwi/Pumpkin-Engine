package com.pumpkin.core.scene

import com.pumpkin.core.Timestep

open class ScriptableEntity {
    @PublishedApi internal lateinit var entity: Entity

    internal fun instantiate(entity: Entity): ScriptableEntity { this.entity = entity; return this }

    inline fun <reified T : Any> getComponent(): T = entity.getComponent()

    open fun onCreate() = Unit

    open fun onDestroy() = Unit

    open fun onUpdate(ts: Timestep) = Unit
}
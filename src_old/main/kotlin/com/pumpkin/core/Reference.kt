package com.pumpkin.core

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Reference<T: Referencable> : ReadWriteProperty<Referencable, T> {
    private var ref: T? = null

    override fun setValue(thisRef: Referencable, property: KProperty<*>, value: T) {
        ref = value
        if (!thisRef.references.contains(ref)) thisRef.references.add(ref!!)
    }

    override fun getValue(thisRef: Referencable, property: KProperty<*>): T {
        return ref!!
    }
}

abstract class Referencable : AutoCloseable {
    val references: MutableList<Referencable> = mutableListOf()

    override fun close() {
        destruct()
        for (reference in references) reference.close()
    }

    abstract fun destruct()
}

class TestIt : Referencable() {
    init {
        Debug.logFatal("Init")
    }

    override fun destruct() {
        Debug.logFatal("Destruct")
    }
}
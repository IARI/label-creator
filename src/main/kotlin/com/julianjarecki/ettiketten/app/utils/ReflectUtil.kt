package com.julianjarecki.ettiketten.app.utils

import kotlin.reflect.KClass

inline fun <reified V> KClass<*>.implements() = V::class.java.isAssignableFrom(java)
infix fun <V : Any> KClass<V>.has(v: Any) = v.javaClass.isAssignableFrom(java)
infix fun <V : Any, T : Any> V.instanceOf(t: KClass<T>) = javaClass.isAssignableFrom(t.java)
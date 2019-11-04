package com.julianjarecki.ettiketten.app.utils

import javafx.beans.Observable
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringExpression
import javafx.beans.binding.When
import javafx.beans.property.Property
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableObjectValue
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import tornadofx.objectBinding
import tornadofx.stringBinding
import java.util.logging.Level
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

val splitComponentsPatternRegex = """^(?<indent>\s*)//\s*/+\s*$\s*//\s*(?<compName>[^\r\n]*?)$(?<params>.*?)^\s*//\s*-+\s*${'$'}"""
        .toRegex(setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL))
        .toPattern()

inline fun Boolean.ifTrue(action: () -> Unit) {
    run { if (this) action() }
}

inline fun <T, R> T.letif(pred: (T) -> Boolean, block: (T) -> R): R? {
    return if (pred(this)) block(this) else null
}

inline fun <T, R> T?.ifnull(block: () -> R): R? {
    return if (this == null) block() else null
}

inline fun <T> T?.default(block: () -> T): T {
    return if (this == null) block() else this
}

fun gcd(a: Int, b: Int): Int {
    if (b == 0) return a
    return gcd(b, a % b)
}

val Int.padStr get() = toString().padStart(2, '0')
val Number.padStr get() = toString().padStart(2, '0')

infix fun IntRange.symAdd(x: Int) = (start - x)..(endInclusive + x)

fun <T, R> Property<R>.bindTo(mainDep: ObservableValue<T>, vararg dependencies: Observable, op: (T?) -> R?) {
    mainDep.objectBinding(*dependencies, op = op).let(::bind)
}

fun <T, R> ObservableValue<T>.objectBindingNonNull(vararg dependencies: Observable, op: (T) -> R?) = objectBinding(*dependencies) {
    it?.let(op)
}

fun <T, R> ObservableValue<T>.objectBindingNonNull2(vararg dependencies: Observable, op: (T) -> R) = objectBinding(*dependencies) {
    it!!.let(op)
}

//fun <T> ObservableValue<Boolean>.cond(ifTrue: ObservableValue<T>, ifFalse: ObservableValue<T>) =
sealed class ThenElse<T> {
    abstract operator fun invoke(`when`: When): ObservableValue<T>

    data class ThenElseObservable12<T>(
            val then: ObservableObjectValue<T>,
            val `else`: ObservableObjectValue<T>
    ) : ThenElse<T>() {
        override fun invoke(`when`: When) = `when`.then(then).otherwise(`else`)
    }

    data class ThenElseObservable2<T>(
            val then: T,
            val `else`: ObservableObjectValue<T>
    ) : ThenElse<T>() {
        override fun invoke(`when`: When) = `when`.then(then).otherwise(`else`)
    }

    data class ThenElseStringObservable2(
            val then: String,
            val `else`: ObservableValue<String>
    ) : ThenElse<String>() {
        override fun invoke(`when`: When) = `when`.then(then).otherwise(`else`.stringBinding { it })
    }

    data class ThenElseStringObservable3(
            val then: String,
            val `else`: String
    ) : ThenElse<String>() {
        override fun invoke(`when`: When) = `when`.then(then).otherwise(`else`)
    }
}

infix fun <T> ObservableObjectValue<T>.`else`(other: ObservableObjectValue<T>) = ThenElse.ThenElseObservable12(this, other)
infix fun String.`else`(other: ObservableValue<String>) = ThenElse.ThenElseStringObservable2(this, other)
infix fun String.`else`(other: String) = ThenElse.ThenElseStringObservable3(this, other)

infix fun <T> ObservableBooleanValue.ifThen(thenElse: ThenElse<T>) = Bindings
        .`when`(this)
        .let(thenElse::invoke)

infix fun <A, B, C> ((A) -> B).compose(f: (B) -> C): (A) -> C = {
    f.invoke(invoke(it))
}

operator fun ObservableValue<String>.plus(other: String): StringExpression = Bindings.concat(this, other)
operator fun ObservableValue<String>.plus(other: ObservableValue<String>): StringExpression = Bindings.concat(this, other)

@Suppress("UNCHECKED_CAST")
fun <T : Any, R> KClass<T>.propByName(name: String) = memberProperties.find { p -> p.name == name } as KProperty1<T, R>

fun <T> ObservableList<T>.syncWith(iterable: Iterable<T>, equals: (T, T) -> Boolean) {
    val done = HashSet<Int>()
    iterable.forEach { new ->
        if (none { old -> equals(old, new) }) {
            add(new)
        }
    }
    forEach { old ->
        if (none { new -> equals(old, new) }) {
            remove(old)
        }
    }
}

//fun <E, F> ObservableList<F>.mapped(mapper: (F) -> E) = MappedList<E, F>(this, mapper)

fun String.increaseOrAddLastDigit(): String {
    val lastDigitRange = (indexOfLast { !it.isDigit() } + 1)..(length - 1)
    val lastNumber = substring(lastDigitRange)
    val prefix = substring(0, lastDigitRange.start)
    val dot = if (prefix.endsWith('.')) "" else "."
    val newNumber = (lastNumber.toIntOrNull() ?: 0) + 1
    return "$prefix$dot${newNumber.padStr}"
}

fun List<String>.makeUnique(str: String, makeNew: String.() -> String = String::increaseOrAddLastDigit): String {
    var result = str
    while (result in this) result = result.makeNew()
    return result
}

fun String.padEndSlash(slash: Char = '/') = trimEnd(slash) + slash

fun Property<String>.makeUnique(others: List<String>, makeNew: String.() -> String = String::increaseOrAddLastDigit) {
    value = others.makeUnique(value, makeNew)
}

operator fun Level.compareTo(other: Level) = intValue().compareTo(other.intValue())

fun MutableMap<String, Int>.reportOccurrence(key: String) = (getOrDefault(key, 0) + 1).also { set(key, it) }

infix fun <X, Y> Iterable<X>.times(ys: Iterable<Y>) = flatMap { x -> ys.map { x to it } }
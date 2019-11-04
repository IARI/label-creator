package com.julianjarecki.ettiketten.app.utils

enum class IndentStrategy {
    None {
        override fun getIndent(source: String): Int = 0
        override fun applyTo(input: String, source: String) = input
    },
    FirstLine {
        override fun getIndent(source: String): Int = source.lineSequence().first().takeWhile { it.isWhitespace() }.length
    },
    Shortest {
        override fun getIndent(source: String): Int = source.lineSequence()
                .map { it.takeWhile { it.isWhitespace() }.length }.min() ?: 0
    };

    open fun applyTo(input: String, source: String) = getIndent(source).let(" "::repeat).let { input.replaceIndent(it) }
    abstract fun getIndent(source: String): Int
}

val String.indentWidth: Int get() = indexOfFirst { !it.isWhitespace() }.let { if (it == -1) length else it }

fun MatchResult.replaceSubgroup(groupId: String, replacer: (String) -> String) = groups
        .get(groupId)?.let { m ->
            //m.range
            val pre = value.substring(0, m.range.start - range.start)
            val post = value.substring(m.range.endInclusive + 1 - range.start)
            "$pre${replacer(m.value)}$post"
        } ?: value

inline val IntRange.length get() = endInclusive + 1 - start
inline val IntRange.end get() = endInclusive + 1

//infix fun MatchResult.absolute(group: MatchGroup) =

fun String.replaceSubgroup(regex: Regex, groupId: String, replacer: (String) -> String) = replace(regex) {
    it.replaceSubgroup(groupId, replacer)
}

fun CharSequence.levenshtein(other: CharSequence): Int {
    val rhsLength = other.length

    if (length == 0) return rhsLength
    if (rhsLength == 0) return length

    var cost = Array(length) { it }
    var newCost = Array(length) { 0 }

    for (i in 1..rhsLength - 1) {
        newCost[0] = i

        for (j in 1..length - 1) {
            val match = if (this[j - 1] == other[i - 1]) 0 else 1

            val costReplace = cost[j - 1] + match
            val costInsert = cost[j] + 1
            val costDelete = newCost[j - 1] + 1

            newCost[j] = Math.min(Math.min(costInsert, costDelete), costReplace)
        }

        val swap = cost
        cost = newCost
        newCost = swap
    }

    return cost[length - 1]
}

fun String.splitWhitespace(limit: Int = 0) = "\\s".toRegex().split(this, limit)

val String.beautified get() = split("-", "_").map { it.capitalize() }.joinToString(" ")

val Sequence<String>.camelCase
    get() = this.mapIndexed { index, s ->
        if (index == 0) s.toLowerCase() else s.capitalize()
    }.joinToString("")

val String.camelCase get() = split(" ", "-", "_").asSequence().camelCase

infix fun String.eqIgnoreCase(other: String) = equals(other, true)


inline val String.trimBraces get() = trim('{', '}')

fun Map<String, String>.resolve(str: String) = get(str) ?: str
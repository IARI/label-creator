package com.julianjarecki.ettiketten.app.fxproperties

import javafx.beans.property.SimpleObjectProperty
import kotlinx.serialization.*

@Serializable
class IntervalProperty(@Transient private val r: IntRange = IntRange.EMPTY) : SimpleObjectProperty<IntRange>(r) {
    @Serializer(forClass = IntervalProperty::class)
    companion object {
        override fun deserialize(decoder: Decoder): IntervalProperty {
            val (min, max) = decoder.decodeString().split("..").map { it.toInt() }
            return IntervalProperty(min..max)
        }

        override fun serialize(encoder: Encoder, obj: IntervalProperty) {
            (obj.value ?: IntRange.EMPTY).let {
                encoder.encodeString("${it.start}..${it.endInclusive}")
            }
        }
    }
}